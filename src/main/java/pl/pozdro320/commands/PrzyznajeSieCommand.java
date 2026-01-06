package pl.pozdro320.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import pl.pozdro320.SCSprawdzMain;
import pl.pozdro320.helpers.MessageHelper;
import pl.pozdro320.managers.ConfigManager;

public class PrzyznajeSieCommand extends Command {

    private final SCSprawdzMain plugin;
    
    public PrzyznajeSieCommand(String name, SCSprawdzMain plugin) {
        super(name);
        this.plugin = plugin;
        this.setDescription("Komenda pozwalająca przyznać się podczas sprawdzania");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {

        ConfigManager cm = plugin.getConfigManager();

        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageHelper.colored("&8» #CF4E4ETylko gracze mogą używać tej komendy!"));
            return true;
        }

        Player player = (Player) sender;

        if (!plugin.czySprawdzany(player)) {
            cm.getMessages().sendMessages(player, "errors.nie-sprawdzany", player.getName(), null);
            return true;
        }

        Player moderator = plugin.getModerator(player);
        String modName = (moderator != null) ? moderator.getName() : "Konsola";

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "spawn " + player.getName());
        
        plugin.usunSprawdzanego(player);

        if (moderator != null && moderator.isOnline()) {
            cm.getMessages().sendMessages(moderator, "przyznanie-mod", player.getName(), modName);
        }

        plugin.usunSprawdzanego(player);
        player.teleport(plugin.getSpawnLocation());

        plugin.getBansHelper().executeBan(player, moderator, "przyznanie-sie");

        return true;
    }
}
