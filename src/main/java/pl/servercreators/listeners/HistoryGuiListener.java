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

public class HistoryGuiListener implements Listener {

    private final ConfigManager cm;
    private final HistoryGUI historyGUI;
    private final CheckGUI checkGUI;

    public HistoryGuiListener(SCSprawdzMain plugin, HistoryGUI historyGUI, CheckGUI checkGUI, ConfigManager cm) {
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

        String guiType = meta.getPersistentDataContainer().get(historyGUI.getKeyType(), PersistentDataType.STRING);
        if (guiType == null || !guiType.equals("HISTORY")) return;

        event.setCancelled(true);
        
        String targetUUIDStr = meta.getPersistentDataContainer().get(historyGUI.getKeyTarget(), PersistentDataType.STRING);
        if (targetUUIDStr == null) return;

        int slot = event.getRawSlot();

        switch (slot) {
            case 31:
                Player target = Bukkit.getPlayer(UUID.fromString(targetUUIDStr));
                
                if (target != null && target.isOnline()) {
                    checkGUI.openGUI(moderator, target);
                    moderator.playSound(moderator.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1f, 1f);
                } else {
                    moderator.closeInventory();
                    cm.getMessages().sendMessages(moderator, "errors.player-offline", null, null);
                }
            break;
        }
    }
}
