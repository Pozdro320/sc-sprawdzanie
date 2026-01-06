package pl.pozdro320.helpers;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import pl.pozdro320.data.GuiItemData;
import pl.pozdro320.managers.ConfigManager;

import java.util.List;

public class GuiHelper {

    public static ItemStack createItem(Material mat, NamespacedKey typeKey, String typeValue, NamespacedKey targetKey, String targetUUID, String name, List<String> lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        meta.setDisplayName(name);
        meta.setLore(lore);

        meta.getPersistentDataContainer().set(typeKey, PersistentDataType.STRING, typeValue);
        
        if (targetKey != null && targetUUID != null) {
            meta.getPersistentDataContainer().set(targetKey, PersistentDataType.STRING, targetUUID);
        }

        item.setItemMeta(meta);
        return item;
    }
    public static void setItemFromConfig(Inventory inv, int slot, String guiName, String itemName,
                                        String targetName, NamespacedKey typeKey, String typeValue,
                                        NamespacedKey targetKey, String targetUUID, ConfigManager cm) {
        
        GuiItemData data = cm.getGui().getGuiItem(guiName, itemName);
        
        String finalName = cm.getGui().getItemNameForPlayer(guiName, itemName, targetName);
        List<String> finalLore = cm.getGui().getItemLoreForPlayer(guiName, itemName, targetName);

        inv.setItem(slot, createItem(
                data.getMaterial(),
                typeKey, typeValue,
                targetKey, targetUUID,
                finalName,
                finalLore
        ));
    }

    public static void fillBackground(Inventory inv, NamespacedKey typeKey, String typeValue) {
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = glass.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(" ");
            meta.getPersistentDataContainer().set(typeKey, PersistentDataType.STRING, typeValue);
            glass.setItemMeta(meta);
        }
        
        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) == null) {
                inv.setItem(i, glass);
            }
        }
    }
}