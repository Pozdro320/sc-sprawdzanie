package pl.servercreators.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import pl.servercreators.SCSprawdzMain;
import pl.servercreators.managers.ConfigManager;

public class ConfessesCommand extends Command {

    private final SCSprawdzMain plugin;
    private final ConfigManager cm;
    
    public ConfessesCommand(String name, SCSprawdzMain plugin, ConfigManager cm) {
        super(name);
        this.plugin = plugin;
        this.cm = cm;

        this.setDescription("Komenda pozwalająca przyznać się podczas sprawdzania");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {

        if (!(sender instanceof Player)) {
            cm.getMessages().sendMessages(sender, "errors.only-players");
            return true;
        }

        Player player = (Player) sender;

        if (!plugin.isChecked(player)) {
            cm.getMessages().sendMessages(player, "errors.not-checked");
            return true;
        }

        Player moderator = plugin.getModerator(player);

        Location spawnLoc = plugin.getSpawnLocation();
        
        player.teleport(spawnLoc);
        plugin.removeChecked(player);

        if (moderator != null && moderator.isOnline()) {
            cm.getMessages().sendMessages(moderator, "admission-mod", "{PLAYER}", player.getName());
        }

        plugin.removeChecked(player);
        player.teleport(plugin.getSpawnLocation());

        plugin.getBansHelper().executeBan(player, moderator, "admission");

        return true;
    }
}
