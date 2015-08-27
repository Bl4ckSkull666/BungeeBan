/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.bungeebans.listener;

import de.bl4ckskull666.bungeebans.BungeeBans;
import de.bl4ckskull666.bungeebans.classes.PlayerBan;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.ServerPing.Players;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

/**
 *
 * @author PapaHarni
 */
public class PingListener implements Listener {
    Favicon _favi = null;
    
    public PingListener() {
        String fav = BungeeBans.getPlugin().getConfig().getString("favicon", "");
        try {
            
            File f = new File(fav);
            BufferedImage bi = ImageIO.read(f);
            if(bi == null)
                return;
                
            _favi = Favicon.create(bi);
        } catch(IOException e) {
            BungeeBans.getPlugin().getLogger().log(Level.WARNING, "{0} is no correct path to an image", fav);
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onProxyPing(ProxyPingEvent e) {
        PlayerBan pb = BungeeBans.getIpBanned(e.getConnection().getAddress().getAddress().getHostAddress());
        if(e.getConnection().getUniqueId() != null)
            BungeeBans.getPlugin().getLogger().log(Level.INFO, "UUID in ProxyPing is not null");
        if(e.getConnection().getName() != null)
            BungeeBans.getPlugin().getLogger().log(Level.INFO, "Name in ProxyPing is not null");
        
        if(pb == null)
            return;
        
        String lang = Language.getLanguageByAddress(e.getConnection().getAddress().getAddress());
        String message;
        
        if(pb.isTemporary())
            message = Language.getMsg(BungeeBans.getPlugin(), lang, "function.ping.temp-banned", "You're address banned because of %message% until %date% on %time%.", new String[] {"%message%", "%date%", "%time%"}, new String[] {pb.getMessage(), BungeeBans.getDateByCalendar(pb.getEnding()), BungeeBans.getTimeByCalendar(pb.getEnding())});
        else
            message = Language.getMsg(BungeeBans.getPlugin(), lang, "function.ping.banned", "You're address is banned because of %message%.", new String[] {"%message%"}, new String[] {pb.getMessage()});
        
        Players ps = e.getResponse().getPlayers();
        ps.setMax(0);
        ps.setOnline(0);
        e.setResponse(
            new ServerPing(
                e.getResponse().getVersion(),
                ps,
                message,
                _favi
            )
        );
    }
}
