package me.gritter.mcquests.api.event;

import me.gritter.mcquests.api.queststate.QuestState;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PostQuestProgressChangeEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final QuestState questState;

    public PostQuestProgressChangeEvent(QuestState questState) {
        this.questState = questState;
    }

    public QuestState getQuestState() {
        return questState;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
