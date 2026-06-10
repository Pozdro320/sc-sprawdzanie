package pl.servercreators.data;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import pl.servercreators.helpers.MessageHelper;

@Getter
public class MessageData {
    private final List<String> rawChat;
    private final String rawActionBar;
    private final String rawTitle;
    private final String rawSubtitle;
    private final String sound;
    private final List<String> rawBroadcast;

    private final List<String> coloredChat;
    private final String coloredActionBar;
    private final String coloredTitle;
    private final String coloredSubtitle;
    private final List<String> coloredBroadcast;

    public MessageData(List<String> chat, String actionBar, String title, String subtitle, String sound, List<String> broadcast) {
        this.rawChat = chat;
        this.rawActionBar = actionBar;
        this.rawTitle = title;
        this.rawSubtitle = subtitle;
        this.sound = sound;
        this.rawBroadcast = broadcast;

        this.coloredChat = chat.stream().map(MessageHelper::colored).collect(Collectors.toList());
        this.coloredActionBar = MessageHelper.colored(actionBar);
        this.coloredTitle = MessageHelper.colored(title);
        this.coloredSubtitle = MessageHelper.colored(subtitle);
        this.coloredBroadcast = broadcast.stream().map(MessageHelper::colored).collect(Collectors.toList());
    }
}