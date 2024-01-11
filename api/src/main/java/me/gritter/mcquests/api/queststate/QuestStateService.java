package me.gritter.mcquests.api.queststate;

import me.gritter.mcquests.api.QuestsApiPlugin;
import me.gritter.mcquests.api.datastore.QuestDataStore;
import me.gritter.mcquests.api.event.PostQuestProgressChangeEvent;
import me.gritter.mcquests.api.questtype.QuestType;
import me.gritter.mcquests.api.questtype.QuestTypeService;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.util.Collections.synchronizedMap;
import static java.util.concurrent.CompletableFuture.completedFuture;

public class QuestStateService implements Listener {

    private static final int FLUSH_STATES_TIMEOUT = 20 * 10;

    private final Logger logger;
    private final QuestDataStore questDataStore;
    private final QuestTypeService questTypeService;

    private final Map<QuestStateKey, QuestState> cachedStates = synchronizedMap(new HashMap<>());

    public QuestStateService() {
        this.logger = QuestsApiPlugin.getInstance().getLogger();
        this.questDataStore = QuestsApiPlugin.getInstance().getQuestDataStore();
        this.questTypeService = QuestsApiPlugin.getInstance().getQuestTypeService();

        Bukkit.getPluginManager().registerEvents(this, QuestsApiPlugin.getInstance());

        Bukkit.getScheduler().runTaskTimer(QuestsApiPlugin.getInstance(), this::flushStatesTask, FLUSH_STATES_TIMEOUT, FLUSH_STATES_TIMEOUT);
    }

    @EventHandler
    public void handlePostQuestProgressChangeEvent(PostQuestProgressChangeEvent event) {
        QuestState state = event.getQuestState();
        if (state.getQuestType().isCached()) {
            QuestStateKey key = QuestStateKey.of(state);
            cachedStates.put(key, state);
        } else {
            questDataStore.putQuestState(state);
        }
    }

    @EventHandler
    public void handlePlayerQuitEvent(PlayerQuitEvent event) {
        UUID playerUuid = event.getPlayer().getUniqueId();

        Iterator<QuestState> it = cachedStates.values().iterator();
        while (it.hasNext()) {
            QuestState state = it.next();
            if (state.getPlayerUuid().equals(playerUuid)) {
                questDataStore.putQuestState(state);
                it.remove();
            }
        }
    }

    private void flushStatesTask() {
        flushStates().thenAccept(x -> logger.info("All states flushed."));
    }

    public CompletableFuture<Void> flushStates() {
        Collection<QuestState> states = new HashSet<>(cachedStates.values());
        return CompletableFuture.allOf(
                states.stream()
                        .map(
                                state -> questDataStore.putQuestState(state)
                                        .thenAccept(x -> cachedStates.remove(QuestStateKey.of(state)))
                        )
                        .toArray(CompletableFuture[]::new)
        );
    }

    public CompletableFuture<QuestState> getQuestState(OfflinePlayer player, QuestType questType) {
        if (!questTypeService.isRegistered(questType)) {
            throw new IllegalArgumentException("Unregistered QuestType.");
        }

        QuestStateKey key = new QuestStateKey(player.getUniqueId(), questType);
        if (questType.isCached()) {
            QuestState cached = cachedStates.get(key);
            if (cached != null) {
                return completedFuture(cached);
            }
        }

        return questDataStore.fetchQuestState(player.getUniqueId(), questType)
                .thenApply(state -> {
                    if (state == null) {
                        state = new QuestState(player.getUniqueId(), questType);
                    }

                    if (questType.isCached()) {
                        cachedStates.put(key, state);
                    }

                    return state;
                });
    }

    public CompletableFuture<Collection<QuestState>> getQuestStates(OfflinePlayer who) {
        Collection<CompletableFuture<QuestState>> stateFutures =
                questTypeService.getQuestTypes().stream()
                        .map(qt -> getQuestState(who, qt))
                        .collect(Collectors.toList());

        return CompletableFuture.allOf(stateFutures.toArray(CompletableFuture[]::new))
                .thenApply(x ->
                        stateFutures.stream()
                                .map(CompletableFuture::join)
                                .collect(Collectors.toList())
                );
    }

    private static class QuestStateKey {

        private final UUID playerUuid;
        private final QuestType questType;

        public QuestStateKey(UUID playerUuid, QuestType questType) {
            this.playerUuid = playerUuid;
            this.questType = questType;
        }

        public UUID getPlayerUuid() {
            return playerUuid;
        }

        public QuestType getQuestType() {
            return questType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            QuestStateKey that = (QuestStateKey) o;
            return Objects.equals(playerUuid, that.playerUuid) && Objects.equals(questType, that.questType);
        }

        @Override
        public int hashCode() {
            return Objects.hash(playerUuid, questType);
        }

        public static QuestStateKey of(QuestState questState) {
            return new QuestStateKey(questState.getPlayerUuid(), questState.getQuestType());
        }
    }
}
