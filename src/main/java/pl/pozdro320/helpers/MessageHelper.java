package pl.pozdro320.helpers;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MessageHelper {
    
    private static final Pattern HEX_PATTERN = Pattern.compile("#[a-fA-F0-9]{6}");
    private final String message;

    private MessageHelper(final String message) {
        this.message = message;
    }

    public static MessageHelper build(final String message) {
        return new MessageHelper(message);
    }

    public static String colored(String message) {
        if (message == null || message.isEmpty()) return "";
        
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuilder buffer = new StringBuilder();
        while (matcher.find()) {
            matcher.appendReplacement(buffer, ChatColor.of(matcher.group()).toString());
        }
        matcher.appendTail(buffer);
        
        return ChatColor.translateAlternateColorCodes('&', buffer.toString()).replace(">>", "»");
    }

    public static List<String> colored(final List<String> texts) {
        return texts.stream().map(MessageHelper::colored).collect(Collectors.toList());
    }

    public static void sendBar(Player player, String text) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(colored(text)));
    }

    public static void sendTitle(Player p, String title, String subttitle) {
        sendTitle(p, title, subttitle, 10, 40, 10);
    }

    public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        if (title == null) title = "";
        if (subtitle == null) subtitle = "";
        player.sendTitle(colored(title), colored(subtitle), fadeIn, stay, fadeOut);
    }

    public void send(final Player player) {
        player.sendMessage(colored(message));
    }

    public void broadcast() {
        Bukkit.getOnlinePlayers().forEach(this::send);
    }

    public void send(CommandSender sender) {
        sender.sendMessage(colored(message));
    }
}
