package me.gritter.mcquests.examplequests;

import me.gritter.mcquests.examplequests.quests.BlockBreakQuestService;
import me.gritter.mcquests.examplequests.quests.WalkingQuestService;
import org.bukkit.plugin.java.JavaPlugin;

public class ExampleQuestsPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        new BlockBreakQuestService(this).register();
        new WalkingQuestService(this).register();
    }
}
