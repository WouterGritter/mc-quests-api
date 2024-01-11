package me.gritter.mcquests.api.gui;

import me.gritter.mcquests.api.QuestsApiPlugin;
import me.gritter.mcquests.api.queststate.QuestState;
import me.gritter.mcquests.api.queststate.QuestStateService;
import org.apache.commons.collections4.ListUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

public class QuestsGUIService implements Listener {

    private static final int DESCRIPTION_MAX_LINE_LENGTH = 40;
    private static final int PROGRESS_BAR_LENGTH = 20;

    private final Collection<Player> inGui = new HashSet<>();

    private final QuestStateService questStateService;

    public QuestsGUIService() {
        this.questStateService = QuestsApiPlugin.getInstance().getQuestStateService();

        Bukkit.getPluginManager().registerEvents(this, QuestsApiPlugin.getInstance());
    }

    public void onDisable() {
        inGui.forEach(Player::closeInventory);
        inGui.clear();
    }

    @EventHandler
    public void handlePlayerQuitEvent(PlayerQuitEvent event) {
        inGui.remove(event.getPlayer());
    }

    @EventHandler
    public void handleInventoryCloseEvent(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player) {
            inGui.remove((Player) event.getPlayer());
        }
    }

    @EventHandler
    public void handleInventoryClickEvent(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player && inGui.contains((Player) event.getWhoClicked())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void handleInventoryDragEvent(InventoryDragEvent event) {
        if (event.getWhoClicked() instanceof Player && inGui.contains((Player) event.getWhoClicked())) {
            event.setCancelled(true);
        }
    }

    public void openGUI(Player who) {
        questStateService.getQuestStates(who).thenAccept(questStates -> {
            Bukkit.getScheduler().runTask(QuestsApiPlugin.getInstance(), () -> {
                Inventory inventory = Bukkit.createInventory(who, 9 * 3, "Quests");

                questStates.forEach(questState -> {
                    ItemStack item = generateQuestItem(questState);

                    inventory.addItem(item);
                });

                who.closeInventory();
                inGui.add(who);
                who.openInventory(inventory);
            });
        });
    }

    private ItemStack generateQuestItem(QuestState state) {
        ItemStack is = new ItemStack(state.getQuestType().getGuiIcon());
        ItemMeta im = requireNonNull(is.getItemMeta());
        im.setDisplayName(ChatColor.DARK_PURPLE + state.getQuestType().getDisplayName());

        List<String> description = lineifyDescription(state.getQuestType().getDescription());
        List<String> lore = List.of(
                "",
                ChatColor.GRAY + "Level: " + ChatColor.GOLD + String.valueOf(state.getLevel()),
                ChatColor.GRAY + "Progress: " + generateProgressBar(state.getProgress()) + ChatColor.GRAY + " (" + ((int) (state.getProgress() * 100.0)) + "%)"
        );
        im.setLore(ListUtils.union(description, lore));

        is.setItemMeta(im);

        return is;
    }

    private List<String> lineifyDescription(String description) {
        if (description.contains("\n")) {
            return Stream.of(description.split("\n"))
                    .map(line -> ChatColor.GRAY + line)
                    .collect(Collectors.toList());
        }

        List<String> lines = new ArrayList<>();

        StringBuilder currentLine = null;
        for (char c : description.toCharArray()) {
            if (currentLine == null) {
                currentLine = new StringBuilder(ChatColor.GRAY.toString());
            }

            if (c == ' ' && currentLine.length() > DESCRIPTION_MAX_LINE_LENGTH) {
                lines.add(currentLine.toString());
                currentLine = null;
            } else {
                currentLine.append(c);
            }
        }

        if (currentLine != null) {
            lines.add(currentLine.toString());
        }

        return lines;
    }

    private String generateProgressBar(double value) {
        int greenBars = (int) (value * PROGRESS_BAR_LENGTH);
        int grayBars = PROGRESS_BAR_LENGTH - greenBars;

        StringBuilder sb = new StringBuilder(PROGRESS_BAR_LENGTH + 4);
        sb.append(String.valueOf(ChatColor.GREEN));
        for (int i = 0; i < greenBars; i++) {
            sb.append('|');
        }

        sb.append(String.valueOf(ChatColor.GRAY));
        for (int i = 0; i < grayBars; i++) {
            sb.append('|');
        }

        return sb.toString();
    }
}
