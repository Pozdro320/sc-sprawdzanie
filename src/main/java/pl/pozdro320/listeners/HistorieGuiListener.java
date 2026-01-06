package pl.pozdro320.listeners;

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

import pl.pozdro320.SCSprawdzMain;
import pl.pozdro320.gui.HistorieGUI;
import pl.pozdro320.gui.SprawdzGUI;
import pl.pozdro320.managers.ConfigManager;

public class HistorieGuiListener implements Listener {
    private final ConfigManager cm;

    public HistorieGuiListener(SCSprawdzMain plugin) {
        cm = plugin.getConfigManager();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player moderator)) return;

        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) return;

        ItemMeta meta = item.getItemMeta();

        String guiType = meta.getPersistentDataContainer().get(HistorieGUI.KEY_TYPE, PersistentDataType.STRING);
        if (guiType == null || !guiType.equals("HISTORIE")) return;

        event.setCancelled(true);
        
        String targetUUIDStr = meta.getPersistentDataContainer().get(HistorieGUI.KEY_TARGET, PersistentDataType.STRING);
        if (targetUUIDStr == null) return;

        int slot = event.getRawSlot();

        switch (slot) {
            case 31:
                Player target = Bukkit.getPlayer(UUID.fromString(targetUUIDStr));
                
                if (target != null && target.isOnline()) {
                    SprawdzGUI.openGUI(moderator, target);
                    moderator.playSound(moderator.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1f, 1f);
                } else {
                    moderator.closeInventory();
                    cm.getMessages().sendMessages(moderator, "errors.gracz-offline", null, null);
                }
            break;
        }
    }
}
