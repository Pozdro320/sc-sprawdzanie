package pl.servercreators.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import pl.servercreators.SCSprawdzMain;
import pl.servercreators.helpers.MessageHelper;

public class OnJoinListener implements Listener {

    private final SCSprawdzMain plugin;

    public OnJoinListener(SCSprawdzMain plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission("sc-sprawdzanie.admin") && plugin.isUpdateAvailable()) {
            
            MessageHelper.build("")
                    .send(player);
            MessageHelper.build(" &8» &fWykryto nową wersję pluginu &dSC-Sprawdzanie&f!")
                    .send(player);
            MessageHelper.build(" &8» &fPobierz ją na &bhttps://discord.gg/P6MBxsa2xs")
                    .send(player);
            MessageHelper.build("")
                    .send(player);

            MessageHelper.sendBar(player, "&8» &fDostępna jest nowa wersja &dsc-sprawdzanie&f!");
        }
    }
    
}
