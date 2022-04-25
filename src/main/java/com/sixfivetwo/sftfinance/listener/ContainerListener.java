package com.sixfivetwo.sftfinance.listener;

import com.sixfivetwo.sftfinance.APILibrary;
import com.sixfivetwo.sftfinance.Main;
import com.sixfivetwo.sftfinance.datalibrary.InventoryHolderData;
import com.sixfivetwo.sftfinance.datalibrary.InventoryHolderEditData;
import com.sixfivetwo.sftfinance.datalibrary.PlayerDealData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ContainerListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        try {
            Player player = (Player) e.getWhoClicked();
            InventoryView inv = player.getOpenInventory();
            if (e.getAction().equals(InventoryAction.NOTHING)) {
                return;
            }
            if (e.getClickedInventory().getHolder() instanceof InventoryHolderData) {
                ItemStack is = e.getCurrentItem();
                ItemStack barrier = APILibrary.getTargetItem(1);
                if (null == is) {
                    return;
                }
                if (is.equals(barrier)) {
                    e.setCancelled(true);
                    inv.close();
                    return;
                }
                ItemStack emerald_block = APILibrary.getTargetItem(2);
                if (is.equals(emerald_block)) {
                    e.setCancelled(true);
                    if (!APILibrary.checkTradeListAmount(APILibrary.getTradeList(4, player.getName(), Main.conn), Main.fileconfig.getInt("TradeAmountLimit"))) {
                        player.sendMessage(Main.SFTInfo + Main.prop.getProperty("nofitymaxlimit"));
                        inv.close();
                        return;
                    }
                    List<ItemStack> details = APILibrary.getItemData(inv.getTopInventory());
                    if (details.isEmpty()) {
                        inv.close();
                        return;
                    }
                    InventoryHolderData invd = (InventoryHolderData) inv.getTopInventory().getHolder();
                    PlayerDealData pdd = new PlayerDealData(invd.type, invd.value, invd.fromid, invd.toid, details);
                    pdd.insertData(Main.conn);
                    pdd.getID(Main.conn);
                    invd.setHas(true);
                    APILibrary.sendMessageFromToPlayer(invd, pdd.id);
                    inv.close();
                    return;
                }
                ItemStack iron_fence = APILibrary.getTargetItem(3);
                if (is.equals(iron_fence)) {
                    e.setCancelled(true);
                    return;
                }
            }
            if (e.getClickedInventory().getHolder() instanceof InventoryHolderEditData) {
                ItemStack is = e.getCurrentItem();
                ItemStack barrier = APILibrary.getTargetItem(1);
                if (null == is) {
                    return;
                }
                if (is.equals(barrier)) {
                    e.setCancelled(true);
                    inv.close();
                    return;
                }
                ItemStack emerald_block = APILibrary.getTargetItem(2);
                if (is.equals(emerald_block)) {
                    e.setCancelled(true);
                    List<ItemStack> details = APILibrary.getItemData(inv.getTopInventory());
                    InventoryHolderEditData invd = (InventoryHolderEditData) inv.getTopInventory().getHolder();
                    PlayerDealData pdd = new PlayerDealData(invd.id, Main.conn);
                    pdd.setDetails(details);
                    pdd.updateData(Main.conn);
                    pdd.getID(Main.conn);
                    invd.setHas(true);
                    APILibrary.sendMessageFromToPlayerUpdate(invd, pdd.fromid, pdd.toid);
                    inv.close();
                    return;
                }
                ItemStack iron_fence = APILibrary.getTargetItem(3);
                if (is.equals(iron_fence)) {
                    e.setCancelled(true);
                    return;
                }
            }
        } catch (Exception ex) {
            return;
        }
    }
}