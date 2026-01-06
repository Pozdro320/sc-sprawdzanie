package pl.pozdro320.gui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import pl.pozdro320.SCSprawdzMain;
import pl.pozdro320.data.GuiItemData;
import pl.pozdro320.helpers.GuiHelper;
import pl.pozdro320.helpers.MessageHelper;
import pl.pozdro320.managers.ConfigManager;
import pl.pozdro320.models.HistoryEntry;

public class HistorieGUI {

    private static SCSprawdzMain plugin;

    public static NamespacedKey KEY_TYPE;
    public static NamespacedKey KEY_TARGET;

    private static final int[] HISTORY_SLOTS = {
        10, 11, 12, 13, 14, 15, 16,
        19, 20, 21, 22, 23, 24, 25
    };

    public static void init(SCSprawdzMain plugin) {
        HistorieGUI.plugin = plugin;

        KEY_TYPE = new NamespacedKey(plugin, "gui_history_type");
        KEY_TARGET = new NamespacedKey(plugin, "target_uuid");
    }

    public static void openGUI(Player player, Player target) {
        ConfigManager cm = plugin.getConfigManager();

        String targetName = target.getName();
        String targetUUID = target.getUniqueId().toString();

        String title = cm.getGui().getGuiTitle("historie", targetName);
        
        Inventory inv = Bukkit.createInventory(null, 36, title);

        GuiHelper.fillBackground(inv, KEY_TYPE, "HISTORIE");

        List<HistoryEntry> logs = plugin.getHistoryManager().getHistory(target.getName());

        if (logs.isEmpty()) {
            GuiHelper.setItemFromConfig(inv, 13, "historie", "puste", targetName, KEY_TYPE, "HISTORIE", KEY_TARGET, targetUUID, cm);
        } else {
            for (int i = 0; i < logs.size() && i < HISTORY_SLOTS.length; i++) {
                int slot = HISTORY_SLOTS[i];
                inv.setItem(slot, createHistoryItem(logs.get(i), targetUUID));
            }
        }

        GuiHelper.setItemFromConfig(inv, 31, "historie", "powrot", targetName, KEY_TYPE, "HISTORIE", KEY_TARGET, targetUUID, cm);

        player.openInventory(inv);
    }

    private static ItemStack createHistoryItem(HistoryEntry entry, String targetUUID) {
        ConfigManager cm = plugin.getConfigManager();
        GuiItemData data = cm.getGui().getGuiItem("historie", "wpisy");

        String name = MessageHelper.colored(data.getName()
                .replace("{DATE}", entry.date())
                .replace("{ACTION}", entry.action())
                .replace("{MODERATOR}", entry.moderator()));

        List<String> lore = new ArrayList<>();
        for (String line : data.getLore()) {
            lore.add(MessageHelper.colored(line
                    .replace("{DATE}", entry.date())
                    .replace("{ACTION}", entry.action())
                    .replace("{MODERATOR}", entry.moderator())));
        }

        return GuiHelper.createItem(
                data.getMaterial(),
                KEY_TYPE, "HISTORIE",
                KEY_TARGET, targetUUID,
                name,
                lore
        );
    }
}