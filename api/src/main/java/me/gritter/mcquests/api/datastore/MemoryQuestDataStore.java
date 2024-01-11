package me.gritter.mcquests.api.datastore;

import me.gritter.mcquests.api.QuestsApiPlugin;
import me.gritter.mcquests.api.questtype.QuestType;
import me.gritter.mcquests.api.queststate.QuestProgress;
import me.gritter.mcquests.api.queststate.QuestState;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import static java.util.concurrent.CompletableFuture.completedFuture;

public class MemoryQuestDataStore implements QuestDataStore {

    private final Map<String, Double> states = new HashMap<>();

    private final Logger logger;

    public MemoryQuestDataStore() {
        this.logger = QuestsApiPlugin.getInstance().getLogger();
    }

    @Override
    public CompletableFuture<QuestState> fetchQuestState(UUID playerUuid, QuestType questType) {
        String key = questType.getNamespace() + ":" + questType.getName() + ":" + playerUuid.toString();

        Double progress = states.get(key);

        QuestState questState = null;
        if (progress != null) {
            questState = new QuestState(playerUuid, questType, new QuestProgress(progress));
        }

        logger.info("[MemoryQuestDataStore] FETCH " + key + " = " + questState);
        return completedFuture(questState);
    }

    @Override
    public CompletableFuture<Void> putQuestState(QuestState questState) {
        String key = questState.getQuestType().getNamespace() + ":" + questState.getQuestType().getName() + ":" + questState.getPlayerUuid().toString();

        double progress = questState.getTotalProgress();
        states.put(key, progress);

        logger.info("[MemoryQuestDataStore] PUT " + key + " = " + questState);
        return completedFuture(null);
    }

    @Override
    public void dispose() {
        logger.info("[MemoryQuestDataStore] DISPOSE");
    }
}
