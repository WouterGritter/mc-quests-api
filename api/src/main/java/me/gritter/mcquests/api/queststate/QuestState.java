package me.gritter.mcquests.api.queststate;

import me.gritter.mcquests.api.QuestsApiPlugin;
import me.gritter.mcquests.api.event.*;
import me.gritter.mcquests.api.questtype.QuestType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class QuestState {

    private final UUID playerUuid;
    private final QuestType questType;

    private QuestProgress progress;

    public QuestState(UUID playerUuid, QuestType questType, QuestProgress progress) {
        this.playerUuid = playerUuid;
        this.questType = questType;
        this.progress = progress;
    }

    public QuestState(UUID playerUuid, QuestType questType) {
        this.playerUuid = playerUuid;
        this.questType = questType;

        reset();
    }

    public void reset() {
        progress = new QuestProgress(questType.getStartingLevel(), 0.0);
    }

    public QuestType getQuestType() {
        return questType;
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

    public double getTotalProgress() {
        return progress.getTotalProgress();
    }

    public int getLevel() {
        return progress.getLevel();
    }

    public double getProgress() {
        return progress.getProgress();
    }

    public void setTotalProgress(double totalProgress) {
        updateProgressParameter(p -> p.setTotalProgress(totalProgress));
    }

    public void setLevel(int level) {
        updateProgressParameter(p -> p.setLevel(level));
    }

    public void setProgress(double progress) {
        updateProgressParameter(p -> p.setProgress(progress));
    }

    public void updateProgress(double progressDelta) {
        updateProgressParameter(p -> p.updateProgress(progressDelta));
    }

    private void updateProgressParameter(Consumer<QuestProgress> modifier) {
        QuestProgress updated = progress.copy();
        modifier.accept(updated);

        if (updated.getLevel() < questType.getStartingLevel()) {
            updated.setLevel(questType.getStartingLevel());
        }

        if (updated.getLevel() > questType.getMaxLevel()) {
            updated.setLevel(questType.getMaxLevel());
        }

        if (updated.equals(progress)) {
            return;
        }

        Bukkit.getScheduler().runTask(QuestsApiPlugin.getInstance(), () -> {
            QuestProgressEvent event = buildQuestProgressEvent(updated);
            Bukkit.getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                this.progress = updated;
                Bukkit.getPluginManager().callEvent(new PostQuestProgressChangeEvent(this));
            }
        });
    }

    private QuestProgressEvent buildQuestProgressEvent(QuestProgress updated) {
        if (updated.getLevel() == progress.getLevel()) {
            return new QuestProgressChangeEvent(playerUuid, questType, updated, progress.getProgress());
        } else if (updated.getLevel() > progress.getLevel()) {
            return new QuestLevelUpEvent(playerUuid, questType, updated, progress.getLevel(), progress.getProgress());
        } else {
            return new QuestLevelDownEvent(playerUuid, questType, updated, progress.getLevel(), progress.getProgress());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuestState that = (QuestState) o;
        return Objects.equals(questType, that.questType) && Objects.equals(playerUuid, that.playerUuid) && Objects.equals(progress, that.progress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(questType, playerUuid, progress);
    }
}
