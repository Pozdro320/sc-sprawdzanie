package pl.pozdro320.task;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import pl.pozdro320.SCSprawdzMain;
import pl.pozdro320.managers.ConfigManager;

public class SprawdzaniGraczeTask {

    private final ConfigManager cm;

    public SprawdzaniGraczeTask(SCSprawdzMain plugin) {

        this.cm = plugin.getConfigManager();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (SCSprawdzMain.sprawdzaniGracze.isEmpty()) return;

                for (Map.Entry<UUID, UUID> entry : SCSprawdzMain.sprawdzaniGracze.entrySet()) {
                    Player target = Bukkit.getPlayer(entry.getKey());
                    Player moderator = Bukkit.getPlayer(entry.getValue());

                    String targetName = (target != null) ? target.getName() : "&cOFFLINE";
                    String modName = (moderator != null) ? moderator.getName() : "Konsola";

                    if (target != null && target.isOnline()) {
                        cm.getMessages().sendMessages(target, "wiadomosc-gracz", targetName, modName);
                    }

                    if (moderator != null && moderator.isOnline()) {
                        cm.getMessages().sendMessages(moderator, "wiadomosc-mod", targetName, modName);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 60L);
    }
}