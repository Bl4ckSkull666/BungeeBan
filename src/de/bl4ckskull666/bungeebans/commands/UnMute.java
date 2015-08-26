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
public class UnMute extends Command {
    
    public UnMute() {
        super("unmute", "bungeebans.unmute");
    }

    @Override
    public void execute(CommandSender s, String[] a) {
        UUID uuid_by_sender = BungeeBans.getPlayer(s.getName()) == null?UUID.fromString("00000000-0000-0000-0000-000000000000"):BungeeBans.getPlayer(s.getName()).getUniqueId();
        if(a.length < 1) {
            s.sendMessage(Language.getMessage(BungeeBans.getPlugin(), uuid_by_sender, "command.unmute.wrongformat", "Please add a Username or IP to unmute."));
            return;
        }
        
        PlayerBan pb = BungeeBans.getMuteByName(a[0]);
        if(pb == null)
            pb = BungeeBans.getIpMuted(a[0]);
        
        if(pb == null) {
            s.sendMessage(Language.getMessage(BungeeBans.getPlugin(), uuid_by_sender, "command.unmute.unknown", "Is it really muted? Cant find one."));
            return;
        }
        
        String unmutedName = pb.getName();
        if(MySQL.delBan(pb)) {
            if(PlayerBan.getMutes().remove(pb.getUUID(), pb)) {
                BungeeBans.TeamInform("command.unmute.successful", "%by% has unuted %name%.", new String[] {"%name%", "%by%"}, new String[] {unmutedName, s.getName()});        
                Tasks.restartMuteTask();
                return;
            }
            MySQL.addBan(pb, s.getName());
        }
        s.sendMessage(Language.getMessage(BungeeBans.getPlugin(), uuid_by_sender, "command.unmute.error", "Gets an error on remove mute. Please try again."));
    }
}
