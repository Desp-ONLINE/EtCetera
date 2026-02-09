package org.swlab.etcetera.Listener;

import com.binggre.binggreapi.utils.ColorManager;
import com.binggre.mmotimereset.api.DailyResetEvent;
import com.binggre.mmotimereset.api.WeeklyResetEvent;
import com.binggre.velocitysocketclient.VelocityClient;
import de.kinglol12345.GUIPlus.events.GUIClickEvent;
import fr.nocsy.mcpets.api.MCPetsAPI;
import fr.nocsy.mcpets.data.Pet;
import fr.phoenixdevt.profiles.event.ProfileSelectEvent;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.adapters.BukkitPlayer;
import io.lumine.mythic.bukkit.events.MythicDamageEvent;
import io.lumine.mythic.bukkit.events.MythicProjectileHitEvent;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.auras.AuraRegistry;
import io.lumine.mythic.lib.api.event.PlayerAttackEvent;
import io.lumine.mythic.lib.api.event.skill.PlayerCastSkillEvent;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.damage.DamageMetadata;
import io.lumine.mythic.lib.glow.external.GlowAPIModule;
import net.Indyuce.mmocore.api.MMOCoreAPI;
import net.Indyuce.mmocore.api.event.PlayerLevelUpEvent;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Repositories.RaidCoinRepository;
import org.swlab.etcetera.Repositories.UserSettingRepository;
import org.swlab.etcetera.Util.CommandUtil;
import org.swlab.etcetera.Util.NameTagUtil;
import org.swlab.etcetera.Util.PetUtil;

public class BasicListener implements Listener {


