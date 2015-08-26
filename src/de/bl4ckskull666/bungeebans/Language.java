/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.bungeebans;
/*
import com.maxmind.geoip.LookupService;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.zip.GZIPInputStream;
import net.craftminecraft.bungee.bungeeyaml.bukkitapi.file.FileConfiguration;
import net.craftminecraft.bungee.bungeeyaml.bukkitapi.file.YamlConfiguration;
import net.md_5.bungee.api.ChatColor;
*/
/**
 *
 * @author Pappi
 */
public final class Language {
    /*
    public static void loadLanguages() {
        if(!BungeeBans.getPlugin().getDataFolder().exists())
            BungeeBans.getPlugin().getDataFolder().mkdir();
        
        File lFold = new File(BungeeBans.getPlugin().getDataFolder(), "languages");
        if(!lFold.exists())
            lFold.mkdir();

        clearLanguages();
        for(File l: lFold.listFiles()) {
            FileConfiguration ul = YamlConfiguration.loadConfiguration(l);
            String name = l.getName();
            int pos = name.lastIndexOf(".");
            if (pos > 0) {
                name = name.substring(0, pos);
            }
            setLanguage(name.toLowerCase(), ul);
            BungeeBans.getPlugin().getLogger().log(Level.INFO, "Load Language {0}", l.getName());
        }
    } 
    
    public static String getLangCode(InetAddress ip) {
        File f = new File(BungeeBans.getPlugin().getDataFolder(), "GeoIp.dat");
        if(!f.exists())
            checkGeoIP();
        
        try {
            LookupService cl = new LookupService(f, LookupService.GEOIP_MEMORY_CACHE);
            String code = cl.getCountry(ip).getCode();
            cl.close();
            return code;
        } catch(IOException e) {
            BungeeBans.getPlugin().getLogger().log(Level.WARNING, "Cant find Country for IP " + ip.getHostAddress(), e);
            return "en";
        }
    }
    
    public static String getMessage(String uuid, String path, String def, HashMap<String, String> replaces) {
        FileConfiguration f = getLanguage(uuid);
        if(f == null) {
            if(replaces != null) {
                if(replaces.size() > 0) {
                    for(Map.Entry<String, String> e : replaces.entrySet()) {
                        def = def.replace(e.getKey(), e.getValue());
                    }
                }
            }
            return ChatColor.translateAlternateColorCodes('&', def);
        }
        
        String out = f.getString(path, def);
        if(replaces != null) {
            if(replaces.size() > 0) {
                for(Map.Entry<String, String> e : replaces.entrySet()) {
                    out = out.replace(e.getKey(), e.getValue());
                }
            }
        }
        return ChatColor.translateAlternateColorCodes('&', def);
    }
    
    public static String getMessage(String uuid, String path, String def, String[] search, String[] replace) {
        FileConfiguration f = getLanguage(uuid);
        if(f == null) {
            if(search.length > 0 && replace.length > 0 && search.length == replace.length) {
                for(int i = 0; i < search.length; i++)
                    def = def.replace(search[i], replace[i]);
            }
            return ChatColor.translateAlternateColorCodes('&', def);
        }
        String out = f.getString(path, def);
        if(search.length > 0 && replace.length > 0 && search.length == replace.length) {
            for(int i = 0; i < search.length; i++)
                out = out.replace(search[i], replace[i]);
        }
        return ChatColor.translateAlternateColorCodes('&', out);
    }
    
    public static String getMessage(String uuid, String path, String def) {
        FileConfiguration f = getLanguage(uuid);
        if(f == null)
            return ChatColor.translateAlternateColorCodes('&', def);
        
        return ChatColor.translateAlternateColorCodes('&', f.getString(path, def));
    }
    
    public static List<String> getMessages(String uuid, String path) {
        FileConfiguration f = getLanguage(uuid);
        List<String> temp = new ArrayList<>();
        if(f == null || !f.isList(path)) {
            temp.add("Admin is to lame to set a default language or it is wrong configurated.");
            return temp;
        }
        return f.getStringList(path);
    }
    
    public static String getObjection(String uuid) {
        FileConfiguration f = getLanguage(uuid);
        if(f == null)
            return ChatColor.translateAlternateColorCodes('&', " - If you would like to raise an objection against it, turn thou via ticket to the Support.");
        
        return ChatColor.translateAlternateColorCodes('&', f.getString("objection", " - If you would like to raise an objection against it, turn thou via ticket to the Support."));
    }
    
    private static int _geoChecks = 0;
    public static void checkGeoIP() {
        File f = new File(BungeeBans.getPlugin().getDataFolder(), "GeoIp.dat");
        if(BungeeBans.getPlugin().getConfig().isLong("last-geo-update")) {
            long lastUpdate = BungeeBans.getPlugin().getConfig().getLong("last-geo-update");
            if((System.currentTimeMillis()-lastUpdate) < (1000*60*60*24*14))
                return;
        }
        
        if(f.exists())
            f.delete();
            
        if(!f.exists()) {
            try {
                URL url = new URL("http://geolite.maxmind.com/download/geoip/database/GeoLiteCountry/GeoIP.dat.gz");
                InputStream in = url.openConnection().getInputStream();
                File fgz = new File(BungeeBans.getPlugin().getDataFolder(), "GeoIp.dat.gz");
                OutputStream out = new FileOutputStream(fgz);
                byte[] buffer = new byte[1024];
                int i;
                while ((i = in.read(buffer)) > 0) {
                    out.write(buffer, 0, i);
                }
                in.close();
                out.close();
                
                fgz = new File(BungeeBans.getPlugin().getDataFolder(), "GeoIp.dat.gz");
                if(fgz.exists()) {
                    GZIPInputStream ingz = new GZIPInputStream(
                            new FileInputStream(fgz)
                    );

                    // Open the output file
                    OutputStream outgz = new FileOutputStream(f);

                    // Transfer bytes from the compressed file to the output file
                    byte[] buf = new byte[1024];
                    while ((i = ingz.read(buf)) > 0) {
                        outgz.write(buf, 0, i);
                    }
                    ingz.close();
                    outgz.close();
                }
                fgz.delete();
                BungeeBans.getPlugin().getConfig().set("last-geo-update", System.currentTimeMillis());
                BungeeBans.getPlugin().saveConfig();
            } catch(IOException e) {
                _geoChecks++;
                if(_geoChecks < 10)
                    checkGeoIP();
                else
                    BungeeBans.getPlugin().getLogger().log(Level.WARNING, "Cant load GeoIp.dat file", e);
            }
        }
    }
    
    private final static HashMap<String, FileConfiguration> _languages = new HashMap<>();
    public static void setLanguage(String name, FileConfiguration fc) {
        _languages.put(name.toLowerCase(), fc);
    }
    
    public static FileConfiguration getLanguage(String uuid) {
        if(_playerLanguage.containsKey(uuid)) {
            if(_languages.containsKey(_playerLanguage.get(uuid)))
               return _languages.get(_playerLanguage.get(uuid));
        }
        
        if(_languages.containsKey(uuid))
            return _languages.get(uuid);
        
        if(BungeeBans.getPlugin().getConfig().isString("language." + uuid.toLowerCase())) {
            if(_languages.containsKey(BungeeBans.getPlugin().getConfig().getString("language." + uuid.toLowerCase())))
                return _languages.get(BungeeBans.getPlugin().getConfig().getString("language." + uuid.toLowerCase()));
        }
        
        if(_languages.containsKey(BungeeBans.getPlugin().getConfig().getString("language.default", "en")))
            return _languages.get(BungeeBans.getPlugin().getConfig().getString("language.default", "en"));
        
        return null;
    }
    
    public static void clearLanguages() {
        _languages.clear();
    }
    
    public static HashMap<String, String> _playerLanguage = new HashMap<>();
    public static void setPlayerLanguage(String uuid, String language) {
        if(BungeeBans.getPlugin().getConfig().isString("language." + language.toLowerCase()))
            language = BungeeBans.getPlugin().getConfig().getString("language." + language.toLowerCase());
        
        if(!_languages.containsKey(language.toLowerCase()))
            language = BungeeBans.getPlugin().getConfig().getString("language.default", "en");
        
        _playerLanguage.put(uuid, language.toLowerCase());
    }
    
    public static String getPlayerLanguage(String uuid) {
        if(_playerLanguage.containsKey(uuid))
            return _playerLanguage.get(uuid);
        return BungeeBans.getPlugin().getConfig().getString("language.default", "en");
    }
    */
}
