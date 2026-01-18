package pl.servercreators.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import pl.servercreators.SCSprawdzMain;
import pl.servercreators.helpers.BansHelper;
import pl.servercreators.helpers.GroupCheckHelper;
import pl.servercreators.helpers.MessageHelper;
import pl.servercreators.managers.ConfigManager;

public class ConfessesCommand extends Command {

    private final SCSprawdzMain plugin;
    private final GroupCheckHelper groupCheckHelper;
    private final ConfigManager cm;
    private final BansHelper bansHelper;
    
    public ConfessesCommand(String name, SCSprawdzMain plugin, GroupCheckHelper groupCheckHelper, ConfigManager cm, BansHelper bansHelper) {
        super(name);
        this.plugin = plugin;
        this.groupCheckHelper = groupCheckHelper;
        this.cm = cm;
        this.bansHelper = bansHelper;

        this.setDescription("Komenda pozwalająca przyznać się podczas sprawdzania");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageHelper.colored("&8» #CF4E4ETylko gracze mogą używać tej komendy!"));
            return true;
        }

        Player player = (Player) sender;

        if (!plugin.isChecked(player)) {
            cm.getMessages().sendMessages(player, "errors.not-checked", player.getName(), null);
            return true;
        }

        Player moderator = plugin.getModerator(player);
        String modName = (moderator != null) ? moderator.getName() : "Console";

        Location spawnLoc = plugin.getSpawnLocation();
        
        player.teleport(spawnLoc);
        plugin.removeChecked(player);

        if (moderator != null && moderator.isOnline()) {
            cm.getMessages().sendMessages(moderator, "admission-mod", player.getName(), modName);
        }

        plugin.removeChecked(player);
        player.teleport(plugin.getSpawnLocation());

        plugin.getBansHelper().executeBan(player, moderator, "admission");

        return true;
    }
}
