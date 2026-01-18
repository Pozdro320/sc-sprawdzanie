package pl.servercreators.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import pl.servercreators.SCSprawdzMain;
import pl.servercreators.utils.GithubUpdater;
import pl.servercreators.utils.SemanticVersion;

public class OnJoinListener implements Listener {

    private final SCSprawdzMain plugin;

    private final GithubUpdater updater;
    private final SemanticVersion currentVersion;

    public OnJoinListener(SCSprawdzMain plugin, GithubUpdater updater){
        this.plugin = plugin;

        this.updater = updater;
        this.currentVersion = new SemanticVersion(plugin.getDescription().getVersion());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        if (player.hasPermission("sc-sprawdzanie.admin")) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                if (updater.hasUpdate(currentVersion)) {
                    player.sendMessage("§8» §fDostępna jest nowa wersja §5sc-sprawdzanie§f!");
                }
            });
        }
    }
    
}
