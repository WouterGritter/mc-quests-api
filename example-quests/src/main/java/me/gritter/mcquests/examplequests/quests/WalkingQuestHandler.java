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
import org.bukkit.event.player.PlayerMoveEvent;

import static org.bukkit.event.EventPriority.LOWEST;

public class WalkingQuestHandler extends QuestHandler {

    public WalkingQuestHandler(ExampleQuestsPlugin plugin) {
        super(
                plugin,
                new QuestTypeBuilder(plugin)
                        .withName("walking")
                        .withDisplayName("Walking quest")
                        .withDescription("Get more points by walking. This is quite a long description, I'm wondering how it will look, but I'm not really sure. Well, I guess we will find out.")
                        .withCached(true)
                        .withGuiIcon(Material.COMPASS)
                        .build()
        );
    }

    @EventHandler(priority = LOWEST, ignoreCancelled = true)
    public void handlePlayerMoveEvent(PlayerMoveEvent event) {
        if (event.getTo() == null) {
            return;
        }

        double progress = event.getFrom().distance(event.getTo()) / 10;
        getQuestState(event.getPlayer())
                .thenAccept(quest -> quest.updateProgress(progress));
    }

    @Override
    public void onQuestLevelUpEvent(QuestLevelUpEvent event) {
        Bukkit.broadcastMessage(String.format("Player %s leveled up their walking quest to level %d",
                event.getOfflinePlayer().getName(), event.getQuestProgress().getLevel()));
    }

    @Override
    public void onQuestLevelDownEvent(QuestLevelDownEvent event) {
    }

    @Override
    public void onQuestProgressChangeEvent(QuestProgressChangeEvent event) {
    }
}
