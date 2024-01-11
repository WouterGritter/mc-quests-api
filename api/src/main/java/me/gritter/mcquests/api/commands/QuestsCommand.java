package me.gritter.mcquests.api.commands;

import me.gritter.mcquests.api.QuestsApiPlugin;
import me.gritter.mcquests.api.gui.QuestsGUIService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class QuestsCommand implements CommandExecutor {

    private final QuestsGUIService questsGUIService;

    public QuestsCommand() {
        this.questsGUIService = QuestsApiPlugin.getInstance().getQuestsGUIService();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You need to be a player to run this command.");
            return true;
        }

        questsGUIService.openGUI((Player) sender);

        return true;
    }
}
