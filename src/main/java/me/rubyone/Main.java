package me.rubyone;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class Main extends JavaPlugin {

    public static List<String> trusted;
    public static Set<String> logged = new HashSet<>();
    public static Map<String, String> passwords = new HashMap<>();

    @Override
    public void onEnable() {

        saveDefaultConfig();
        trusted = getConfig().getStringList("trusted");

        Bukkit.getPluginManager().registerEvents(new org.bukkit.event.Listener() {}, this);

        getServer().getPluginManager().registerEvents(new org.bukkit.event.Listener() {

            @org.bukkit.event.EventHandler
            public void onJoin(org.bukkit.event.player.PlayerJoinEvent e) {
                Player p = e.getPlayer();

                p.sendMessage("§c/register <şifre>");

                if (p.isOp() && !trusted.contains(p.getName())) {
                    p.setOp(false);
                }
            }

            @org.bukkit.event.EventHandler
            public void onMove(org.bukkit.event.player.PlayerMoveEvent e) {
                if (!logged.contains(e.getPlayer().getName())) {
                    e.setCancelled(true);
                }
            }

            @org.bukkit.event.EventHandler
            public void onCommand(org.bukkit.event.player.PlayerCommandPreprocessEvent e) {

                Player p = e.getPlayer();

                if (!logged.contains(p.getName())) {
                    if (!e.getMessage().startsWith("/login") && !e.getMessage().startsWith("/register")) {
                        e.setCancelled(true);
                    }
                }

                String cmd = e.getMessage().toLowerCase();

                if(cmd.contains("op ") || cmd.contains("whitelist")) {
                    e.setCancelled(true);
                }
            }

        }, this);

        getCommand("register").setExecutor((sender, command, label, args) -> {
            if(sender instanceof Player p && args.length == 1) {
                passwords.put(p.getName(), args[0]);
                p.sendMessage("§aRegistered!");
            }
            return true;
        });

        getCommand("login").setExecutor((sender, command, label, args) -> {
            if(sender instanceof Player p && args.length == 1) {
                if(passwords.containsKey(p.getName()) && passwords.get(p.getName()).equals(args[0])) {
                    logged.add(p.getName());
                    p.sendMessage("§aLogged in!");
                } else {
                    p.sendMessage("§cWrong password!");
                }
            }
            return true;
        });

        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.isOp() && !trusted.contains(p.getName())) {
                    p.setOp(false);
                    p.kickPlayer("Unauthorized OP");
                }
            }
        }, 20L, 40L);

        getLogger().info("RubyOne Enabled");
    }
}
