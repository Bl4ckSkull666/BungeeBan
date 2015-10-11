/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.bungeebans.commands;

import com.google.common.collect.ObjectArrays;
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
public class Mute extends Command {
    
    public Mute() {
        super("mute", "bungeebans.mute");
    }

    @Override
    public void execute(CommandSender s, String[] a) {
        UUID uuid_by_sender = BungeeBans.getPlayer(s.getName()) == null?UUID.fromString("00000000-0000-0000-0000-000000000000"):BungeeBans.getPlayer(s.getName()).getUniqueId();
        if(a.length <= 2) {
            s.sendMessage(Language.getMessage(BungeeBans.getPlugin(), uuid_by_sender, "command.mute.wrongformat", "Please add a Username and a Reason."));
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
        for(int i = 1; i < a.length; i++)
            msg += (msg.isEmpty()?"":" ") + a[i];
        
        if(msg.isEmpty()) {
            s.sendMessage(Language.getMessage(BungeeBans.getPlugin(), uuid_by_sender, "command.mute.needreason", "Please add a Reason to mute %name% successful.", new String[] {"%name%"}, new String[] {nick}));
            return;
        }
        
        if(PlayerBan.isMuted(name) || PlayerBan.isMuted(nick)) {
            s.sendMessage(Language.getMessage(BungeeBans.getPlugin(), uuid_by_sender, "command.mute.is-muted", "%name% is already muted.", new String[] {"%name%"}, new String[] {nick}));
            return;
        }
        
        PlayerBan pb = new PlayerBan(name, nick, msg, 0L, "mute");
        MySQL.addBan(pb, BungeeBans.getPlayer(s.getName()).getUniqueId().toString());
        
        if(pp != null)
            pp.sendMessage(Language.convertString(Language.getMsg(BungeeBans.getPlugin(), pp.getUniqueId(), "command.mute.muted", "You're muted by %by% because %message%", new String[] {"%message%", "%by%"}, new String[] {msg, s.getName()}) + Language.getMsg(BungeeBans.getPlugin(), pp.getUniqueId(), "objection", "")));

        BungeeBans.TeamInform("command.mute.message", "%name% was muted by %by% because %message%.", new String[] {"%message%", "%name%", "%by%"}, new String[] {msg, nick, s.getName()});
        Tasks.restartMuteTask();
    }
}
