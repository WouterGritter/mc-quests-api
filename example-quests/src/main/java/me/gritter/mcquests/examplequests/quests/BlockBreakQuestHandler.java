package me.gritter.mcquests.examplequests.quests;

import me.gritter.mcquests.api.event.QuestLevelDownEvent;
import me.gritter.mcquests.api.event.QuestLevelUpEvent;
import me.gritter.mcquests.api.event.QuestProgressChangeEvent;
import me.gritter.mcquests.api.questhandler.QuestHandler;
import me.gritter.mcquests.api.questtype.QuestTypeBuilder;
import me.gritter.mcquests.examplequests.ExampleQuestsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

import static org.bukkit.event.EventPriority.LOWEST;

public class BlockBreakQuestHandler extends QuestHandler {

    public BlockBreakQuestHandler(ExampleQuestsPlugin plugin) {
        super(
                plugin,
                new QuestTypeBuilder(plugin)
                        .withName("blocks_broken")
                        .withDisplayName("Block breaking quest")
                        .withDescription("Get more points by breaking blocks.")
                        .withCached(false)
                        .withGuiIcon(Material.DIAMOND_PICKAXE)
                        .build()
        );
    }

    @EventHandler(priority = LOWEST, ignoreCancelled = true)
    public void handleBlockBreakEvent(BlockBreakEvent event) {
        getQuestState(event.getPlayer()).thenAccept(quest -> {
            quest.updateProgress(0.1);
        });
    }

    @Override
    public void onQuestLevelUpEvent(QuestLevelUpEvent event) {
        Bukkit.broadcastMessage(String.format("Player %s leveled up their block level quest to level %d",
                event.getOfflinePlayer().getName(), event.getQuestProgress().getLevel()));
    }

    @Override
    public void onQuestLevelDownEvent(QuestLevelDownEvent event) {
    }

    @Override
    public void onQuestProgressChangeEvent(QuestProgressChangeEvent event) {
    }
}
