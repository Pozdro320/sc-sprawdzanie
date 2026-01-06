package pl.pozdro320.gui;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import pl.pozdro320.SCSprawdzMain;
import pl.pozdro320.helpers.GuiHelper;
import pl.pozdro320.managers.ConfigManager;

public class SprawdzGUI {

        private static SCSprawdzMain plugin;

        public static NamespacedKey KEY_TYPE;
        public static NamespacedKey KEY_TARGET;


        public static void init(SCSprawdzMain plugin) {
                SprawdzGUI.plugin = plugin;

                KEY_TYPE = new NamespacedKey(plugin, "gui_type");
                KEY_TARGET = new NamespacedKey(plugin, "target_uuid");
        }

        public static void openGUI(Player player, Player target) {
                ConfigManager cm = plugin.getConfigManager();

                String targetName = target.getName();
                String targetUUID = target.getUniqueId().toString();

                String title = cm.getGui().getGuiTitle("sprawdzanie", target.getName());

                Inventory inv = Bukkit.createInventory(null, 36, title);

                GuiHelper.fillBackground(inv, KEY_TYPE, "SPRAWDZANIE");

                GuiHelper.setItemFromConfig(inv, 11, "sprawdzanie", "sprawdz", targetName, KEY_TYPE, "SPRAWDZANIE", KEY_TARGET, targetUUID, cm);
                GuiHelper.setItemFromConfig(inv, 13, "sprawdzanie", "czysty", targetName, KEY_TYPE, "SPRAWDZANIE", KEY_TARGET, targetUUID, cm);
                GuiHelper.setItemFromConfig(inv, 15, "sprawdzanie", "cheater", targetName, KEY_TYPE, "SPRAWDZANIE", KEY_TARGET, targetUUID, cm);
                GuiHelper.setItemFromConfig(inv, 21, "sprawdzanie", "przyznanie", targetName, KEY_TYPE, "SPRAWDZANIE", KEY_TARGET, targetUUID, cm);
                GuiHelper.setItemFromConfig(inv, 23, "sprawdzanie", "wspolpraca", targetName, KEY_TYPE, "SPRAWDZANIE", KEY_TARGET, targetUUID, cm);
                GuiHelper.setItemFromConfig(inv, 34, "sprawdzanie", "teleport", targetName, KEY_TYPE, "SPRAWDZANIE", KEY_TARGET, targetUUID, cm);
                GuiHelper.setItemFromConfig(inv, 35, "sprawdzanie", "historia", targetName, KEY_TYPE, "SPRAWDZANIE", KEY_TARGET, targetUUID, cm);

                player.openInventory(inv);
        }

}
