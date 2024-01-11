package me.gritter.mcquests.api.questtype;

import org.bukkit.Material;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class QuestType {

    private final String name;
    private final String namespace;
    private final boolean cached;
    private final int startingLevel;
    private final int maxLevel;
    private final Material guiIcon;
    private final String displayName;
    private final String description;

    protected QuestType(String name, String namespace, boolean cached, int startingLevel, int maxLevel, Material guiIcon, String displayName, String description) {
        if (startingLevel < 0) {
            throw new IllegalArgumentException("Starting level should be zero or more.");
        }

        if (maxLevel <= startingLevel) {
            throw new IllegalArgumentException("Max level should be more than the starting level.");
        }

        this.name = requireNonNull(name);
        this.namespace = requireNonNull(namespace);
        this.cached = cached;
        this.startingLevel = startingLevel;
        this.maxLevel = maxLevel;
        this.guiIcon = requireNonNull(guiIcon);
        this.displayName = requireNonNull(displayName);
        this.description = requireNonNull(description);
    }

    public String getNamespacedName() {
        return namespace + ":" + name;
    }

    public String getName() {
        return name;
    }

    public String getNamespace() {
        return namespace;
    }

    public boolean isCached() {
        return cached;
    }

    public int getStartingLevel() {
        return startingLevel;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public Material getGuiIcon() {
        return guiIcon;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "QuestType{" +
                "name='" + name + '\'' +
                ", namespace='" + namespace + '\'' +
                ", cached=" + cached +
                ", startingLevel=" + startingLevel +
                ", maxLevel=" + maxLevel +
                ", guiIcon=" + guiIcon +
                ", displayName='" + displayName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuestType questType = (QuestType) o;
        return cached == questType.cached && startingLevel == questType.startingLevel && maxLevel == questType.maxLevel && Objects.equals(name, questType.name) && Objects.equals(namespace, questType.namespace) && guiIcon == questType.guiIcon && Objects.equals(displayName, questType.displayName) && Objects.equals(description, questType.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, namespace, cached, startingLevel, maxLevel, guiIcon, displayName, description);
    }
}
