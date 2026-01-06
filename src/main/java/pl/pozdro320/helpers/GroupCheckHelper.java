package pl.pozdro320.helpers;

import org.bukkit.entity.Player;
import pl.pozdro320.SCSprawdzMain;

public class GroupCheckHelper {
    
    public static String getPrimaryGroup(Player player) {
        try {
            return SCSprawdzMain.getPerms().getPrimaryGroup(player);
        } catch (Exception e) {
            return "default";
        }
    }

    public static boolean canCheck(Player moderator, Player target, SCSprawdzMain plugin) {
        String modGroup = getPrimaryGroup(moderator);
        String targetGroup = getPrimaryGroup(target);
        
        return plugin.getConfigManager().getHierarchy().canCheck(modGroup, targetGroup);
    }
}