    int firstJoinCount = 0;



    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getCause().equals(EntityDamageEvent.DamageCause.WITHER)) {
            e.setCancelled(true);
        }
    }


    @EventHandler
    public void onFishingRodHitPlayer(PlayerFishEvent e) {
        if (e.getCaught() != null) {
            if (e.getCaught() instanceof Player) {
                e.getPlayer().sendMessage("§c 낚싯대로 사람을 낚지 마세요! 인생을 낚으세요!");
                return;
            }
        }
    }


    @EventHandler
    public void cancelPlayerDebuff(MythicProjectileHitEvent e) {
        if (e.getProjectile().getData().getCaster().getEntity().isPlayer()) {
            if (e.getEntity().isPlayer()) {
                MMOCoreAPI mmoCoreAPI = new MMOCoreAPI(EtCetera.getInstance());
                if (mmoCoreAPI.isInSameParty((Player) e.getProjectile().getData().getCaster().getEntity().asPlayer().getBukkitEntity(), (Player) e.getEntity().asPlayer().getBukkitEntity())) {
                    e.setCancelled(true);
                }
            }
        }

        if (EtCetera.getChannelType().equals("dungeon")) {
            if (e.getProjectile().getData().getCaster().getEntity() instanceof BukkitPlayer) {
                if (e.getEntity().isPlayer()) {
                    e.setCancelled(true);
                }
            }
        }
        if (EtCetera.getChannelType().equals("lobby")) {
            String worldName = "pvp";
            if (!e.getEntity().getWorld().getName().equals(worldName)) {
                if (e.getProjectile().getData().getCaster().getEntity() instanceof BukkitPlayer) {
                    if (e.getEntity().isPlayer()) {
                        e.setCancelled(true);
                    }
                }
            }
            if (e.getEntity().getWorld().getName().equals(worldName)) {
                if (e.getEntity().getLocation().getY() >= 18) {
                    e.setCancelled(true);
                }
            }
        }
    }


    @EventHandler
    public void skillOnVillage(PlayerCastSkillEvent e) {
        if (EtCetera.getInstance().getChannelType().equals("lobby") && (e.getPlayer().getWorld().getName().equals("world") || e.getPlayer().getWorld().getName().equals("fishing") || e.getPlayer().getWorld().getName().equals("jump")) || e.getPlayer().getWorld().getName().equals("pandora")) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onDying(PlayerInteractEntityEvent e){
        if(e.getPlayer().isOp()){
            return;
        }
        if(e.getRightClicked() instanceof Wolf wolf){
            e.setCancelled(true);
        }
    }



    @EventHandler
    public void onGuiClick(GUIClickEvent e) {
        InventoryClickEvent inventoryClickEvent = e.getInventoryClickEvent();
        Player player = (Player) inventoryClickEvent.getWhoClicked();
        player.playSound(player, "uisounds:buttonclick2", 1.0F, 1.0F);
    }

    @EventHandler
    public void onSculkBloom(SculkBloomEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onLevelUp(PlayerLevelUpEvent e) {
        Player player = e.getPlayer();
        player.playSound(player, "uisounds:congratulations", 1.0F, 1.0F);
        NameTagUtil.setPlayerNameTag(player);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();


        e.setJoinMessage("");
        if ((!player.hasPlayedBefore() && EtCetera.getChannelType().equals("lobby")) && EtCetera.getChannelNumber() == 1) {
            firstJoinCount++;
            String text = " §f摩 #8FFFAE" + player.getName() + " 님께서 서버에 &e첫 접속 #8FFFAE하셨습니다! 환영해주세요!";
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (onlinePlayer.getName().equals("dople_L")) {
                    onlinePlayer.sendMessage("§7(오늘의 " + firstJoinCount + "번째 첫 접속자입니다.)");
                }
            }
            String format = ColorManager.format(text);
            Bukkit.broadcastMessage(format);

            VelocityClient.getInstance().getConnectClient().send(FirstJoinVelocityListener.class, format);


            if (!player.hasPermission("tutorial") && EtCetera.getChannelType().equals("lobby")) {
                Bukkit.getScheduler().runTaskLater(EtCetera.getInstance(), () -> {
                    CommandUtil.runCommandAsOP(player, "튜토리얼");
                    player.sendMessage("§a 튜토리얼을 진행해주세요!");
                }, 200L);
            }

        }
        Bukkit.getScheduler().runTaskLater(EtCetera.getInstance(), () -> {
            {
                PetUtil.loadPlayerPetData(player);

            }
        }, 40L);


        Bukkit.getScheduler().runTaskLater(EtCetera.getInstance(), new Runnable() {
            @Override
            public void run() {
                player.clearActivePotionEffects();
            }
        }, 20L);
        if (EtCetera.getInstance().getChannelType().equals("lobby")) {
            CommandUtil.runCommandAsOP(player, "spawn");
        }
        MMOCoreAPI mmoCoreAPI = new MMOCoreAPI(EtCetera.getInstance());
        mmoCoreAPI.getPlayerData(player).setClassPoints(999);

        Bukkit.getScheduler().runTaskLater(EtCetera.getInstance(), new Runnable() {
            @Override
            public void run() {
                player.setNoDamageTicks(0);
                player.setHealth(player.getMaxHealth());
            }
        }, 20L);

        UserSettingRepository.getInstance().loadUserSetting(player);
        RaidCoinRepository.getInstance().loadUserData(player);

    }

    @EventHandler
    public void onWeeklyChange(WeeklyResetEvent e){
        RaidCoinRepository.getInstance().resetDatas();
    }

    @EventHandler
    public void onRightClickToBlock(PlayerInteractEvent e) {
        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Location location = e.getClickedBlock().getLocation();

            if (location.getBlockX() == 1193 && location.getBlockY() == 15 && location.getBlockZ() == 1229) {
                e.getPlayer().teleport(new Location(Bukkit.getWorld("world"), 1146, 6, 1242));
            }
        }


    }

    @EventHandler
    public void onDailyReset(DailyResetEvent e) {
        firstJoinCount = 0;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        e.setQuitMessage("");
        Pet activePet = MCPetsAPI.getActivePet(player.getUniqueId());
        if (activePet == null) {
            PetUtil.savePlayerPetData(player, "");
        } else {
            String id = activePet.getId();
            PetUtil.savePlayerPetData(player, id);
        }
        UserSettingRepository.getInstance().saveUserSetting(player);
        RaidCoinRepository.getInstance().saveUserData(player);


    }


    @EventHandler
    public void onPotion(EntityPotionEffectEvent e) {

        Entity entity = e.getEntity();

        // 대상: 소 (다른 엔티티도 원하면 조건 제거)
        if (!(entity instanceof Cow cow)) return;

        PotionEffect newEffect = e.getNewEffect();
        PotionEffect oldEffect = e.getOldEffect();

        /* ---------------- 흉조 적용 / 갱신 ---------------- */
        if (newEffect != null && newEffect.getType() == PotionEffectType.BAD_OMEN) {
            cow.setGlowing(true);
            return;
        }

        /* ---------------- 흉조 제거 / 만료 ---------------- */
        if (oldEffect != null
                && oldEffect.getType() == PotionEffectType.BAD_OMEN
                && e.getAction() != EntityPotionEffectEvent.Action.ADDED) {

            cow.setGlowing(false);
        }
    }


    @EventHandler
    public void onFireTick(BlockIgniteEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onProfileLoad(ProfileSelectEvent e) {
        Player player = e.getPlayer();
        player.setHealth(player.getMaxHealth());
        NameTagUtil.setPlayerNameTag(player);

        if (!e.getPlayer().hasPermission("tutorial") && EtCetera.getChannelType().equals("lobby")) {
            CommandUtil.runCommandAsOP(e.getPlayer(), "튜토리얼");
        }
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerUseNetherPortal(PlayerPortalEvent e) {
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void cancelThrowItem(PlayerDropItemEvent e) {
        Item itemDrop = e.getItemDrop();
        ItemStack itemStack = itemDrop.getItemStack();
        if (!e.getPlayer().isOp()) {
            e.setCancelled(true);
        }
        String id = MMOItems.getID(itemStack);
        if (id == null) {
            return;
        }
//        System.out.println("id = " + id);
        if (id.startsWith("직업무기_5")) {
            e.setCancelled(true);

        }
    }

    @EventHandler
    public void cancelInteractItem(PlayerInteractEvent e) {
        if (!e.getPlayer().isOp()) {
            if (e.getClickedBlock() == null) {
                return;
            }
            if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getClickedBlock().getType().isInteractable()) {
                if (e.getClickedBlock().getType().equals(Material.CHEST)) {
                    if (!e.getPlayer().getWorld().getName().equals("adventures")) {
                        e.setCancelled(true);
                    }
                } else {
                    e.setCancelled(true);


                }
            }
        }
    }

    @EventHandler
    public void onFishing(PlayerFishEvent e) {
        boolean fishing = e.getPlayer().getWorld().getName().equals("fishing");
        if (!fishing) {
            e.getPlayer().sendMessage("§c 이곳에서 낚시 하실 수 없습니다.");
            e.setCancelled(true);

            return;
        }
    }

    @EventHandler
    public void onFishing(PlayerInteractEvent e) {
        if (e.getItem() == null) {
            return;
        }
        if (e.getItem().getType().equals(Material.ENDER_PEARL)) {
            e.getPlayer().sendMessage("§c 엔더진주 사용 불가합니다.");
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void dontStomp(EntityChangeBlockEvent e) {
        Block block = e.getBlock();
        if (block.getType() == Material.FARMLAND && e.getEntity() != null) {
            e.setCancelled(true);
        }
    }


    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player player = e.getPlayer();
        e.setKeepInventory(true);
        e.setDeathMessage(null);
        if (e.getPlayer().getWorld().getName().equals("raid")) {
            return;
        }
        int delay = 0;
        Note[] notes = {
                Note.natural(0, Note.Tone.D),   // 낮은 파
                Note.natural(0, Note.Tone.C),   // 중간 라
                Note.natural(0, Note.Tone.B),   // 높은 도
                Note.natural(0, Note.Tone.A)    // 높은 파
        };

        String[] messages = {
                "#FFD4D4D   E   A   D", "#FA9C9CD  E  A  D", "#FF5A5AD E A D", "#FF1D1DDEAD"
        };
        for (int i = 0; i < 4; i++) {
            Note note = notes[i].sharped();
            int finalI = i;
            int finalDelay = delay;
            Bukkit.getScheduler().runTaskLater(EtCetera.getInstance(), new Runnable() {
                @Override
                public void run() {
                    player.sendTitle(ColorManager.format(messages[finalI]), "§f사망하여 로비에서 부활합니다.", 0, 10, 0);
                    player.playNote(player.getLocation(), Instrument.PIANO, note);
                    player.addPotionEffect(PotionEffectType.BLINDNESS.createEffect(finalDelay, 20));

                }
            }, delay);

            delay += 5;
        }

    }

    @EventHandler
    public void cancelCraftItem(CraftItemEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void cancelHungerChange(FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void cancelBlockPlace(BlockPlaceEvent e) {
        if (!e.getPlayer().isOp()) {
            e.setCancelled(true);
        }
    }


    @EventHandler
    public void cancelItemConsume(PlayerItemConsumeEvent e) {
        if (!e.getPlayer().isOp()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (!e.getPlayer().isOp()) {
            e.setCancelled(true);
        }
    }


}
