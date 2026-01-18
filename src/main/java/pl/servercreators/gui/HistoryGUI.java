package pl.servercreators.gui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import pl.servercreators.SCSprawdzMain;
import pl.servercreators.data.GuiItemData;
import pl.servercreators.helpers.GroupCheckHelper;
import pl.servercreators.helpers.GuiHelper;
import pl.servercreators.helpers.MessageHelper;
import pl.servercreators.managers.ConfigManager;
import pl.servercreators.managers.HistoryManager;
import pl.servercreators.models.HistoryEntry;

public class HistoryGUI {


    private final NamespacedKey KEY_TYPE;
    private final NamespacedKey KEY_TARGET;

    private final GuiHelper guiHelper;

    private final GroupCheckHelper groupCheckHelper;
    private final ConfigManager cm;

    private final HistoryManager historyManager;

    private final int[] HISTORY_SLOTS = {
        10, 11, 12, 13, 14, 15, 16,
        19, 20, 21, 22, 23, 24, 25
    };

    public HistoryGUI(SCSprawdzMain plugin, GroupCheckHelper groupCheckHelper, HistoryManager historyManager, ConfigManager cm, GuiHelper guiHelper){

        this.KEY_TYPE = new NamespacedKey(plugin, "gui_history_type");
        this.KEY_TARGET = new NamespacedKey(plugin, "target_uuid");

        this.guiHelper = guiHelper;

        this.groupCheckHelper = groupCheckHelper;
        this.cm = cm;
        this.historyManager = historyManager;

    }

    public void openGUI(Player player, Player target) {

        String targetName = target.getName();
        String targetUUID = target.getUniqueId().toString();

        String title = cm.getGui().getGuiTitle("history", targetName);
        
        Inventory inv = Bukkit.createInventory(null, 36, title);

        guiHelper.fillBackground(inv, KEY_TYPE, "HISTORY");

        List<HistoryEntry> logs = historyManager.getHistory(target.getName());

        if (logs.isEmpty()) {
            guiHelper.setItemFromConfig(inv, 13, "history", "empty", targetName, KEY_TYPE, "HISTORY", KEY_TARGET, targetUUID);
            } else {

            GuiItemData template = cm.getGui().getGuiItem("history", "entries");
            
            for (int i = 0; i < logs.size() && i < HISTORY_SLOTS.length; i++) {
                inv.setItem(HISTORY_SLOTS[i], createHistoryItem(template, logs.get(i), targetUUID));
            }
        }

        guiHelper.setItemFromConfig(inv, 31, "history", "back", targetName, KEY_TYPE, "HISTORY", KEY_TARGET, targetUUID);

        player.openInventory(inv);
    }

    private ItemStack createHistoryItem(GuiItemData template, HistoryEntry entry, String targetUUID) {

        String name = MessageHelper.colored(template.getName()
                .replace("{DATE}", entry.date())
                .replace("{ACTION}", entry.action())
                .replace("{MODERATOR}", entry.moderator()));

        List<String> lore = new ArrayList<>();
        for (String line : template.getLore()) {
            lore.add(MessageHelper.colored(line
                    .replace("{DATE}", entry.date())
                    .replace("{ACTION}", entry.action())
                    .replace("{MODERATOR}", entry.moderator())));
        }

        return GuiHelper.createItem(
                template.getMaterial(),
                KEY_TYPE, "HISTORY",
                KEY_TARGET, targetUUID,
                name,
                lore
        );
    }

    public NamespacedKey getKeyType() { return KEY_TYPE; }
    public NamespacedKey getKeyTarget() { return KEY_TARGET; }
}