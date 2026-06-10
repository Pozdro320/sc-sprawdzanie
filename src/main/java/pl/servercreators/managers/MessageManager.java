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

        data.getRawChat().forEach(line -> sender.sendMessage(MessageHelper.colored(replace(line, placeholders))));

        if (!data.getRawBroadcast().isEmpty()) {
            data.getRawBroadcast().forEach(line -> Bukkit.broadcastMessage(MessageHelper.colored(replace(line, placeholders))));
        }
    }

    public void sendMessagesToPlayer(Player player, String path, String... placeholders) {
        MessageData data = messagesData.get(path);
        if (data == null) return;

        boolean hasPlaceholders = placeholders != null && placeholders.length >= 2;

        if (hasPlaceholders) {
            data.getRawChat().forEach(line -> player.sendMessage(MessageHelper.colored(replace(line, placeholders))));
        } else {
            data.getColoredChat().forEach(player::sendMessage);
        }

        if (!data.getRawActionBar().isEmpty()) {
            String bar = hasPlaceholders ?
                MessageHelper.colored(replace(data.getRawActionBar(), placeholders)) :
                data.getColoredActionBar();
            MessageHelper.sendBar(player, bar);
        }

        if (!data.getRawTitle().isEmpty() || !data.getRawSubtitle().isEmpty()) {
            String t = hasPlaceholders ?
                MessageHelper.colored(replace(data.getRawTitle(), placeholders)) :
                data.getColoredTitle();
            String s = hasPlaceholders ?
                MessageHelper.colored(replace(data.getRawSubtitle(), placeholders)) :
                data.getColoredSubtitle();
            player.sendTitle(t, s, 10, 40, 10);
        }

        if (!data.getSound().isEmpty()) {
            try {
                Sound sound = Sound.valueOf(data.getSound().toUpperCase());
                player.playSound(player.getLocation(), sound, 1f, 1f);
            } catch (IllegalArgumentException ignored) {}
        }

        if (!data.getRawBroadcast().isEmpty()) {
            if (hasPlaceholders) {
                data.getRawBroadcast().forEach(line ->
                    Bukkit.broadcastMessage(MessageHelper.colored(replace(line, placeholders))));
            } else {
                data.getColoredBroadcast().forEach(Bukkit::broadcastMessage);
            }
        }
    }

    private String replace(String text, String... placeholders) {
        if (text == null || placeholders == null || placeholders.length < 2) return text;
        String result = text;
        for (int i = 0; i < placeholders.length; i += 2) {
            if (i + 1 < placeholders.length) {
                result = result.replace(placeholders[i], placeholders[i + 1]);
            }
        }
        return result;
    }

    public String getSimpleMessage(String path, String... placeholders) {
        MessageData data = messagesData.get(path);

        if (data == null || data.getRawChat().isEmpty()){
            return MessageHelper.colored("&8> &4Brak wiadomosci w messages.yml: " + path);
        }

        String message = data.getRawChat().get(0);
        return MessageHelper.colored(replace(message, placeholders));
    }

    public FileConfiguration getConfig() {
        return config;
    }
}
