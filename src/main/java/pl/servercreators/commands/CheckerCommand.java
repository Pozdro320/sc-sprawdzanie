package pl.servercreators.commands;

import java.util.List;
import java.util.Map;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import pl.servercreators.SCSprawdzMain;
import pl.servercreators.managers.ConfigManager;
import pl.servercreators.utils.TabCompleteUtil;

public class CheckerCommand extends Command {

    private final SCSprawdzMain plugin;
    private final ConfigManager cm;
    
    public CheckerCommand(String name, SCSprawdzMain plugin, ConfigManager cm) {
        super(name);
        this.plugin = plugin;
        
        this.cm = cm;

        this.setDescription("Komenda zarządzająca sprawdzarką");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {

        if (!(sender instanceof Player)) {
            cm.getMessages().sendMessages(sender, "errors.only-players");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            cm.getMessages().sendMessages(player, "usage.checker", "{PLAYER}", player.getName());
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "set-checker" -> {
                if (!player.hasPermission("sc-sprawdzanie.admin")) {
                    cm.getMessages().sendMessages(player, "errors.no-permission-checker", "{PLAYER}", player.getName());
                    return true;
                }

                Location loc = player.getLocation();
                plugin.setCheckedLocation(loc);

                cm.getMessages().sendMessages(player, "locations-settings.set-checker");
                
                DustOptions redDust = new DustOptions(Color.RED, 1f);
                player.getWorld().spawnParticle(Particle.REDSTONE, loc.add(0, 1, 0), 30, 0.5, 1, 0.5, redDust);
            }

            case "set-spawn" -> {
                if (!player.hasPermission("sc-sprawdzanie.admin")) {
                    cm.getMessages().sendMessages(player, "errors.no-permission-checker", "{PLAYER}", player.getName());
                    return true;
                }

                Location loc = player.getLocation();
                plugin.setSpawnLocation(loc);

                cm.getMessages().sendMessages(player, "locations-settings.set-spawn");
                
                DustOptions yellowDust = new DustOptions(Color.YELLOW, 1f);
                player.getWorld().spawnParticle(Particle.REDSTONE, loc.add(0, 1, 0), 30, 0.5, 1, 0.5, yellowDust);
            }

            case "reload" -> {
                if (!player.hasPermission("sc-sprawdzanie.admin")) {
                    cm.getMessages().sendMessages(player, "errors.no-permission-checker", "{PLAYER}", player.getName());
                    return true;
                }

                plugin.reloadPlugin();
                cm.getMessages().sendMessages(player, "reload-success");
            }

            case "teleport" -> {
                if (!player.hasPermission("sc-sprawdzanie.sprawdz")) {
                    cm.getMessages().sendMessages(player, "errors.no-permission-check");
                    return true;
                }

                Location checkLoc = plugin.getCheckLocation();
                if (checkLoc == null) {
                    cm.getMessages().sendMessages(player, "errors.no-checker");
                    return true;
                }

                player.teleport(checkLoc);
                cm.getMessages().sendMessages(player, "tp-checker");
            }

            default -> {
                cm.getMessages().sendMessages(player, "usage.checker");
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