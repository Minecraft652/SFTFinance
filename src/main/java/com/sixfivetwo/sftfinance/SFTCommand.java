package com.sixfivetwo.sftfinance;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class SFTCommand implements CommandExecutor {

    @Override
    @ParametersAreNonnullByDefault
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(Main.class), () -> {
            try {
                if (command.getName().equals("wallet")) {
                    PlayerWalletData commander = new PlayerWalletData(commandSender.getName());
                    List<String> message = new ArrayList<>();
                    List<TextComponent> inimsg = new ArrayList<>();
                    switch (args.length) {
                        case 0:
                            if (!commander.has) {
                                commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("Notreg"));
                                return;
                            }
                            String balance = String.valueOf(APILibrary.getEthBalance(commander.fromaddress, true));
                            TextComponent msg1 = new TextComponent(Main.SFTInfo + Main.prop.getProperty("YourAddress") + commander.fromaddress);
                            TextComponent msg2 = new TextComponent(Main.SFTInfo + Main.prop.getProperty("YourBalance") + balance + " " + Main.chainlibrary.symbol);
                            msg1.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, commander.fromaddress));
                            msg1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Main.prop.getProperty("clicktocopy"))));
                            msg2.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, commander.fromaddress));
                            msg2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Main.prop.getProperty("clicktocopy"))));
                            inimsg.add(msg1);
                            inimsg.add(msg2);
                            for (Entry<String, Map<Integer, String>> ERC20Map : Main.ERC20ContractMap.entrySet()) {
                                ERC20ContractData ERC20Data = new ERC20ContractData(Main.ERC20ContractMap.get(ERC20Map.getKey()));
                                BigDecimal sftbalance = new BigDecimal(APILibrary.getERC20Balance(ERC20Data, commander, commander.fromaddress)).divide(new BigDecimal(ERC20Data.decimal));
                                inimsg.add(new TextComponent(Main.SFTInfo + Main.prop.getProperty("YourBalance") + sftbalance + " " + ERC20Data.symbol));
                            }
                            inimsg.add(new TextComponent(Main.SFTInfo + Main.prop.getProperty("HelpPage")));
                            APILibrary.playerSendInteractiveMessage(commandSender, inimsg);
                            return;
                        case 1:
                            if (args[0].equals("help")) {
                                APILibrary.playerSendMessage(commandSender, APILibrary.getHelpData("1", message));
                                return;
                            }
                            if (args[0].equals("version")) {
                                commandSender.sendMessage(APILibrary.getVersion());
                                return;
                            }
                            if (args[0].equals("blockchain")) {
                                message.add(Main.SFTInfo + Main.prop.getProperty("ChainName") + Main.fileconfig.getString("ChainName"));
                                message.add(Main.SFTInfo + Main.prop.getProperty("ClientVersion") + Main.chainlibrary.web3j.web3ClientVersion().send().getWeb3ClientVersion());
                                message.add(Main.SFTInfo + Main.prop.getProperty("CurrentBlock") + Main.chainlibrary.web3j.ethBlockNumber().send().getBlockNumber());
                                message.add(Main.SFTInfo + Main.prop.getProperty("DefaultGasPrice") + Main.chainlibrary.web3j.ethGasPrice().send().getGasPrice());
                                for (Entry<String, Map<Integer, String>> ERC20Map : Main.ERC20ContractMap.entrySet()) {
                                    ERC20ContractData ERC20Data = new ERC20ContractData(Main.ERC20ContractMap.get(ERC20Map.getKey()));
                                    message.add(Main.SFTInfo + Main.prop.getProperty("TokenSymbol") + ERC20Data.symbol);
                                    message.add(Main.SFTInfo + ERC20Data.symbol + " " + Main.prop.getProperty("ExchangeGasLimit") + ERC20Data.gaslimit);
                                }
                                APILibrary.playerSendMessage(commandSender, message);
                                return;
                            }
                            if (args[0].equals("keys")) {
                                if (!commander.has) {
                                    commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("Notreg"));
                                    return;
                                }
                                inimsg.add(new TextComponent(Main.SFTInfo + Main.prop.getProperty("keywarning")));
                                TextComponent msg10 = new TextComponent(Main.SFTInfo + Main.prop.getProperty("Privatekey") + commander.privatekey);
                                TextComponent msg11 = new TextComponent(Main.SFTInfo + Main.prop.getProperty("Seed") + commander.seed);
                                msg10.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, commander.privatekey));
                                msg10.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Main.prop.getProperty("clicktocopy"))));
                                msg11.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, commander.seed));
                                msg11.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Main.prop.getProperty("clicktocopy"))));
                                inimsg.add(msg10);
                                inimsg.add(msg11);
                                APILibrary.playerSendInteractiveMessage(commandSender, inimsg);
                                return;
                            }
                            if (args[0].equals("create")) {
                                if (!commander.has) {
                                    Player commandSenderPlayer = (Player) commandSender;
                                    String UUID = commandSenderPlayer.getUniqueId().toString();
                                    String PlayerID = commandSenderPlayer.getName();
                                    if (APILibrary.createWallet(UUID, PlayerID)) {
                                        commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("Createwalletsuccess"));
                                        return;
                                    }
                                    commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("Createwalletfail"));
                                    return;
                                }
                                commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("AlreadyReg"));
                                return;
                            }
                            if (args[0].equals("exchange")) {
                                commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("Avadeal"));
                                for (Entry<String, Map<Integer, String>> DealMap : Main.ExchangeMap.entrySet()) {
                                    commandSender.sendMessage(Main.SFTInfo + "\u00A7a" + DealMap.getKey());
                                }
                                return;
                            }
                            if (args[0].equals("gas")) {
                                List<String> gasRequire = new ArrayList<>();
                                for (Entry<String, Map<Integer, String>> ERC20Map : Main.ERC20ContractMap.entrySet()) {
                                    ERC20ContractData ERC20Data = new ERC20ContractData(Main.ERC20ContractMap.get(ERC20Map.getKey()));
                                    if (new BigDecimal(ERC20Data.gasrequire).compareTo(new BigDecimal(String.valueOf(APILibrary.getEthBalance(commander.fromaddress, true)))) > 0) {
                                        gasRequire.add(Main.SFTInfo + ERC20Data.symbol + " " + Main.prop.getProperty("GasRequire") + ERC20Data.gasrequire);
                                        message.add(Main.SFTInfo + Main.prop.getProperty("Ycantsend") + ERC20Data.symbol);
                                    } else {
                                        gasRequire.add(Main.SFTInfo + ERC20Data.symbol + " " + Main.prop.getProperty("GasRequire") + ERC20Data.gasrequire);
                                    }
                                }
                                message.add(Main.SFTInfo + Main.prop.getProperty("GasPrice") + Main.chainlibrary.web3j.ethGasPrice().send().getGasPrice().divide(new BigInteger("1000000000")));
                                message.add(Main.SFTInfo + Main.prop.getProperty("GasWarning"));
                                APILibrary.playerSendMessage(commandSender, gasRequire);
                                APILibrary.playerSendMessage(commandSender, message);
                                return;
                            }
                            if (args[0].equals("delete")) {
                                if (!commander.has) {
                                    commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("Notreg"));
                                    return;
                                }
                                commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("Privatekey") + commander.privatekey);
                                commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("Seed") + commander.seed);
                                if (APILibrary.deleteData(commander.playerid)) {
                                    commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("successdelete"));
                                    return;
                                }
                                return;
                            }
                        case 2:
                            if (args[0].equals("help")) {
                                if (args[1].equals("1")) {
                                    APILibrary.playerSendMessage(commandSender, APILibrary.getHelpData("1", message));
                                    return;
                                }
                                if (args[1].equals("2")) {
                                    APILibrary.playerSendMessage(commandSender, APILibrary.getHelpData("2", message));
                                    return;
                                }
                            }
                            if (args[0].equals("player")) {
                                if (!commander.has) {
                                    commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("Notreg"));
                                    return;
                                }
                                PlayerWalletData playerwallet = new PlayerWalletData(args[1]);
                                String ethbalance = String.valueOf(APILibrary.getEthBalance(playerwallet.fromaddress, true));
                                TextComponent msg10 = new TextComponent(Main.SFTInfo + Main.prop.getProperty("TargetAddress") + playerwallet.fromaddress);
                                TextComponent msg11 = new TextComponent(Main.SFTInfo + Main.prop.getProperty("TargetBalance") + ethbalance + " " + Main.chainlibrary.symbol);
                                msg10.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, playerwallet.fromaddress));
                                msg10.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Main.prop.getProperty("clicktocopy"))));
                                msg11.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, playerwallet.fromaddress));
                                msg11.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Main.prop.getProperty("clicktocopy"))));
                                inimsg.add(msg10);
                                inimsg.add(msg11);
                                for (Entry<String, Map<Integer, String>> ERC20Map : Main.ERC20ContractMap.entrySet()) {
                                    ERC20ContractData ERC20Data = new ERC20ContractData(Main.ERC20ContractMap.get(ERC20Map.getKey()));
                                    BigDecimal balances = new BigDecimal(APILibrary.getERC20Balance(ERC20Data, commander, commander.fromaddress)).divide(new BigDecimal(ERC20Data.decimal));
                                    inimsg.add(new TextComponent(Main.SFTInfo + Main.prop.getProperty("TargetBalance") + balances + " " + ERC20Data.symbol));
                                }
                                APILibrary.playerSendInteractiveMessage(commandSender, inimsg);
                                return;
                            }
                            if (args[0].equals("exchange")) {
                                if (!commander.has) {
                                    commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("Notreg"));
                                    return;
                                }
                                String Type = args[1];
                                String DealType = APILibrary.CheckDealType(Type);
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
                                                    if (APILibrary.CheckLegal(contractData, commander, commander.fromaddress, ToAddress, gasLimit, gasPrice, value, true)) {
                                                        commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("dispose"));
                                                        String TransactionHash = APILibrary.TransferETH(commander, ToAddress, gasLimit, gasPrice, value);
                                                        if (TransactionHash.contains("null")) {
                                                            commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("donterror"));
                                                            return;
                                                        }
                                                        commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("TransferSuccess") + "\n\u00a7a" + Main.prop.getProperty("ExplorerUrl") + TransactionHash);
                                                        Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), () -> {
                                                            Player commandSenderPlayer = (Player) commandSender;
                                                            boolean isOp = commandSenderPlayer.isOp();
                                                            if (APILibrary.executeCommand(commandSenderPlayer, executecommand, isOp)) {
                                                                commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("ExchangeSuccess") + " \u00a7a" + exchangeData.price + " \u00a7c" + exchangeData.tokentype);
                                                            }
                                                        });
                                                        return;
                                                    }
                                                } else {
                                                    String gasLimit = contractData.gaslimit;
                                                    String gasPrice = String.valueOf(Main.chainlibrary.web3j.ethGasPrice().send().getGasPrice().divide(new BigInteger("1000000000")));
                                                    if (TokenType.equals(contractData.symbol)) {
                                                        if (APILibrary.CheckLegal(contractData, commander, commander.fromaddress, ToAddress, gasLimit, gasPrice, value, false)) {
                                                            commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("dispose"));
                                                            String TransactionHash = APILibrary.TransferSFT(contractData, commander, ToAddress, gasLimit, gasPrice, value).getTransactionHash();
                                                            if (TransactionHash.contains("null")) {
                                                                commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("donterror"));
                                                                return;
                                                            }
                                                            commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("TransferSuccess") + "\n\u00a7a" + Main.prop.getProperty("ExplorerUrl") + TransactionHash);
                                                            Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), () -> {
                                                                Player commandSenderPlayer = (Player) commandSender;
                                                                boolean isOp = commandSenderPlayer.isOp();
                                                                if (APILibrary.executeCommand(commandSenderPlayer, executecommand, isOp)) {
                                                                    commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("ExchangeSuccess") + " \u00a7a" + exchangeData.price + " \u00a7c" + exchangeData.tokentype);
                                                                }
                                                            });
                                                            return;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        case 3:
                            if (args[0].equals("exchange")) {
                                String DealType = args[1];
                                if (!args[2].equals("info")) {
                                    return;
                                }
                                for (Entry<String, Map<Integer, String>> DealMap : Main.ExchangeMap.entrySet()) {
                                    ExchangeData exchangeData = new ExchangeData(Main.ExchangeMap.get(DealMap.getKey()));
                                    if (DealType.equals(DealMap.getKey())) {
                                        message.add(Main.SFTInfo + Main.prop.getProperty("exchangetokentype") + exchangeData.tokentype);
                                        message.add(Main.SFTInfo + Main.prop.getProperty("exchangeprice") + exchangeData.price);
                                        message.add(Main.SFTInfo + Main.prop.getProperty("exchangeexecutecommand") + exchangeData.executecommand);
                                        APILibrary.playerSendMessage(commandSender, message);
                                        return;
                                    }
                                }
                                commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("Help"));
                                return;
                            }
                        case 4:
                            if (!commander.has) {
                                commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("Notreg"));
                                return;
                            }
                            if (args[0].equals("transfer")) {
                                ReceiptData rd = new ReceiptData(args[1], "null", args[2], args[3], "null", String.valueOf(Main.chainlibrary.web3j.ethGasPrice().send().getGasPrice().divide(new BigInteger("1000000000"))));
                                if (APILibrary.executeTransfer(commander, rd, commandSender)) return;
                            }
                            if (args[0].equals("approve")) {
                                ReceiptData rd = new ReceiptData(args[1], "null", args[2], args[3], "null", String.valueOf(Main.chainlibrary.web3j.ethGasPrice().send().getGasPrice().divide(new BigInteger("1000000000"))));
                                if (APILibrary.executeApprove(commander, rd, commandSender)) return;
                            }
                        case 5:
                            if (!commander.has) {
                                commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("Notreg"));
                                return;
                            }
                            if (args[0].equals("transfer")) {
                                ReceiptData rd = new ReceiptData(args[1], "null", args[2], args[3], "null", args[4]);
                                if (APILibrary.executeTransfer(commander, rd, commandSender)) return;
                            }
                            if (args[0].equals("transferfrom")) {
                                ReceiptData rd = new ReceiptData(args[1], args[2], args[3], args[4], "null", String.valueOf(Main.chainlibrary.web3j.ethGasPrice().send().getGasPrice().divide(new BigInteger("1000000000"))));
                                if (APILibrary.executeTransferFrom(commander, rd, commandSender)) return;
                            }
                            if (args[0].equals("approve")) {
                                ReceiptData rd = new ReceiptData(args[1], "null", args[2], args[3], "null", args[4]);
                                if (APILibrary.executeApprove(commander, rd, commandSender)) return;
                            }
                        case 6:
                            if (!commander.has) {
                                commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("Notreg"));
                                return;
                            }
                            if (args[0].equals("transfer")) {
                                ReceiptData rd = new ReceiptData(args[1], "null", args[2], args[3], args[5], args[4]);
                                if (APILibrary.executeTransfer(commander, rd, commandSender)) return;
                            }
                            if (args[0].equals("transferfrom")) {
                                ReceiptData rd = new ReceiptData(args[1], args[2], args[3], args[4], "null", args[5]);
                                if (APILibrary.executeTransferFrom(commander, rd, commandSender)) return;
                            }
                            if (args[0].equals("approve")) {
                                ReceiptData rd = new ReceiptData(args[1], "null", args[2], args[3], args[5], args[4]);
                                if (APILibrary.executeApprove(commander, rd, commandSender)) return;
                            }
                        case 7:
                            if (args[0].equals("transferfrom")) {
                                ReceiptData rd = new ReceiptData(args[1], args[2], args[3], args[4], args[6], args[5]);
                                if (APILibrary.executeTransferFrom(commander, rd, commandSender)) return;
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