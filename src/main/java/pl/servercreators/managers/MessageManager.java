package pl.servercreators.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import pl.servercreators.data.MessageData;
import pl.servercreators.helpers.MessageHelper;

public class MessageManager {
    private final Map<String, MessageData> messagesData = new HashMap<>();
    private final FileConfiguration config;

    public MessageManager(FileConfiguration messagesConfig) {
        this.config = messagesConfig;
        this.loadMessages();
    }

    public void loadMessages() {
        this.messagesData.clear();
        ConfigurationSection root = config.getConfigurationSection("messages");
        if (root == null) return;

        for (String key : root.getKeys(true)) {
            if (root.isConfigurationSection(key)) {
                ConfigurationSection bundle = root.getConfigurationSection(key);
                
                if (bundle != null && (bundle.contains("chat") || bundle.contains("title") || bundle.contains("actionbar"))) {
                    
                    List<String> chat = bundle.isList("chat") ?
                            bundle.getStringList("chat") :
                            (bundle.contains("chat") ? List.of(bundle.getString("chat", "")) : new ArrayList<>());

                    List<String> broadcast = bundle.getStringList("broadcast");

                    this.messagesData.put(key, new MessageData(
                        chat,
                        bundle.getString("actionbar", ""),
                        bundle.getString("title", ""),
                        bundle.getString("subtitle", ""),
                        bundle.getString("sound", ""),
                        broadcast
                    ));
                }
            }
        }
    }

    public void sendMessages(Player player, String path, String targetName, String moderatorName) {
        MessageData data = messagesData.get(path);
        if (data == null) return;

        data.getChat().forEach(line ->
            player.sendMessage(format(line, targetName, moderatorName)));

        if (!data.getActionBar().isEmpty()) {
            MessageHelper.sendBar(player, format(data.getActionBar(), targetName, moderatorName));
        }

        if (!data.getTitle().isEmpty() || !data.getSubtitle().isEmpty()) {
            MessageHelper.sendTitle(player,
                format(data.getTitle(), targetName, moderatorName),
                format(data.getSubtitle(), targetName, moderatorName));
        }

        if (!data.getSound().isEmpty()) {
            try {
                Sound sound = Sound.valueOf(data.getSound().toUpperCase());
                player.playSound(player.getLocation(), sound, 1f, 1f);
            } catch (IllegalArgumentException ignored) {}
        }

        if (!data.getBroadcast().isEmpty()) {
            String bMsg = format(String.join("\n", data.getBroadcast()), targetName, moderatorName);
            Bukkit.broadcastMessage(bMsg);
        }
    }

    private String format(String text, String target, String mod) {
        return PlaceholdersManager.replacePlaceholders(text, target, mod);
    }

    public String getSimpleMessage(String path, String targetName, String moderatorName) {
        MessageData data = messagesData.get(path);
        
        if (data == null || data.getChat().isEmpty()) {
            return MessageHelper.colored("&8» &4Brak wiadomości w messages.yml dla klucza: &f" + path);
        }

        return format(data.getChat().get(0), targetName, moderatorName);
    }
}
