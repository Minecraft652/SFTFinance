package com.sixfivetwo.sftfinance.datalibrary;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.List;

public class InventoryHolderData implements InventoryHolder {
    public boolean has;
    public String type;
    public PlayerWalletData fromid;
    public PlayerWalletData toid;
    public String value;

    public InventoryHolderData(String type, PlayerWalletData fromid, PlayerWalletData toid, String value) {
        this.type = type;
        this.fromid = fromid;
        this.toid = toid;
        this.value = value;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }

    public void setHas(boolean has) {
        this.has = has;
    }

    public void nofity(List<String> fromMessage, List<String> toMessage) {
        try {
            for (String message : fromMessage) {
                Bukkit.getPlayer(fromid.playerid).sendMessage(message);
            }
            for (String message : toMessage) {
                Bukkit.getPlayer(toid.playerid).sendMessage(message);
            }
        } catch (Exception ignored) {}
    }
}
