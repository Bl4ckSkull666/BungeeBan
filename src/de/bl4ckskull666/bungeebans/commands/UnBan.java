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
public class UnBan extends Command {
    
    public UnBan() {
        super("unban", "bungeebans.unban");
    }

    @Override
    public void execute(CommandSender s, String[] a) {
        UUID uuid_by_sender = BungeeBans.getPlayer(s.getName()) == null?UUID.fromString("00000000-0000-0000-0000-000000000000"):BungeeBans.getPlayer(s.getName()).getUniqueId();
        if(a.length < 1) {
            Language.sendMessage(BungeeBans.getPlugin(), s, "command.unban.wrongformat", "Please add a Username or IP to unban.");
            return;
        }
        
        PlayerBan pb = BungeeBans.getBanByName(a[0]);
        if(pb == null)
            pb = BungeeBans.getIpBanned(a[0]);
        
        if(pb == null) {
            Language.sendMessage(BungeeBans.getPlugin(), s, "command.unban.unknown", "Is it really banned? Cant find one.");
            return;
        }
        String unbannedName = pb.getName();
        if(MySQL.delBan(pb)) {
            if(PlayerBan.getBans().remove(pb.getUUID(), pb)) {
                BungeeBans.TeamInform("command.unban.successful", "%by% has unbanned %name%.", new String[] {"%name%", "%by%"}, new String[] {unbannedName, s.getName()});        
                Tasks.restartBanTask();
                return;
            }
            MySQL.addBan(pb, s.getName());
        }
        Language.sendMessage(BungeeBans.getPlugin(), s, "command.unban.error", "Gets an error on remove ban. Please try again.");
    }
}