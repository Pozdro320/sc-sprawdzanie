package pl.servercreators.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import pl.servercreators.SCSprawdzMain;
import pl.servercreators.managers.ConfigManager;

public class ChatListener implements Listener {

    private final SCSprawdzMain plugin;
    private final ConfigManager cm;

    public ChatListener(SCSprawdzMain plugin, ConfigManager cm) {
        this.plugin = plugin;
        this.cm = cm;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        if (plugin.isChecked(player)) {
            event.setCancelled(true);
            Player moderator = plugin.getModerator(player);
            
            if (moderator != null && moderator.isOnline()) {

            String rawFormat = cm.getMessages().getSimpleMessage("chat-checker", player.getName(), moderator.getName());
            String finalMsg = rawFormat.replace("{MESSAGE}", message);

            player.sendMessage(finalMsg);
            moderator.sendMessage(finalMsg);
            } else {
                cm.getMessages().sendMessages(player, "chat-no-moderator", player.getName(), null);
            }
            return;
        }

        Player checker = plugin.getChecked(player);
        if (checker != null && checker.isOnline()) {
            event.setCancelled(true);

            String rawFormat = cm.getMessages().getSimpleMessage("chat-moderator", checker.getName(), player.getName());
            String finalMsg = rawFormat.replace("{MESSAGE}", message);

            player.sendMessage(finalMsg);
            checker.sendMessage(finalMsg);
        }
    }
}