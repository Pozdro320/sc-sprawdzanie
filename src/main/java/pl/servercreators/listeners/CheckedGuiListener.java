package pl.servercreators.listeners;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import pl.servercreators.SCSprawdzMain;
import pl.servercreators.gui.CheckGUI;
import pl.servercreators.gui.HistoryGUI;
import pl.servercreators.managers.ConfigManager;

public class CheckedGuiListener implements Listener {

    private final SCSprawdzMain plugin;
    private final ConfigManager cm;
    private final HistoryGUI historyGUI;
    private final CheckGUI checkGUI;

    public CheckedGuiListener(SCSprawdzMain plugin, HistoryGUI historyGUI, CheckGUI checkGUI, ConfigManager cm) {
        this.plugin = plugin;
        
        this.cm = cm;
        this.historyGUI = historyGUI;
        this.checkGUI = checkGUI;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player moderator)) return;

        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) return;

        ItemMeta meta = item.getItemMeta();
        
        String guiType = meta.getPersistentDataContainer().get(checkGUI.getKeyType(), PersistentDataType.STRING);
        if (guiType == null || !guiType.equals("CHECKER")) return;

        event.setCancelled(true);

        String targetUUIDStr = meta.getPersistentDataContainer().get(checkGUI.getKeyTarget(), PersistentDataType.STRING);
        if (targetUUIDStr == null) return;

        Player target = Bukkit.getPlayer(UUID.fromString(targetUUIDStr));
        if (target == null) {
            cm.getMessages().sendMessages(moderator, "errors.player-offline");
            moderator.closeInventory();
            return;
        }

        int slot = event.getRawSlot();

        switch (slot) {
        case 11:
            if (plugin.getCheckLocation() == null) {
                cm.getMessages().sendMessages(moderator, "errors.no-checker");
                return;
            }
            
            if (plugin.getSpawnLocation() == null) {
                cm.getMessages().sendMessages(moderator, "errors.no-spawn");
                return;
            }

            moderator.teleport(plugin.getCheckLocation());
            target.teleport(plugin.getCheckLocation());

            plugin.addChecked(target, moderator);

            cm.getMessages().sendMessages(target, "check-start-player", "{MODERATOR}", moderator.getName());
            cm.getMessages().sendMessages(moderator, "check-start-mod", "{PLAYER}", target.getName(), "{MODERATOR}", moderator.getName());
            break;

        case 13:
            if (!plugin.isChecked(target)) {
                cm.getMessages().sendMessages(moderator, "errors.is-checked", "{PLAYER}", target.getName());
                moderator.closeInventory();
                return;
            }

            target.teleport(plugin.getSpawnLocation());

            cm.getMessages().sendMessages(target, "player-clean", "{PLAYER}", target.getName(), "{MODERATOR}", moderator.getName());
            cm.getMessages().sendMessages(moderator, "mod-clean", "{PLAYER}", target.getName(), "{MODERATOR}", moderator.getName());

            String cleanReason = plugin.getConfig().getString("history-reasons.clean", "MARKED AS CLEAN");
            plugin.getHistoryManager().log(target.getName(), cleanReason, moderator.getName());
            plugin.removeChecked(target);
            break;

        case 15:
            if (!plugin.isChecked(target)) {
                cm.getMessages().sendMessages(moderator, "errors.is-checked", "{PLAYER}", target.getName());
                moderator.closeInventory();
                return;
            }

            cm.getMessages().sendMessages(moderator, "cheats-mod", "{PLAYER}", target.getName());

            String cheatsReason = plugin.getConfig().getString("history-reasons.cheats", "BANNED (CHEATS)");
            plugin.getHistoryManager().log(target.getName(), cheatsReason, moderator.getName());
            plugin.removeChecked(target);
            target.teleport(plugin.getSpawnLocation());

            plugin.getBansHelper().executeBan(target, moderator, "cheats-detected");
            break;

        case 21:
            if (!plugin.isChecked(target)) {
                cm.getMessages().sendMessages(moderator, "errors.is-checked", "{PLAYER}", target.getName());
                moderator.closeInventory();
                return;
            }

            cm.getMessages().sendMessages(moderator, "admission-mod", "{PLAYER}", target.getName());

            String admissionReason = plugin.getConfig().getString("history-reasons.admission", "BANNED (ADMISSION)");
            plugin.getHistoryManager().log(target.getName(), admissionReason, moderator.getName());
            plugin.removeChecked(target);
            target.teleport(plugin.getSpawnLocation());

            plugin.getBansHelper().executeBan(target, moderator, "admission");
            break;

        case 23:
            if (!plugin.isChecked(target)) {
                cm.getMessages().sendMessages(moderator, "errors.is-checked", "{PLAYER}", target.getName());
                moderator.closeInventory();
                return;
            }

            cm.getMessages().sendMessages(moderator, "lack-of-cooperation-mod", "{PLAYER}", target.getName());

            String noCoopReason = plugin.getConfig().getString("history-reasons.no-cooperation", "BANNED (LACK OF COOPERATION)");
            plugin.getHistoryManager().log(target.getName(), noCoopReason, moderator.getName());
            plugin.removeChecked(target);
            target.teleport(plugin.getSpawnLocation());
            plugin.getBansHelper().executeBan(target, moderator, "lack-of-cooperation");
            break;
            
        case 34:
            if (plugin.getCheckLocation() == null) {
                cm.getMessages().sendMessages(moderator, "errors.no-checker");
                return;
            }

            moderator.teleport(plugin.getCheckLocation());
            cm.getMessages().sendMessages(moderator, "tp-checker");
            break;

        case 35:
            historyGUI.openGUI(moderator, target);
            moderator.playSound(moderator.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1f, 1f);
            return;
        }

        moderator.closeInventory();
    }
}
