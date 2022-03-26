package com.sixfivetwo.sftfinance.listener;

import com.sixfivetwo.sftfinance.APILibrary;
import com.sixfivetwo.sftfinance.Main;
import com.sixfivetwo.sftfinance.datalibrary.InventoryHolderData;
import com.sixfivetwo.sftfinance.datalibrary.InventoryHolderEditData;
import com.sixfivetwo.sftfinance.datalibrary.PlayerDealData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InventoryCloseListener implements Listener {
    @EventHandler
    public void onClose(InventoryCloseEvent e) throws SQLException {
        if (e.getInventory().getHolder() instanceof InventoryHolderData) {
            InventoryHolderData invd = (InventoryHolderData) e.getInventory().getHolder();
            if (!invd.has) {
                List<ItemStack> details = APILibrary.getItemData(e.getInventory());
                for (ItemStack itemStack : details) {
                    e.getPlayer().getInventory().addItem(itemStack);
                }
            }
        }
        if (e.getInventory().getHolder() instanceof InventoryHolderEditData) {
            InventoryHolderEditData invd = (InventoryHolderEditData) e.getInventory().getHolder();
            if (!invd.has) {
                List<ItemStack> details = APILibrary.getItemData(e.getInventory());
                PlayerDealData pdd = new PlayerDealData(invd.id, Main.conn);
                pdd.setDetails(details);
                if (details.isEmpty()) {
                    pdd.deleteData(Main.conn);
                    List<String> fromMessage = new ArrayList<>();
                    List<String> toMessage = new ArrayList<>();
                    fromMessage.add(Main.SFTInfo + Main.prop.getProperty("dealdelete") + invd.id);
                    toMessage.add(Main.SFTInfo + Main.prop.getProperty("dealdelete") + invd.id);
                    invd.nofity(fromMessage, toMessage, pdd.fromid, pdd.toid);
                } else {
                    pdd.updateData(Main.conn);
                    pdd.getID(Main.conn);
                    APILibrary.sendMessageFromToPlayerUpdate(invd, pdd.fromid, pdd.toid);
                }
            }
        }
    }
}