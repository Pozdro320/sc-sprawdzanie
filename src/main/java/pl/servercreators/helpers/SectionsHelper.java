package pl.servercreators.helpers;

import org.bukkit.configuration.ConfigurationSection;

public class SectionsHelper {

    public static boolean hasMessageData(ConfigurationSection section) {
        if (section == null) return false;
        return section.contains("chat") ||
            section.contains("title") ||
            section.contains("subtitle") ||
            section.contains("actionbar") ||
            section.contains("broadcast") ||
            section.contains("sound");
    }

}
