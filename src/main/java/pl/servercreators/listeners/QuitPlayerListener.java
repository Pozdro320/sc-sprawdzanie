package pl.servercreators.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import pl.servercreators.SCSprawdzMain;
import pl.servercreators.managers.ConfigManager;

public class QuitPlayerListener implements Listener {

    private final SCSprawdzMain plugin;
    private final ConfigManager cm;

    public QuitPlayerListener(SCSprawdzMain plugin, ConfigManager cm) {
        this.plugin = plugin;
        this.cm = cm;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (plugin.isChecked(player)) {
            Player moderator = plugin.getModerator(player);
            String modName = (moderator != null) ? moderator.getName() : "Console";

            String logoutReason = plugin.getConfig().getString("history-reasons.logout", "BANNED (LOGOUT)");
            plugin.getHistoryManager().log(player.getName(), logoutReason, moderator.getName());
            
            if (moderator != null && moderator.isOnline()) {
                cm.getMessages().sendMessages(moderator, "logged-out-mod", player.getName(), modName);
            }

            plugin.getBansHelper().executeBan(player, moderator, "logged-out");

            plugin.removeChecked(player);

            
        }
    }
}
