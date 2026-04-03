package pl.servercreators.gui;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import pl.servercreators.SCSprawdzMain;
import pl.servercreators.data.GuiItemData;
import pl.servercreators.helpers.GuiHelper;
import pl.servercreators.managers.ConfigManager;

public class CheckGUI {
        private final ConfigManager cm;

        private final NamespacedKey KEY_TYPE;
        private final NamespacedKey KEY_TARGET;

        private final GuiHelper guiHelper;

        public CheckGUI(SCSprawdzMain plugin, GuiHelper guiHelper, ConfigManager cm, GuiItemData guiItemData) {

                this.KEY_TYPE = new NamespacedKey(plugin, "gui_type");
                this.KEY_TARGET = new NamespacedKey(plugin, "target_uuid");

                this.guiHelper = guiHelper;
                this.cm = cm;
        }

        public void openGUI(Player player, Player target) {

                String targetName = target.getName();
                String targetUUID = target.getUniqueId().toString();

                String title = cm.getGui().getGuiTitle("checker", target.getName());

                Inventory inv = Bukkit.createInventory(null, 36, title);

                guiHelper.fillBackground(inv, KEY_TYPE, "CHECKER");

                guiHelper.setItemFromConfig(inv, 11, "checker", "check", targetName, KEY_TYPE, "CHECKER", KEY_TARGET, targetUUID);
                guiHelper.setItemFromConfig(inv, 13, "checker", "clean", targetName, KEY_TYPE, "CHECKER", KEY_TARGET, targetUUID);
                guiHelper.setItemFromConfig(inv, 15, "checker", "cheater", targetName, KEY_TYPE, "CHECKER", KEY_TARGET, targetUUID );
                guiHelper.setItemFromConfig(inv, 21, "checker", "admission", targetName, KEY_TYPE, "CHECKER", KEY_TARGET, targetUUID);
                guiHelper.setItemFromConfig(inv, 23, "checker", "lack-of-cooperation", targetName, KEY_TYPE, "CHECKER", KEY_TARGET, targetUUID);
                guiHelper.setItemFromConfig(inv, 34, "checker", "teleport", targetName, KEY_TYPE, "CHECKER", KEY_TARGET, targetUUID);
                guiHelper.setItemFromConfig(inv, 35, "checker", "history", targetName, KEY_TYPE, "CHECKER", KEY_TARGET, targetUUID);

                player.openInventory(inv);
        }

        public NamespacedKey getKeyType() { return KEY_TYPE; }
        public NamespacedKey getKeyTarget() { return KEY_TARGET; }

}
