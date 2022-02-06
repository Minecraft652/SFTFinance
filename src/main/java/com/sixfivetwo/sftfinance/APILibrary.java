package com.sixfivetwo.sftfinance;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import org.bitcoinj.crypto.*;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.web3j.contracts.eip20.generated.ERC20;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.tx.gas.StaticGasProvider;
import org.web3j.utils.Convert;
import org.web3j.utils.Convert.Unit;
import org.web3j.utils.Numeric;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

public class APILibrary {
    final static ImmutableList<ChildNumber> BIP44_ETH_ACCOUNT_ZERO_PATH =
            ImmutableList.of(new ChildNumber(44, true), new ChildNumber(60, true),
                    ChildNumber.ZERO_HARDENED, ChildNumber.ZERO);

    public static void inputStream2File(InputStream is, File file) throws IOException {
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            int len;
            byte[] buffer = new byte[8192];
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
        } finally {
            os.close();
            is.close();
        }
    }

    public static Connection getConnection(String type, String url, String user, String pass) throws SQLException {
        try {
            if (type.equals("mysql")) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                String nurl = url.replace("SFTFinance", "mysql");
                Connection nconn = DriverManager.getConnection(nurl, user, pass);
                Statement statement = nconn.createStatement();
                statement.execute("create database sftfinance");
                statement.execute("use sftfinance");
                statement.execute("create table wallets (UUID varchar(255) primary key, PlayerID varchar(255), Seed varchar(255), Address varchar(255), PrivateKey varchar(255), PublicKey varchar(255))");
                statement.close();
                nconn.close();
                return DriverManager.getConnection(url, user, pass);
            } else if (type.equals("sqlite")) {
                Class.forName("org.sqlite.JDBC");
                Connection conn = DriverManager.getConnection(url);
                Statement statement = conn.createStatement();
                statement.executeUpdate("create table wallets (UUID string primary key, PlayerID string, Seed string, Address string, PrivateKey string, PublicKey string)");
                statement.close();
                return conn;
            }
        } catch (Exception ex) {
            if (type.equals("mysql")) {
                return DriverManager.getConnection(url, user, pass);
            } else if (type.equals("sqlite")) {
                return DriverManager.getConnection(url);
            }
        }
        return null;
    }

    public static boolean createWallet(String UUID, String PlayerID) {
        try {
            SecureRandom secureRandom = new SecureRandom();
            byte[] entropy = new byte[DeterministicSeed.DEFAULT_SEED_ENTROPY_BITS / 8];
            secureRandom.nextBytes(entropy);
            List<String> str = MnemonicCode.INSTANCE.toMnemonic(entropy);
            byte[] seed = MnemonicCode.toSeed(str, "");
            DeterministicKey masterPrivateKey = HDKeyDerivation.createMasterPrivateKey(seed);
            DeterministicHierarchy deterministicHierarchy = new DeterministicHierarchy(masterPrivateKey);
            DeterministicKey deterministicKey = deterministicHierarchy.deriveChild(BIP44_ETH_ACCOUNT_ZERO_PATH, false, true, new ChildNumber(0));
            byte[] bytes = deterministicKey.getPrivKeyBytes();
            ECKeyPair keyPair = ECKeyPair.create(bytes);
            String address = Keys.getAddress(keyPair.getPublicKey());
            String Address = "0x" + address;
            String MasterPrivateKey = Joiner.on(",").join(str);
            String PrivateKey = "0x" + keyPair.getPrivateKey().toString(16);
            String PublicKey = keyPair.getPublicKey().toString(16);
            insertData(UUID, PlayerID, MasterPrivateKey, Address, PrivateKey, PublicKey);
        } catch (Exception ignored) {
        }
        return true;
    }

    public static void insertData(String UUID, String PlayerID, String Seed, String Address, String PrivateKey, String PublicKey) throws SQLException {
        PreparedStatement statement = Main.conn.prepareStatement("insert into wallets values(?,?,?,?,?,?)");
        statement.setString(1, UUID);
        statement.setString(2, PlayerID);
        statement.setString(3, Seed);
        statement.setString(4, Address);
        statement.setString(5, PrivateKey);
        statement.setString(6, PublicKey);
        statement.executeUpdate();
        statement.close();
    }

    public static boolean deleteData(String PlayerID) {
        try {
            PreparedStatement statement = Main.conn.prepareStatement("delete from wallets where PlayerID = ?");
            statement.setString(1, PlayerID);
            statement.executeUpdate();
            statement.close();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static List<String> getHelpData(String page, List<String> message) {
        for (Entry<String, Map<Integer, String>> HELPMap : Main.HelpPageMap.entrySet()) {
            HelpPageData HelpData = new HelpPageData(Main.HelpPageMap.get(HELPMap.getKey()));
            if (HELPMap.getKey().equals("Page" + page)) {
                message.add(HelpData.front);
                message.add(HelpData.comment1);
                message.add(HelpData.comment2);
                message.add(HelpData.comment3);
                message.add(HelpData.comment4);
                message.add(HelpData.comment5);
                message.add(HelpData.comment6);
                message.add(HelpData.comment7);
                message.add(HelpData.comment8);
                message.add(HelpData.comment9);
                message.add(HelpData.comment10);
            }
        }
        return message;
    }

    public static boolean executeCommand(Player player, String command, boolean isOp) {
        player.setOp(true);
        player.chat(command);
        player.setOp(isOp);
        return true;
    }

    public static void playerSendMessage(CommandSender commandSender, List<String> messageList) {
        for (String message : messageList) {
            commandSender.sendMessage(message);
        }
    }

    /*
        public static void playerSendInteractiveMessage(CommandSender commandSender, List<TextComponent> messageList) {
            for (TextComponent message : messageList) {
                commandSender.spigot().sendMessage(message);
            }
        }
    */
    public static boolean sendSFTTransaction(@NotNull CommandSender commandSender, PlayerWalletData commander, ReceiptData rd, ERC20ContractData contractData) throws Exception {
        if (APILibrary.CheckLegal(contractData, commander, commander.fromaddress, rd.toAddress, rd.gasLimit, rd.gasPrice, rd.value, false)) {
            commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("dispose"));
            String TransactionHash = APILibrary.TransferSFT(contractData, commander, rd.toAddress, rd.gasLimit, rd.gasPrice, rd.value).getTransactionHash();
            return checkTransactionHash(commandSender, TransactionHash);
        }
        return false;
    }

    public static boolean sendETHTransaction(@NotNull CommandSender commandSender, PlayerWalletData commander, ReceiptData rd, ERC20ContractData contractData) throws Exception {
        if (APILibrary.CheckLegal(contractData, commander, commander.fromaddress, rd.toAddress, rd.gasLimit, rd.gasPrice, rd.value, true)) {
            commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("dispose"));
            String TransactionHash = APILibrary.TransferETH(commander, rd.toAddress, rd.gasLimit, rd.gasPrice, rd.value);
            return checkTransactionHash(commandSender, TransactionHash);
        }
        return false;
    }

    public static boolean sendApproveTransaction(@NotNull CommandSender commandSender, PlayerWalletData commander, ReceiptData rd, ERC20ContractData contractData) throws Exception {
        commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("dispose"));
        TransactionReceipt receipt = APILibrary.approve(contractData, commander, rd.toAddress, rd.value, rd.gasPrice, rd.gasLimit);
        return checkTransactionHash(commandSender, receipt.getTransactionHash());
    }

    public static boolean sendTransferFromTransaction(@NotNull CommandSender commandSender, PlayerWalletData commander, ReceiptData rd, ERC20ContractData contractData) throws Exception {
        commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("dispose"));
        TransactionReceipt receipt = APILibrary.TransferFromSFT(contractData, commander, rd.fromAddress, rd.toAddress, rd.gasLimit, rd.gasPrice, rd.value);
        return checkTransactionHash(commandSender, receipt.getTransactionHash());
    }

    public static boolean checkTransactionHash(@NotNull CommandSender commandSender, String transactionHash) {
        if (transactionHash.contains("null")) {
            commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("donterror"));
            return true;
        }
        commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("TransferSuccess") + "\n\u00a7a" + Main.prop.getProperty("ExplorerUrl") + transactionHash);
        return true;
    }

    public static String getVersion() {
        try {
            if (Objects.requireNonNull(Main.fileconfig.getString("Language")).contains("zh")) {
                return Main.SFTInfo + "§a Release1.5, 作者保留所有权利";
            } else {
                return Main.SFTInfo + "Release1.5, Author all rights reserved";
            }
        } catch (Exception ex) {
            return Main.SFTInfo + "Release1.5, Author all rights reserved";
        }
    }

    public static Web3j getWeb3j(String HttpUrl) {
        Web3j web3j = Web3j.build(new HttpService(HttpUrl));
        try {
            Web3ClientVersion clientVersion = web3j.web3ClientVersion().send();
            EthBlockNumber blockNumber = web3j.ethBlockNumber().send();
            EthGasPrice gasPrice = web3j.ethGasPrice().send();
            System.out.println(Main.SFTInfo + Main.prop.getProperty("ClientVersion") + clientVersion.getWeb3ClientVersion());
            System.out.println(Main.SFTInfo + Main.prop.getProperty("CurrentBlock") + blockNumber.getBlockNumber());
            System.out.println(Main.SFTInfo + Main.prop.getProperty("DefaultGasPrice") + gasPrice.getGasPrice());
            return web3j;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static Credentials getCredential(String PrivateKey, String PublicKey) {
        return Credentials.create(PrivateKey, PublicKey);
    }

    public static ERC20 getDefaultGasERC20(ERC20ContractData erc20ContractData, PlayerWalletData playerWalletData) {
        TransactionManager transactionmanager = new RawTransactionManager(Main.chainlibrary.web3j, playerWalletData.creds, Main.chainlibrary.chainid);
        return ERC20.load(erc20ContractData.address, Main.chainlibrary.web3j, transactionmanager, new DefaultGasProvider());
    }

    public static ERC20 getStaticGasERC20(ERC20ContractData erc20ContractData, PlayerWalletData playerWalletData, BigInteger gasPrice, BigInteger gasLimit) {
        TransactionManager transactionmanager = new RawTransactionManager(Main.chainlibrary.web3j, playerWalletData.creds, Main.chainlibrary.chainid);
        return ERC20.load(erc20ContractData.address, Main.chainlibrary.web3j, transactionmanager, new StaticGasProvider(gasPrice, gasLimit));
    }

    public static Object getEthBalance(String toAddress, boolean type) throws IOException {
        EthGetBalance balanceInWei = Main.chainlibrary.web3j.ethGetBalance(toAddress, DefaultBlockParameterName.LATEST).send();
        if (type) {
            return Convert.fromWei(balanceInWei.getBalance().toString(), Unit.ETHER).toString();
        }
        return balanceInWei.getBalance();
    }

    public static BigInteger getERC20Balance(ERC20ContractData erc20ContractData, PlayerWalletData playerWalletData, String ToAddress) throws Exception {
        ERC20 TokenERC20 = getDefaultGasERC20(erc20ContractData, playerWalletData);
        return TokenERC20.balanceOf(ToAddress).send();
    }

    public static TransactionReceipt approve(
            ERC20ContractData erc20ContractData, PlayerWalletData playerWalletData,
            String toAddress, String value, String gasPrice, String gasLimit) throws Exception {
        BigInteger TxValue = new BigDecimal(value).multiply(new BigDecimal(erc20ContractData.decimal)).toBigInteger();
        BigInteger TxGasPrice = Convert.toWei(gasPrice, Unit.GWEI).toBigInteger();
        BigInteger TxGasLimit = new BigInteger(gasLimit);
        ERC20 TokenERC20 = getStaticGasERC20(erc20ContractData, playerWalletData, TxGasPrice, TxGasLimit);
        return TokenERC20.approve(toAddress, TxValue).send();
    }

    public static String TransferETH(
            PlayerWalletData playerWalletData,
            String ToAddress,
            String gasLimit, String gasPrice,
            String value) throws IOException {
        EthGetTransactionCount TransactionCount = Main.chainlibrary.web3j.ethGetTransactionCount(playerWalletData.creds.getAddress(), DefaultBlockParameterName.LATEST).send();
        BigInteger nonce = TransactionCount.getTransactionCount();
        BigInteger TxValue = Convert.toWei(value, Unit.ETHER).toBigInteger();
        BigInteger TxGasLimit = new BigInteger(gasLimit);
        BigInteger TxGasPrice = Convert.toWei(gasPrice, Unit.GWEI).toBigInteger();
        RawTransaction rawTransaction = RawTransaction.createEtherTransaction(nonce, TxGasPrice, TxGasLimit, ToAddress, TxValue);
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, playerWalletData.creds);
        String hexValue = Numeric.toHexString(signedMessage);
        EthSendTransaction ethSendTransaction = Main.chainlibrary.web3j.ethSendRawTransaction(hexValue).send();
        return ethSendTransaction.getTransactionHash();
    }

    public static TransactionReceipt TransferSFT(
            ERC20ContractData erc20ContractData,
            PlayerWalletData playerWalletData,
            String ToAddress, String gasLimit,
            String gasPrice, String value) throws Exception {
        BigInteger TxValue = new BigDecimal(value).multiply(new BigDecimal(erc20ContractData.decimal)).toBigInteger();
        BigInteger TxGasPrice = Convert.toWei(gasPrice, Unit.GWEI).toBigInteger();
        BigInteger TxGasLimit = new BigInteger(gasLimit);
        ERC20 TokenERC20 = getStaticGasERC20(erc20ContractData, playerWalletData, TxGasPrice, TxGasLimit);
        return TokenERC20.transfer(ToAddress, TxValue).send();
    }

    public static TransactionReceipt TransferFromSFT(
            ERC20ContractData erc20ContractData,
            PlayerWalletData playerWalletData, String FromAddress,
            String ToAddress, String gasLimit,
            String gasPrice, String value) throws Exception {
        BigInteger TxValue = new BigDecimal(value).multiply(new BigDecimal(erc20ContractData.decimal)).toBigInteger();
        BigInteger TxGasPrice = Convert.toWei(gasPrice, Unit.GWEI).toBigInteger();
        BigInteger TxGasLimit = new BigInteger(gasLimit);
        ERC20 TokenERC20 = getStaticGasERC20(erc20ContractData, playerWalletData, TxGasPrice, TxGasLimit);
        return TokenERC20.transferFrom(FromAddress, ToAddress, TxValue).send();
    }

    public static boolean executeTransfer(
            PlayerWalletData commander,
            ReceiptData rd, CommandSender commandSender) throws Exception {
        String TokenType = APILibrary.CheckTokenType(rd.type);
        for (Entry<String, Map<Integer, String>> ERC20Map : Main.ERC20ContractMap.entrySet()) {
            ERC20ContractData contractData = new ERC20ContractData(Main.ERC20ContractMap.get(ERC20Map.getKey()));
            if (TokenType.equals(Main.chainlibrary.symbol)) {
                rd.setGasLimit("21000");
                if (APILibrary.sendETHTransaction(commandSender, commander, rd, contractData)) return true;
            } else {
                if (TokenType.equals(contractData.symbol)) {
                    rd.setGasLimit(contractData.gaslimit);
                    if (APILibrary.sendSFTTransaction(commandSender, commander, rd, contractData)) return true;
                }
            }
        }
        commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("CheckGas"));
        return false;
    }

    public static boolean executeTransferFrom(
            PlayerWalletData commander,
            ReceiptData rd, CommandSender commandSender) throws Exception {
        String TokenType = APILibrary.CheckTokenType(rd.type);
        for (Entry<String, Map<Integer, String>> ERC20Map : Main.ERC20ContractMap.entrySet()) {
            ERC20ContractData contractData = new ERC20ContractData(Main.ERC20ContractMap.get(ERC20Map.getKey()));
            if (TokenType.equals(contractData.symbol)) {
                rd.setGasLimit(contractData.gaslimit);
                if (APILibrary.sendTransferFromTransaction(commandSender, commander, rd, contractData)) return true;
            } else {
                commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("cerror"));
                return true;
            }
        }
        commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("CheckGas"));
        return false;
    }

    public static boolean executeApprove(
            PlayerWalletData commander,
            ReceiptData rd, CommandSender commandSender) throws Exception {
        String TokenType = APILibrary.CheckTokenType(rd.type);
        for (Entry<String, Map<Integer, String>> ERC20Map : Main.ERC20ContractMap.entrySet()) {
            ERC20ContractData contractData = new ERC20ContractData(Main.ERC20ContractMap.get(ERC20Map.getKey()));
            if (TokenType.equals(contractData.symbol)) {
                rd.setGasLimit(contractData.gaslimit);
                if (APILibrary.sendApproveTransaction(commandSender, commander, rd, contractData)) return true;
            }
        }
        return false;
    }

    public static String CheckTokenType(String Type) {
        for (Entry<String, Map<Integer, String>> ERC20Map : Main.ERC20ContractMap.entrySet()) {
            String TokenSymbol = Main.ERC20ContractMap.get(ERC20Map.getKey()).get(2);
            if (Type.equals(Main.chainlibrary.symbol)) {
                return Main.chainlibrary.symbol;
            } else if (Type.equals(TokenSymbol)) {
                return TokenSymbol;
            }
        }
        return "null";
    }

    public static String CheckDealType(String Type) {
        for (Entry<String, Map<Integer, String>> DealMap : Main.ExchangeMap.entrySet()) {
            if (Type.equals(DealMap.getKey())) {
                return DealMap.getKey();
            }
        }
        return "null";
    }

    public static boolean systemETHExchange(PlayerWalletData commander, ExchangeData exchangeData, CommandSender commandSender, String ToAddress, String value, String executecommand, String gasLimit, String gasPrice) {
        commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("dispose"));
        String TransactionHash = null;
        try {
            TransactionHash = TransferETH(commander, ToAddress, gasLimit, gasPrice, value);
        } catch (IOException e) {
            commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("TransferFail"));
            return false;
        }
        return executeCommand(exchangeData, commandSender, executecommand, TransactionHash);
    }

    public static boolean executeCommand(ExchangeData exchangeData, CommandSender commandSender, String executecommand, String transactionHash) {
        commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("TransferSuccess") + "\n\u00a7a" + Main.prop.getProperty("ExplorerUrl") + transactionHash);
        Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), () -> {
            Player commandSenderPlayer = (Player) commandSender;
            boolean isOp = commandSenderPlayer.isOp();
            if (APILibrary.executeCommand(commandSenderPlayer, executecommand, isOp)) {
                commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("ExchangeSuccess") + " \u00a7a" + exchangeData.price + " \u00a7c" + exchangeData.tokentype);
            }
        });
        return true;
    }

    public static boolean systemSFTExchange(PlayerWalletData commander, ExchangeData exchangeData, ERC20ContractData contractData, CommandSender commandSender, String ToAddress, String value, String executecommand, String gasLimit, String gasPrice) {
        commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("dispose"));
        String TransactionHash = null;
        try {
            TransactionHash = APILibrary.TransferSFT(contractData, commander, ToAddress, gasLimit, gasPrice, value).getTransactionHash();
        } catch (Exception e) {
            commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("TransferFail"));
            return false;
        }
        return executeCommand(exchangeData, commandSender, executecommand, TransactionHash);
    }

    public static String checkLegalExchange(String Type, PlayerWalletData commander, CommandSender commandSender) throws IOException {
        String DealType = CheckDealType(Type);
        for (Entry<String, Map<Integer, String>> DealMap : Main.ExchangeMap.entrySet()) {
            if (DealType.equals(DealMap.getKey())) {
                ExchangeData exchangeData = new ExchangeData(Main.ExchangeMap.get(DealMap.getKey()));
                String TokenType = CheckTokenType(exchangeData.tokentype);
                if (!TokenType.equals(exchangeData.tokentype)) {
                    return "errortokentype";
                }
                String ToAddress = Main.ConsoleWallet.fromaddress;
                String value = exchangeData.price;
                String executecommand = exchangeData.executecommand.replace("{player}", commander.playerid);
                for (Entry<String, Map<Integer, String>> ERC20Map : Main.ERC20ContractMap.entrySet()) {
                    ERC20ContractData contractData = new ERC20ContractData(Main.ERC20ContractMap.get(ERC20Map.getKey()));
                    if (TokenType.equals(Main.chainlibrary.symbol)) {
                        String gasLimit = "21000";
                        String gasPrice = String.valueOf(Main.chainlibrary.web3j.ethGasPrice().send().getGasPrice().divide(new BigInteger("1000000000")));
                        if (!CheckLegal(contractData, commander, commander.fromaddress, ToAddress, gasLimit, gasPrice, value, true)) {
                            return "unlegalparameters";
                        }
                        if (systemETHExchange(commander, exchangeData, commandSender, ToAddress, value, executecommand, gasLimit, gasPrice)) {
                            return "success";
                        }
                        return "CheckGas";
                    } else {
                        String gasLimit = contractData.gaslimit;
                        String gasPrice = String.valueOf(Main.chainlibrary.web3j.ethGasPrice().send().getGasPrice().divide(new BigInteger("1000000000")));
                        if (TokenType.equals(contractData.symbol)) {
                            if (!CheckLegal(contractData, commander, commander.fromaddress, ToAddress, gasLimit, gasPrice, value, false)) {
                                return "unlegalparameters";
                            }
                            if (systemSFTExchange(commander, exchangeData, contractData, commandSender, ToAddress, value, executecommand, gasLimit, gasPrice)) {
                                return "success";
                            }
                            return "unknowerror";
                        }
                    }
                }
            }
        }
        return "errordeal";
    }

    public static boolean CheckLegal(
            ERC20ContractData erc20ContractData,
            PlayerWalletData playerWalletData, String fromAddress,
            String toAddress, String gasLimit,
            String gasPrice, String value, boolean Type) {
        if (fromAddress.equals(toAddress)) {
            return false;
        }
        if (Type) {
            try {
                BigInteger FromCurrentBalance = (BigInteger) getEthBalance(fromAddress, false);
                BigInteger ToCurrentBalance = (BigInteger) getEthBalance(toAddress, false);
                BigInteger TxValue = Convert.toWei(value, Unit.ETHER).toBigInteger();
                BigInteger TxGasPrice = Convert.toWei(gasPrice, Unit.GWEI).toBigInteger();
                BigInteger TxGasLimit = new BigInteger(gasLimit);
                if (FromCurrentBalance.compareTo(TxGasLimit.multiply(TxGasPrice).add(TxValue)) >= 0) {
                    if (ToCurrentBalance.compareTo(ToCurrentBalance.add(TxValue)) < 0) {
                        return true;
                    }
                }
                return false;
            } catch (Exception e) {
                return false;
            }
        } else {
            try {
                BigInteger FromCurrentBalance = (BigInteger) getEthBalance(fromAddress, false);
                BigInteger ToCurrentBalance = getERC20Balance(erc20ContractData, playerWalletData, toAddress);
                BigInteger TxValue = new BigDecimal(value).multiply(new BigDecimal(erc20ContractData.decimal)).toBigInteger();
                BigInteger TxGasPrice = Convert.toWei(gasPrice, Unit.GWEI).toBigInteger();
                BigInteger TxGasLimit = new BigInteger(gasLimit);
                if (FromCurrentBalance.compareTo(TxGasLimit.multiply(TxGasPrice)) >= 0) {
                    if (ToCurrentBalance.compareTo(ToCurrentBalance.add(TxValue)) < 0) {
                        return true;
                    }
                }
                return false;
            } catch (Exception ex) {
                return false;
            }
        }
    }
}