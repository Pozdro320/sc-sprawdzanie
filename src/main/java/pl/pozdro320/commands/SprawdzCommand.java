package pl.pozdro320.commands;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import pl.pozdro320.SCSprawdzMain;
import pl.pozdro320.gui.SprawdzGUI;
import pl.pozdro320.helpers.MessageHelper;
import pl.pozdro320.managers.ConfigManager;

public class SprawdzCommand extends Command {

    private final SCSprawdzMain plugin;

    public SprawdzCommand(String name, SCSprawdzMain plugin) {
        super(name);
        this.plugin = plugin;
        this.setDescription("Komenda pozwalająca sprawdzić gracza");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {

        ConfigManager cm = plugin.getConfigManager();

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
            cm.getMessages().sendMessages(player, "errors.gracz-offline", null, player.getName());
            return true;
    }

        if (target.equals(player)) {
            cm.getMessages().sendMessages(player, "errors.samego-siebie", target.getName(), player.getName());
            return true;
    }

       // if (!GroupCheckHelper.canCheck(player, target, plugin)) {
        //    cm.getMessages().sendMessages(player, "errors.hierarchia", target.getName(), player.getName());
         //   return true;
        //}

        SprawdzGUI.openGUI(player, target);
        return true;
    }
}
