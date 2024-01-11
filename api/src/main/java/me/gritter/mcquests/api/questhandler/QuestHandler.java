package me.gritter.mcquests.api.questhandler;

import me.gritter.mcquests.api.QuestsApiPlugin;
import me.gritter.mcquests.api.event.QuestLevelDownEvent;
import me.gritter.mcquests.api.event.QuestLevelUpEvent;
import me.gritter.mcquests.api.event.QuestProgressChangeEvent;
import me.gritter.mcquests.api.questtype.QuestType;
import me.gritter.mcquests.api.queststate.QuestState;
import me.gritter.mcquests.api.queststate.QuestStateService;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.bukkit.event.EventPriority.LOWEST;

public abstract class QuestHandler implements Listener {

    private final JavaPlugin plugin;
    private final QuestType questType;

    private QuestStateService questStateService;

    public QuestHandler(JavaPlugin plugin, QuestType questType) {
        this.plugin = plugin;
        this.questType = questType;
    }

    @EventHandler(priority = LOWEST, ignoreCancelled = true)
    public void handleQuestLevelUpEvent(QuestLevelUpEvent event) {
        if (event.getQuestType().equals(questType)) {
            onQuestLevelUpEvent(event);
        }
    }

    @EventHandler(priority = LOWEST, ignoreCancelled = true)
    public void handleQuestLevelDownEvent(QuestLevelDownEvent event) {
        if (event.getQuestType().equals(questType)) {
            onQuestLevelDownEvent(event);
        }
    }

    @EventHandler(priority = LOWEST, ignoreCancelled = true)
    public void handleQuestProgressChangeEvent(QuestProgressChangeEvent event) {
        if (event.getQuestType().equals(questType)) {
            onQuestProgressChangeEvent(event);
        }
    }

    public abstract void onQuestLevelUpEvent(QuestLevelUpEvent event);

    public abstract void onQuestLevelDownEvent(QuestLevelDownEvent event);

    public abstract void onQuestProgressChangeEvent(QuestProgressChangeEvent event);

    public void register() {
        QuestsApiPlugin apiPlugin = findQuestsApiPlugin().orElse(null);
        if (apiPlugin == null) {
            throw new IllegalStateException("Tried to register a QuestHandler implementation, but cannot find the QuestApi plugin.");
        }

        apiPlugin.getQuestTypeService().registerQuestType(questType);
        Bukkit.getPluginManager().registerEvents(this, plugin);

        questStateService = apiPlugin.getQuestStateService();
    }

    public CompletableFuture<QuestState> getQuestState(OfflinePlayer player) {
        if (questStateService == null) {
            throw new IllegalStateException("QuestHandler is not registered yet. Please call QuestHandler#register first.");
        }

        return questStateService.getQuestState(player, questType);
    }

    public QuestType getQuestType() {
        return questType;
    }

    private Optional<QuestsApiPlugin> findQuestsApiPlugin() {
        return Optional.ofNullable(Bukkit.getPluginManager().getPlugin("QuestApi"))
                .filter(p -> p instanceof QuestsApiPlugin)
                .map(p -> (QuestsApiPlugin) p);
    }
}
