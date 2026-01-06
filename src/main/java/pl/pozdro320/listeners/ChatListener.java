package pl.pozdro320.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import pl.pozdro320.SCSprawdzMain;
import pl.pozdro320.managers.ConfigManager;

public class ChatListener implements Listener {

    private final SCSprawdzMain plugin;
    private final ConfigManager cm;

    public ChatListener(SCSprawdzMain plugin) {
        this.plugin = plugin;
        cm = plugin.getConfigManager();
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        if (plugin.czySprawdzany(player)) {
            event.setCancelled(true);
            Player moderator = plugin.getModerator(player);
            
            if (moderator != null && moderator.isOnline()) {

            String rawFormat = cm.getMessages().getSimpleMessage("chat-sprawdzanie", player.getName(), moderator.getName());
            String finalMsg = rawFormat.replace("{MESSAGE}", message);

            player.sendMessage(finalMsg);
            moderator.sendMessage(finalMsg);
            } else {
                cm.getMessages().sendMessages(player, "chat-brak-moderatora", player.getName(), null);
            }
            return;
        }

        Player sprawdzany = plugin.getSprawdzany(player);
        if (sprawdzany != null && sprawdzany.isOnline()) {
            event.setCancelled(true);

            String rawFormat = cm.getMessages().getSimpleMessage("chat-moderator", sprawdzany.getName(), player.getName());
            String finalMsg = rawFormat.replace("{MESSAGE}", message);

            player.sendMessage(finalMsg);
            sprawdzany.sendMessage(finalMsg);
        }
    }
}