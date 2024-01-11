package me.gritter.mcquests.api.datastore;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;
import me.gritter.mcquests.api.QuestsApiPlugin;
import me.gritter.mcquests.api.questtype.QuestType;
import me.gritter.mcquests.api.queststate.QuestProgress;
import me.gritter.mcquests.api.queststate.QuestState;
import org.bson.Document;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class MongoDBQuestDataStore implements QuestDataStore {

    private final MongoClient mongoClient;
    private final MongoCollection<Document> questCollection;
    private final Logger logger;

    public MongoDBQuestDataStore(String connectionString, String databaseName, String collectionName) {
        this.mongoClient = MongoClients.create(connectionString);
        this.logger = QuestsApiPlugin.getInstance().getLogger();

        MongoDatabase database = mongoClient.getDatabase(databaseName);
        this.questCollection = database.getCollection(collectionName);
    }

    @Override
    public CompletableFuture<QuestState> fetchQuestState(UUID playerUuid, QuestType questType) {
        return CompletableFuture.supplyAsync(() -> {
            String key = questType.getNamespace() + ":" + questType.getName() + ":" + playerUuid.toString();

            Document query = new Document("_id", key);
            Document result = questCollection.find(query).first();

            QuestState questState = null;
            if (result != null) {
                double progress = result.getDouble("progress");
                questState = new QuestState(playerUuid, questType, new QuestProgress(progress));
            }

            logger.info("[MongoDBQuestDataStore] FETCH " + key + " = " + questState);
            return questState;
        });
    }

    @Override
    public CompletableFuture<Void> putQuestState(QuestState questState) {
        return CompletableFuture.supplyAsync(() -> {
            String key = questState.getQuestType().getNamespace() + ":" + questState.getQuestType().getName() + ":" + questState.getPlayerUuid().toString();

            Document document = new Document("_id", key)
                    .append("progress", questState.getTotalProgress());

            questCollection.replaceOne(new Document("_id", key), document, new ReplaceOptions().upsert(true));

            logger.info("[MongoDBQuestDataStore] PUT " + key + " = " + questState);
            return null;
        });
    }

    @Override
    public void dispose() {
        logger.info("[MongoDBQuestDataStore] DISPOSE");

        mongoClient.close();
    }
}
