/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.bungeebans;

import de.bl4ckskull666.bungeebans.classes.PlayerBan;
import de.bl4ckskull666.bungeebans.database.MySQL;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import java.util.Calendar;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.scheduler.ScheduledTask;

/**
 *
 * @author PapaHarni
 */
public final class Tasks {
    private static ScheduledTask _bans = null;
    private static ScheduledTask _mute = null;
    public static class checkBans implements Runnable {
        private final PlayerBan _unban;
        private final String _unbanStr;
        public checkBans(PlayerBan unban, String unbanStr) {
            _unban = unban;
            _unbanStr = unbanStr;
        }
        
        @Override
        public void run() {
            if(_bans != null)
                _bans.cancel();
            
            Calendar now = Calendar.getInstance();
            now.setTimeInMillis(System.currentTimeMillis());
            
            if(_unban != null && _unban.isTemporary() && _unban.getEnding() != null && _unban.getEnding().before(now)) {
                String name = _unban.getName();
                if(MySQL.delBan(_unban)) {
                    PlayerBan.getBans().remove(_unbanStr);
                    BungeeBans.getPlugin().getLogger().log(Level.INFO, "{0} was unbanned.", _unbanStr);
                    BungeeBans.TeamInform("function.auto-unmute", "%name%'s ban time has end.", new String[] {"%name%"}, new String[] {name});
                }
            }
            
            Calendar temp = null;
            PlayerBan temp_pb = null;
            String temp_str = "";
            for(Map.Entry<String, PlayerBan> me: PlayerBan.getBans().entrySet()) {
                if(me.getValue().isTemporary() && (temp == null || me.getValue().getEnding().before(temp) || me.getValue().getEnding().before(now))) {
                    temp_str = me.getKey();
                    temp = me.getValue().getEnding();
                    temp_pb = me.getValue();
                }
            }
            
            if(temp != null) {
                long diff = (temp.getTimeInMillis()-System.currentTimeMillis())/1000;
                if(diff <= 10L)
                    diff = 10L;
                _bans = ProxyServer.getInstance().getScheduler().schedule(BungeeBans.getPlugin(), new checkBans(temp_pb, temp_str), diff, TimeUnit.SECONDS);
            }
        }
    }
    
    public static void restartBanTask() {
        if(_bans != null)
            _bans.cancel();
        
        _bans = ProxyServer.getInstance().getScheduler().schedule(BungeeBans.getPlugin(), new checkBans(null, ""), 10L, TimeUnit.SECONDS);
    }
    
    public static class checkMutes implements Runnable {
        private final PlayerBan _unmute;
        private final String _unmuteStr;
        public checkMutes(PlayerBan unmute, String unmuteStr) {
            _unmute = unmute;
            _unmuteStr = unmuteStr;
        }
        
        @Override
        public void run() {
            if(_mute != null)
                _mute.cancel();
            
            Calendar now = Calendar.getInstance();
            now.setTimeInMillis(System.currentTimeMillis());
            if(_unmute != null && _unmute.isTemporary() && _unmute.getEnding() != null && _unmute.getEnding().before(now)) {
                String name = _unmute.getName();
                if(MySQL.delBan(_unmute)) {
                    PlayerBan.getMutes().remove(_unmuteStr);
                    BungeeBans.getPlugin().getLogger().log(Level.INFO, "{0} was unmuted.", _unmuteStr);
                    BungeeBans.TeamInform("function.auto-unmute", "%name%'s mute time has end.", new String[] {"%name%"}, new String[] {name});
                    if(ProxyServer.getInstance().getPlayer(_unmute.getUUID()) != null)
                        Language.sendMessage(BungeeBans.getPlugin(), ProxyServer.getInstance().getPlayer(_unmute.getUUID()), "function.auto-unmute-me", "Your mute time has end. You can now talk again.");
                }
            }

            Calendar temp = null;
            PlayerBan temp_pb = null;
            String temp_str = "";
            for(Map.Entry<String, PlayerBan> me: PlayerBan.getMutes().entrySet()) {
                if(me.getValue().isTemporary() && (temp == null || me.getValue().getEnding().before(temp) || me.getValue().getEnding().before(now))) {
                    temp_str = me.getKey();
                    temp = me.getValue().getEnding();
                    temp_pb = me.getValue();
                }
            }
            
            if(temp != null) {
                long diff = (temp.getTimeInMillis()-System.currentTimeMillis())/1000;
                if(diff <= 10L)
                    diff = 10L;
                _mute = ProxyServer.getInstance().getScheduler().schedule(BungeeBans.getPlugin(), new checkMutes(temp_pb, temp_str), diff, TimeUnit.SECONDS);
            }
        }
    }
    
    public static void restartMuteTask() {
        if(_mute != null)
            _mute.cancel();
        
        _mute = ProxyServer.getInstance().getScheduler().schedule(BungeeBans.getPlugin(), new checkMutes(null, ""), 10L, TimeUnit.SECONDS);
    }
    
    public static void stopTasks() {
        if(_bans != null)
            _bans.cancel();
        if(_mute != null)
            _mute.cancel();
    }
}
