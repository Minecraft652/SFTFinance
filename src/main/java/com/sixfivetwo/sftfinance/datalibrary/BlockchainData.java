package com.sixfivetwo.sftfinance.datalibrary;

import com.sixfivetwo.sftfinance.APILibrary;
import org.web3j.protocol.Web3j;

public class BlockchainData {
    public String chainname;
    public String httpurl;
    public Long chainid;
    public String symbol;
    public Web3j web3j;

    public BlockchainData(String chainname, String httpurl, long chainid, String symbol) {
        this.chainname = chainname;
        this.httpurl = httpurl;
        this.chainid = chainid;
        this.symbol = symbol;
        web3j = APILibrary.getWeb3j(httpurl);
    }
}
