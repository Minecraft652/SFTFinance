package com.sixfivetwo.sftfinance.datalibrary;

import com.sixfivetwo.sftfinance.Main;
import org.web3j.utils.Convert;

import java.math.BigInteger;
import java.util.Map;

public class ERC20ContractData {
    public String address;
    public String symbol;
    public String gaslimit;
    public String decimal;
    public String gasrequire;
    public boolean error;

    public ERC20ContractData(Map<Integer, String> FileMap, BlockchainData blockchainData) {
        try {
            address = FileMap.get(1);
            symbol = FileMap.get(2);
            gaslimit = FileMap.get(3);
            decimal = FileMap.get(4);
            BigInteger gasprice = blockchainData.web3j.ethGasPrice().send().getGasPrice();
            BigInteger biggaslimit = new BigInteger(gaslimit);
            gasrequire = Convert.fromWei(gasprice.multiply(biggaslimit).toString(), Convert.Unit.ETHER).toString();
        } catch (Exception ex) {
            error = true;
        }
    }
}