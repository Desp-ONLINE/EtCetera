package org.swlab.etcetera.Ranking;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.Particle;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TextDisplay;
import org.swlab.etcetera.EtCetera;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 통합 랭킹 홀로그램 관리자.
 *
 * <p>등록된 모든 컨텐츠({@link RankingProvider})를 30분마다 갱신해
 * 컨텐츠별 홀로그램(TextDisplay)으로 표시한다.
 * 위치는 /랭킹위치설정 &lt;컨텐츠이름&gt; 으로 지정하며 plugins/EtCetera/rankings.yml에 저장된다.
 */
public class RankingHologramManager {

    /** 갱신 주기 (30분) */
    private static final long REFRESH_PERIOD_TICKS = 20L * 60L * 30L;
    /** 등록 후 첫 갱신까지 대기. 수집 단계가 로드 안 된 플레이어를 건너뛰므로 다음 틱이면 충분하다 */
    private static final long FIRST_REFRESH_DELAY_TICKS = 1L;

    private static final String HOLOGRAM_TAG_PREFIX = "ranking_hologram_";
    private static final String C_FOOTER = hex("#8A8A9A");

    private static RankingHologramManager instance;

    public static RankingHologramManager getInstance() {
        return instance;
    }

    public static void enable(EtCetera plugin) {
        instance = new RankingHologramManager(plugin);
    }

    public static void disable() {
        if (instance != null) {
            instance.holograms.values().forEach(Entity::remove);
            instance.holograms.clear();
            instance = null;
        }
    }

    /** hex 색상 문자열(#RRGGBB)을 레거시 색코드로 변환한다. Provider들도 공용으로 사용. */
    public static String hex(String color) {
        return net.md_5.bungee.api.ChatColor.of(color).toString();
    }

    private static final String C_RANK_1 = hex("#FFD700");   // 1위 금색
    private static final String C_RANK_2 = hex("#C7D6E8");   // 2위 은색
    private static final String C_RANK_3 = hex("#E8883A");   // 3위 동색

    private static final String C_NICK_1 = hex("#FFEBA8");   // 1위 닉네임 (연한 금색)
    private static final String C_NICK_2 = hex("#E4EDF6");   // 2위 닉네임 (연한 은색)
    private static final String C_NICK_3 = hex("#F4C9A4");   // 3위 닉네임 (연한 동색)

    private static final String C_RANK_GRAY = hex("#8A8A9A"); // 6위 이하 순위 (회색, 테마 무관)
    private static final String C_NICK_GRAY = hex("#C4C4CE"); // 6위 이하 닉네임 (연회색)

    /** 순위 라벨. 1~3위는 금/은/동, 4~5위는 컨텐츠 테마 색, 6위 이하는 테마 무관 회색으로 "N위"를 표시한다. */
    public static String rankLabel(int rank, String etcColor) {
        switch (rank) {
            case 1:
                return C_RANK_1 + "1위 ";
            case 2:
                return C_RANK_2 + "2위 ";
            case 3:
                return C_RANK_3 + "3위 ";
            default:
                return (rank <= 5 ? etcColor : C_RANK_GRAY) + rank + "위 ";
        }
    }

    /** 닉네임 색. 순위 색보다 연한 파스텔톤 — 1~3위 연한 금/은/동, 4~5위 컨텐츠별 연한 테마 색, 6위 이하 연회색. */
    public static String nicknameColor(int rank, String etcColor) {
        switch (rank) {
            case 1:
                return C_NICK_1;
            case 2:
                return C_NICK_2;
            case 3:
                return C_NICK_3;
            default:
                return rank <= 5 ? etcColor : C_NICK_GRAY;
        }
    }

    private final EtCetera plugin;
    private final File locationFile;
    private final Map<String, RankingProvider> providers = new LinkedHashMap<>();
    private final Map<String, Location> locations = new HashMap<>();
    private final Map<String, TextDisplay> holograms = new HashMap<>();

    private RankingHologramManager(EtCetera plugin) {
        this.plugin = plugin;
        this.locationFile = new File(plugin.getDataFolder(), "rankings.yml");
        Bukkit.getScheduler().runTaskTimer(plugin, this::refreshAll, REFRESH_PERIOD_TICKS, REFRESH_PERIOD_TICKS);
        Bukkit.getScheduler().runTaskTimer(plugin, this::playParticles, PARTICLE_PERIOD_TICKS, PARTICLE_PERIOD_TICKS);
    }

