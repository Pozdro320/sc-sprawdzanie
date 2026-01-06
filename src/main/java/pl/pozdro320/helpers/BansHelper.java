package pl.pozdro320.helpers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import pl.pozdro320.SCSprawdzMain;

public class BansHelper {
    private final SCSprawdzMain plugin;
    
    public BansHelper(SCSprawdzMain plugin) {
        this.plugin = plugin;
    }

    public void executeBan(Player target, Player moderator, String type) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (String cmd : plugin.getConfigManager().getBanCommands(type)) {
                String finalCmd = cmd.replace("{PLAYER}", target.getName())
                                    .replace("{MODERATOR}", moderator.getName());
                
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCmd);
            }
        }, 20L);
    }
}
