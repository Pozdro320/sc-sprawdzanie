package pl.servercreators.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import pl.servercreators.gui.CheckGUI;
import pl.servercreators.helpers.GroupCheckHelper;
import pl.servercreators.managers.ConfigManager;

public class CheckCommand extends Command {

    private final CheckGUI checkGUI;
    private final GroupCheckHelper groupCheckHelper;
    private final ConfigManager cm;

    public CheckCommand(String name, CheckGUI checkGUI, GroupCheckHelper groupCheckHelper, ConfigManager cm) {
        super(name);
        
        this.checkGUI = checkGUI;
        this.groupCheckHelper = groupCheckHelper;
        this.cm = cm;

        this.setDescription("Komenda pozwalająca sprawdzić gracza");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {

        if (!(sender instanceof Player)) {
            cm.getMessages().sendMessages(sender, "errors.only-players");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("sc-sprawdzanie.sprawdz")) {
            cm.getMessages().sendMessages(player, "errors.no-permission-check");
            return true;
        }

        if (args.length != 1) {
            cm.getMessages().sendMessages(player, "usage.check", "{PLAYER}", player.getName());
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            cm.getMessages().sendMessages(player, "errors.player-offline", "{PLAYER}", player.getName());

            return true;
        }

        if (target.equals(player)) {
            cm.getMessages().sendMessages(player, "errors.him-self");
            return true;
        }

        if (!groupCheckHelper.canCheck(player, target)) {
            cm.getMessages().sendMessages(player, "errors.hierarchy", "{PLAYER}", player.getName());
            return true;
        }

        checkGUI.openGUI(player, target);
        return true;
    }
}
