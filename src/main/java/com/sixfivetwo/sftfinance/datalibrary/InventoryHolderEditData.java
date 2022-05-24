package com.sixfivetwo.sftfinance.datalibrary;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class InventoryHolderEditData implements InventoryHolder {
    public boolean has;
    public int id;

    public InventoryHolderEditData(int id) {
        this.id = id;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }

    public void nofity(List<String> fromMessage, List<String> toMessage, PlayerWalletData fromid, PlayerWalletData toid) {
        try {
            for (String message : fromMessage) {
                Bukkit.getPlayer(fromid.playerid).sendMessage(message);
            }
            for (String message : toMessage) {
                Bukkit.getPlayer(toid.playerid).sendMessage(message);
            }
        } catch (Exception ignored) {}
    }

    public boolean isHas() {
        return has;
    }

    public int getId() {
        return id;
    }

    public void setHas(boolean has) {
        this.has = has;
    }

    public void setId(int id) {
        this.id = id;
    }
}
