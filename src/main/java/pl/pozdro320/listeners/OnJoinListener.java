package pl.pozdro320.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import pl.pozdro320.SCSprawdzMain;
import pl.pozdro320.utils.GithubUpdater;
import pl.pozdro320.utils.SemanticVersion;

public class OnJoinListener implements Listener {

    private final SCSprawdzMain plugin;
    
    public OnJoinListener(SCSprawdzMain plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        if (player.hasPermission("sc-sprawdzanie.admin")) {
            SemanticVersion currentVersion = new SemanticVersion(plugin.getDescription().getVersion());
            GithubUpdater updater = new GithubUpdater("pozdro320", "sc-sprawdzanie");

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                if (updater.hasUpdate(currentVersion)) {
                    player.sendMessage("§8» §6Dostępna jest nowa wersja §esc-sprawdzanie§6!");
                    player.sendMessage("§8» §7Pobierz: §e" + updater.getDownloadLink());
                }
            });
        }
    }
    
}
