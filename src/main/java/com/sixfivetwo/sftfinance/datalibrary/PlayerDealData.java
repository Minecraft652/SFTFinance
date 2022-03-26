package com.sixfivetwo.sftfinance.datalibrary;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sixfivetwo.sftfinance.APILibrary;
import com.sixfivetwo.sftfinance.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class PlayerDealData {
    public int id;
    public String type;
    public String value;
    public PlayerWalletData fromid;
    public PlayerWalletData toid;
    public List<ItemStack> details;
    public String allStrDetails;

    public PlayerDealData(String type, String value, PlayerWalletData fromid, PlayerWalletData toid, List<ItemStack> details) {
        this.type = type;
        this.value = value;
        this.fromid = fromid;
        this.toid = toid;
        this.details = details;
    }

    public PlayerDealData(int id, Connection connection) throws SQLException {
        this.id = id;
        List<ItemStack> listDetails = new ArrayList<>();
        PreparedStatement statement = connection.prepareStatement("select * from transactions where ID = ?;");
        statement.setInt(1, id);
        ResultSet rs = statement.executeQuery();
        while (rs.next()) {
            this.type = rs.getString("tokentype");
            this.value = rs.getString("value");
            this.fromid = new PlayerWalletData(rs.getString("fromid"));
            this.toid = new PlayerWalletData(rs.getString("toid"));
            this.allStrDetails = rs.getString("details");
            List<String> strDetails = new Gson().fromJson(allStrDetails, new TypeToken<List<String>>(){}.getType());
            for (String strIs : strDetails) {
                ItemStack is = ItemStack.deserialize(new org.json.JSONObject(strIs).toMap());
                listDetails.add(is);
            }
        }
        this.details = listDetails;
    }

    public void setDetails(List<ItemStack> details) {
        this.details = details;
    }

    public void insertData(Connection conn) throws SQLException {
        List<String> strDetails = new ArrayList<>();
        for (ItemStack is : details) {
            String strIs = JSONObject.toJSONString(is.serialize());
            strDetails.add(strIs);
        }
        this.allStrDetails = JSONArray.toJSONString(strDetails);
        APILibrary.advancedInsertData(fromid.playerid, toid.playerid, type, value, allStrDetails);
    }

    public void updateData(Connection conn) throws SQLException {
        List<String> strDetails = new ArrayList<>();
        for (ItemStack is : details) {
            String strIs = JSONObject.toJSONString(is.serialize());
            strDetails.add(strIs);
        }
        this.allStrDetails = JSONArray.toJSONString(strDetails);
        APILibrary.advancedUpdateData(id, allStrDetails, Main.conn);
    }

    public void deleteData(Connection conn) throws SQLException {
        PreparedStatement statement = conn.prepareStatement("delete from transactions where ID = ?");
        statement.setInt(1, this.id);
        statement.executeUpdate();
        statement.close();
    }

    public void denyDeal(Connection conn) throws SQLException {
        PreparedStatement statement = conn.prepareStatement("update transactions set toid = ? where id = ?");
        statement.setString(1, "NONE");
        statement.setInt(2, this.id);
        statement.executeUpdate();
        statement.close();
    }

    public Inventory getInventoryFromItemData() {
        Inventory inv = APILibrary.createInventory(new InventoryHolderEditData(id));
        for (ItemStack is : details) {
            inv.addItem(is);
        }
        return inv;
    }

    public void getID(Connection conn) throws SQLException {
        PreparedStatement statement = conn.prepareStatement("select ID from transactions where tokentype = ? and fromid = ? and toid = ? and details = ? and value = ?");
        statement.setString(1, type);
        statement.setString(2, fromid.playerid);
        statement.setString(3, toid.playerid);
        statement.setString(4, allStrDetails);
        statement.setString(5, value);
        ResultSet rs = statement.executeQuery();
        while (rs.next()) {
            this.id = rs.getInt("ID");
        }
    }

    public List<String> strListItem() {
        List<String> list = new ArrayList<>();
        for (ItemStack is : details) {
            String strIs = is.toString().replaceAll("ItemStack","");
            list.add(strIs);
        }
        return list;
    }
}