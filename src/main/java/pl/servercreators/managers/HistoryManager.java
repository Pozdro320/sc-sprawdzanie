package pl.servercreators.managers;

import org.bukkit.configuration.file.YamlConfiguration;

import pl.servercreators.SCSprawdzMain;
import pl.servercreators.models.HistoryEntry;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class HistoryManager {
    
    private final File historyFolder;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public HistoryManager(SCSprawdzMain plugin) {
        this.historyFolder = new File(plugin.getDataFolder(), "history");
        if (!this.historyFolder.exists()) {
            this.historyFolder.mkdirs();
        }
    }

    public void log(String playerName, String action, String moderatorName) {
        File playerFile = new File(historyFolder, playerName + ".yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
        
        String time = LocalDateTime.now().format(formatter);
        
        Map<String, Object> entry = Map.of(
            "date", time,
            "action", action,
            "moderator", moderatorName
        );

        List<?> rawLogs = config.getList("entries");
        List<Map<Object, Object>> logs = new ArrayList<>();

        if (rawLogs != null) {
            for (Object obj : rawLogs) {
                if (obj instanceof Map) {
                    logs.add((Map<Object, Object>) obj);
                }
            }
        }

        logs.add(new java.util.HashMap<>(entry));
        
        config.set("entries", logs);
        config.set("last_update", time);

        try {
            config.save(playerFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<HistoryEntry> getHistory(String playerName) {
        File playerFile = new File(historyFolder, playerName + ".yml");
        if (!playerFile.exists()) return Collections.emptyList();

        YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
        List<?> rawEntries = config.getList("entries");
        
        if (rawEntries == null) return Collections.emptyList();

        List<HistoryEntry> entries = new ArrayList<>();
        
        for (Object obj : rawEntries) {
            if (obj instanceof Map<?, ?> map) {
                String date = String.valueOf(map.get("date"));
                String action = String.valueOf(map.get("action"));
                String moderator = String.valueOf(map.get("moderator"));
                
                entries.add(new HistoryEntry(date, action, moderator));
            }
        }
        
        Collections.reverse(entries);
        return entries;
    }
}