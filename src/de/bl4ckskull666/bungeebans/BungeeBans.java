/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.bungeebans;

import com.google.common.collect.ObjectArrays;
import de.bl4ckskull666.bungeebans.classes.PlayerBan;
import de.bl4ckskull666.bungeebans.commands.Ban;
import de.bl4ckskull666.bungeebans.commands.IpBan;
import de.bl4ckskull666.bungeebans.commands.IpMute;
import de.bl4ckskull666.bungeebans.commands.IpTempBan;
import de.bl4ckskull666.bungeebans.commands.IpTempMute;
import de.bl4ckskull666.bungeebans.commands.Mute;
import de.bl4ckskull666.bungeebans.commands.TempBan;
import de.bl4ckskull666.bungeebans.commands.TempMute;
import de.bl4ckskull666.bungeebans.commands.UnBan;
import de.bl4ckskull666.bungeebans.commands.UnMute;
import de.bl4ckskull666.bungeebans.database.MySQL;
import de.bl4ckskull666.bungeebans.listener.ChatListener;
import de.bl4ckskull666.bungeebans.listener.LoginListener;
import de.bl4ckskull666.bungeebans.listener.PingListener;
import de.bl4ckskull666.mu1ti1ingu41.Mu1ti1ingu41;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import java.util.Calendar;
import java.util.Map;
import java.util.logging.Level;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import yamlapi.file.FileConfiguration;

/**
 *
 * @author PapaHarni
 */
