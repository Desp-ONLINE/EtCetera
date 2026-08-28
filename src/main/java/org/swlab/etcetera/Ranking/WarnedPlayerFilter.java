package org.swlab.etcetera.Ranking;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.swlab.etcetera.Database.DatabaseRegister;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 경고 누적 유저 랭킹 제외 필터.
 *
 * <p>IDE-Manager의 경고 DB(IDEManager.WarnPlayer)를 직접 읽어
 * 경고 횟수 합({@code warnLogs[].amount})이 기준 이상인 유저의 uuid/닉네임을 모은다.
 * 유저 단위 랭킹 프로바이더들이 갱신 시점(비동기)에 호출해 집계·표시에서 걸러낸다.
 */
public final class WarnedPlayerFilter {

    /** 랭킹 제외 기준 경고 횟수 (이 값 이상이면 제외) */
    private static final int EXCLUDE_WARN_AMOUNT = 5;

    public record Excluded(Set<String> uuids, Set<String> nicknames) {

        public boolean contains(String uuid, String nickname) {
            return (uuid != null && uuids.contains(uuid))
                    || (nickname != null && nicknames.contains(nickname));
        }
    }

    private WarnedPlayerFilter() {
    }

    /** 경고 누적 제외 대상을 DB에서 조회한다. 비동기 스레드에서 호출할 것. */
    public static Excluded load() {
        Set<String> uuids = new HashSet<>();
        Set<String> nicknames = new HashSet<>();
        MongoCollection<Document> collection = DatabaseRegister.getInstance().getMongoClient()
                .getDatabase("IDEManager").getCollection("WarnPlayer");
        for (Document document : collection.find()) {
            List<Document> warnLogs = document.getList("warnLogs", Document.class);
            if (warnLogs == null) {
                continue;
            }
            int sum = 0;
            for (Document log : warnLogs) {
                Number amount = log.get("amount", Number.class);
                if (amount != null) {
                    sum += amount.intValue();
                }
            }
            if (sum < EXCLUDE_WARN_AMOUNT) {
                continue;
            }
            String uuid = document.getString("id");
            if (uuid != null) {
                uuids.add(uuid);
            }
            String nickname = document.getString("nickname");
            if (nickname != null) {
                nicknames.add(nickname);
            }
        }
        return new Excluded(uuids, nicknames);
    }
}
