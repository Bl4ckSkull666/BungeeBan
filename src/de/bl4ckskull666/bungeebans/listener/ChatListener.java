/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.bungeebans.listener;

import de.bl4ckskull666.bungeebans.BungeeBans;
import de.bl4ckskull666.bungeebans.classes.PlayerBan;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

/**
 *
 * @author PapaHarni
 */
public class ChatListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onChatEvent(ChatEvent e) {
        ProxiedPlayer sender = BungeeBans.getPlayer(e.getSender());
        if(sender == null)
            return;
        
        PlayerBan pb = PlayerBan.getMute(sender.getUniqueId().toString());
        if(pb == null)
            pb = BungeeBans.getMuteByName(sender.getName());
        if(pb == null)
            pb = BungeeBans.getIpMuted(e.getSender().getAddress().getAddress().getHostAddress());
        
        if(pb == null)
            return;
        
        if(e.isCommand()) {
            if(BungeeBans.getPlugin().getConfig().getStringList("forbidden-commands-on-mute").isEmpty())
                return;

            String[] msg = e.getMessage().split(" ");
            if(msg[0].startsWith("/"))
                msg[0] = msg[0].substring(1);
            
            if(!BungeeBans.getPlugin().getConfig().getStringList("forbidden-commands-on-mute").contains(msg[0].toLowerCase()))
                return;
        }
        
        if(pb.isTemporary())
           sender.sendMessage(new TextComponent[] {Language.getText(BungeeBans.getPlugin(), sender.getUniqueId(), "function.temp-muted", "You're muted because %message% until %date% on %time%.", new String[] {"%message%", "%date%", "%time%"}, new String[] {pb.getMessage(), BungeeBans.getDateByCalendar(pb.getEnding()), BungeeBans.getTimeByCalendar(pb.getEnding())}), Language.getText(BungeeBans.getPlugin(), sender.getUniqueId(), "objection", "")});
        else
           sender.sendMessage(new TextComponent[] {Language.getText(BungeeBans.getPlugin(), sender.getUniqueId(), "function.muted", "You're muted because %message%.", new String[] {"%message%"}, new String[] {pb.getMessage()}), Language.getText(BungeeBans.getPlugin(), sender.getUniqueId(), "objection", "")});
        e.setMessage("");
        e.setCancelled(true);
    }
}