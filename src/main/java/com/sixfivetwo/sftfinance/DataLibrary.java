
package com.sixfivetwo.sftfinance;

import java.io.IOException;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.util.Map;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.utils.Convert;
import org.web3j.utils.Convert.Unit;

class BlockchainData {
    public String chainname;
    public String httpurl;
    public Long chainid;
    public String symbol;
    public Web3j web3j;
    BlockchainData(String chainname, String httpurl, long chainid, String symbol) throws Exception {
        this.chainname = chainname;
        this.httpurl = httpurl;
        this.chainid = chainid;
        this.symbol = symbol;
        web3j = APILibrary.GetWeb3j(httpurl);
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
    PlayerWalletData(String playerid) throws Exception {
        this.playerid = playerid;
        ResultSet rs = Main.statement.executeQuery("select * from wallets where PlayerID = "+"'"+playerid+"';");
        try {
            while (rs.next()) {
                privatekey = rs.getString("PrivateKey");
                publickey = rs.getString("PublicKey");
                fromaddress = rs.getString("Address");
                seed = rs.getString("Seed");
            }
            rs.close();
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
    ERC20ContractData(Map<Integer, String> FileMap) throws IOException {
        address = FileMap.get(1);
        symbol = FileMap.get(2);
        gaslimit = FileMap.get(3);
        decimal = FileMap.get(4);
        BigInteger gasprice = Main.chainlibrary.web3j.ethGasPrice().send().getGasPrice();
        BigInteger biggaslimit = new BigInteger(gaslimit);
        gasrequire = Convert.fromWei(gasprice.multiply(biggaslimit).toString(), Unit.ETHER).toString();
    }
}

class ExchangeData {
    public String tokentype;
    public String price;
    public String executecommand;
    ExchangeData(Map<Integer, String> FileMap) throws IOException {
        tokentype = FileMap.get(1);
        price = FileMap.get(2);
        executecommand = FileMap.get(3);
    }
}