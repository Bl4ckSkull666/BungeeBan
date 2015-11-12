/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.bungeebans.commands;

import de.bl4ckskull666.bungeebans.BungeeBans;
import de.bl4ckskull666.bungeebans.Tasks;
import de.bl4ckskull666.bungeebans.classes.PlayerBan;
import de.bl4ckskull666.bungeebans.database.MySQL;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import java.util.UUID;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 *
 * @author PapaHarni
 */
public class TempMute extends Command {
    
    public TempMute() {
        super("tempmute", "bungeebans.tempmute");
    }

    @Override
    public void execute(CommandSender s, String[] a) {
        UUID uuid_by_sender = BungeeBans.getPlayer(s.getName()) == null?UUID.fromString("00000000-0000-0000-0000-000000000000"):BungeeBans.getPlayer(s.getName()).getUniqueId();
        if(a.length <= 2) {
            s.sendMessage(Language.getMessage(BungeeBans.getPlugin(), uuid_by_sender, "command.tempmute.wrongformat", "Please add a Name,a Reason and a Time."));
            return;
        }
        
        String name = a[0];
        String nick = a[0];
        ProxiedPlayer pp = null;
        if(ProxyServer.getInstance().getPlayer(a[0]) != null) {
            pp = ProxyServer.getInstance().getPlayer(a[0]);
            name = pp.getUniqueId().toString();
            nick = pp.getName();
        } else {
            UUID u = BungeeBans.getUUIDByName(a[0]);
            if(u != null)
                name = u.toString();
        }
        
        String msg = "";
        long time = 0L;
        for(int i = 1; i < a.length; i++) {
            long tadd = BungeeBans.getTimeIsTime(a[i]);
            if(tadd > 0L) {
                time += tadd;
                continue;
            }
            msg += (msg.isEmpty()?"":" ") + a[i];
        }
        
        if(msg.isEmpty()) {
            s.sendMessage(Language.getMessage(BungeeBans.getPlugin(), uuid_by_sender, "command.tempmute.needreason", "Please add a Reason to ban %name% for a time.", new String[] {"%name%"}, new String[] {nick}));
            return;
        }
        
        if(time == 0L) {
            s.sendMessage(Language.getMessage(BungeeBans.getPlugin(), uuid_by_sender, "command.tempmute.needtime", "Please add a time to ban %name% successful.", new String[] {"%name%"}, new String[] {nick}));
            return;
        }
        
        if(PlayerBan.isMuted(name) || PlayerBan.isMuted(nick)) {
            s.sendMessage(Language.getMessage(BungeeBans.getPlugin(), uuid_by_sender, "command.mute.is-muted", "%name% is already muted.", new String[] {"%name%"}, new String[] {nick}));
            return;
        }
        
        PlayerBan pb = new PlayerBan(name, nick, msg, time, "mute");
        MySQL.addBan(pb, uuid_by_sender.toString());
        
        if(pp != null)
            pp.sendMessage(Language.convertString(Language.getMsg(BungeeBans.getPlugin(), pp.getUniqueId(), "command.tempmute.muted", "You're now muted by %by% because of %message% until %date% on %time%.", new String[] {"%message%", "%by%", "%date%", "%time%"}, new String[] {msg, s.getName(), BungeeBans.getDateByCalendar(pb.getEnding()), BungeeBans.getTimeByCalendar(pb.getEnding())}) + Language.getMsg(BungeeBans.getPlugin(), pp.getUniqueId(), "objection", "")));
        
        BungeeBans.TeamInform("command.tempmute.message", "%name% was muted by %by% because %message% until %date% on %time%.", new String[] {"%message%", "%name%", "%by%", "%date%", "%time%"}, new String[] {msg, nick, s.getName(), BungeeBans.getDateByCalendar(pb.getEnding()), BungeeBans.getTimeByCalendar(pb.getEnding())});
        Tasks.restartMuteTask();
    }
}