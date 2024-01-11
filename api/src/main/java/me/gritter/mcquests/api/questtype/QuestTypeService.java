package me.gritter.mcquests.api.questtype;

import me.gritter.mcquests.api.QuestsApiPlugin;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.logging.Logger;

public class QuestTypeService {

    private final Collection<QuestType> questTypes = new HashSet<>();

    private final Logger logger;

    public QuestTypeService() {
        this.logger = QuestsApiPlugin.getInstance().getLogger();
    }

    public Collection<QuestType> getQuestTypes() {
        return Collections.unmodifiableCollection(questTypes);
    }

    public void registerQuestType(QuestType questType) {
        if (questTypes.stream().anyMatch(qt -> qt.getNamespacedName().equals(questType.getNamespacedName()))) {
            throw new IllegalArgumentException(String.format("A QuestType with name %s is already registered.", questType.getNamespacedName()));
        }

        questTypes.add(questType);
        logger.info(String.format("Registered QuestType %s", questType.getNamespacedName()));
    }

    public boolean isRegistered(QuestType questType) {
        return questTypes.contains(questType);
    }
}
