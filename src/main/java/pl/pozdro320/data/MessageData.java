package pl.pozdro320.data;

import java.util.List;

public class MessageData {
    private final List<String> chat;
    private final String actionBar;
    private final String title;
    private final String subtitle;
    private final String sound;
    private final List<String> broadcast;

    public MessageData(List<String> chat, String actionBar, String title, String subtitle, String sound, List<String> broadcast) {
        this.chat = chat;
        this.actionBar = actionBar;
        this.title = title;
        this.subtitle = subtitle;
        this.sound = sound;
        this.broadcast = broadcast;
    }

    public List<String> getChat() { return chat; }
    public String getActionBar() { return actionBar; }
    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public String getSound() { return sound; }
    public List<String> getBroadcast() { return broadcast; }
}