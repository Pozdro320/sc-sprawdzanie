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
        Player moderator = event.getPlayer();
        String message = event.getMessage();

        if (plugin.isChecked(moderator)) {
            Player checker = moderator;
            event.setCancelled(true);
            Player mod = plugin.getModerator(checker);
            
            if (mod != null && mod.isOnline()) {
                String rawFormat = cm.getMessages().getSimpleMessage("chat-checker", "{PLAYER}", checker.getName());
                String finalMsg = rawFormat.replace("{MESSAGE}", message);

                checker.sendMessage(finalMsg);
                mod.sendMessage(finalMsg);
            } else {
                cm.getMessages().sendMessages(checker, "chat-no-moderator", "{PLAYER}", checker.getName());
            }
            return;
        }

        Player checked = plugin.getChecked(moderator);
        if (checked != null && checked.isOnline()) {
            event.setCancelled(true);

            String rawFormat = cm.getMessages().getSimpleMessage("chat-moderator", "{MODERATOR}", moderator.getName());
            String finalMsg = rawFormat.replace("{MESSAGE}", message);

            checked.sendMessage(finalMsg);
            moderator.sendMessage(finalMsg);
        }
    }
}