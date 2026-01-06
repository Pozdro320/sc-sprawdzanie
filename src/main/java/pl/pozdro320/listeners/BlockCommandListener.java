package pl.pozdro320.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import pl.pozdro320.SCSprawdzMain;
import pl.pozdro320.data.BlockCommandData;
import pl.pozdro320.managers.ConfigManager;

public class BlockCommandListener implements Listener {
    
    private final SCSprawdzMain plugin;
    private final ConfigManager cm;

    public BlockCommandListener(SCSprawdzMain plugin) {
        this.plugin = plugin;
        cm = plugin.getConfigManager();
    }

    @EventHandler
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (!plugin.czySprawdzany(player)) return;

        BlockCommandData data = cm.getBlockCommandData();
        if (data == null) return;

        String rawMessage = event.getMessage().toLowerCase();
        String mainCommand = rawMessage.split(" ")[0];

        boolean isAllowed = data.getAllowedCommands().stream()
                .anyMatch(allowedCmd -> mainCommand.equalsIgnoreCase(allowedCmd));

        if (isAllowed) {
            return;
        }

        event.setCancelled(true);
        cm.getMessages().sendMessages(player, "errors.blokada-komend", player.getName(), null);
    }
    
}
