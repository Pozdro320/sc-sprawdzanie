package pl.servercreators.commands;

import java.util.List;
import java.util.Map;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import pl.servercreators.SCSprawdzMain;
import pl.servercreators.helpers.GroupCheckHelper;
import pl.servercreators.helpers.MessageHelper;
import pl.servercreators.managers.ConfigManager;
import pl.servercreators.utils.TabCompleteUtil;

public class CheckerCommand extends Command {

    private final SCSprawdzMain plugin;
    private final GroupCheckHelper groupCheckHelper;
    private final ConfigManager cm;
    
    public CheckerCommand(String name, SCSprawdzMain plugin, GroupCheckHelper groupCheckHelper, ConfigManager cm) {
        super(name);
        this.plugin = plugin;
        
        this.groupCheckHelper = groupCheckHelper;
        this.cm = cm;

        this.setDescription("Komenda zarządzająca sprawdzarką");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageHelper.colored("&8» #CF4E4ETylko gracze mogą używać tej komendy!"));
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(MessageHelper.colored("&8» #CF4E4EPoprawne użycie: &8/&4sprawdzarka &8<&4set-checker&8|&4set-spawn&8|&4reload&8|&4teleport&8>"));
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "set-checker" -> {
                if (!player.hasPermission("sc-sprawdzanie.admin")) {
                    player.sendMessage(MessageHelper.colored("&8» #CF4E4ENie masz uprawnień &8(&4sc-sprawdzanie.admin&8)"));
                    return true;
                }

                Location loc = player.getLocation();
                plugin.setCheckedLocation(loc);

                cm.getMessages().sendMessages(player, "locations-settings.set-checker", null, player.getName());
                
                DustOptions redDust = new DustOptions(Color.RED, 1f);
                player.getWorld().spawnParticle(Particle.REDSTONE, loc.add(0, 1, 0), 30, 0.5, 1, 0.5, redDust);
            }

            case "set-spawn" -> {
                if (!player.hasPermission("sc-sprawdzanie.admin")) {
                    player.sendMessage(MessageHelper.colored("&8» #CF4E4ENie masz uprawnień &8(&4sc-sprawdzanie.admin&8)"));
                    return true;
                }

                Location loc = player.getLocation();
                plugin.setSpawnLocation(loc);

                cm.getMessages().sendMessages(player, "locations-settings.set-spawn", null, player.getName());
                
                DustOptions yellowDust = new DustOptions(Color.YELLOW, 1f);
                player.getWorld().spawnParticle(Particle.REDSTONE, loc.add(0, 1, 0), 30, 0.5, 1, 0.5, yellowDust);
            }

            case "reload" -> {
                if (!player.hasPermission("sc-sprawdzanie.admin")) {
                    player.sendMessage(MessageHelper.colored("&8» #CF4E4ENie masz uprawnień &8(&4sc-sprawdzanie.admin&8)"));
                    return true;
                }

                plugin.reloadPlugin();

                player.sendMessage(MessageHelper.colored("&8» &aPomyślnie przeładowano plugin!"));
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1f, 1.5f);
            }

            case "teleport" -> {
                if (!player.hasPermission("sc-sprawdzanie.sprawdz")) {
                    player.sendMessage(MessageHelper.colored("&8» #CF4E4ENie masz uprawnień &8(&4sc-sprawdzanie.sprawdz&8)"));
                    return true;
                }

                Location checkLoc = plugin.getCheckLocation();
                if (checkLoc == null) {
                    player.sendMessage(MessageHelper.colored("&8» #CF4E4EMiejsce sprawdzania nie jest jeszcze ustawione!"));
                    return true;
                }

                player.teleport(checkLoc);
                cm.getMessages().sendMessages(player, "tp-checker", null, player.getName());
            }

            default -> {
                player.sendMessage(MessageHelper.colored("&8» #CF4E4EPoprawne użycie: &8/&4sprawdzarka &8<&4set-checker&8|&4set-spawn&8|&4teleport&8>"));
            }
        }

        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(
            @NotNull CommandSender sender,
            @NotNull String alias,
            @NotNull String[] args
    ) {

        Map<String, String> options = Map.of(
                "set-checker", "sc-sprawdzanie.admin",
                "set-spawn", "sc-sprawdzanie.admin",
                "reload", "sc-sprawdzanie.admin",
                "teleport", "sc-sprawdzanie.sprawdz"
        );

        return TabCompleteUtil.tab(sender, args, options);
    }

}