package pl.servercreators.helpers;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import pl.servercreators.SCSprawdzMain;
import pl.servercreators.managers.ConfigManager;

public class BansHelper {
    
    private final SCSprawdzMain plugin;
    private final ConfigManager cm;
    
    public BansHelper(SCSprawdzMain plugin, ConfigManager cm) {
        this.plugin = plugin;
        this.cm = cm;
    }

public void executeBan(Player target, Player moderator, String type) {
        String targetName = target.getName();
        String modName = (moderator != null) ? moderator.getName() : "Console";
        
        List<String> banCommands = cm.getBanCommands(type);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (banCommands == null || banCommands.isEmpty()) return;

            for (String cmd : banCommands) {
                String finalCmd = cmd.replace("{PLAYER}", targetName).replace("{MODERATOR}", modName);
                
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCmd);
            }
        }, 20L);
    }
}
