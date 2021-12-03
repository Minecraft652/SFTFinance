
package com.sixfivetwo.sftfinance;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.utils.Convert;
import org.web3j.utils.Convert.Unit;

import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

class BlockchainData {
    public String chainname;
    public String httpurl;
    public Long chainid;
    public String symbol;
    public Web3j web3j;

    BlockchainData(String chainname, String httpurl, long chainid, String symbol) {
        this.chainname = chainname;
        this.httpurl = httpurl;
        this.chainid = chainid;
        this.symbol = symbol;
        web3j = APILibrary.getWeb3j(httpurl);
    }
}

class PlayerWalletData {
    public String playerid;
    public String privatekey;
    public String publickey;
    public String fromaddress;
    public Credentials creds;
    public String seed;
    public boolean has;

    PlayerWalletData(String playerid) {
        try {
            this.playerid = playerid;
            PreparedStatement statement = Main.conn.prepareStatement("select * from wallets where PlayerID = ?;");
            statement.setString(1, playerid);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                privatekey = rs.getString("PrivateKey");
                publickey = rs.getString("PublicKey");
                fromaddress = rs.getString("Address");
                seed = rs.getString("Seed");
            }
            rs.close();
            statement.close();
            creds = APILibrary.getCredential(privatekey, publickey);
            if (fromaddress.equals("null")) {
                has = false;
            }
            has = true;
        } catch (Exception ex) {
            has = false;
        }
    }
}

class ERC20ContractData {
    public String address;
    public String symbol;
    public String gaslimit;
    public String decimal;
    public String gasrequire;
    public boolean error;

    ERC20ContractData(Map<Integer, String> FileMap) {
        try {
            address = FileMap.get(1);
            symbol = FileMap.get(2);
            gaslimit = FileMap.get(3);
            decimal = FileMap.get(4);
            BigInteger gasprice = Main.chainlibrary.web3j.ethGasPrice().send().getGasPrice();
            BigInteger biggaslimit = new BigInteger(gaslimit);
            gasrequire = Convert.fromWei(gasprice.multiply(biggaslimit).toString(), Unit.ETHER).toString();
        } catch (Exception ex) {
            error = true;
        }
    }
}

class ExchangeData {
    public String tokentype;
    public String price;
    public String executecommand;

    ExchangeData(Map<Integer, String> FileMap) {
        tokentype = FileMap.get(1);
        price = FileMap.get(2);
        executecommand = FileMap.get(3);
    }
}

class ReceiptData {
    public String type;
    public String fromAddress;
    public String toAddress;
    public String value;
    public String gasLimit;
    public String gasPrice;

    ReceiptData(String type, String fromAddress, String toAddress, String value, String gasLimit, String gasPrice) {
        this.type = type;
        this.fromAddress = fromAddress;
        this.toAddress = toAddress;
        this.value = value;
        this.gasLimit = gasLimit;
        this.gasPrice = gasPrice;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setGasLimit(String gasLimit) {
        this.gasLimit = gasLimit;
    }

    public void setGasPrice(String gasPrice) {
        this.gasPrice = gasPrice;
    }
}

class HelpPageData {
    public String front;
    public String comment1;
    public String comment2;
    public String comment3;
    public String comment4;
    public String comment5;
    public String comment6;
    public String comment7;
    public String comment8;
    public String comment9;
    public String comment10;

    HelpPageData(Map<Integer, String> FileMap) {
        front = FileMap.get(1);
        comment1 = FileMap.get(2);
        comment2 = FileMap.get(3);
        comment3 = FileMap.get(4);
        comment4 = FileMap.get(5);
        comment5 = FileMap.get(6);
        comment6 = FileMap.get(7);
        comment7 = FileMap.get(8);
        comment8 = FileMap.get(9);
        comment9 = FileMap.get(10);
        comment10 = FileMap.get(11);
    }
}
