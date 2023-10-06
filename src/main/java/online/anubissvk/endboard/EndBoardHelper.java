package online.anubissvk.endboard;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class EndBoardHelper {
    private static HashMap<UUID, EndBoardHelper> players = new HashMap<>();

    private Player player;

    private Scoreboard scoreboard;

    private Objective sidebar;

    public static EndBoardHelper create(Player player, boolean healthName, boolean healthTab) {
        return new EndBoardHelper(player, healthName, healthTab);
    }

    public static EndBoardHelper get(Player player) {
        return players.get(player.getUniqueId());
    }

    public static void remove(Player player) {
        players.remove(player.getUniqueId());
    }

    private EndBoardHelper(Player player, boolean healthName, boolean healthTab) {
        this.player = player;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.sidebar = this.scoreboard.registerNewObjective("sidebar", "dummy");
        this.sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
        player.setScoreboard(this.scoreboard);
        for (int i = 1; i <= 15; i++) {
            Team team = this.scoreboard.registerNewTeam("SLOT_" + i);
            team.addEntry(genEntry(i));
        }
        if (healthName) {
            Objective hName = this.scoreboard.registerNewObjective("hname", "health");
            hName.setDisplaySlot(DisplaySlot.BELOW_NAME);
            hName.setDisplayName(ChatColor.RED + "❤");
        }
        if (healthTab) {
            Objective hTab = this.scoreboard.registerNewObjective("htab", "health");
            hTab.setDisplaySlot(DisplaySlot.PLAYER_LIST);
        }
        players.put(player.getUniqueId(), this);
    }

    public void setTitle(String title) {
        title = PlaceholderAPI.setPlaceholders(this.player, title);
        if (title.length() > 32)
            title = title.substring(0, 32);
        if (!this.sidebar.getDisplayName().equals(title))
            this.sidebar.setDisplayName(title);
    }

    public void setSlot(int slot, String text) {
        Team team = this.scoreboard.getTeam("SLOT_" + slot);
        String entry = genEntry(slot);
        if (!this.scoreboard.getEntries().contains(entry))
            this.sidebar.getScore(entry).setScore(slot);
        text = PlaceholderAPI.setPlaceholders(this.player, text);
        String pre = getFirstSplit(text);
        String suf = getFirstSplit(ChatColor.getLastColors(pre) + getSecondSplit(text));
        if (!team.getPrefix().equals(pre))
            team.setPrefix(pre);
        if (!team.getSuffix().equals(suf))
            team.setSuffix(suf);
    }

    public void removeSlot(int slot) {
        String entry = genEntry(slot);
        if (this.scoreboard.getEntries().contains(entry))
            this.scoreboard.resetScores(entry);
    }

    public void setSlotsFromList(List<String> list) {
        int slot = list.size();
        if (slot < 15)
            for (int i = slot + 1; i <= 15; i++)
                removeSlot(i);
        for (String line : list) {
            setSlot(slot, line);
            slot--;
        }
    }
    private String genEntry(int slot) {
        return ChatColor.values()[slot].toString();
    }

    private String getFirstSplit(String s) {
        return (s.length() > 16) ? s.substring(0, 16) : s;
    }

    private String getSecondSplit(String s) {
        if (s.length() > 32)
            s = s.substring(0, 32);
        return (s.length() > 16) ? s.substring(16, s.length()) : "";
    }
}
