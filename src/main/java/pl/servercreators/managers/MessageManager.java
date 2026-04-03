package pl.servercreators.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
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
                
                if (bundle != null && (
                    bundle.contains("chat") ||
                    bundle.contains("title") ||
                    bundle.contains("subtitle") ||
                    bundle.contains("actionbar") ||
                    bundle.contains("sound") ||
                    bundle.contains("broadcast"))) {
                    
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

    public void sendMessages(CommandSender sender, String path, String... placeholders) {
        if (sender instanceof Player player) {
            sendMessagesToPlayer(player, path, placeholders);
            return;
        }

        MessageData data = messagesData.get(path);
        if (data == null) return;

        data.getChat().forEach(line -> sender.sendMessage(MessageHelper.colored(replace(line, placeholders))));

        if (!data.getBroadcast().isEmpty()) {
            data.getBroadcast().forEach(line -> Bukkit.broadcastMessage(MessageHelper.colored(replace(line, placeholders))));
        }
    }

    public void sendMessagesToPlayer(Player player, String path, String... placeholders) {
        MessageData data = messagesData.get(path);
        if (data == null) return;

        data.getChat().forEach(line -> player.sendMessage(MessageHelper.colored(replace(line, placeholders))));

        if (!data.getActionBar().isEmpty()) {
            MessageHelper.sendBar(player, replace(data.getActionBar(), placeholders));
        }

        if (!data.getTitle().isEmpty() || !data.getSubtitle().isEmpty()) {
            MessageHelper.sendTitle(player,
                replace(data.getTitle(), placeholders),
                replace(data.getSubtitle(), placeholders)
            );
        }

        if (!data.getSound().isEmpty()) {
            try {
                Sound sound = Sound.valueOf(data.getSound().toUpperCase());
                player.playSound(player.getLocation(), sound, 1f, 1f);
            } catch (IllegalArgumentException ignored) {}
        }

        if (!data.getBroadcast().isEmpty()) {
            data.getBroadcast().forEach(line -> Bukkit.broadcastMessage(MessageHelper.colored(replace(line, placeholders))));
        }
    }

    private String replace(String text, String... placeholders) {
        if (text == null || placeholders == null || placeholders.length < 2) return text;
        for (int i = 0; i < placeholders.length; i += 2) {
            if (i + 1 < placeholders.length) {
                text = text.replace(placeholders[i], placeholders[i + 1]);
            }
        }
        return text;
    }

    public String getSimpleMessage(String path, String... placeholders) {
        MessageData data = messagesData.get(path);

        if (data == null || data.getChat().isEmpty()){
            return MessageHelper.colored("&8> &4Brak wiadomosci w messages.yml: " + path);
        }

        String message = data.getChat().get(0);
        return MessageHelper.colored(replace(message, placeholders));
    }

    public FileConfiguration getConfig() {
        return config;
    }
}
