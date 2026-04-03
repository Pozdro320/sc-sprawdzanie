package pl.servercreators;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import net.milkbowl.vault.permission.Permission;
import pl.servercreators.commands.CheckCommand;
import pl.servercreators.commands.CheckerCommand;
import pl.servercreators.commands.ConfessesCommand;
import pl.servercreators.data.GuiItemData;
import pl.servercreators.gui.CheckGUI;
import pl.servercreators.gui.HistoryGUI;
import pl.servercreators.helpers.BansHelper;
import pl.servercreators.helpers.GroupCheckHelper;
import pl.servercreators.helpers.GuiHelper;
import pl.servercreators.listeners.BlockCommandListener;
import pl.servercreators.listeners.ChatListener;
import pl.servercreators.listeners.CheckedGuiListener;
import pl.servercreators.listeners.HistoryGuiListener;
import pl.servercreators.listeners.OnJoinListener;
import pl.servercreators.listeners.QuitPlayerListener;
import pl.servercreators.managers.ConfigManager;
import pl.servercreators.managers.HistoryManager;
import pl.servercreators.task.CheckedPlayersTask;
import pl.servercreators.utils.RegistryUtil;
import pl.servercreators.utils.Updater.GithubUpdater;
import pl.servercreators.utils.Updater.SemanticVersion;

@Getter
public class SCSprawdzMain extends JavaPlugin {

    private final Map<UUID, UUID> checkedPlayers = new ConcurrentHashMap<>();
    
    private ConfigManager configManager;
    private HistoryManager historyManager;
    private BansHelper bansHelper;
    private GuiHelper guiHelper;
    private GuiItemData guiItemData;
    private GroupCheckHelper groupCheckHelper;
    
    private Location checkLocation;
    private Location spawnLocation;

    private CheckGUI checkGUI;
    private HistoryGUI historyGUI;

    private CheckedPlayersTask checkedPlayersTask;
    private Permission perms = null;

    private boolean updateAvailable = false;

    @Override
    public void onEnable() {
        if (!setupPermissions()) {
            getLogger().severe("Nie znaleziono Vault! Wylaczanie pluginu...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        this.configManager = new ConfigManager(this);
        this.historyManager = new HistoryManager(this);
        this.bansHelper = new BansHelper(this, configManager);
        this.guiHelper = new GuiHelper(configManager);

        this.checkLocation = configManager.getLocations().load("checker");
        this.spawnLocation = configManager.getLocations().load("spawn");

        this.groupCheckHelper = new GroupCheckHelper(this, perms, configManager);
        this.historyGUI = new HistoryGUI(this, historyManager, configManager, guiHelper);
        this.checkGUI = new CheckGUI(this, guiHelper, configManager, guiItemData);

        this.checkedPlayersTask = new CheckedPlayersTask(this, configManager);
        this.checkedPlayersTask.start();
        
        new RegistryUtil(this)
            .registerListeners(
                new CheckedGuiListener(this, historyGUI, checkGUI, configManager),
                new ChatListener(this, configManager),
                new QuitPlayerListener(this, configManager),
                new BlockCommandListener(this, configManager),
                new HistoryGuiListener(this, historyGUI, checkGUI, configManager),
                new OnJoinListener(this)
            )
            .registerCommands(
                new ConfessesCommand("przyznajesie", this, configManager),
                new CheckCommand("sprawdz", checkGUI, groupCheckHelper, configManager),
                new CheckerCommand("sprawdzarka", this, configManager)
            );

        String updateUrl = "https://gist.githubusercontent.com/Pozdro320/2c46fe72d5a9f822c55f087e00cc47a5/raw/gistfile1.txt";
        GithubUpdater updater = new GithubUpdater(updateUrl);

        checkUpdate(updater);
    }

    @Override
    public void onDisable() {
        for (UUID uuid : checkedPlayers.keySet()) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null && spawnLocation != null) {
                p.teleport(spawnLocation);
                p.sendMessage("§cPlugin został wyłączony. Sprawdzanie przerwane.");
            }
        }

        if (this.checkedPlayersTask != null) {
            this.checkedPlayersTask.stop();
        }

        checkedPlayers.clear();
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

    public Location getCheckLocation() {
        return checkLocation;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public void setCheckedLocation(Location loc) {
        this.checkLocation = loc;
        configManager.getLocations().save("checker", loc);
    }

    public void setSpawnLocation(Location loc) {
        this.spawnLocation = loc;
        configManager.getLocations().save("spawn", loc);
    }
    
    public void addChecked(Player checker, Player moderator) {
        checkedPlayers.put(checker.getUniqueId(), moderator.getUniqueId());
    }

    public void removeChecked(Player checker) {
        checkedPlayers.remove(checker.getUniqueId());
    }

    public boolean isChecked(Player p) {
        return checkedPlayers.containsKey(p.getUniqueId());
    }

    public Map<UUID, UUID> getCheckedPlayers() {
        return this.checkedPlayers;
    }

    public Player getModerator(Player checker) {
        UUID modUUID = checkedPlayers.get(checker.getUniqueId());
        return modUUID != null ? Bukkit.getPlayer(modUUID) : null;
    }
    
    public Player getChecked(Player moderator) {
        for (Map.Entry<UUID, UUID> entry : checkedPlayers.entrySet()) {
            if (entry.getValue().equals(moderator.getUniqueId())) {
                return Bukkit.getPlayer(entry.getKey());
            }
        }
        return null;
    }

    public void reloadPlugin() {
        configManager.reload();

        this.checkLocation = configManager.getLocations().load("checker");
        this.spawnLocation = configManager.getLocations().load("spawn");

        this.historyGUI = new HistoryGUI(this, historyManager, configManager, guiHelper);
        this.checkGUI = new CheckGUI(this, guiHelper, configManager, guiItemData);
    }

    private boolean setupPermissions() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) return false;
        org.bukkit.plugin.RegisteredServiceProvider<Permission> rsp =
            getServer().getServicesManager().getRegistration(Permission.class);
        if (rsp == null) return false;
        perms = rsp.getProvider();
        return perms != null;
    }

    public Permission getPerms() {
        return perms;
    }
    
    private void checkUpdate(GithubUpdater updater) {
        SemanticVersion currentVersion = new SemanticVersion(this.getDescription().getVersion());
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            if (updater.hasUpdate(currentVersion)) {
                this.updateAvailable = true;
                getLogger().warning("");
                getLogger().warning("DOSTEPNA JEST NOWA WERSJA PLUGINU!");
                getLogger().warning("");
            } else {
                getLogger().info("Uzywasz najnowszej wersji pluginu!.");
            }
        });
    }
}