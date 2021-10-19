package com.sixfivetwo.sftfinance;

import jnr.posix.POSIXHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.web3j.contracts.eip20.generated.ERC20;

import javax.annotation.ParametersAreNonnullByDefault;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

public class SFTCommand implements CommandExecutor {

    @Override
    @ParametersAreNonnullByDefault
    public boolean onCommand(@NotNull CommandSender commandSender,@NotNull Command command,@NotNull String label, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(Main.class), ()->{
            try {
                if (command.getName().equals("wallet")) {
                    PlayerWalletData commander = new PlayerWalletData(commandSender.getName());
                    if (commander.error) {
                        System.out.println("Create PlayerWalletData Error");
                        return;
                    }
                    switch (args.length) {
                        case 0:
                            if (commander.has) {
                                try {
                                    commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("YourAddress") + commander.fromaddress);
                                    String ethbalances = APILibrary.getEthBanlance(commander.fromaddress);
                                    commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("YourBalance") + ethbalances + " " + Main.chainlibrary.symbol);
                                    for (Entry<String, Map<Integer, String>> ERC20Map : Main.ERC20ContractMap.entrySet()) {
                                        ERC20ContractData ERC20Data = new ERC20ContractData(Main.ERC20ContractMap.get(ERC20Map.getKey()));
                                        BigDecimal sftbalances = new BigDecimal(APILibrary.getERC20Balance(ERC20Data, commander, commander.fromaddress)).divide(new BigDecimal(ERC20Data.decimal));
                                        String finalsftbalances = String.valueOf(sftbalances);
                                        commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("YourBalance") + finalsftbalances + " " + ERC20Data.symbol);
                                        commandSender.sendMessage(Main.SFTInfo + ERC20Data.symbol + " " + Main.prop.getProperty("GasRequire") + ERC20Data.gasrequire);
                                        if (new BigDecimal(ERC20Data.gasrequire).compareTo(new BigDecimal(ethbalances)) > 0) {
                                            commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("Ycantsend") + ERC20Data.symbol);
                                        }
                                    }
                                    commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("GasPrice") + Main.chainlibrary.web3j.ethGasPrice().send().getGasPrice().divide(new BigInteger("1000000000")));
                                    commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("GasWarning"));
                                    return;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("Notreg"));
                                return;
                            }
                        case 1:
                            if (args[0].equals("help")) {
                                commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("Helpurl"));
                                return;
                            }
                            if (args[0].equals("version")) {
                                commandSender.sendMessage(APILibrary.getVersion());
                                return;
                            }
                            if (args[0].equals("blockchain")) {
                                commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("ChainName") + Main.fileconfig.getString("ChainName"));
                                commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("ClientVersion") + Main.chainlibrary.web3j.web3ClientVersion().send().getWeb3ClientVersion());
                                commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("CurrentBlock") + Main.chainlibrary.web3j.ethBlockNumber().send().getBlockNumber());
                                commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("DefaultGasPrice") + Main.chainlibrary.web3j.ethGasPrice().send().getGasPrice());
                                for (Entry<String, Map<Integer, String>> ERC20Map : Main.ERC20ContractMap.entrySet()) {
                                    ERC20ContractData ERC20Data = new ERC20ContractData(Main.ERC20ContractMap.get(ERC20Map.getKey()));
                                    commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("TokenSymbol") + ERC20Data.symbol);
                                    commandSender.sendMessage(Main.SFTInfo + ERC20Data.symbol + " " + Main.prop.getProperty("ExchangeGasLimit") + ERC20Data.gaslimit);
                                }
                                return;
                            }
                            if (args[0].equals("keys")) {
                                if (commander.has) {
                                    commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("keywarning"));
                                    commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("Privatekey") + commander.privatekey);
                                    commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("Seed") + commander.seed);
                                    return;
                                } else {
                                    commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("Notreg"));
                                    return;
                                }
                            }
                            if (args[0].equals("create")) {
                                if (!commander.has) {
                                    Player commandSenderPlayer = (Player) commandSender;
                                    String UUID = commandSenderPlayer.getUniqueId().toString();
                                    String PlayerID = commandSenderPlayer.getName();
                                    if (APILibrary.CreateWallet(UUID, PlayerID)) {
                                        commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("Createwalletsuccess"));
                                        return;
                                    } else {
                                        commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("Createwalletfail"));
                                        return;
                                    }
                                } else {
                                    commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("AlreadyReg"));
                                    return;
                                }
                            }
                            if (args[0].equals("exchange")) {
                                commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("Avadeal"));
                                for (Entry<String, Map<Integer, String>> DealMap : Main.ExchangeMap.entrySet()) {
                                    commandSender.sendMessage(Main.SFTInfo + "\u00A7a" + DealMap.getKey());
                                }
                                return;
                            }
                            commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("Help"));
                            return;
                        case 2:
                            if (args[0].equals("player")) {
                                PlayerWalletData playerwallet = APILibrary.getPlayerWallet(args[1]);
                                assert playerwallet != null;
                                if (playerwallet.error) {
                                    commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("Help"));
                                    return;
                                }
                                try {
                                    String ethbalance = APILibrary.getEthBanlance(playerwallet.fromaddress);
                                    commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("TargetAddress") + playerwallet.fromaddress);
                                    commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("TargetBalance") + ethbalance + " " + Main.chainlibrary.symbol);
                                    for (Entry<String, Map<Integer, String>> ERC20Map : Main.ERC20ContractMap.entrySet()) {
                                        ERC20ContractData ERC20Data = new ERC20ContractData(Main.ERC20ContractMap.get(ERC20Map.getKey()));
                                        BigDecimal sftbalances = new BigDecimal(APILibrary.getERC20Balance(ERC20Data, commander, commander.fromaddress)).divide(new BigDecimal(ERC20Data.decimal));
                                        String finalsftbalances = String.valueOf(sftbalances);
                                        commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("TargetBalance") + finalsftbalances + " " + ERC20Data.symbol);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return;
                            }
                            if (args[0].equals("exchange")) {
                                if (commander.has) {
                                    String Type = args[1];
                                    String DealType = APILibrary.CheckDealType(Type);
                                    if (DealType.contains("null")) {
                                        commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("Help"));
                                        return;
                                    } else {
                                        for (Entry<String, Map<Integer, String>> DealMap : Main.ExchangeMap.entrySet()) {
                                            ExchangeData exchangeData = new ExchangeData(Main.ExchangeMap.get(DealMap.getKey()));
                                            if (DealType.equals(DealMap.getKey())) {
                                                String TokenType = APILibrary.CheckTokenType(exchangeData.tokentype);
                                                if (TokenType.equals(exchangeData.tokentype)) {
                                                    String ToAddress = Main.ConsoleWallet.fromaddress;
                                                    String value = exchangeData.price;
                                                    String executecommand = exchangeData.executecommand.replace("{player}", commander.playerid);
                                                    for (Entry<String, Map<Integer, String>> ERC20Map : Main.ERC20ContractMap.entrySet()) {
                                                        ERC20ContractData contractData = new ERC20ContractData(Main.ERC20ContractMap.get(ERC20Map.getKey()));
                                                        if (TokenType.equals(Main.chainlibrary.symbol)) {
                                                            String gasLimit = "21000";
                                                            String gasPrice = String.valueOf(Main.chainlibrary.web3j.ethGasPrice().send().getGasPrice().divide(new BigInteger("1000000000")));
                                                            if (APILibrary.CheckLegal(contractData, commander, commander.creds, commander.fromaddress, ToAddress, gasLimit, gasPrice, value, true)) {
                                                                commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("dispose"));
                                                                String TransactionHash = APILibrary.TransferETH(commander, ToAddress, gasLimit, gasPrice, value);
                                                                commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("TransferSuccess") + "\n\u00a7a" + Main.prop.getProperty("ExplorerUrl") + TransactionHash);
                                                                Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), ()->{
                                                                    Player commandSenderPlayer = (Player) commandSender;
                                                                    boolean isOp = commandSenderPlayer.isOp();
                                                                    commandSenderPlayer.setOp(true);
                                                                    commandSenderPlayer.chat(executecommand);
                                                                    commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("ExchangeSuccess") + " \u00a7a" + exchangeData.price + " \u00a7c" + exchangeData.tokentype);
                                                                    commandSenderPlayer.setOp(isOp);
                                                                });
                                                                return;
                                                            }
                                                        } else {
                                                            String gasLimit = contractData.gaslimit;
                                                            String gasPrice = String.valueOf(Main.chainlibrary.web3j.ethGasPrice().send().getGasPrice().divide(new BigInteger("1000000000")));
                                                            if (TokenType.equals(contractData.symbol)) {
                                                                if (APILibrary.CheckLegal(contractData, commander, commander.creds, commander.fromaddress, ToAddress, gasLimit, gasPrice, value, false)) {
                                                                    commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("dispose"));
                                                                    String TransactionHash = APILibrary.TransferSFT(contractData, commander, ToAddress, gasLimit, gasPrice, value).getTransactionHash();
                                                                    commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("TransferSuccess") + "\n\u00a7a" + Main.prop.getProperty("ExplorerUrl") + TransactionHash);
                                                                    Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), ()-> {
                                                                        Player commandSenderPlayer = (Player) commandSender;
                                                                        boolean isOp = commandSenderPlayer.isOp();
                                                                        commandSenderPlayer.setOp(true);
                                                                        commandSenderPlayer.chat(executecommand);
                                                                        commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("ExchangeSuccess") + " \u00a7a" + exchangeData.price + " \u00a7c" + exchangeData.tokentype);
                                                                        commandSenderPlayer.setOp(isOp);
                                                                    });
                                                                    return;
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("Help"));
                                        return;
                                    }
                                } else {
                                    commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("Notreg"));
                                    return;
                                }
                            }
                            commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("Help"));
                            return;
                        case 4:
                            if (commander.has) {
                                if (args[0].equals("transfer")) {
                                    String Type = args[1];
                                    String ToAddress = args[2];
                                    String value = args[3];
                                    String gasPrice = String.valueOf(Main.chainlibrary.web3j.ethGasPrice().send().getGasPrice().divide(new BigInteger("1000000000")));
                                    try {
                                        String TokenType = APILibrary.CheckTokenType(Type);
                                        if (TokenType.contains("null")) {
                                            commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("Help"));
                                            return;
                                        } else {
                                            for (Entry<String, Map<Integer, String>> ERC20Map : Main.ERC20ContractMap.entrySet()) {
                                                ERC20ContractData contractData = new ERC20ContractData(Main.ERC20ContractMap.get(ERC20Map.getKey()));
                                                if (TokenType.equals(Main.chainlibrary.symbol)) {
                                                    String gasLimit = "21000";
                                                    if (APILibrary.CheckLegal(contractData, commander, commander.creds, commander.fromaddress, ToAddress, gasLimit, gasPrice, value, true)) {
                                                        commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("dispose"));
                                                        String TransactionHash = APILibrary.TransferETH(commander, ToAddress, gasLimit, gasPrice, value);
                                                        commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("TransferSuccess") + "\n\u00a7a" + Main.prop.getProperty("ExplorerUrl") + TransactionHash);
                                                        return;
                                                    }
                                                } else {
                                                    if (TokenType.equals(contractData.symbol)) {
                                                        String gasLimit = contractData.gaslimit;
                                                        if (APILibrary.CheckLegal(contractData, commander, commander.creds, commander.fromaddress, ToAddress, gasLimit, gasPrice, value, false)) {
                                                            commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("dispose"));
                                                            String TransactionHash = APILibrary.TransferSFT(contractData, commander, ToAddress, gasLimit, gasPrice, value).getTransactionHash();
                                                            commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("TransferSuccess") + "\n\u00a7a" + Main.prop.getProperty("ExplorerUrl") + TransactionHash);
                                                            return;
                                                        }
                                                    }
                                                }
                                            }
                                            commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("Help"));
                                            commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("CheckGas"));
                                            return;
                                        }
                                    } catch (Exception ex) {
                                        commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("Help"));
                                        return;
                                    }
                                } else {
                                    commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("Notreg"));
                                    return;
                                }
                            }
                        case 5:
                            if (commander.has) {
                                if (args[0].equals("transfer")) {
                                    String Type = args[1];
                                    String ToAddress = args[2];
                                    String value = args[3];
                                    String gasPrice = args[4];
                                    try {
                                        String TokenType = APILibrary.CheckTokenType(Type);
                                        if (TokenType.contains("null")) {
                                            commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("Help"));
                                        } else {
                                            for (Entry<String, Map<Integer, String>> ERC20Map : Main.ERC20ContractMap.entrySet()) {
                                                ERC20ContractData contractData = new ERC20ContractData(Main.ERC20ContractMap.get(ERC20Map.getKey()));
                                                if (TokenType.equals(Main.chainlibrary.symbol)) {
                                                    String gasLimit = "21000";
                                                    if (APILibrary.CheckLegal(contractData, commander, commander.creds, commander.fromaddress, ToAddress, gasLimit, gasPrice, value, true)) {
                                                        commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("dispose"));
                                                        String TransactionHash = APILibrary.TransferETH(commander, ToAddress, gasLimit, gasPrice, value);
                                                        commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("TransferSuccess") + "\n\u00a7a" + Main.prop.getProperty("ExplorerUrl") + TransactionHash);
                                                    }
                                                } else {
                                                    if (TokenType.equals(contractData.symbol)) {
                                                        String gasLimit = contractData.gaslimit;
                                                        if (APILibrary.CheckLegal(contractData, commander, commander.creds, commander.fromaddress, ToAddress, gasLimit, gasPrice, value, false)) {
                                                            commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("dispose"));
                                                            String TransactionHash = APILibrary.TransferSFT(contractData, commander, ToAddress, gasLimit, gasPrice, value).getTransactionHash();
                                                            commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("TransferSuccess") + "\n\u00a7a" + Main.prop.getProperty("ExplorerUrl") + TransactionHash);
                                                        }
                                                    }
                                                }
                                            }
                                            commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("Help"));
                                            commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("CheckGas"));
                                        }
                                    } catch (Exception ex) {
                                        commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("Help"));
                                    }
                                } else {
                                    commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("Notreg"));
                                }
                            }
                        case 6:
                            if (commander.has) {
                                if (args[0].equals("transfer")) {
                                    String Type = args[1];
                                    String ToAddress = args[2];
                                    String value = args[3];
                                    String gasPrice = args[4];
                                    String gasLimit = args[5];
                                    try {
                                        String TokenType = APILibrary.CheckTokenType(Type);
                                        if (TokenType.contains("null")) {
                                            commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("Help"));
                                        } else {
                                            for (Entry<String, Map<Integer, String>> ERC20Map : Main.ERC20ContractMap.entrySet()) {
                                                ERC20ContractData contractData = new ERC20ContractData(Main.ERC20ContractMap.get(ERC20Map.getKey()));
                                                if (TokenType.equals(Main.chainlibrary.symbol)) {
                                                    if (APILibrary.CheckLegal(contractData, commander, commander.creds, commander.fromaddress, ToAddress, gasLimit, gasPrice, value, true)) {
                                                        commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("dispose"));
                                                        String TransactionHash = APILibrary.TransferETH(commander, ToAddress, gasLimit, gasPrice, value);
                                                        commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("TransferSuccess") + "\n\u00a7a" + Main.prop.getProperty("ExplorerUrl") + TransactionHash);
                                                    }
                                                } else {
                                                    if (TokenType.equals(contractData.symbol)) {
                                                        if (APILibrary.CheckLegal(contractData, commander, commander.creds, commander.fromaddress, ToAddress, gasLimit, gasPrice, value, false)) {
                                                            commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("dispose"));
                                                            String TransactionHash = APILibrary.TransferSFT(contractData, commander, ToAddress, gasLimit, gasPrice, value).getTransactionHash();
                                                            commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("TransferSuccess") + "\n\u00a7a" + Main.prop.getProperty("ExplorerUrl") + TransactionHash);
                                                        }
                                                    }
                                                }
                                            }
                                            commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("Help"));
                                            commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("CheckGas"));
                                            return;
                                        }
                                    } catch (Exception ex) {
                                        commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("Help"));
                                        return;
                                    }
                                }
                            } else {
                                commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("Notreg"));
                                return;
                            }
                    }
                }
                commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("Help"));
            } catch (Exception ex) {
                commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("Help"));
            }
        });
        return true;
    }
}