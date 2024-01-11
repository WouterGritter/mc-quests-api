package me.gritter.mcquests.api.event;

import me.gritter.mcquests.api.questtype.QuestType;
import me.gritter.mcquests.api.queststate.QuestProgress;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

import java.util.Optional;
import java.util.UUID;

public abstract class QuestProgressEvent extends Event implements Cancellable {

    private final UUID playerUuid;
    private final QuestType questType;
    private final QuestProgress questProgress;

    private boolean cancelled = false;

    public QuestProgressEvent(UUID playerUuid, QuestType questType, QuestProgress questProgress) {
        this.playerUuid = playerUuid;
        this.questType = questType;
        this.questProgress = questProgress;
    }

    public UUID getPlayerUuid() {
        return playerUuid;
    }

    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(playerUuid);
    }

    public Optional<Player> getPlayer() {
        return Optional.ofNullable(Bukkit.getPlayer(playerUuid));
    }

    public QuestType getQuestType() {
        return questType;
    }

    public QuestProgress getQuestProgress() {
        return questProgress;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
