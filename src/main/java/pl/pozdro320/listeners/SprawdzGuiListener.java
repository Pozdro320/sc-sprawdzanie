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

public class SprawdzGuiListener implements Listener {

    private final SCSprawdzMain plugin;
    private final ConfigManager cm;

    public SprawdzGuiListener(SCSprawdzMain plugin) {
        this.plugin = plugin;
        cm = plugin.getConfigManager();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player moderator)) return;

        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) return;

        ItemMeta meta = item.getItemMeta();
        
        String guiType = meta.getPersistentDataContainer().get(SprawdzGUI.KEY_TYPE, PersistentDataType.STRING);
        if (guiType == null || !guiType.equals("SPRAWDZANIE")) return;

        event.setCancelled(true);

        String targetUUIDStr = meta.getPersistentDataContainer().get(SprawdzGUI.KEY_TARGET, PersistentDataType.STRING);
        if (targetUUIDStr == null) return;

        Player target = Bukkit.getPlayer(UUID.fromString(targetUUIDStr));
        if (target == null) {
            cm.getMessages().sendMessages(moderator, "errors.gracz-offline", null, null);
            moderator.closeInventory();
            return;
        }

        int slot = event.getRawSlot();

        switch (slot) {
        case 11:
            if (plugin.getSprawdzLocation() == null) {
                cm.getMessages().sendMessages(moderator, "errors.brak-sprawdzarki", null, null);
                return;
            }
            
            if (plugin.getSpawnLocation() == null) {
                cm.getMessages().sendMessages(moderator, "errors.brak-spawnu", null, null);
                return;
            }

            moderator.teleport(plugin.getSprawdzLocation());
            target.teleport(plugin.getSprawdzLocation());

            plugin.dodajSprawdzanego(target, moderator);

            cm.getMessages().sendMessages(target, "sprawdz-start-gracz", target.getName(), moderator.getName());
            cm.getMessages().sendMessages(moderator, "sprawdz-start-mod", target.getName(), moderator.getName());
            break;

        case 13:
            if (!plugin.czySprawdzany(target)) {
                cm.getMessages().sendMessages(moderator, "errors.czy-sprawdzany", target.getName(), moderator.getName());
                moderator.closeInventory();
                return;
            }

            target.teleport(plugin.getSpawnLocation());

            cm.getMessages().sendMessages(target, "gracz-czysty", target.getName(), moderator.getName());
            cm.getMessages().sendMessages(moderator, "czysty-mod", target.getName(), moderator.getName());

            plugin.getHistoryManager().log(target.getName(), "UZNANO ZA CZYSTEGO", moderator.getName());
            plugin.usunSprawdzanego(target);
            break;

        case 15:
            if (!plugin.czySprawdzany(target)) {
                cm.getMessages().sendMessages(moderator, "errors.czy-sprawdzany", target.getName(), moderator.getName());
                moderator.closeInventory();
                return;
            }

            cm.getMessages().sendMessages(moderator, "cheaty-mod", target.getName(), moderator.getName());

            plugin.getHistoryManager().log(target.getName(), "ZBANOWANY (CHEATY)", moderator.getName());
            plugin.usunSprawdzanego(target);
            target.teleport(plugin.getSpawnLocation());

            plugin.getBansHelper().executeBan(target, moderator, "wykryto-cheaty");
            break;

        case 21:
            if (!plugin.czySprawdzany(target)) {
                cm.getMessages().sendMessages(moderator, "errors.czy-sprawdzany", target.getName(), moderator.getName());
                moderator.closeInventory();
                return;
            }

            cm.getMessages().sendMessages(moderator, "przyznanie-mod", target.getName(), moderator.getName());

            plugin.getHistoryManager().log(target.getName(), "ZBANOWANY (PRZYZNANIE SIE)", moderator.getName());
            plugin.usunSprawdzanego(target);
            target.teleport(plugin.getSpawnLocation());

            plugin.getBansHelper().executeBan(target, moderator, "przyznanie-sie");
            break;

        case 23:
            if (!plugin.czySprawdzany(target)) {
                cm.getMessages().sendMessages(moderator, "errors.czy-sprawdzany", target.getName(), moderator.getName());
                moderator.closeInventory();
                return;
            }

            cm.getMessages().sendMessages(moderator, "brak-wspolpracy-mod", target.getName(), moderator.getName());

            plugin.getHistoryManager().log(target.getName(), "ZBANOWANY (BRAK WSPÓŁPRACY)", moderator.getName());
            plugin.usunSprawdzanego(target);
            target.teleport(plugin.getSpawnLocation());
            plugin.getBansHelper().executeBan(target, moderator, "brak-wspolpracy");
            break;
            
        case 34:
            if (plugin.getSprawdzLocation() == null) {
                cm.getMessages().sendMessages(moderator, "errors.brak-sprawdzarki", null, null);
                return;
            }

            moderator.teleport(plugin.getSprawdzLocation());
            cm.getMessages().sendMessages(moderator, "tp-sprawdzarka", null, null);;
            break;

        case 35:
            HistorieGUI.openGUI(moderator, target);
            moderator.playSound(moderator.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1f, 1f);
            return;
    }

    moderator.closeInventory();

    }
}
