package me.gritter.mcquests.examplequests;

import me.gritter.mcquests.examplequests.quests.BlockBreakQuestHandler;
import me.gritter.mcquests.examplequests.quests.WalkingQuestHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class ExampleQuestsPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        new BlockBreakQuestHandler(this).register();
        new WalkingQuestHandler(this).register();
    }
}
