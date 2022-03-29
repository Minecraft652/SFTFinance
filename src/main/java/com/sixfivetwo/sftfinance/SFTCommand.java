package com.sixfivetwo.sftfinance;

import com.sixfivetwo.sftfinance.datalibrary.*;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.web3j.crypto.Credentials;

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
                    List<net.md_5.bungee.api.chat.TextComponent> interactiveMessage = new ArrayList<>();
                    switch (args.length) {
                        case 0:
                            if (!commander.has) {
                                commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("Notreg"));
                                return;
                            }
                            String balance = String.valueOf(APILibrary.getEthBalance(commander.fromaddress, true));
                            interactiveMessage.add(APILibrary.textAddEffect(new TextComponent(Main.SFTInfo + Main.prop.getProperty("YourAddress") + commander.fromaddress), commander.fromaddress));
                            interactiveMessage.add(APILibrary.textAddEffect(new TextComponent(Main.SFTInfo + Main.prop.getProperty("YourBalance") + balance + " " + Main.chainlibrary.symbol), balance));
                            for (Entry<String, Map<Integer, String>> ERC20Map : Main.ERC20ContractMap.entrySet()) {
                                ERC20ContractData ERC20Data = new ERC20ContractData(Main.ERC20ContractMap.get(ERC20Map.getKey()), Main.chainlibrary);
                                BigDecimal sftbalance = new BigDecimal(APILibrary.getERC20Balance(ERC20Data, commander, commander.fromaddress)).divide(new BigDecimal(ERC20Data.decimal));
                                interactiveMessage.add(APILibrary.textAddEffect(new TextComponent(Main.SFTInfo + Main.prop.getProperty("YourBalance") + sftbalance + " " + ERC20Data.symbol), sftbalance.toString()));
                            }
                            interactiveMessage.add(APILibrary.textAddEffect(new TextComponent(Main.SFTInfo + Main.prop.getProperty("HelpPage")), "/wallet help"));
                            if (commandSender instanceof ConsoleCommandSender) {
                                APILibrary.playerSendInteractiveMessage((ConsoleCommandSender) commandSender, interactiveMessage);
                            } else {
                                APILibrary.playerSendInteractiveMessage(commandSender, interactiveMessage);
                            }
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
                                    ERC20ContractData ERC20Data = new ERC20ContractData(Main.ERC20ContractMap.get(ERC20Map.getKey()), Main.chainlibrary);
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
                                interactiveMessage.add(APILibrary.textAddEffect(new TextComponent(Main.SFTInfo + Main.prop.getProperty("keywarning")), ""));
                                interactiveMessage.add(APILibrary.textAddEffect(new TextComponent(Main.SFTInfo + Main.prop.getProperty("Privatekey")+ commander.privatekey), commander.privatekey));
                                interactiveMessage.add(APILibrary.textAddEffect(new TextComponent(Main.SFTInfo + Main.prop.getProperty("Seed") + commander.seed), commander.seed));
                                APILibrary.playerSendInteractiveMessage(commandSender, interactiveMessage);
                                return;
                            }
                            if (args[0].equals("create")) {
                                if (!commander.has) {
                                    String UUID = null;
                                    String PlayerID = null;
                                    if (commandSender instanceof ConsoleCommandSender) {
                                        UUID = "00000000-0000-0000-0000-000000000000";
                                        PlayerID = "CONSOLE";
                                    } else {
                                        Player commandSenderPlayer = (Player) commandSender;
                                        UUID = commandSenderPlayer.getUniqueId().toString();
                                        PlayerID = commandSenderPlayer.getName();
                                    }
                                    if (Main.fileconfig.getBoolean("LegacyWalletGenerator")) {
                                        if (APILibrary.legacyCreateWallet(UUID, PlayerID, Main.legacyDirectory)) {
                                            commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("Createwalletsuccess"));
                                            return;
                                        }
                                    } else {
                                        if (APILibrary.createWallet(UUID, PlayerID)) {
                                            commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("Createwalletsuccess"));
                                            return;
                                        }
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
                                    ERC20ContractData ERC20Data = new ERC20ContractData(Main.ERC20ContractMap.get(ERC20Map.getKey()), Main.chainlibrary);
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
                                interactiveMessage.add(APILibrary.textAddEffect(new TextComponent(Main.SFTInfo + Main.prop.getProperty("Privatekey") + commander.privatekey), commander.privatekey));
                                interactiveMessage.add(APILibrary.textAddEffect(new TextComponent(Main.SFTInfo + Main.prop.getProperty("Seed") + commander.seed), commander.seed));
                                APILibrary.playerSendInteractiveMessage(commandSender, interactiveMessage);
                                if (APILibrary.deleteData(commander.playerid)) {
                                    commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("successdelete"));
                                    return;
                                }
                                return;
                            }
                        case 2:
                            if (args[0].equals("help")) {
                                APILibrary.playerSendMessage(commandSender, APILibrary.getHelpData(args[1], message));
                                return;
                            }
                            if (args[0].equals("player")) {
                                if (!commander.has) {
                                    commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("Notreg"));
                                    return;
                                }
                                PlayerWalletData playerwallet = new PlayerWalletData(args[1]);
                                String ethbalance = String.valueOf(APILibrary.getEthBalance(playerwallet.fromaddress, true));
                                interactiveMessage.add(APILibrary.textAddEffect(new TextComponent(Main.SFTInfo + Main.prop.getProperty("TargetAddress") + playerwallet.fromaddress), playerwallet.fromaddress));
                                interactiveMessage.add(APILibrary.textAddEffect(new TextComponent(Main.SFTInfo + Main.prop.getProperty("TargetBalance") + ethbalance + " " + Main.chainlibrary.symbol), ethbalance));
                                for (Entry<String, Map<Integer, String>> ERC20Map : Main.ERC20ContractMap.entrySet()) {
                                    ERC20ContractData ERC20Data = new ERC20ContractData(Main.ERC20ContractMap.get(ERC20Map.getKey()), Main.chainlibrary);
                                    BigDecimal balances = new BigDecimal(APILibrary.getERC20Balance(ERC20Data, playerwallet, playerwallet.fromaddress)).divide(new BigDecimal(ERC20Data.decimal));
                                    interactiveMessage.add(APILibrary.textAddEffect(new TextComponent(Main.SFTInfo + Main.prop.getProperty("TargetBalance") + balances + " " + ERC20Data.symbol), balances.toString()));
                                }
                                if (commandSender instanceof ConsoleCommandSender) {
                                    APILibrary.playerSendInteractiveMessage((ConsoleCommandSender) commandSender, interactiveMessage);
                                } else {
                                    APILibrary.playerSendInteractiveMessage(commandSender, interactiveMessage);
                                }
                                return;
                            }
                            if (args[0].equals("exchange")) {
                                if (!commander.has) {
                                    commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("Notreg"));
                                    return;
                                }
                                String Type = args[1];
                                String Message = APILibrary.checkLegalExchange(Type, commander, commandSender, Main.ConsoleWallet.fromaddress);
                                message.add(Main.SFTInfo + Main.prop.getProperty(Message));
                                APILibrary.playerSendMessage(commandSender, message);
                                return;
                            }
                            if (args[0].equals("import")) {
                                if (!Main.fileconfig.getBoolean("playerCanImportTheyOwnWallet")) {
                                    commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("featureunable"));
                                    return;
                                }
                                if (commander.has) {
                                    commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("AlreadyReg"));
                                    return;
                                }
                                String PrivateKey = args[1];
                                try {
                                    Credentials creds = APILibrary.getCredential(PrivateKey);
                                    if (commandSender instanceof ConsoleCommandSender) {
                                        if (commander.playerid.equals("CONSOLE")) {
                                            APILibrary.insertData("00000000-0000-0000-0000-000000000000", "CONSOLE", "0", creds.getAddress(), PrivateKey, "0");
                                            commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("importsuccess"));
                                            return;
                                        }
                                    }
                                    Player player = Bukkit.getPlayer(commandSender.getName());
                                    APILibrary.insertData( String.valueOf(player.getUniqueId()), player.getName(), "0", creds.getAddress(), PrivateKey, "0");
                                    commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("importsuccess"));
                                    return;
                                } catch (Exception ex) {
                                    commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("errorprivatekey"));
                                    return;
                                }
                            }
                            if (args[0].equals("trade")) {
                                if (APILibrary.checkTradeAvailable(commandSender, commander)) return;
                                if (commandSender instanceof ConsoleCommandSender) {
                                    if (commander.playerid.equals("CONSOLE")) {
                                        commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("consoledeny"));
                                        return;
                                    }
                                }
                                if (args[1].equals("list")) {
                                    List<String> from = APILibrary.getTradeList(1, commander.playerid, Main.conn);
                                    List<String> to = APILibrary.getTradeList(2, commander.playerid, Main.conn);
                                    List<String> fromDeny = APILibrary.getTradeList(3, commander.playerid, Main.conn);
                                    message.add(Main.SFTInfo + Main.prop.getProperty("yourfromlist"));
                                    message.add(Main.SFTInfo + String.join(",", from));
                                    message.add(Main.SFTInfo + Main.prop.getProperty("yourtolist"));
                                    message.add(Main.SFTInfo + String.join(",", to));
                                    message.add(Main.SFTInfo + Main.prop.getProperty("yourfromdenylist"));
                                    message.add(Main.SFTInfo + String.join(",", fromDeny));
                                    APILibrary.playerSendMessage(commandSender, message);
                                    return;
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
                            if (args[0].equals("trade")) {
                                if (APILibrary.checkTradeAvailable(commandSender, commander)) return;
                                int id = Integer.parseInt(args[2]);
                                if (args[1].equals("info")) {
                                    if (APILibrary.checkDealAcceptDenyPermission(commandSender, "all", commander.playerid, id, Main.conn)) {
                                        commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("permissiondeny"));
                                        return;
                                    }
                                    PlayerDealData pdd = new PlayerDealData(id, Main.conn);
                                    APILibrary.sendMessageInfo(commandSender, message, pdd);
                                    return;
                                }
                                if (args[1].equals("edit")) {
                                    if (APILibrary.checkDealAcceptDenyPermission(commandSender, "from", commander.playerid, id, Main.conn)) {
                                        commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("permissiondeny"));
                                        return;
                                    }
                                    PlayerDealData pdd = new PlayerDealData(id, Main.conn);
                                    Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), () -> {
                                        Bukkit.getPlayer(commander.playerid).openInventory(APILibrary.createCustomInventory(new InventoryHolderEditData(id), pdd.details));
                                    });
                                    return;
                                }
                                if (args[1].equals("accept")) {
                                    if (APILibrary.checkDealAcceptDenyPermission(commandSender, "to", commander.playerid, id, Main.conn)) {
                                        commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("permissiondeny"));
                                        return;
                                    }
                                    PlayerDealData pdd = new PlayerDealData(id, Main.conn);
                                    if (APILibrary.executeTrade(pdd, pdd.toid, commandSender)) {
                                        interactiveMessage.add(APILibrary.textAddEffect(new TextComponent(Main.SFTInfo + Main.prop.getProperty("acceptdealsuccess")), "/wallet trade edit " + pdd.id));
                                        interactiveMessage.add(APILibrary.textAddEffect(new TextComponent(Main.SFTInfo + Main.prop.getProperty("acceptdealsuccessa") + pdd.id), "/wallet trade edit " + pdd.id));
                                        Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), () -> {
                                            try {
                                                Bukkit.getPlayer(pdd.fromid.playerid).sendMessage(Main.SFTInfo + Main.prop.getProperty("acceptdealsuccessb") + pdd.id);
                                            } catch (Exception ignored) {}
                                        });
                                        APILibrary.playerSendInteractiveMessage(commandSender, interactiveMessage);
                                        return;
                                    }
                                    interactiveMessage.add(APILibrary.textAddEffect(new TextComponent(Main.SFTInfo + Main.prop.getProperty("acceptdealfail")), "/wallet trade info " + pdd.id));
                                    interactiveMessage.add(APILibrary.textAddEffect(new TextComponent(Main.SFTInfo + Main.prop.getProperty("acceptdealfaila") + pdd.id), "/wallet trade info " + pdd.id));
                                    Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), () -> {
                                        try {
                                            Bukkit.getPlayer(pdd.fromid.playerid).sendMessage(Main.SFTInfo + Main.prop.getProperty("acceptdealfailb") + pdd.id);
                                            Bukkit.getPlayer(pdd.fromid.playerid).sendMessage(Main.SFTInfo + Main.prop.getProperty("acceptdealfaila") + pdd.id);
                                        } catch (Exception ignored) {}
                                    });
                                    APILibrary.playerSendInteractiveMessage(commandSender, interactiveMessage);
                                    return;
                                }
                                if (args[1].equals("deny")) {
                                    if (APILibrary.checkDealAcceptDenyPermission(commandSender, "all", commander.playerid, id, Main.conn)) {
                                        commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("permissiondeny"));
                                        return;
                                    }
                                    PlayerDealData pdd = new PlayerDealData(id, Main.conn);
                                    pdd.denyDeal(Main.conn);
                                    commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("denydealsuccess"));
                                    return;
                                }
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
                            if (args[0].equals("trade")) {
                                if (APILibrary.checkTradeAvailable(commandSender, commander)) return;
                                if (APILibrary.checkSelf(commandSender, args[1])) return;
                                if (commander.playerid.equals(args[1])) {
                                    commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("Help"));
                                    return;
                                }
                                if (commandSender instanceof ConsoleCommandSender) {
                                    if (commander.playerid.equals("CONSOLE")) {
                                        commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("consoledeny"));
                                        return;
                                    }
                                }
                                PlayerWalletData playerWalletData = new PlayerWalletData(args[1]);
                                String tokenType = APILibrary.CheckTokenType(args[2]);
                                String value = args[3];
                                if ("null".equals(tokenType)) {
                                    commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("errortokentype"));
                                    return;
                                }
                                if (!playerWalletData.has) {
                                    commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("targetnotreg"));
                                    return;
                                }
                                Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), () -> {
                                    Bukkit.getPlayer(commander.playerid).openInventory(APILibrary.createInventory(new InventoryHolderData(tokenType, commander, playerWalletData, value)));
                                });
                                return;
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