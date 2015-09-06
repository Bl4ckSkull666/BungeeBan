/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.bungeebans.classes;

import java.util.Calendar;
import java.util.HashMap;

/**
 *
 * @author PapaHarni
 */
public final class PlayerBan {
    private final String _uuid;
    private final String _lastName;
    private final String _banMessage;
    private final String _type;
    private final Calendar _calSince = Calendar.getInstance();;
    private final Calendar _calEnd = Calendar.getInstance();;
    
    public PlayerBan(String uuid, String lastName, String banMsg, Long t, String ty) {
         _uuid = uuid;
        _lastName = lastName;
        _banMessage = banMsg;
        _type = ty;
        _calSince.setTimeInMillis(System.currentTimeMillis());
        _calEnd.setTimeInMillis(System.currentTimeMillis());
        
        if(t > 0) {
            _calEnd.setTimeInMillis(System.currentTimeMillis()+(t*1000));
        } else
            _calEnd.setTimeInMillis(_calSince.getTimeInMillis());

        PlayerBan pb = this;
        if(_type.equalsIgnoreCase("mute"))
            _mutes.put(uuid, pb);
        else
            _bans.put(uuid, pb);
    }
    
    public PlayerBan(String uuid, String lastName, String banMsg, Calendar begin, Calendar ending, String ty) {
        _uuid = uuid;
        _lastName = lastName;
        _banMessage = banMsg;
        _type = ty;
        _calSince.setTimeInMillis(begin.getTimeInMillis());
        _calEnd.setTimeInMillis(ending.getTimeInMillis());
        
        PlayerBan pb = this;
        if(_type.equalsIgnoreCase("mute"))
            _mutes.put(uuid, pb);
        else
            _bans.put(uuid, pb);
    }
    
    public String getUUID() {
        return _uuid;
    }
    
    public String getName() {
        return _lastName;
    }
    
    public String getMessage() {
        return _banMessage;
    }
    
    public String getType() {
        return _type;
    }
    
    public boolean isTemporary() {
        return (_calEnd != null && _calEnd.after(_calSince));
    }
    
    public long endInSeconds() {
        return (((_calEnd.getTimeInMillis()-_calSince.getTimeInMillis())-(_calEnd.getTimeInMillis()-System.currentTimeMillis()))/1000);
    }
    
    public Calendar getSince() {
        return _calSince;
    }
    
    public Calendar getEnding() {
        return _calEnd;
    }

    
    private static final HashMap<String, PlayerBan> _bans = new HashMap<>();
    private static final HashMap<String, PlayerBan> _mutes = new HashMap<>();
    
    public static HashMap<String, PlayerBan> getBans() {
        return _bans;
    }
    
    public static PlayerBan getBan(String uuid) {
        if(!_bans.containsKey(uuid))
            return null;
        return _bans.get(uuid);
    }
    
    public static HashMap<String, PlayerBan> getMutes() {
        return _mutes;
    }
    
    public static PlayerBan getMute(String uuid) {
        if(!_mutes.containsKey(uuid))
            return null;
        return _mutes.get(uuid);
    }
}
