package me.gritter.mcquests.api.event;

import me.gritter.mcquests.api.questtype.QuestType;
import me.gritter.mcquests.api.queststate.QuestProgress;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class QuestProgressChangeEvent extends QuestProgressEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final double previousProgress;

    public QuestProgressChangeEvent(UUID playerUuid, QuestType questType, QuestProgress questProgress, double previousProgress) {
        super(playerUuid, questType, questProgress);

        this.previousProgress = previousProgress;
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
