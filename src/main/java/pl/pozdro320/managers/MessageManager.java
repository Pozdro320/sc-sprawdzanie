package pl.pozdro320.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import pl.pozdro320.data.MessageData;
import pl.pozdro320.helpers.MessageHelper;
import pl.pozdro320.helpers.SectionsHelper;

public class MessageManager {
    private final Map<String, MessageData> messagesData = new HashMap<>();
    private final FileConfiguration config;

    public MessageManager(FileConfiguration messagesConfig) {
        this.config = messagesConfig;
        loadMessages();
    }

    public void loadMessages() {
        messagesData.clear();
        ConfigurationSection section = config.getConfigurationSection("messages");
        if (section == null) return;

        for (String key : section.getKeys(true)) {

            if (section.isConfigurationSection(key)) {
                ConfigurationSection bundle = section.getConfigurationSection(key);
                if (bundle == null) continue;

                if (!SectionsHelper.hasMessageData(bundle)) continue;

                List<String> chat = bundle.isList("chat") ?
                    bundle.getStringList("chat") :
                    (bundle.getString("chat") != null ? List.of(bundle.getString("chat")) : new ArrayList<>());

                List<String> broadcast = bundle.getStringList("broadcast");

                messagesData.put(key, new MessageData(
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

    public void sendMessages(Player player, String path, String targetName, String moderatorName) {
        MessageData data = messagesData.get(path);
        
        if (data == null) {
            return;
        }

        data.getChat().forEach(line -> player.sendMessage(PlaceholdersManager.replacePlaceholders(line, targetName, moderatorName)));

        if (!data.getActionBar().isEmpty()) {
            MessageHelper.sendBar(player, PlaceholdersManager.replacePlaceholders(data.getActionBar(), targetName, moderatorName));
        }

        if (!data.getTitle().isEmpty() || !data.getSubtitle().isEmpty()) {
            MessageHelper.sendTitle(player,
                PlaceholdersManager.replacePlaceholders(data.getTitle(), targetName, moderatorName),
                PlaceholdersManager.replacePlaceholders(data.getSubtitle(), targetName, moderatorName));
        }

        if (!data.getSound().isEmpty()) {
            try {
                player.playSound(player.getLocation(), Sound.valueOf(data.getSound().toUpperCase()), 1f, 1f);
            } catch (Exception ignored) {}
        }

        if (!data.getBroadcast().isEmpty()) {
            data.getBroadcast().forEach(line ->
                Bukkit.broadcastMessage(PlaceholdersManager.replacePlaceholders(line, targetName, moderatorName)));
        }
    }

    public String getSimpleMessage(String path, String targetName, String moderatorName) {
        MessageData data = messagesData.get(path);
        
        if (data == null || data.getChat().isEmpty()) {
            return MessageHelper.colored("&8» &4Brak wiadomości w messages.yml dla klucza: &f" + path);
        }

        return PlaceholdersManager.replacePlaceholders(data.getChat().get(0), targetName, moderatorName);
    }
}
