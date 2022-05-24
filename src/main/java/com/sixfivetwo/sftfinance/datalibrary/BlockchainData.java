package com.sixfivetwo.sftfinance.datalibrary;

import com.sixfivetwo.sftfinance.APILibrary;
import org.web3j.protocol.Web3j;

public class BlockchainData {
    public String chainname;
    public String httpurl;
    public Long chainid;
    public String symbol;
    public Web3j web3j;

    public BlockchainData() {}

    public BlockchainData(String chainname, String httpurl, long chainid, String symbol, Web3j web3j) {
        this.chainname = chainname;
        this.httpurl = httpurl;
        this.chainid = chainid;
        this.symbol = symbol;
        this.web3j = web3j;
    }

    public String getChainname() {
        return chainname;
    }

    public String getHttpurl() {
        return httpurl;
    }

    public String getSymbol() {
        return symbol;
    }

    public Long getChainid() {
        return chainid;
    }

    public Web3j getWeb3j() {
        return web3j;
    }

    public void setChainid(Long chainid) {
        this.chainid = chainid;
    }

    public void setChainname(String chainname) {
        this.chainname = chainname;
    }

    public void setHttpurl(String httpurl) {
        this.httpurl = httpurl;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setWeb3j(Web3j web3j) {
        this.web3j = web3j;
    }
}
