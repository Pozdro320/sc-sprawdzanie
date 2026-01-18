package pl.servercreators.listeners;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import pl.servercreators.SCSprawdzMain;
import pl.servercreators.managers.ConfigManager;

public class BlockCommandListener implements Listener {
    
    private final SCSprawdzMain plugin;
    private final ConfigManager cm;

    public BlockCommandListener(SCSprawdzMain plugin, ConfigManager cm) {
        this.plugin = plugin;
        this.cm = cm;
    }

    @EventHandler
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (!plugin.isChecked(player)) return;

        List<String> allowedCommands = cm.getAllowedCommands();
        if (allowedCommands == null) return;

        String rawMessage = event.getMessage().toLowerCase();
        String mainCommand = rawMessage.split(" ")[0];

        if (allowedCommands.contains(mainCommand)) {
            return;
        }

        event.setCancelled(true);
        cm.getMessages().sendMessages(player, "errors.block-commands", player.getName(), null);
    }
    
}
