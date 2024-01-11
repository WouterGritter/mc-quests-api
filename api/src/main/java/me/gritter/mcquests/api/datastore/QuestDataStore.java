package me.gritter.mcquests.api.datastore;

import me.gritter.mcquests.api.questtype.QuestType;
import me.gritter.mcquests.api.queststate.QuestState;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface QuestDataStore {

    CompletableFuture<QuestState> fetchQuestState(UUID playerUuid, QuestType questType);

    CompletableFuture<Void> putQuestState(QuestState questState);

    void dispose();
}
