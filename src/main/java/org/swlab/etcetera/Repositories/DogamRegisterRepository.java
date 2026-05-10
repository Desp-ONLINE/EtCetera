package org.swlab.etcetera.Repositories;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.swlab.etcetera.Database.DatabaseRegister;
import org.swlab.etcetera.Dto.DogamRegisterDTO;
import org.swlab.etcetera.Dto.DogamRegisterDTO.CertificateInfo;
import org.swlab.etcetera.Dto.DogamRegisterDTO.MMOItemRef;
import org.swlab.etcetera.Dto.DogamRegisterDTO.RequirementGroup;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DogamRegisterRepository {

    private static DogamRegisterRepository instance;

    private final HashMap<String, DogamRegisterDTO> nameToDogam = new HashMap<>();

    private static final MongoCollection<Document> definitionCollection =
            DatabaseRegister.getInstance().getMongoDatabase().getCollection("DogamRegister");

    private static final MongoCollection<Document> issueLogCollection =
            DatabaseRegister.getInstance().getMongoDatabase().getCollection("DogamIssueLog");

    public DogamRegisterRepository() {
        instance = this;
    }

    public static DogamRegisterRepository getInstance() {
        if (instance == null) {
            instance = new DogamRegisterRepository();
        }
        return instance;
    }

    public void loadData() {
        nameToDogam.clear();

        for (Document doc : definitionCollection.find()) {
            String name = doc.getString("name");
            if (name == null) {
                continue;
            }

            List<RequirementGroup> requirements = new ArrayList<>();
            List<Document> reqDocs = doc.getList("requirements", Document.class);
            if (reqDocs != null) {
                for (Document reqDoc : reqDocs) {
                    List<MMOItemRef> alternatives = new ArrayList<>();
                    List<Document> altDocs = reqDoc.getList("alternatives", Document.class);
                    if (altDocs != null) {
                        for (Document altDoc : altDocs) {
                            alternatives.add(new MMOItemRef(
                                    altDoc.getString("type"),
                                    altDoc.getString("id")
                            ));
                        }
                    }
                    requirements.add(new RequirementGroup(
                            alternatives,
                            reqDoc.getInteger("amount", 1),
                            reqDoc.getBoolean("consume", true)
                    ));
                }
            }

            Document certDoc = doc.get("certificate", Document.class);
            CertificateInfo certificate = null;
            if (certDoc != null) {
                certificate = new CertificateInfo(
                        certDoc.getString("type"),
                        certDoc.getString("id"),
                        certDoc.getInteger("amount", 1)
                );
            }

            DogamRegisterDTO dto = DogamRegisterDTO.builder()
                    .name(name)
                    .requirements(requirements)
                    .certificate(certificate)
                    .build();

            nameToDogam.put(name, dto);
        }
    }

    public DogamRegisterDTO getByName(String name) {
        return nameToDogam.get(name);
    }

    public int getLoadedCount() {
        return nameToDogam.size();
    }

    public List<String> getAllNames() {
        return new ArrayList<>(nameToDogam.keySet());
    }

    public void recordIssue(Player player, String dogamName) {
        Document log = new Document()
                .append("playerName", player.getName())
                .append("playerUUID", player.getUniqueId().toString())
                .append("dogamName", dogamName)
                .append("issuedAt", new Date());
        issueLogCollection.insertOne(log);
    }
}
