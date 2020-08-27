package gq.islanddeath;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SetBaseUtils {
    private Main instance;

    public SetBaseUtils(Main instance) {
        this.instance = instance;
    }

    public void setHome(Player player) {
        this.instance.bases.set("Homes." + player.getScoreboard().getPlayerTeam(player).getName() + ".X", Double.valueOf(player.getLocation().getX()));
        this.instance.bases.set("Homes." + player.getScoreboard().getPlayerTeam(player).getName() + ".Y", Double.valueOf(player.getLocation().getY()));
        this.instance.bases.set("Homes." + player.getScoreboard().getPlayerTeam(player).getName() + ".Z", Double.valueOf(player.getLocation().getZ()));
        this.instance.bases.set("Homes." + player.getScoreboard().getPlayerTeam(player).getName() + ".Yaw", Float.valueOf(player.getLocation().getYaw()));
        this.instance.bases.set("Homes." + player.getScoreboard().getPlayerTeam(player).getName() + ".Pitch", Float.valueOf(player.getLocation().getPitch()));
        this.instance.bases.set("Homes." + player.getScoreboard().getPlayerTeam(player).getName() + ".World", player.getLocation().getWorld().getName());
        this.instance.saveBasesFile();
    }

    public void sendHome(Player player) {
        player.teleport(getHomeLocation(player));
    }

    public Location getHomeLocation(Player player) {
        return new Location(
                Bukkit.getWorld(this.instance.bases.getString("Homes." + player.getScoreboard().getPlayerTeam(player).getName() + ".World")), this.instance.bases
                .getDouble("Homes." + player.getScoreboard().getPlayerTeam(player).getName() + ".X"), this.instance.bases
                .getDouble("Homes." + player.getScoreboard().getPlayerTeam(player).getName() + ".Y"), this.instance.bases
                .getDouble("Homes." + player.getScoreboard().getPlayerTeam(player).getName() + ".Z"),
                (float)this.instance.bases.getLong("Homes." + player.getScoreboard().getPlayerTeam(player).getName() + ".Yaw"),
                (float)this.instance.bases.getLong("Homes." + player.getScoreboard().getPlayerTeam(player).getName() + ".Pitch"));
    }

    public boolean homeIsNull(Player player) {
        return (this.instance.bases.getString("Homes." + player.getScoreboard().getPlayerTeam(player).getName()) == null);
    }
}
