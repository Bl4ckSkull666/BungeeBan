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
import net.md_5.bungee.api.plugin.Command;

/**
 *
 * @author PapaHarni
 */
public class IpMute extends Command {
    
    public IpMute() {
        super("ipmute", "bungeebans.ipmute");
    }

    @Override
    public void execute(CommandSender s, String[] a) {
        UUID uuid_by_sender = BungeeBans.getPlayer(s.getName()) == null?UUID.fromString("00000000-0000-0000-0000-000000000000"):BungeeBans.getPlayer(s.getName()).getUniqueId();
        if(a.length <= 2) {
            //Wrong format
            s.sendMessage(Language.getMessage(BungeeBans.getPlugin(), uuid_by_sender, "command.ipmute.wrongformat", "Please add a IP and a Reason."));
            return;
        }
        String ip = a[0];
        
        String msg = "";
        for(int i = 1; i < a.length; i++)
            msg += (msg.isEmpty()?"":" ") + a[i];
        
        if(msg.isEmpty()) {
            s.sendMessage(Language.getMessage(BungeeBans.getPlugin(), uuid_by_sender, "command.ipmute.needreason", "Please add a Reason to mute %ip% successful.", new String[] {"%ip%"}, new String[] {ip}));
            return;
        }
        
        if(PlayerBan.isMuted(ip)) {
            s.sendMessage(Language.getMessage(BungeeBans.getPlugin(), uuid_by_sender, "command.ipmute.is-muted", "IP %ip% is already muted.", new String[] {"%ip%"}, new String[] {ip}));
            return;
        }
        
        PlayerBan pb = new PlayerBan(BungeeBans.getTheIP(ip, true), BungeeBans.getTheIP(ip, true), msg, 0L, "mute");
        MySQL.addBan(pb, uuid_by_sender.toString());
        
        BungeeBans.MessagePlayerByIP(BungeeBans.getTheIP(ip, false), "command.ipmute.muted", "Your address was muted by %by% because of %message%.", new String[] {"%message%", "%by%"}, new String[] {msg, s.getName()});
        BungeeBans.TeamInform("command.ipmute.message", "%ip% muted by %by% while %message%.", new String[] {"%message%", "%ip%", "%by%"}, new String[] {msg, BungeeBans.getTheIP(ip, true), s.getName()});
        Tasks.restartMuteTask();
    }
}
