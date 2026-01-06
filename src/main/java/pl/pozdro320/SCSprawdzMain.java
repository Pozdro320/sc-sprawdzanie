package pl.pozdro320;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.permission.Permission;
import pl.pozdro320.commands.PrzyznajeSieCommand;
import pl.pozdro320.commands.SprawdzCommand;
import pl.pozdro320.commands.SprawdzarkaCommand;
import pl.pozdro320.gui.HistorieGUI;
import pl.pozdro320.gui.SprawdzGUI;
import pl.pozdro320.helpers.BansHelper;
import pl.pozdro320.listeners.BlockCommandListener;
import pl.pozdro320.listeners.ChatListener;
import pl.pozdro320.listeners.HistorieGuiListener;
import pl.pozdro320.listeners.OnJoinListener;
import pl.pozdro320.listeners.QuitPlayerListener;
import pl.pozdro320.listeners.SprawdzGuiListener;
import pl.pozdro320.managers.ConfigManager;
import pl.pozdro320.managers.HistoryManager;
import pl.pozdro320.task.SprawdzaniGraczeTask;
import pl.pozdro320.utils.GithubUpdater;
import pl.pozdro320.utils.RegistryUtil;
import pl.pozdro320.utils.SemanticVersion;

public class SCSprawdzMain extends JavaPlugin {
    
    private HistoryManager historyManager;
    private Location sprawdzLocation;
    private Location spawnLocation;
    private ConfigManager configManager;
    private BansHelper bansHelper;
    public static Map<UUID, UUID> sprawdzaniGracze = new ConcurrentHashMap<>();

    private static Permission perms = null;

    @Override
    public void onEnable() {
        if (!setupPermissions()) {
            getLogger().severe("Nie znaleziono Vault! Wylaczanie pluginu...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        SemanticVersion currentVersion = new SemanticVersion(this.getDescription().getVersion());
        GithubUpdater updater = new GithubUpdater("pozdro320", "sc-sprawdzanie");

        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            if (updater.hasUpdate(currentVersion)) {
                getLogger().warning("");
                getLogger().warning("DOSTEPNA JEST NOWA WERSJA PLUGINU!");
                getLogger().warning("");
            } else {
                getLogger().info("Uzywasz najnowszej wersji pluginu.");
            }
        });
        
        saveDefaultConfig();
        this.configManager = new ConfigManager(this);
        this.bansHelper = new BansHelper(this);
        this.historyManager = new HistoryManager(this);

        this.sprawdzLocation = configManager.getLocations().load("sprawdzarka");
        this.spawnLocation = configManager.getLocations().load("spawn");
        
        SprawdzGUI.init(this);
        HistorieGUI.init(this);

        new SprawdzaniGraczeTask(this);

        new RegistryUtil(this)
            .registerListeners(
                new SprawdzGuiListener(this),
                new ChatListener(this),
                new QuitPlayerListener(this),
                new BlockCommandListener(this),
                new HistorieGuiListener(this),
                new OnJoinListener(this)
            )
            .registerCommands(
                new PrzyznajeSieCommand("przyznajesie", this),
                new SprawdzCommand("sprawdz", this),
                new SprawdzarkaCommand("sprawdzarka", this)
            );

        getLogger().info("Plugin zostal wlaczony!");
    }

    @Override
    public void onDisable() {
        for (UUID uuid : sprawdzaniGracze.keySet()) {
                Player p = Bukkit.getPlayer(uuid);
                if (p != null && spawnLocation != null) {
                    p.teleport(spawnLocation);
                    p.sendMessage("§cPlugin został wyłączony. Sprawdzanie przerwane.");
                }
            }
            
        sprawdzaniGracze.clear();
        getLogger().info("Plugin zostal wylaczony!");
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    public BansHelper getBansHelper() {
        return bansHelper;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public Location getSprawdzLocation() {
        return sprawdzLocation;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public void setSprawdzLocation(Location loc) {
        this.sprawdzLocation = loc;
        configManager.getLocations().save("sprawdzarka", loc);
    }

    public void setSpawnLocation(Location loc) {
        this.spawnLocation = loc;
        configManager.getLocations().save("spawn", loc);
    }
    
    public void dodajSprawdzanego(Player sprawdzany, Player moderator) {
        sprawdzaniGracze.put(sprawdzany.getUniqueId(), moderator.getUniqueId());
    }

    public void usunSprawdzanego(Player sprawdzany) {
        sprawdzaniGracze.remove(sprawdzany.getUniqueId());
    }

    public boolean czySprawdzany(Player p) {
        return sprawdzaniGracze.containsKey(p.getUniqueId());
    }

    public Player getModerator(Player sprawdzany) {
        UUID modUUID = sprawdzaniGracze.get(sprawdzany.getUniqueId());
        return modUUID != null ? Bukkit.getPlayer(modUUID) : null;
    }
    
    public Player getSprawdzany(Player moderator) {
        for (Map.Entry<UUID, UUID> entry : sprawdzaniGracze.entrySet()) {
            if (entry.getValue().equals(moderator.getUniqueId())) {
                return Bukkit.getPlayer(entry.getKey());
            }
        }
        return null;
    }

    public void reloadPlugin() {
        this.configManager.loadConfig();
        
        SprawdzGUI.init(this);
        HistorieGUI.init(this);
    }

    private boolean setupPermissions() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) return false;
        org.bukkit.plugin.RegisteredServiceProvider<Permission> rsp =
            getServer().getServicesManager().getRegistration(Permission.class);
        if (rsp == null) return false;
        perms = rsp.getProvider();
        return perms != null;
    }

    public static Permission getPerms() {
        return perms;
    }
}