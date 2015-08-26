/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.bungeebans.commands;

import de.bl4ckskull666.bungeebans.BungeeBans;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import java.util.UUID;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

/**
 *
 * @author PapaHarni
 */
public class BungeeBan extends Command {
    
    public BungeeBan() {
        super("BungeeBan", "bungeebans.admin");
    }

    @Override
    public void execute(CommandSender s, String[] a) {
        UUID uuid_by_sender = BungeeBans.getPlayer(s.getName()) == null?UUID.fromString("00000000-0000-0000-0000-000000000000"):BungeeBans.getPlayer(s.getName()).getUniqueId();
        if(a.length < 1) {
            s.sendMessage(Language.getMessage(BungeeBans.getPlugin(), uuid_by_sender, "command.bungeeban.wrongformat", "Please add info playername or reload."));
            return;
        }
        
        switch(a[0].toLowerCase()) {
            case "reload":
                
                break;
            case "info":
                
                break;
        }
    }
    
}
