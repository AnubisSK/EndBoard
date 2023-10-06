package online.anubissvk.endboard;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class EndBoard extends JavaPlugin implements Listener{
    private HashMap<String, EndBoardWorld> scoreWorlds;
    private boolean healthName;
    private boolean healthTab;

    public void onEnable() {
        saveDefaultConfig();
        loadObjects();
        createAll();
        Bukkit.getPluginCommand("endboard").setExecutor((CommandExecutor)this);
        Bukkit.getPluginManager().registerEvents(this, (Plugin)this);
        long ticks = getConfig().getLong("Options.update-ticks");
        (new BukkitRunnable() {
            public void run() {
                EndBoard.this.updateAll();
            }
        }).runTaskTimer((Plugin)this, ticks, ticks);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            reloadConfig();
            loadObjects();
            createAll();
            sender.sendMessage(ChatColor.GREEN + "ᴛʜᴇ ᴄᴏɴғɪɢᴜʀᴀᴛɪᴏɴ ʜᴀs ʙᴇᴇɴ ʀᴇʟᴏᴀᴅᴇᴅ!");
        } else {
            sender.sendMessage(ChatColor.GREEN + "ᴛʜᴇ ᴜsᴇ" + ChatColor.WHITE + "/" + label + " reload");
        }
        return true;
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        create(event.getPlayer());
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {
        EndBoardHelper.remove(event.getPlayer());
    }

    @EventHandler
    private void onChangeWorld(PlayerChangedWorldEvent event) {
        update(event.getPlayer());
    }

    private void loadObjects() {
        this.scoreWorlds = new HashMap<>();
        for (String world : getConfig().getConfigurationSection("Worlds").getKeys(false)) {
            String title = getConfig().getString("Worlds." + world + ".title");
            List<String> lines = getConfig().getStringList("Worlds." + world + ".lines");
            this.scoreWorlds.put(world, new EndBoardWorld(title, lines));
        }
        this.healthName = getConfig().getBoolean("Options.health-name");
        this.healthTab = getConfig().getBoolean("Options.health-tab");
    }

    private void create(Player player) {
        EndBoardHelper.create(player, this.healthName, this.healthTab);
        update(player);
    }

    private void createAll() {
        for (Player player : Bukkit.getOnlinePlayers())
            create(player);
    }

    private void update(Player player) {
        EndBoardHelper helper = EndBoardHelper.get(player);
        if (this.scoreWorlds.containsKey(player.getWorld().getName())) {
            EndBoardWorld sw = this.scoreWorlds.get(player.getWorld().getName());
            helper.setTitle(sw.getTitle());
            helper.setSlotsFromList(sw.getLines());
        } else {
            helper.setSlotsFromList(Collections.emptyList());
        }
    }

    private void updateAll() {
        for (Player player : Bukkit.getOnlinePlayers())
            update(player);
    }

}
