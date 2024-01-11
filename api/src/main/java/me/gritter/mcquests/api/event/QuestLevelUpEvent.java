package me.gritter.mcquests.api.event;

import me.gritter.mcquests.api.questtype.QuestType;
import me.gritter.mcquests.api.queststate.QuestProgress;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class QuestLevelUpEvent extends QuestProgressEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final int previousLevel;
    private final double previousProgress;

    public QuestLevelUpEvent(UUID playerUuid, QuestType questType, QuestProgress questProgress, int previousLevel, double previousProgress) {
        super(playerUuid, questType, questProgress);

        this.previousLevel = previousLevel;
        this.previousProgress = previousProgress;
    }

    public int getPreviousLevel() {
        return previousLevel;
    }

    public double getPreviousProgress() {
        return previousProgress;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