    /* ===== 파티클 (텍스트 테두리 사각형) ===== */

    /** 파티클 주기 (0.5초) */
    private static final long PARTICLE_PERIOD_TICKS = 10L;
    /** 테두리 점 간격 (블럭) */
    private static final double PARTICLE_STEP = 0.25;
    /** 텍스트와 테두리 사이 여백 (블럭) */
    private static final double BORDER_PADDING = 0.35;

    /** 컨텐츠별 홀로그램 텍스트 크기 추정치 [가로, 세로] (블럭) */
    private final Map<String, double[]> borderSizes = new HashMap<>();

    /**
     * 표시 중인 각 홀로그램 텍스트 둘레에 컨텐츠 테마색 더스트 파티클로 사각형 테두리를 그린다.
     * 사각형은 위치 설정 당시 바라보던 방향(yaw) 기준으로 세워진다.
     */
    private void playParticles() {
        for (Map.Entry<String, TextDisplay> entry : holograms.entrySet()) {
            TextDisplay hologram = entry.getValue();
            if (hologram == null || !hologram.isValid()) {
                continue;
            }
            RankingProvider provider = providers.get(entry.getKey());
            double[] size = borderSizes.get(entry.getKey());
            if (provider == null || provider.getParticleColor() == null || size == null) {
                continue;
            }
            Location base = locations.get(entry.getKey());
            if (base == null || base.getWorld() == null) {
                continue;
            }
            World world = base.getWorld();
            Particle.DustOptions dust = new Particle.DustOptions(provider.getParticleColor(), 0.9f);

            // 위치 설정 당시 시선의 가로(수평 수직) 방향 벡터
            double yawRad = Math.toRadians(base.getYaw());
            double px = Math.cos(yawRad);
            double pz = Math.sin(yawRad);

            double halfWidth = size[0] / 2 + BORDER_PADDING;
            double bottom = base.getY() - BORDER_PADDING;
            double top = base.getY() + size[1] + BORDER_PADDING;

            // 상·하 가로변
            for (double t = -halfWidth; t <= halfWidth; t += PARTICLE_STEP) {
                spawnDust(world, dust, base.getX() + px * t, bottom, base.getZ() + pz * t);
                spawnDust(world, dust, base.getX() + px * t, top, base.getZ() + pz * t);
            }
            // 좌·우 세로변
            for (double y = bottom + PARTICLE_STEP; y < top; y += PARTICLE_STEP) {
                spawnDust(world, dust, base.getX() - px * halfWidth, y, base.getZ() - pz * halfWidth);
                spawnDust(world, dust, base.getX() + px * halfWidth, y, base.getZ() + pz * halfWidth);
            }
        }
    }

    private void spawnDust(World world, Particle.DustOptions dust, double x, double y, double z) {
        world.spawnParticle(Particle.DUST, x, y, z, 1, 0, 0, 0, 0, dust);
    }

    /**
     * 홀로그램 텍스트의 렌더 크기를 추정한다. (스케일 1 기준: 1픽셀 = 0.025블럭,
     * 줄 높이 10픽셀 = 0.25블럭, 영문/숫자 약 6픽셀, 한글·기호 약 10픽셀)
     */
    private double[] measureText(String text) {
        String[] textLines = text.split("\n", -1);
        double maxWidth = 0;
        for (String line : textLines) {
            String stripped = line.replaceAll("§.", "");
            double width = 0;
            for (char c : stripped.toCharArray()) {
                width += c < 0x2000 ? 0.15 : 0.25;
            }
            maxWidth = Math.max(maxWidth, width);
        }
        return new double[]{maxWidth, textLines.length * 0.25};
    }

    /**
     * 랭킹 컨텐츠를 등록한다. 외부 플러그인은 onEnable에서 호출하면 된다.
     * 등록 10초 후 첫 갱신이 실행된다.
     */
    public void registerProvider(RankingProvider provider) {
        String key = provider.getContentKey();
        providers.put(key, provider);
        loadLocation(key);
        Bukkit.getScheduler().runTaskLater(plugin, () -> refresh(key), FIRST_REFRESH_DELAY_TICKS);
        plugin.getLogger().info("랭킹 홀로그램 컨텐츠 등록: " + key);
    }

    public Set<String> getContentKeys() {
        return providers.keySet();
    }

    /* ===== 위치 저장/로드 ===== */

