package com.sixfivetwo.sftfinance.datalibrary;

import com.sixfivetwo.sftfinance.APILibrary;
import com.sixfivetwo.sftfinance.Main;
import org.web3j.crypto.Credentials;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PlayerWalletData {
    public String playerid;
    public String privatekey;
    public String fromaddress;
    public Credentials creds;
    public String seed;
    public boolean has;

    public PlayerWalletData(String playerid) {
        try {
            this.playerid = playerid;
            PreparedStatement statement = Main.conn.prepareStatement("select * from wallets where PlayerID = ?;");
            statement.setString(1, playerid);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                privatekey = rs.getString("PrivateKey");
                fromaddress = rs.getString("Address");
                seed = rs.getString("Seed");
            }
            rs.close();
            statement.close();
            creds = APILibrary.getCredential(privatekey);
            if (fromaddress.equals("null")) {
                has = false;
            }
            has = true;
        } catch (Exception ex) {
            if (Main.fileconfig.getBoolean("EnableErrorPrint")) {
                ex.printStackTrace();
            }
            has = false;
        }
    }

    public boolean isHas() {
        return has;
    }

    public Credentials getCreds() {
        return creds;
    }

    public String getFromaddress() {
        return fromaddress;
    }

    public String getPlayerid() {
        return playerid;
    }

    public String getPrivatekey() {
        return privatekey;
    }

    public String getSeed() {
        return seed;
    }

    public void setCreds(Credentials creds) {
        this.creds = creds;
    }

    public void setFromaddress(String fromaddress) {
        this.fromaddress = fromaddress;
    }

    public void setHas(boolean has) {
        this.has = has;
    }

    public void setPlayerid(String playerid) {
        this.playerid = playerid;
    }

    public void setPrivatekey(String privatekey) {
        this.privatekey = privatekey;
    }

    public void setSeed(String seed) {
        this.seed = seed;
    }
}