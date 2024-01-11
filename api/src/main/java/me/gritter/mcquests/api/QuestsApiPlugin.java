package me.gritter.mcquests.api;

import me.gritter.mcquests.api.commands.QuestsCommand;
import me.gritter.mcquests.api.datastore.MongoDBQuestDataStore;
import me.gritter.mcquests.api.datastore.QuestDataStore;
import me.gritter.mcquests.api.gui.QuestsGUIService;
import me.gritter.mcquests.api.queststate.QuestStateService;
import me.gritter.mcquests.api.questtype.QuestTypeService;
import org.bukkit.plugin.java.JavaPlugin;

public class QuestsApiPlugin extends JavaPlugin {

    private static QuestsApiPlugin instance;

    private QuestDataStore questDataStore;

    private QuestTypeService questTypeService;
    private QuestStateService questStateService;
    private QuestsGUIService questsGUIService;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

//        questDataStore = new MemoryQuestDataStore();
        questDataStore = new MongoDBQuestDataStore(
                getConfig().getString("mongodb.url"),
                getConfig().getString("mongodb.database"),
                getConfig().getString("mongodb.collection")
        );

        questTypeService = new QuestTypeService();
        questStateService = new QuestStateService();
        questsGUIService = new QuestsGUIService();

        getCommand("quests").setExecutor(new QuestsCommand());
    }

    @Override
    public void onDisable() {
        questStateService.flushStates().join();
        questsGUIService.onDisable();
        questDataStore.dispose();
    }

    public QuestDataStore getQuestDataStore() {
        return questDataStore;
    }

    public QuestTypeService getQuestTypeService() {
        return questTypeService;
    }

    public QuestStateService getQuestStateService() {
        return questStateService;
    }

    public QuestsGUIService getQuestsGUIService() {
        return questsGUIService;
    }

    public static QuestsApiPlugin getInstance() {
        return instance;
    }
}
