package me.gritter.mcquests.api.questtype;

import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

public class QuestTypeBuilder {

    private final JavaPlugin registeringPlugin;

    private String name = null;
    private boolean cached = true;
    private int startingLevel = 0;
    private int maxLevel = Integer.MAX_VALUE;
    private Material guiIcon = Material.FILLED_MAP;
    private String displayName = null;
    private String description = "";

    public QuestTypeBuilder(JavaPlugin registeringPlugin) {
        this.registeringPlugin = registeringPlugin;
    }

    public QuestType build() {
        return new QuestType(name, registeringPlugin.getName(), cached, startingLevel, maxLevel, guiIcon, displayName != null ? displayName : name, description);
    }

    public QuestTypeBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public QuestTypeBuilder withCached(boolean cached) {
        this.cached = cached;
        return this;
    }

    public QuestTypeBuilder withStartingLevel(int startingLevel) {
        this.startingLevel = startingLevel;
        return this;
    }

    public QuestTypeBuilder withMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
        return this;
    }

    public QuestTypeBuilder withGuiIcon(Material guiIcon) {
        this.guiIcon = guiIcon;
        return this;
    }

    public QuestTypeBuilder withDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public QuestTypeBuilder withDescription(String description) {
        this.description = description;
        return this;
    }
}
