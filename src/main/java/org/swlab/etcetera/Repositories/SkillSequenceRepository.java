package org.swlab.etcetera.Repositories;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.swlab.etcetera.Database.DatabaseRegister;
import org.swlab.etcetera.Dto.SkillSequenceDTO;
import org.swlab.etcetera.EtCetera;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 자동 연계 시스템(등록한 합성무기 순서) 저장소. Mongo 컬렉션 "SkillSequence".
 * 접속 시 로드 / 변경 시 비동기 저장 / 퇴장·리로드 시 저장.
 */
public class SkillSequenceRepository {

    private static SkillSequenceRepository instance;

    public static SkillSequenceRepository getInstance() {
        if (instance == null) instance = new SkillSequenceRepository();
        return instance;
    }

    public static final int MAX_SIZE = 9;

    private final MongoCollection<Document> collection = DatabaseRegister.getInstance().getMongoDatabase().getCollection("SkillSequence");
    private final Map<String, SkillSequenceDTO> cache = new ConcurrentHashMap<>();

    public SkillSequenceRepository() {
        instance = this;
    }

    public void loadUserData(Player player) {
        String uuid = player.getUniqueId().toString();
        Document document = collection.find(new Document("uuid", uuid)).first();
        List<String> weapons = new ArrayList<>();
        if (document != null) {
            List<String> stored = document.getList("weapons", String.class);
            if (stored != null) weapons.addAll(stored);
        }
        cache.put(uuid, SkillSequenceDTO.builder().uuid(uuid).weapons(weapons).build());
    }

    public void saveUserData(Player player) {
        SkillSequenceDTO dto = cache.get(player.getUniqueId().toString());
        if (dto == null) return;
        save(dto);
    }

    private void save(SkillSequenceDTO dto) {
        Document document = new Document("uuid", dto.getUuid())
                .append("weapons", new ArrayList<>(dto.getWeapons()));
        collection.replaceOne(new Document("uuid", dto.getUuid()), document, new ReplaceOptions().upsert(true));
    }

    private void saveAsync(SkillSequenceDTO dto) {
        // 메인 스레드에서 스냅샷을 떠서 비동기 저장 (리스트 동시 수정 방지)
        SkillSequenceDTO copy = SkillSequenceDTO.builder().uuid(dto.getUuid()).weapons(new ArrayList<>(dto.getWeapons())).build();
        Bukkit.getScheduler().runTaskAsynchronously(EtCetera.getInstance(), () -> save(copy));
    }

    public void unload(Player player) {
        cache.remove(player.getUniqueId().toString());
    }

    public boolean isLoaded(Player player) {
        return cache.containsKey(player.getUniqueId().toString());
    }

    /** 등록 순서 그대로의 무기 키 목록(복사본). */
    public List<String> getSequence(Player player) {
        SkillSequenceDTO dto = cache.get(player.getUniqueId().toString());
        if (dto == null) return Collections.emptyList();
        return new ArrayList<>(dto.getWeapons());
    }

    public boolean contains(Player player, String key) {
        return getSequence(player).contains(key);
    }

    /** 맨 뒤에 추가. 이미 있거나 꽉 찼으면 false. */
    public boolean add(Player player, String key) {
        SkillSequenceDTO dto = cache.get(player.getUniqueId().toString());
        if (dto == null) return false;
        List<String> weapons = dto.getWeapons();
        if (weapons.contains(key) || weapons.size() >= MAX_SIZE) return false;
        weapons.add(key);
        saveAsync(dto);
        return true;
    }

    public boolean remove(Player player, int index) {
        SkillSequenceDTO dto = cache.get(player.getUniqueId().toString());
        if (dto == null) return false;
        List<String> weapons = dto.getWeapons();
        if (index < 0 || index >= weapons.size()) return false;
        weapons.remove(index);
        saveAsync(dto);
        return true;
    }

    /** index 번째 무기를 한 칸 앞(먼저 시전)으로 이동. */
    public boolean moveForward(Player player, int index) {
        SkillSequenceDTO dto = cache.get(player.getUniqueId().toString());
        if (dto == null) return false;
        List<String> weapons = dto.getWeapons();
        if (index <= 0 || index >= weapons.size()) return false;
        Collections.swap(weapons, index, index - 1);
        saveAsync(dto);
        return true;
    }

    public void clear(Player player) {
        SkillSequenceDTO dto = cache.get(player.getUniqueId().toString());
        if (dto == null) return;
        dto.getWeapons().clear();
        saveAsync(dto);
    }
}
