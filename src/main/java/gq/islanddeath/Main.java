package gq.islanddeath;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.tags.CustomItemTagContainer;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public final class Main extends JavaPlugin implements Listener {

    private String scoreboard = "TeamChat";
    private boolean color = true;
    private final Map<String, Long> delays = new ConcurrentHashMap<>();
    private File file = new File(getDataFolder(), "Bases.yml");
    YamlConfiguration bases = YamlConfiguration.loadConfiguration(this.file);

    private SetBaseUtils utils = new SetBaseUtils(this);

    @EventHandler
    public void Join(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (!e.getPlayer().hasPlayedBefore()) {
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&l✸ &7Bienvenido a &d&oIslandDeath T2&7. Ahora se le asignara su reino."));
        p.getScoreboard().getObjective(this.scoreboard).getScore((OfflinePlayer) p).setScore(1);
        p.setGameMode(GameMode.SURVIVAL);
        p.setHealth(20);
        p.setFoodLevel(20);
        e.setJoinMessage(null);
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&8⚜ &d" + e.getPlayer().getDisplayName() + " &fse a unido por primera vez"));
            player.playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 5.0F, 1.0F);
        }
        } else {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&l✸ &7Bienvenido a &d&oIslandDeath T2&7. &8(Ver 1.4.2)"));
            p.getScoreboard().getObjective(this.scoreboard).getScore((OfflinePlayer) p).setScore(1);
            e.setJoinMessage(null);
            p.setGameMode(GameMode.SURVIVAL);
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&8⚜ &e" + e.getPlayer().getDisplayName() + " &fse a unido."));
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 10.0F, 2.0F);
            }
        }
    }

    @EventHandler
    public void playerChatEvent(PlayerChatEvent chat) {
        String getPrefix = "";
        Player player = chat.getPlayer();
        Team team = player.getScoreboard().getPlayerTeam((OfflinePlayer) player);
        Set<Player> recipients = chat.getRecipients();
        Iterator<Player> iterator = recipients.iterator();
        if (team != null) {
            if (this.color) {
                getPrefix = team.getPrefix();
            } else {
                getPrefix = "";
            }
            if (chat.getMessage().startsWith("!")) {
                player.getScoreboard().getObjective(this.scoreboard).getScore((OfflinePlayer) player).setScore(0);
                String newMessage = chat.getMessage().replace("!", "");
                chat.setMessage(newMessage);
                chat.setFormat(ChatColor.translateAlternateColorCodes('&', "&cⒼ &8[&7" + player.getScoreboard().getPlayerTeam(player).getDisplayName() + "&8] &f" + player.getDisplayName() + " &8» &f" + chat.getMessage()));
            }
            if (player.getScoreboard().getObjective(this.scoreboard).getScore((OfflinePlayer) player).getScore() == 1) {
                chat.setFormat(ChatColor.translateAlternateColorCodes('&', "&eⒺ &f" + player.getDisplayName() + " &8» &f" + chat.getMessage()));
                while (iterator.hasNext()) {
                    if (team.hasPlayer((OfflinePlayer) iterator.next()))
                        continue;
                    iterator.remove();
                }
            } else {
                chat.setFormat(ChatColor.translateAlternateColorCodes('&', "&cⒼ &8[&7" + player.getScoreboard().getPlayerTeam(player).getDisplayName() + "&8] &f" + player.getDisplayName() + " &8» &f" + chat.getMessage()));
            }
        }
        if (player.getScoreboard().getObjective(this.scoreboard).getScore((OfflinePlayer) player).getScore() == 0) {
            if (chat.getMessage().startsWith("!")) {
                player.getScoreboard().getObjective(this.scoreboard).getScore((OfflinePlayer) player).setScore(0);
            } else {
                player.getScoreboard().getObjective(this.scoreboard).getScore((OfflinePlayer) player).setScore(1);
            }
        }
    }

    void sendPlayerHome(Player player) {
        this.utils.sendHome(player);
        if (true)
            player.playSound(this.utils.getHomeLocation(player), Sound.BLOCK_ANVIL_BREAK, 1.0F, 1.0F);
    }
    void sendPlayerDeath(Player player) {
        this.utils.sendHome(player);
    }
    void setPlayerHome(Player player) {
        this.utils.setHome(player);
        if (true) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8♖ &aTu base se a guardado."));
        }
    }

    public void saveBasesFile() {
        try {
            this.bases.save(this.file);
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "No se puedo guardar el archivo de Bases.");
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        if (command.getName().equals("islanddeath")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&l&6&l✸ &fPlugin: &d&oIslandDeath T2&f, creado por RemineHD y Penayer &8(Ver 1.4.2)"));
        }
        if (command.getName().equals("guardarbase")) {
            if (sender instanceof org.bukkit.command.ConsoleCommandSender) {
                getLogger().log(Level.WARNING, "Solo los jugadores pueden guardar su base");
            } else if (sender instanceof Player) {
                setPlayerHome(player);
            } else {
                sender.sendMessage(ChatColor.RED + "No se a podido guardar tu base, contacta con RemineHD o Penayer");
            }
        }
            if (command.getName().equals("base")) {
                if (this.utils.homeIsNull(player)) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l✘ &fPara ir a tu base primero tienes que guardarla con: &e/guardarbase"));
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 10.0F, 2.0F);
                } else if (player.getLevel() > 4) {
                    player.setLevel(player.getLevel() - 5);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b✈ &fCogiendo el vuelo, tardaras 10 segundos."));
                    this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                        @Override
                        public void run() {
                            player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&b✈ &FViajando... Tardaras &39 &fsegundos. &b✈"));
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 5.0F, 2.0F);
                        }
                    }, 20 * 1);
                    this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                        @Override
                        public void run() {
                            player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&b✈ &FViajando... Tardaras &38 &fsegundos. &b✈"));
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 5.0F, 2.0F);
                        }
                    }, 20 * 2);
                    this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                        @Override
                        public void run() {
                            player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&b✈ &FViajando... Tardaras &37 &fsegundos. &b✈"));
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 5.0F, 2.0F);
                        }
                    }, 20 * 3);
                    this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                        @Override
                        public void run() {
                            player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&b✈ &FViajando... Tardaras &36 &fsegundos. &b✈"));
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 5.0F, 2.0F);
                        }
                    }, 20 * 4);
                    this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                        @Override
                        public void run() {
                            player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&b✈ &FViajando... Tardaras &35 &fsegundos. &b✈"));
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 5.0F, 2.0F);
                        }
                    }, 20 * 5);
                    this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                        @Override
                        public void run() {
                            player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&b✈ &FViajando... Tardaras &34 &fsegundos. &b✈"));
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 5.0F, 2.0F);
                        }
                    }, 20 * 6);
                    this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                        @Override
                        public void run() {
                            player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&b✈ &FViajando... Tardaras &33 &fsegundos. &b✈"));
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 5.0F, 2.0F);
                        }
                    }, 20 * 7);
                    this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                        @Override
                        public void run() {
                            player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&b✈ &FViajando... Tardaras &32 &fsegundos. &b✈"));
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 5.0F, 2.0F);
                        }
                    }, 20 * 8);
                    this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                        @Override
                        public void run() {
                            player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&b✈ &FViajando... Tardaras &31 &fsegundos. &b✈"));
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 5.0F, 2.0F);
                        }
                    }, 20 * 9);
                    this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                        @Override
                        public void run() {
                            player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&b✈ &FTu vuelo a aterrizado. &b✈"));
                            sendPlayerHome(player);
                        }
                    }, 20 * 10);
                } else {
                    player.sendMessage(ChatColor.RED + "✈ No tienes experiencia suficiente para pagar el avión.");
                }
            }
        return false;
    }

    @EventHandler
    public void Muerte(PlayerDeathEvent e) {
        if (e.getDeathMessage().contains("wither")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                e.setDeathMessage(null);
                player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&4☠ &e" + e.getEntity().getPlayer().getDisplayName() + " &ffue eliminado por un &0Whiter&4☠"));
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 5.0F, 2.0F);
                Player player2 = e.getEntity().getPlayer();
                    }
                } else if (e.getDeathMessage().contains("dragon")) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    e.setDeathMessage(null);
                    player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&4☠ &e" + e.getEntity().getPlayer().getDisplayName() + " &ffue eliminado por &dLa Dragona &4☠"));
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 5.0F, 2.0F);
                    }
                } else if (e.getDeathMessage().contains("was stung to death")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        e.setDeathMessage(null);
                        player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&4☠ &e" + e.getEntity().getPlayer().getDisplayName() + " &ffue eliminado por una &dabeja &4☠"));
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 5.0F, 2.0F);
                    }
                } else if (e.getDeathMessage().contains("Cave Spider")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        e.setDeathMessage(null);
                        player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&4☠ &e" + e.getEntity().getPlayer().getDisplayName() + " &ffue eliminado por una &cAraña de Cueva &4☠"));
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 5.0F, 2.0F);
                    }
                } else if (e.getDeathMessage().contains("Creeper")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        e.setDeathMessage(null);
                        player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&4☠ &e" + e.getEntity().getPlayer().getDisplayName() + " &frevento gracias a un &ACreeper &4☠"));
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 5.0F, 2.0F);
                    }
                } else if (e.getDeathMessage().contains("Drowned")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        e.setDeathMessage(null);
                        player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&4☠ &e" + e.getEntity().getPlayer().getDisplayName() + " &ffue eliminado por un &bAhogado &4☠"));
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 5.0F, 2.0F);
                    }
                } else if (e.getDeathMessage().contains("Guardian")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        e.setDeathMessage(null);
                        player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&4☠ &e" + e.getEntity().getPlayer().getDisplayName() + " &ffue eliminado por un &3Guardian &4☠"));
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 5.0F, 2.0F);
                    }
                } else if (e.getDeathMessage().contains("Enderman")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        e.setDeathMessage(null);
                        player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&4☠ &e" + e.getEntity().getPlayer().getDisplayName() + " &ffue eliminado por un &5Enderman &4☠"));
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 5.0F, 2.0F);
                    }
                } else if (e.getDeathMessage().contains("Endermite")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        e.setDeathMessage(null);
                        player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&4☠ &e" + e.getEntity().getPlayer().getDisplayName() + " &ffue eliminado por un &dEndermite &4☠"));
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 5.0F, 2.0F);
                    }
                } else if (e.getDeathMessage().contains("Blaze")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        e.setDeathMessage(null);
                        player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&4☠ &e" + e.getEntity().getPlayer().getDisplayName() + " &ffue eliminado por un &6Blaze &4☠"));
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 5.0F, 2.0F);
                    }
                } else if (e.getDeathMessage().contains("Ghast")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        e.setDeathMessage(null);
                        player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&4☠ &e" + e.getEntity().getPlayer().getDisplayName() + " &ffue eliminado por un &cGhast &4☠"));
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 5.0F, 2.0F);
                    }
                } else if (e.getDeathMessage().contains("Husk")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        e.setDeathMessage(null);
                        player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&4☠ &e" + e.getEntity().getPlayer().getDisplayName() + " &ffue eliminado por un &eZombie Momificado &4☠"));
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 5.0F, 2.0F);
                    }
                } else if (e.getDeathMessage().contains("Llama")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        e.setDeathMessage(null);
                        player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&4☠ &e" + e.getEntity().getPlayer().getDisplayName() + " &ffue eliminado por una Llama &4☠"));
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 5.0F, 2.0F);
                    }
                } else if (e.getDeathMessage().contains("Magma")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        e.setDeathMessage(null);
                        player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&4☠ &e" + e.getEntity().getPlayer().getDisplayName() + " &ffue eliminado por un &cMagma cube &4☠"));
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 5.0F, 2.0F);
                    }
                } else if (e.getDeathMessage().contains("Phantom")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        e.setDeathMessage(null);
                        player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&4☠ &e" + e.getEntity().getPlayer().getDisplayName() + " &ffue eliminado por un &dPhantom &4☠"));
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 5.0F, 2.0F);
                    }
                } else if (e.getDeathMessage().contains("Ravager")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        e.setDeathMessage(null);
                        player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&4☠ &e" + e.getEntity().getPlayer().getDisplayName() + " &ffue eliminado por un &4Ravager &4☠"));
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 5.0F, 2.0F);
                    }
                } else if (e.getDeathMessage().contains("Shulker")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        e.setDeathMessage(null);
                        player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&4☠ &e" + e.getEntity().getPlayer().getDisplayName() + " &ffue eliminado por un &dShulker &4☠"));
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 5.0F, 2.0F);
                    }
                } else if (e.getDeathMessage().contains("Silverfish")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        e.setDeathMessage(null);
                        player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&4☠ &e" + e.getEntity().getPlayer().getDisplayName() + " &ffue eliminado por un &7Silverfish &4☠"));
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 5.0F, 2.0F);
                    }
                } else if (e.getDeathMessage().contains("Skeleton")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        e.setDeathMessage(null);
                        player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&4☠ &e" + e.getEntity().getPlayer().getDisplayName() + " &ffue eliminado por un &7Esqueleto &4☠"));
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 5.0F, 2.0F);
                    }
                } else if (e.getDeathMessage().contains("Slime")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        e.setDeathMessage(null);
                        player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&4☠ &e" + e.getEntity().getPlayer().getDisplayName() + " &ffue eliminado por un &aSlime &4☠"));
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 5.0F, 2.0F);
                    }
                } else if (e.getDeathMessage().contains("Spider")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        e.setDeathMessage(null);
                        player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&4☠ &e" + e.getEntity().getPlayer().getDisplayName() + " &ffue eliminado por un &8Araña &4☠"));
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 5.0F, 2.0F);
                    }
                } else if (e.getDeathMessage().contains("Stray")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        e.setDeathMessage(null);
                        player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&4☠ &e" + e.getEntity().getPlayer().getDisplayName() + " &ffue eliminado por un &8Stray &4☠"));
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 5.0F, 2.0F);
                    }
                } else if (e.getDeathMessage().contains("Vex")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        e.setDeathMessage(null);
                        player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&4☠ &e" + e.getEntity().getPlayer().getDisplayName() + " &ffue eliminado por un &dVex &4☠"));
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 5.0F, 2.0F);
                    }
                } else if (e.getDeathMessage().contains("Vindicator")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        e.setDeathMessage(null);
                        player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&4☠ &e" + e.getEntity().getPlayer().getDisplayName() + " &ffue eliminado por un &dVindicator &4☠"));
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 5.0F, 2.0F);
                    }
                } else if (e.getDeathMessage().contains("Witch")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        e.setDeathMessage(null);
                        player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&4☠ &e" + e.getEntity().getPlayer().getDisplayName() + " &ffue eliminado por una &dBruja &4☠"));
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 5.0F, 2.0F);
                    }
                } else if (e.getDeathMessage().contains("Whiter")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        e.setDeathMessage(null);
                        player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&4☠ &e" + e.getEntity().getPlayer().getDisplayName() + " &ffue eliminado por un &0Whiter &4☠"));
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 5.0F, 2.0F);
                    }
                } else if (e.getDeathMessage().contains("Whiter Skeleton")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        e.setDeathMessage(null);
                        player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&4☠ &e" + e.getEntity().getPlayer().getDisplayName() + " &ffue eliminado por un &0Esqueleto Whiter &4☠"));
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 5.0F, 2.0F);
                    }
                } else if (e.getDeathMessage().contains("Wolf")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        e.setDeathMessage(null);
                        player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&4☠ &e" + e.getEntity().getPlayer().getDisplayName() + " &ffue eliminado por un &8Wolf &4☠"));
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 5.0F, 2.0F);
                    }
                } else if (e.getDeathMessage().contains("Zombie")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        e.setDeathMessage(null);
                        player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&4☠ &e" + e.getEntity().getPlayer().getDisplayName() + " &ffue eliminado por un &aZombie &4☠"));
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 5.0F, 2.0F);
                    }
                } else if (e.getDeathMessage().contains("Zombie Pigman")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        e.setDeathMessage(null);
                        player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&4☠ &e" + e.getEntity().getPlayer().getDisplayName() + " &ffue eliminado por un &aZombie Pigman &4☠"));
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 5.0F, 2.0F);
                    }
                } else if (e.getDeathMessage().contains("Zombie Villager")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        e.setDeathMessage(null);
                        player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&4☠ &e" + e.getEntity().getPlayer().getDisplayName() + " &ffue eliminado por un &dAldeano Zombie &4☠"));
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 5.0F, 2.0F);
                    }
                } else if (e.getDeathMessage().contains("hit the ground too hard")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        e.setDeathMessage(null);
                        player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&4☠ &e" + e.getEntity().getPlayer().getDisplayName() + " &fa muerto por caida &4☠"));
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 5.0F, 2.0F);
                    }
                } else if (e.getDeathMessage().contains("drowned")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        e.setDeathMessage(null);
                        player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&4☠ &e" + e.getEntity().getPlayer().getDisplayName() + " &fse ahogo &4☠"));
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 5.0F, 2.0F);
                    }
                } else if (e.getDeathMessage().contains("swim in lava")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        e.setDeathMessage(null);
                        player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&4☠ &e" + e.getEntity().getPlayer().getDisplayName() + " &fintento nadar en lava &4☠"));
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 5.0F, 2.0F);
                    }
                } else if (e.getDeathMessage().contains("burned to death")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        e.setDeathMessage(null);
                        player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&4☠ &e" + e.getEntity().getPlayer().getDisplayName() + " &fintento nadar en lava &4☠"));
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 5.0F, 2.0F);
                    }
                } else if (e.getDeathMessage().contains("discovered the floor was lava")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        e.setDeathMessage(null);
                        player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&4☠ &e" + e.getEntity().getPlayer().getDisplayName() + " &fintento nadar en lava &4☠"));
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 5.0F, 2.0F);
                    }
                } else if (e.getDeathMessage().contains("struck by lightning")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        e.setDeathMessage(null);
                        player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&4☠ &e" + e.getEntity().getPlayer().getDisplayName() + " &ffue golpeado por un rayo &4☠"));
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 5.0F, 2.0F);
                    }
                } else if (e.getDeathMessage().contains("blew up")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        e.setDeathMessage(null);
                        player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&4☠ &e" + e.getEntity().getPlayer().getDisplayName() + " &fexploto &4☠"));
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 5.0F, 2.0F);
                    }
                } else if (e.getDeathMessage().contains("went up in flames")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        e.setDeathMessage(null);
                        player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&4☠ &e" + e.getEntity().getPlayer().getDisplayName() + " &fsalio ardiendo &4☠"));
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 5.0F, 2.0F);
                    }
                } else if (e.getDeathMessage().contains("shot")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        e.setDeathMessage(null);
                        player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&4☠ &e" + e.getEntity().getPlayer().getDisplayName() + " &ffue alcanzado por una flecha &4☠"));
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 5.0F, 2.0F);
                    }
                } else if (e.getDeathMessage().contains("pricked to death")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        e.setDeathMessage(null);
                        player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&4☠ &e" + e.getEntity().getPlayer().getDisplayName() + " &fmurio &4☠"));
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 5.0F, 2.0F);
                    }
                } else if (e.getDeathMessage().contains("falling anvil")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        e.setDeathMessage(null);
                        player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&4☠ &e" + e.getEntity().getPlayer().getDisplayName() + " &ffue golpeado por un yunque &4☠"));
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 5.0F, 2.0F);
                    }
                } else if (e.getDeathMessage().contains("starved")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        e.setDeathMessage(null);
                        player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&4☠ &e" + e.getEntity().getPlayer().getDisplayName() + " &f? &4☠"));
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 5.0F, 2.0F);
                    }
                } else if (e.getDeathMessage().contains("suffocated in")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        e.setDeathMessage(null);
                        player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&4☠ &e" + e.getEntity().getPlayer().getDisplayName() + " &fse murio asfixiado &4☠"));
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 5.0F, 2.0F);
                    }
                } else if (e.getDeathMessage().contains("fell out of the world")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        e.setDeathMessage(null);
                        player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&4☠ &e" + e.getEntity().getPlayer().getDisplayName() + " &fcalló del mundo &4☠"));
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 5.0F, 2.0F);
                    }
                } else if (e.getDeathMessage().contains("tried to swim in lava")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        e.setDeathMessage(null);
                        player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&4☠ &e" + e.getEntity().getPlayer().getDisplayName() + " &fse quemo el dedo &4☠"));
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 5.0F, 2.0F);
                    }
                } else if (e.getDeathMessage().contains("fell from a high place")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        e.setDeathMessage(null);
                        player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&4☠ &e" + e.getEntity().getPlayer().getDisplayName() + " &fcallo desde muy alto &4☠"));
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 5.0F, 2.0F);
                    }
                } else {
                    Player a = e.getEntity().getKiller();
                    Player m = e.getEntity().getPlayer();
                    e.setDeathMessage(null);
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.playSound(player.getLocation(), Sound.ENTITY_WITHER_DEATH, 5.0F, 2.0F);
                        player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&4☠ &e" + e.getEntity().getPlayer().getDisplayName() + " &f⚔ &a" + e.getEntity().getKiller().getDisplayName() + " &4☠"));
                    }
                }
            }

    @EventHandler
    public void Respawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();
        this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            public void run() {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6♽ &fTe estas recomponiendo, tardaras 20 segundos..."));
                player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&6♽ &FSeras recuperado en &420 &fsegundos... &6♽"));
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 5.0F, 2.0F);
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 999999, 100));
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 999999, 100));
                sendPlayerDeath(player);
            }
        }, 20 * 0);
        this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            public void run() {
                player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&6♽ &FSeras recuperado en &419 &fsegundos... &6♽"));
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 5.0F, 2.0F);
            }
        }, 20 * 1);
        this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            public void run() {
                player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&6♽ &FSeras recuperado en &418 &fsegundos... &6♽"));
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 5.0F, 2.0F);
            }
        }, 20 * 2);
        this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            public void run() {
                player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&6♽ &FSeras recuperado en &417 &fsegundos... &6♽"));
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 5.0F, 2.0F);
            }
        }, 20 * 3);
        this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            public void run() {
                player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&6♽ &FSeras recuperado en &416 &fsegundos... &6♽"));
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 5.0F, 2.0F);
            }
        }, 20 * 4);
        this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            public void run() {
                player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&6♽ &FSeras recuperado en &c15 &fsegundos... &6♽"));
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 5.0F, 2.0F);
            }
        }, 20 * 5);
        this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            public void run() {
                player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&6♽ &FSeras recuperado en &c14 &fsegundos... &6♽"));
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 5.0F, 2.0F);
            }
        }, 20 * 6);
        this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            public void run() {
                player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&6♽ &FSeras recuperado en &c13 &fsegundos... &6♽"));
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 5.0F, 2.0F);
            }
        }, 20 * 7);
        this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            public void run() {
                player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&6♽ &FSeras recuperado en &c12 &fsegundos... &6♽"));
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 5.0F, 2.0F);
            }
        }, 20 * 8);
        this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            public void run() {
                player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&6♽ &FSeras recuperado en &c11 &fsegundos... &6♽"));
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 5.0F, 2.0F);
            }
        }, 20 * 9);
        this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            public void run() {
                player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&6♽ &FSeras recuperado en &c10 &fsegundos... &6♽"));
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 5.0F, 2.0F);
            }
        }, 20 * 10);
        this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            public void run() {
                player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&6♽ &FSeras recuperado en &69 &fsegundos... &6♽"));
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 5.0F, 2.0F);
            }
        }, 20 * 11);
        this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            public void run() {
                player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&6♽ &FSeras recuperado en &68 &fsegundos... &6♽"));
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 5.0F, 2.0F);
            }
        }, 20 * 12);
        this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            public void run() {
                player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&6♽ &FSeras recuperado en &67 &fsegundos... &6♽"));
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 5.0F, 2.0F);
            }
        }, 20 * 13);
        this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            public void run() {
                player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&6♽ &FSeras recuperado en &66 &fsegundos... &6♽"));
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 5.0F, 2.0F);
            }
        }, 20 * 14);
        this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            public void run() {
                player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&6♽ &FSeras recuperado en &e5 &fsegundos... &6♽"));
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 5.0F, 2.0F);
            }
        }, 20 * 15);
        this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            public void run() {
                player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&6♽ &FSeras recuperado en &e4 &fsegundos... &6♽"));
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 5.0F, 2.0F);
            }
        }, 20 * 16);
        this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            public void run() {
                player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&6♽ &FSeras recuperado en &a3 &fsegundos... &6♽"));
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 5.0F, 2.0F);
            }
        }, 20 * 17);
        this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            public void run() {
                player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&6♽ &FSeras recuperado en &a2 &fsegundos... &6♽"));
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 5.0F, 2.0F);
            }
        }, 20 * 18);
        this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            public void run() {
                player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&6♽ &FSeras recuperado en &a1 &fsegundos... &6♽"));
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 5.0F, 2.0F);
            }
        }, 20 * 19);
        this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            public void run() {
                player.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&6♼ &F¡Te has recuperado! &6♼"));
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 5.0F, 2.0F);
                player.removePotionEffect(PotionEffectType.SLOW);
                player.removePotionEffect(PotionEffectType.BLINDNESS);
            }
        }, 20 * 20);
    }

    @Override
    public void onEnable() {
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&d&lIslandDeath T2 &8┃ &fPlugin cargado correctamente."));
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("guardarbase").setExecutor((CommandExecutor) this);
        getCommand("base").setExecutor((CommandExecutor) this);
        if (Bukkit.getScoreboardManager().getMainScoreboard().getObjective(this.scoreboard) != null) {
            Bukkit.getLogger().info("IslandDeath Objective ya creado");
        } else {
            Bukkit.getScoreboardManager().getMainScoreboard().registerNewObjective(this.scoreboard, "dummy");
        }
        if (!this.file.exists())
            saveBasesFile();

        ItemStack prote = new ItemStack(Material.COAL_BLOCK, 1);
        ItemMeta protemeta = prote.getItemMeta();
        protemeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6&l✦ &eTorre de control &6&l✦"));
        ArrayList<String> lore = new ArrayList<String>();
        lore.add(ChatColor.translateAlternateColorCodes('&', "&8● &fCreara una torre que protegera a tu base de los enemigos."));
        protemeta.setLore(lore);
        protemeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        protemeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        prote.setItemMeta(protemeta);
        NamespacedKey protekey = new NamespacedKey(this, "prote_block");
        ShapedRecipe proterecipe = new ShapedRecipe(protekey, prote);
        proterecipe.shape("iir", "oco", "rii");
        proterecipe.setIngredient('i', Material.IRON_INGOT);
        proterecipe.setIngredient('r', Material.REDSTONE);
        proterecipe.setIngredient('o', Material.GOLD_INGOT);
        proterecipe.setIngredient('c', Material.COAL);

        Bukkit.addRecipe(proterecipe);
    }

    @Override
    public void onDisable() {
    }
}
