package pl.pozdro320.managers;

import org.bukkit.configuration.file.YamlConfiguration;
import pl.pozdro320.SCSprawdzMain;
import pl.pozdro320.models.HistoryEntry;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistoryManager {
    private final File historyFolder;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public HistoryManager(SCSprawdzMain plugin) {
        this.historyFolder = new File(plugin.getDataFolder(), "history");
        if (!historyFolder.exists()) historyFolder.mkdirs();
    }

    public void log(String playerName, String action, String moderatorName) {
        File playerFile = new File(historyFolder, playerName + ".yml");
        if (!playerFile.exists()) {
            try { playerFile.createNewFile(); } catch (IOException e) { return; }
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
        String time = LocalDateTime.now().format(formatter);
        
        List<String> logs = config.getStringList("logs");
        String entry = String.format("[%s] Akcja: %s | Moderator: %s", time, action, moderatorName);
        logs.add(entry);
        
        config.set("logs", logs);
        config.set("last_update", time);
        try { config.save(playerFile); } catch (IOException e) { e.printStackTrace(); }
    }

    public List<HistoryEntry> getHistory(String playerName) {
        File playerFile = new File(historyFolder, playerName + ".yml");
        if (!playerFile.exists()) return Collections.emptyList();

        YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
        List<String> rawLogs = config.getStringList("logs");
        List<HistoryEntry> entries = new ArrayList<>();

        for (String line : rawLogs) {
            try {
                String date = line.substring(line.indexOf("[") + 1, line.indexOf("]"));
                String action = line.substring(line.indexOf("Akcja: ") + 7, line.indexOf(" | Moderator:"));
                String moderator = line.substring(line.indexOf("Moderator: ") + 11);
                
                entries.add(new HistoryEntry(date, action, moderator));
            } catch (Exception e) {
                continue;
            }
        }
        
        Collections.reverse(entries);
        return entries;
    }
}