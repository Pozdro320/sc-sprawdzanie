package pl.pozdro320.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import pl.pozdro320.SCSprawdzMain;
import pl.pozdro320.managers.ConfigManager;

public class QuitPlayerListener implements Listener {

    private final SCSprawdzMain plugin;
    private final ConfigManager cm;

    public QuitPlayerListener(SCSprawdzMain plugin) {
        this.plugin = plugin;
        cm = plugin.getConfigManager();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (plugin.czySprawdzany(player)) {
            Player moderator = plugin.getModerator(player);
            String modName = (moderator != null) ? moderator.getName() : "System/Konsola";

            plugin.getHistoryManager().log(player.getName(), "ZBANOWANY (WYLOGOWAŁ SIĘ)", modName);
            
            if (moderator != null && moderator.isOnline()) {
                cm.getMessages().sendMessages(moderator, "wylogowal-mod", player.getName(), modName);
            }

            plugin.getBansHelper().executeBan(player, moderator, "wylogowal-sie");

            plugin.usunSprawdzanego(player);

            
        }
    }
}
