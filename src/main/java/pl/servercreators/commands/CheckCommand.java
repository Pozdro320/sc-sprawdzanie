package pl.servercreators.commands;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import pl.servercreators.SCSprawdzMain;
import pl.servercreators.gui.CheckGUI;
import pl.servercreators.helpers.GroupCheckHelper;
import pl.servercreators.helpers.MessageHelper;
import pl.servercreators.managers.ConfigManager;

public class CheckCommand extends Command {

    private final SCSprawdzMain plugin;
    private final CheckGUI checkGUI;
    private final GroupCheckHelper groupCheckHelper;
    private final ConfigManager cm;

    public CheckCommand(String name, SCSprawdzMain plugin, CheckGUI checkGUI, GroupCheckHelper groupCheckHelper, ConfigManager cm) {
        super(name);
        this.plugin = plugin;
        
        this.checkGUI = checkGUI;
        this.groupCheckHelper = groupCheckHelper;
        this.cm = cm;

        this.setDescription("Komenda pozwalająca sprawdzić gracza");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageHelper.colored("&8» #CF4E4ETylko gracze mogą używać tej komendy!"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("sc-sprawdzanie.sprawdz")) {
            player.sendMessage(MessageHelper.colored("&8» #CF4E4ENie masz uprawnień &8(&4sc-sprawdzanie.sprawdz&8)"));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.6f);
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(MessageHelper.colored("&8» #CF4E4EPoprawne użycie: &8/&4sprawdz &8<&4nick&8>"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            cm.getMessages().sendMessages(player, "errors.player-offline", null, player.getName());
            return true;
        }

        if (target.equals(player)) {
            cm.getMessages().sendMessages(player, "errors.him-self", target.getName(), player.getName());
            return true;
        }

        if (!groupCheckHelper.canCheck(player, target)) {
            cm.getMessages().sendMessages(player, "errors.hierarchy", target.getName(), player.getName());
            return true;
        }

        checkGUI.openGUI(player, target);
        return true;
    }
}