public class BungeeBans extends Plugin {
    private FileConfiguration _config;
    @Override
    public void onEnable() {
        if(!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        _config = Mu1ti1ingu41.loadConfig(this);
        
        _p = this;
        
        if(!MySQL.checkMySQL()) {
            getLogger().log(Level.WARNING, "I'm disable me self. Can't load Database. Please configure it and restart the server.");
            return;
        }
        
        MySQL.loadBans();
        
        //Register Commands - Bans
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new Ban());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new TempBan());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new IpBan());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new IpTempBan());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new UnBan());
        
        //Register Commands - Mutes
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new Mute());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new TempMute());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new IpMute());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new IpTempMute());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new UnMute());
        
        //Register Listeners
        ProxyServer.getInstance().getPluginManager().registerListener(this, new ChatListener());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new LoginListener());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new PingListener());

        Mu1ti1ingu41.loadExternalDefaultLanguage(this, "languages");
        Tasks.restartBanTask();
        Tasks.restartMuteTask();
    }
    
    @Override
    public void onDisable() {
        Tasks.stopTasks();
    }
    
    public FileConfiguration getConfig() {
        return _config;
    }
    
    private static BungeeBans _p;
    public static BungeeBans getPlugin() {
        return _p;
    }
    
    public static ProxiedPlayer getPlayer(Connection c) {
        for(ProxiedPlayer pl: ProxyServer.getInstance().getPlayers()) {
            if(pl.getAddress().equals(c.getAddress()))
                return pl;
        }
        return null;
    }
    
    public static ProxiedPlayer getPlayer(String name) {
        for(ProxiedPlayer pl: ProxyServer.getInstance().getPlayers()) {
            if(pl.getName().equalsIgnoreCase(name) || pl.getUniqueId().toString().equalsIgnoreCase(name))
                return pl;
        }
        return null;
    }
    
    public static String getTheIP(String ipAddress, boolean withZero) {
        String[] ip = ipAddress.split("\\.");
        String searchIp = ip[0] + ".";
        if(ip.length > 1 && !ip[1].equalsIgnoreCase("*") && isNumeric(ip[1]))
            searchIp += ip[1] + ".";
        else if(withZero)
            searchIp += "0.";
        
        if(ip.length > 2 && !ip[2].equalsIgnoreCase("*") && isNumeric(ip[2]))
            searchIp += ip[2] + ".";
        else if(withZero)
            searchIp += "0.";
        
        if(ip.length > 3 && !ip[3].equalsIgnoreCase("*") && isNumeric(ip[3]))
            searchIp += ip[3];
        else if(withZero)
            searchIp += "0";
        
        return searchIp;
    }
    
    public static void KickPlayerByIP(String ipAddress, String path, String def, String[] search, String[] replace) {
        for(ProxiedPlayer pp: ProxyServer.getInstance().getPlayers()) {
            if(pp.getPendingConnection().getAddress().getAddress().getHostAddress().startsWith(ipAddress))
                pp.disconnect(Language.convertString(Language.getMsg(BungeeBans.getPlugin(), pp.getUniqueId(), path, def, search, replace) + Language.getMsg(BungeeBans.getPlugin(), pp.getUniqueId(), "objection", "")));
        }
    }
    
    public static void MessagePlayerByIP(String ipAddress, String path, String def, String[] search, String[] replace) {
        for(ProxiedPlayer pp: ProxyServer.getInstance().getPlayers()) {
            if(pp.getPendingConnection().getAddress().getAddress().getHostAddress().startsWith(ipAddress)) {
                pp.sendMessage(Language.convertString(Language.getMsg(BungeeBans.getPlugin(), pp.getUniqueId(), path, def, search, replace) + Language.getMsg(BungeeBans.getPlugin(), pp.getUniqueId(), "objection", "")));
            }
        }
    }
    
    public static void TeamInform(String path, String def, String[] search, String[] replace) {
        for(ProxiedPlayer pp: ProxyServer.getInstance().getPlayers()) {
            if(pp.hasPermission("bungeebans.team"))
                pp.sendMessage(Language.getMessage(BungeeBans.getPlugin(), pp.getUniqueId(), path, def, search, replace));
        }
    }
    
    public static PlayerBan getIpMuted(String ipAddress) {
        String[] ip = ipAddress.split("\\.");
        if(ip.length < 4)
            return null;
        
        if(PlayerBan.getMute(ip[0] + "." + ip[1] + ".0.0") != null)
            return PlayerBan.getMute(ip[0] + "." + ip[1] + ".0.0");
        if(PlayerBan.getMute(ip[0] + "." + ip[1] + "." + ip[2] + ".0") != null)
            return PlayerBan.getMute(ip[0] + "." + ip[1] + "." + ip[2] + ".0");
        if(PlayerBan.getMute(ip[0] + "." + ip[1] + "." + ip[2] + "." + ip[3]) != null)
            return PlayerBan.getMute(ip[0] + "." + ip[1] + "." + ip[2] + "." + ip[3]);
        return null;
    }
    
    public static PlayerBan getIpBanned(String ipAddress) {
        String[] ip = ipAddress.split("\\.");
        if(ip.length < 4)
            return null;
        
        if(PlayerBan.getBan(ip[0] + "." + ip[1] + ".0.0") != null)
            return PlayerBan.getBan(ip[0] + "." + ip[1] + ".0.0");
        if(PlayerBan.getBan(ip[0] + "." + ip[1] + "." + ip[2] + ".0") != null)
            return PlayerBan.getBan(ip[0] + "." + ip[1] + "." + ip[2] + ".0");
        if(PlayerBan.getBan(ip[0] + "." + ip[1] + "." + ip[2] + "." + ip[3]) != null)
            return PlayerBan.getBan(ip[0] + "." + ip[1] + "." + ip[2] + "." + ip[3]);
        return null;
    }
    
    public static PlayerBan getBanByName(String name) {
        for(Map.Entry<String, PlayerBan> me: PlayerBan.getBans().entrySet()) {
            if(me.getValue().getName().equalsIgnoreCase(name) || me.getValue().getUUID().equalsIgnoreCase(name) || me.getKey().equalsIgnoreCase(name))
                return me.getValue();
        }
        return null;
    }
    
    public static PlayerBan getMuteByName(String name) {
        for(Map.Entry<String, PlayerBan> me: PlayerBan.getMutes().entrySet()) {
            if(me.getValue().getName().equalsIgnoreCase(name) || me.getValue().getUUID().equalsIgnoreCase(name) || me.getKey().equalsIgnoreCase(name))
                return me.getValue();
        }
        return null;
    }
    
    public static String getDateByCalendar(Calendar cal) {
        String temp = (cal.get(Calendar.DAY_OF_MONTH) <= 9?"0":"") + cal.get(Calendar.DAY_OF_MONTH);
        temp += "." + (cal.get(Calendar.MONTH) <= 8?"0":"") + String.valueOf(cal.get(Calendar.MONTH)+1);
        temp += "." + cal.get(Calendar.YEAR);
        return temp;
    }
    
    public static String getTimeByCalendar(Calendar cal) {
        return (cal.get(Calendar.HOUR_OF_DAY) <= 9?"0":"") + cal.get(Calendar.HOUR_OF_DAY)
                + ":" + (cal.get(Calendar.MINUTE) <= 9?"0":"") + cal.get(Calendar.MINUTE)
                + ":" + (cal.get(Calendar.SECOND) <= 9?"0":"") + cal.get(Calendar.SECOND);
    }
    
    private static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch(NumberFormatException e) {
            return false;
        }
    }
    
    private static String shortString(String str, String[] remove) {
        for(String search: remove) {
            str = str.replace(search, "");
        }
        return str;
    }
    
    public static long getTimeIsTime(String str) {
        if(str.endsWith("week") || str.endsWith("weeks") || str.equalsIgnoreCase("w")) {
            String number = shortString(str, new String[] {"weeks", "week", "w"});
            if(isNumeric(number))
                return (604800L*Long.parseLong(number));
        } else if(str.endsWith("day") || str.endsWith("days") || str.endsWith("d")) {
            String number = shortString(str, new String[] {"days", "day", "d"});
            if(isNumeric(number))
                return (86400L*Long.parseLong(number));
        } else if(str.endsWith("hour") || str.endsWith("hours") || str.endsWith("h")) {
            String number = shortString(str, new String[] {"hours", "hour", "h"});
            if(isNumeric(number))
                return (3600L*Long.parseLong(number));
        } else if(str.endsWith("min") || str.endsWith("mins") || str.endsWith("minute") || str.endsWith("minutes") || str.endsWith("m")) {
            String number = shortString(str, new String[] {"minutes", "minute", "mins", "min", "m"});
            if(isNumeric(number))
                return (60L*Long.parseLong(number));
        } else if(str.endsWith("sec") || str.endsWith("secs") || str.endsWith("second") || str.endsWith("seconds") || str.endsWith("s")) {
            String number = shortString(str, new String[] {"seconds", "second", "secs", "sec", "s"});
            if(isNumeric(number))
                return Long.parseLong(number);
        }
        return -1;
    }
}