    private void loadLocation(String key) {
        if (!locationFile.exists()) {
            return;
        }
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(locationFile);
        String path = "locations." + key;
        if (!yml.contains(path + ".world")) {
            return;
        }
        String worldName = yml.getString(path + ".world");
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            plugin.getLogger().warning("랭킹 홀로그램(" + key + ") 월드를 찾을 수 없습니다: " + worldName);
            return;
        }
        locations.put(key, new Location(world,
                yml.getDouble(path + ".x"),
                yml.getDouble(path + ".y"),
                yml.getDouble(path + ".z"),
                (float) yml.getDouble(path + ".yaw"),
                0f));
    }

    /**
     * 컨텐츠의 홀로그램 위치를 설정하고 파일에 저장한 뒤 즉시 갱신한다.
     *
     * @return 등록되지 않은 컨텐츠 키면 false
     */
    public boolean setLocation(String key, Location location) {
        if (!providers.containsKey(key)) {
            return false;
        }
        locations.put(key, location.clone());

        YamlConfiguration yml = locationFile.exists()
                ? YamlConfiguration.loadConfiguration(locationFile)
                : new YamlConfiguration();
        String path = "locations." + key;
        yml.set(path + ".world", location.getWorld().getName());
        yml.set(path + ".x", location.getX());
        yml.set(path + ".y", location.getY());
        yml.set(path + ".z", location.getZ());
        yml.set(path + ".yaw", (double) location.getYaw());
        try {
            yml.save(locationFile);
        } catch (IOException e) {
            plugin.getLogger().warning("rankings.yml 저장 실패: " + e.getMessage());
        }

        TextDisplay old = holograms.remove(key);
        if (old != null) {
            old.remove();
        }
        refresh(key);
        return true;
    }

    /* ===== 갱신 ===== */

    public void refreshAll() {
        for (String key : providers.keySet()) {
            refresh(key);
        }
    }

    /**
     * 수집(메인 스레드) → 줄 생성(비동기, DB 조회 가능) → 홀로그램 반영(메인 스레드).
     */
    public void refresh(String key) {
        RankingProvider provider = providers.get(key);
        if (provider == null) {
            return;
        }
        provider.collectSync();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            List<String> lines;
            try {
                lines = provider.buildLines();
            } catch (Exception e) {
                plugin.getLogger().warning("랭킹 갱신 실패 (" + key + "): " + e.getMessage());
                return;
            }
            String text = String.join("\n", lines)
                    + "\n" + C_FOOTER + "갱신: " + new SimpleDateFormat("MM/dd HH:mm").format(new Date())+" (30분 주기)";
            Bukkit.getScheduler().runTask(plugin, () -> applyHologram(key, text));
        });
    }

    /* ===== 홀로그램 ===== */

    private void applyHologram(String key, String text) {
        borderSizes.put(key, measureText(text));
        Location location = locations.get(key);
        if (location == null || location.getWorld() == null) {
            return;
        }
        TextDisplay hologram = holograms.get(key);
        if (hologram == null || !hologram.isValid()) {
            hologram = spawnHologram(key, location);
        }
        hologram.setText(text);
    }

    private TextDisplay spawnHologram(String key, Location location) {
        if (!location.getChunk().isLoaded()) {
            location.getChunk().load();
        }
        // 리로드 등으로 남아있을 수 있는 이전 홀로그램 제거
        String tag = HOLOGRAM_TAG_PREFIX + key;
        for (Entity entity : location.getChunk().getEntities()) {
            if (entity instanceof TextDisplay && entity.getScoreboardTags().contains(tag)) {
                entity.remove();
            }
        }
        // 설정 당시 바라본 방향으로 고정 배치. 텍스트가 설정자를 마주 보도록 yaw 반전, 기울어지지 않게 pitch 0
        Location spawnLocation = location.clone();
        spawnLocation.setYaw(location.getYaw() + 180f);
        spawnLocation.setPitch(0f);
        TextDisplay display = location.getWorld().spawn(spawnLocation, TextDisplay.class, d -> {
            d.addScoreboardTag(tag);
            d.setPersistent(false);
            d.setBillboard(Display.Billboard.FIXED);
            d.setAlignment(TextDisplay.TextAlignment.CENTER);
            d.setLineWidth(300);
            d.setShadowed(true);
            // 주변 광원과 무관하게 항상 최대 밝기로 표시
            d.setBrightness(new Display.Brightness(15, 15));
            d.setSeeThrough(false);
            d.setViewRange(1.0f);
        });
        holograms.put(key, display);
        return display;
    }
}
