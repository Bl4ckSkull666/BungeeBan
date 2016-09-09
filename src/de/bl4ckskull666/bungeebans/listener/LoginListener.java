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
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

/**
 *
 * @author PapaHarni
 */
public class LoginListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLoginEvent(LoginEvent e) {
        e.setCancelReason(null);
        PlayerBan pb = PlayerBan.getBan(e.getConnection().getUniqueId().toString());
        if(pb == null)
            pb = BungeeBans.getBanByName(e.getConnection().getName());
        if(pb == null)
            pb = BungeeBans.getIpBanned(e.getConnection().getAddress().getAddress().getHostAddress());
        
        if(pb == null)
            return;
        
        if(pb.isTemporary())
           e.setCancelReason(Language.getPlainText(BungeeBans.getPlugin(), e.getConnection().getUniqueId(), "function.temp-banned", "You're banned because %message% until %date% on %time%.", new String[] {"%message%", "%date%", "%time%"}, new String[] {pb.getMessage(), BungeeBans.getDateByCalendar(pb.getEnding()), BungeeBans.getTimeByCalendar(pb.getEnding())}) + Language.getPlainText(BungeeBans.getPlugin(), e.getConnection().getUniqueId(), "objection", ""));
        else
           e.setCancelReason(Language.getPlainText(BungeeBans.getPlugin(), e.getConnection().getUniqueId(), "function.banned", "You're banned because %message%.", new String[] {"%message%"}, new String[] {pb.getMessage()}) + Language.getPlainText(BungeeBans.getPlugin(), e.getConnection().getUniqueId(), "objection", ""));
        
        e.setCancelled(true);
    }
}
