/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.bungeebans.database;

import de.bl4ckskull666.bungeebans.BungeeBans;
import de.bl4ckskull666.bungeebans.classes.PlayerBan;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.logging.Level;
import net.md_5.bungee.api.ProxyServer;

/**
 *
 * @author PapaHarni
 */
public final class MySQL {
    private static int _lastLoadedId = 0;
    
    private static boolean isMySQLDriver() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            return true;
        } catch(ClassNotFoundException t) {
            ProxyServer.getInstance().getLogger().log(Level.WARNING, "Konnte den MySQL Treiber nicht finden! Beende Plugin.", t);
            return false;
        }
    }
    
    private static Connection getConnect() {
        Connection con = null;
        try {
            con = DriverManager.getConnection("jdbc:mysql://" + BungeeBans.getPlugin().getConfig().getString("database.host", "localhost") + ":" + 
                    BungeeBans.getPlugin().getConfig().getString("database.port", "3306") + "/" +
                    BungeeBans.getPlugin().getConfig().getString("database.data", "bungee"), 
                    BungeeBans.getPlugin().getConfig().getString("database.user", "root"),
                    BungeeBans.getPlugin().getConfig().getString("database.pass", "root")
            );
        } catch(SQLException e) {
            ProxyServer.getInstance().getLogger().log(Level.WARNING, "Fehler beim Herstellen der Verbindung zum MySQL Server!", e);
        }
        return con;
    }
    
    private static boolean setupStructureBanTable(Connection con) {
        try {
            PreparedStatement statement;
            statement = con.prepareStatement("CREATE TABLE IF NOT EXISTS `bungeebans` (" 
                + " `id` int(11) NOT NULL AUTO_INCREMENT,"
                + " `uuid` varchar(40) NOT NULL,"
                + " `lastName` varchar(32) NOT NULL,"
                + " `since` datetime NOT NULL,"
                + " `ending` datetime NOT NULL,"
                + " `message` text NOT NULL,"
                + " `btype` enum('ban','mute','ip') NOT NULL DEFAULT 'ban',"
                + " `by` varchar(40) NOT NULL,"
                + " PRIMARY KEY (`id`)"
                    
                + ") ENGINE=MyISAM DEFAULT CHARSET=latin1"
            );
            statement.execute();
            statement.close();
            
            statement = con.prepareStatement("CREATE TABLE IF NOT EXISTS `bungeebans_history` (" 
                + " `id` int(11) NOT NULL AUTO_INCREMENT,"
                + " `uuid` varchar(40) NOT NULL,"
                + " `lastName` varchar(32) NOT NULL,"
                + " `since` datetime NOT NULL,"
                + " `ending` datetime NOT NULL,"
                + " `message` text NOT NULL,"
                + " `btype` enum('ban','mute','ip') NOT NULL DEFAULT 'ban',"
                + " `by` varchar(40) NOT NULL,"
                + " PRIMARY KEY (`id`)"
                + ") ENGINE=MyISAM DEFAULT CHARSET=latin1"
            );
            statement.execute();
            statement.close();
        } catch(SQLException e) {
            ProxyServer.getInstance().getLogger().log(Level.WARNING, "Fehler beim Erstellen der Datenbank Struktur!", e);
            return false;
        }
        return true;
    }
    
    public static boolean checkMySQL() {
        if(!isMySQLDriver())
            return false;
        
        Connection con;
        try {
            con = getConnect();
            if(con == null || !setupStructureBanTable(con))
                return false;
            con.close();
            return true;
        } catch(SQLException e) {
            ProxyServer.getInstance().getLogger().log(Level.WARNING, "Fehler beim Herstellen der Verbindung zum MySQL Server!", e);
        }
        return false;
    }
    
    public static void loadBans() {
        Connection con;
        try {
            con = getConnect();
            PreparedStatement statement;
            ResultSet rs;
            statement = con.prepareStatement("SELECT `id`,`uuid`,`lastName`,`since`,`ending`,`message`,`btype` FROM `bungeebans` WHERE `id` > ? ORDER BY `since` DESC");
            statement.setInt(1, _lastLoadedId);
            rs = statement.executeQuery();
            while(rs.next()) {
                Calendar since = Calendar.getInstance();
                since.setTime(rs.getDate("since"));
                Calendar ending = Calendar.getInstance();
                ending.setTime(rs.getDate("ending"));

                PlayerBan pb = new PlayerBan(rs.getString("uuid"), rs.getString("lastName"), rs.getString("message"), since, ending, rs.getString("btype"));
                _lastLoadedId = rs.getInt("id");
            }            
        } catch(SQLException e) {
            ProxyServer.getInstance().getLogger().log(Level.WARNING, "Fehler beim Herstellen der Verbindung zum MySQL Server!", e);
        }
    }
    
    public static boolean addBan(PlayerBan pb, String by) {
        Connection con;
        try {
            con = getConnect();
            PreparedStatement statement;
            statement = con.prepareStatement("INSERT INTO `bungeebans` (`uuid`,`lastName`,`since`,`ending`,`message`,`btype`,`by`) VALUES (?,?,?,?,?,?,?)");
            statement.setString(1, pb.getUUID());
            statement.setString(2, pb.getName());
            statement.setString(3, getDateTime(pb.getSince()));
            statement.setString(4, pb.getEnding() != null?getDateTime(pb.getEnding()):"0000-00-00 00:00:00");
            statement.setString(5, pb.getMessage());
            statement.setString(6, pb.getType());
            statement.setString(7, by);
            statement.execute();
            statement.close();
            
            statement = con.prepareStatement("INSERT INTO `bungeebans_history` (`uuid`,`lastName`,`since`,`ending`,`message`,`btype`,`by`) VALUES (?,?,?,?,?,?,?)");
            statement.setString(1, pb.getUUID());
            statement.setString(2, pb.getName());
            statement.setString(3, getDateTime(pb.getSince()));
            statement.setString(4, pb.getEnding() != null?getDateTime(pb.getEnding()):"0000-00-00 00:00:00");
            statement.setString(5, pb.getMessage());
            statement.setString(6, pb.getType());
            statement.setString(7, by);
            statement.execute();
            statement.close();
            con.close();
            return true;
        } catch(SQLException e) {
            ProxyServer.getInstance().getLogger().log(Level.WARNING, "Fehler beim Herstellen der Verbindung zum MySQL Server!", e);
        }
        return false;
    }
    
    public static boolean delBan(PlayerBan pb) {
        Connection con;
        try {
            con = getConnect();
            PreparedStatement statement;
            statement = con.prepareStatement("DELETE FROM `bungeebans` WHERE `uuid` = ? AND `btype` = ?");
            statement.setString(1, pb.getUUID());
            statement.setString(2, pb.getType());
            statement.execute();
            statement.close();
            con.close();
            return true;
        } catch(SQLException e) {
            ProxyServer.getInstance().getLogger().log(Level.WARNING, "Fehler beim Herstellen der Verbindung zum MySQL Server!", e);
        }
        return false;
    }
    
    public static String getUUIDFromName(String name) {
        Connection con;
        String uuid = "";
        try {
            con = getConnect();
            PreparedStatement statement;
            statement = con.prepareStatement("SELECT `uuid` FROM `uuiddatabase` WHERE `name` = ? LIMIT 0,1");
            statement.setString(1, name);
            ResultSet rs = statement.executeQuery();
            if(rs.next() && !rs.getString("uuid").isEmpty() && rs.getString("uuid").length() > 32)
                uuid = rs.getString("uuid");
            rs.close();
            statement.close();
            con.close();
        } catch(SQLException e) {
            ProxyServer.getInstance().getLogger().log(Level.WARNING, "Fehler beim Herstellen der Verbindung zum MySQL Server!", e);
        }
        return uuid;
    }
    
    private static String getDateTime(Calendar cal) {
        String temp = String.valueOf(cal.get(Calendar.YEAR));
        temp += "-" + (cal.get(Calendar.MONTH) <= 8?"0":"") + String.valueOf(cal.get(Calendar.MONTH)+1);
        temp += "-" + (cal.get(Calendar.DAY_OF_MONTH) <= 9?"0":"") + String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        temp += " " + (cal.get(Calendar.HOUR_OF_DAY) <= 9?"0":"") + String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
        temp += ":" + (cal.get(Calendar.MINUTE) <= 9?"0":"") + String.valueOf(cal.get(Calendar.MINUTE));
        temp += ":" + (cal.get(Calendar.SECOND) <= 9?"0":"") + String.valueOf(cal.get(Calendar.SECOND));
        return temp;
    }
    
    private static Calendar getCalendar(String str) {
        BungeeBans.getPlugin().getLogger().log(Level.INFO, "Set Date in as Calendar : {0}", str);
        Calendar cal = Calendar.getInstance();
        String[] temp = str.split(" ");
        if(temp.length == 2) {
            String[] date = temp[0].split("-");
            String[] time = temp[1].split(":");
            
            cal.set(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2]), Integer.parseInt(time[0]), Integer.parseInt(time[1]), Integer.parseInt(time[2]));
        }
        return cal;
    }
}