package pl.servercreators.helpers;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.entity.Player;
import pl.servercreators.SCSprawdzMain;
import pl.servercreators.managers.ConfigManager;

public class GroupCheckHelper {

    private final Permission perms;
    private final ConfigManager cm;

    public GroupCheckHelper(SCSprawdzMain plugin, Permission perms, ConfigManager cm) {
        this.perms = perms;
        this.cm = cm;
    }
    
    public String getPrimaryGroup(Player player) {
        if (perms == null || player == null) return "default";
        
        try {
            String group = perms.getPrimaryGroup(player);
            return group != null ? group : "default";
        } catch (Exception e) {
            return "default";
        }
    }

    public boolean canCheck(Player moderator, Player target) {
        if (moderator.isOp()) return true;

        String modGroup = getPrimaryGroup(moderator);
        String targetGroup = getPrimaryGroup(target);
        
        return cm.getHierarchy().canCheck(modGroup, targetGroup);
    }
}