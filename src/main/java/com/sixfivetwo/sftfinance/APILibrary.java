package com.sixfivetwo.sftfinance;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.sixfivetwo.sftfinance.datalibrary.*;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bitcoinj.crypto.*;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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
import java.util.*;
import java.util.Map.Entry;

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

    public static net.md_5.bungee.api.chat.TextComponent textAddEffect(TextComponent textComponent, String content) {
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, content));
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(Main.prop.getProperty("clicktocopy"))}));
        return textComponent;
    }

    public static void loadHelpFile(File helpfile, FileConfiguration filehelp, Map<String, Map<Integer, String>> HelpPageMap) {
        filehelp = YamlConfiguration.loadConfiguration(helpfile);
        for (String helproot : filehelp.getKeys(false)) {
            Map<Integer, String> FileMapHelp = new HashMap<>();
            FileMapHelp.put(1, filehelp.getString(helproot + ".front"));
            FileMapHelp.put(2, filehelp.getString(helproot + ".comment1"));
            FileMapHelp.put(3, filehelp.getString(helproot + ".comment2"));
            FileMapHelp.put(4, filehelp.getString(helproot + ".comment3"));
            FileMapHelp.put(5, filehelp.getString(helproot + ".comment4"));
            FileMapHelp.put(6, filehelp.getString(helproot + ".comment5"));
            FileMapHelp.put(7, filehelp.getString(helproot + ".comment6"));
            FileMapHelp.put(8, filehelp.getString(helproot + ".comment7"));
            FileMapHelp.put(9, filehelp.getString(helproot + ".comment8"));
            FileMapHelp.put(10, filehelp.getString(helproot + ".comment9"));
            FileMapHelp.put(11, filehelp.getString(helproot + ".comment10"));
            HelpPageMap.put(helproot, FileMapHelp);
        }
    }

    public static boolean legacyCreateWallet(String UUID, String PlayerID, File directory) {
        String walletName = null;
        try {
            String password = String.valueOf(new SecureRandom().nextInt(1000000000 - 1));
            walletName = WalletUtils.generateNewWalletFile(password, directory);
            Credentials credentials = WalletUtils.loadCredentials(password, new File(directory, walletName));
            String Address = credentials.getAddress();
            String PrivateKey = "0x" + credentials.getEcKeyPair().getPrivateKey().toString(16);
            String MasterPrivateKey = "0";
            String PublicKey = "0";
            insertData(UUID, PlayerID, MasterPrivateKey, Address, PrivateKey, PublicKey);
            new File(directory, walletName).delete();
            return true;
        } catch (Exception ex) {
            return false;
        } finally {
            new File(directory, walletName).delete();
        }
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
            String PublicKey = "0";
            insertData(UUID, PlayerID, MasterPrivateKey, Address, PrivateKey, PublicKey);
        } catch (Exception ignored) {
        }
        return true;
    }

    public static Inventory createInventory(InventoryHolder invd) {
        Inventory inv = Bukkit.createInventory(invd, 6 * 9 , Main.prop.getProperty("tradegui"));
        ItemStack barrier = APILibrary.getTargetItem(1);
        ItemStack emerald_block = APILibrary.getTargetItem(2);
        ItemStack iron_fence = APILibrary.getTargetItem(3);
        inv.setItem(53, barrier);
        inv.setItem(45, emerald_block);
        inv.setItem(0, iron_fence);
        inv.setItem(1, iron_fence);
        inv.setItem(2, iron_fence);
        inv.setItem(3, iron_fence);
        inv.setItem(4, iron_fence);
        inv.setItem(5, iron_fence);
        inv.setItem(6, iron_fence);
        inv.setItem(7, iron_fence);
        inv.setItem(8, iron_fence);
        inv.setItem(9, iron_fence);
        inv.setItem(18, iron_fence);
        inv.setItem(27, iron_fence);
        inv.setItem(17, iron_fence);
        inv.setItem(26, iron_fence);
        inv.setItem(35, iron_fence);
        inv.setItem(36, iron_fence);
        inv.setItem(37, iron_fence);
        inv.setItem(38, iron_fence);
        inv.setItem(39, iron_fence);
        inv.setItem(40, iron_fence);
        inv.setItem(41, iron_fence);
        inv.setItem(42, iron_fence);
        inv.setItem(43, iron_fence);
        inv.setItem(44, iron_fence);
        inv.setItem(46, iron_fence);
        inv.setItem(47, iron_fence);
        inv.setItem(48, iron_fence);
        inv.setItem(49, iron_fence);
        inv.setItem(50, iron_fence);
        inv.setItem(51, iron_fence);
        inv.setItem(52, iron_fence);
        return inv;
    }

    public static Inventory createCustomInventory(InventoryHolder invd, List<ItemStack> details) {
        Inventory inv = createInventory(invd);
        for (ItemStack itemStack : details) {
            inv.addItem(itemStack);
        }
        return inv;
    }

    public static void createTradeTable(String type, Connection conn) throws ClassNotFoundException {
        try {
            if (type.equals("mysql")) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                PreparedStatement statement = Main.conn.prepareStatement("create table transactions (ID integer NOT NULL PRIMARY KEY AUTO_INCREMENT, fromid varchar(255), toid varchar(255), tokentype varchar(255), value varchar(255), details varchar(255))");
                statement.executeUpdate();
                statement.close();
            } else if (type.equals("sqlite")) {
                Class.forName("org.sqlite.JDBC");
                PreparedStatement statement = Main.conn.prepareStatement("create table transactions (ID integer not null primary key autoincrement, fromid string, toid string, tokentype string, value string, details string)");
                statement.executeUpdate();
                statement.close();
            }
        } catch (SQLException ignored) {
        }
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

    public static void advancedInsertData(String fromid, String toid, String tokentype, String value, String details) throws SQLException {
        PreparedStatement statement = Main.conn.prepareStatement("insert into transactions (fromid,toid,tokentype,value,details) values(?,?,?,?,?)");
        statement.setString(1, fromid);
        statement.setString(2, toid);
        statement.setString(3, tokentype);
        statement.setString(4, value);
        statement.setString(5, details);
        statement.executeUpdate();
        statement.close();
    }

    public static void advancedUpdateData(int id, String details, Connection conn) throws SQLException {
        PreparedStatement statement = conn.prepareStatement("update transactions set details = ? where id = ?");
        statement.setString(1, details);
        statement.setInt(2, id);
        statement.executeUpdate();
        statement.close();
    }

    public static boolean advancedUpdateData(PlayerDealData pdd, Connection conn) throws SQLException {
        PreparedStatement statement = conn.prepareStatement("update transactions set fromid = ?, toid = ? where id = ?");
        statement.setString(1, pdd.toid.playerid);
        statement.setString(2, "NONE");
        statement.setInt(3, pdd.id);
        int line = statement.executeUpdate();
        statement.close();
        if (line == 0) {
            return false;
        }
        return true;
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

    public static void playerSendInteractiveMessage(CommandSender commandSender, List<TextComponent> messageList) {
        for (TextComponent message : messageList) {
            if (commandSender instanceof ConsoleCommandSender) {
                commandSender.sendMessage(message.toPlainText());
            } else {
                Player player = (Player) commandSender;
                player.spigot().sendMessage(message);
            }
        }
    }

    public static void playerSendInteractiveMessage(ConsoleCommandSender commandSender, List<TextComponent> messageList) {
        for (TextComponent message : messageList) {
            commandSender.sendMessage(message.toPlainText());
        }
    }

    public static void sendMessageFromToPlayer(InventoryHolderData invd, int id) {
        List<String> fromMessage = new ArrayList<>();
        List<String> toMessage = new ArrayList<>();
        fromMessage.add(Main.SFTInfo + Main.prop.getProperty("nofityfrom") + id);
        fromMessage.add(Main.SFTInfo + Main.prop.getProperty("nofityfroma") + id);
        toMessage.add(Main.SFTInfo + Main.prop.getProperty("nofityto") + id);
        toMessage.add(Main.SFTInfo + Main.prop.getProperty("nofitytoc") + invd.type);
        toMessage.add(Main.SFTInfo + Main.prop.getProperty("nofitytod") + invd.value);
        toMessage.add(Main.SFTInfo + Main.prop.getProperty("nofitytoa") + id);
        toMessage.add(Main.SFTInfo + Main.prop.getProperty("nofitytob") + id);
        invd.nofity(fromMessage, toMessage);
    }

    public static void sendMessageFromToPlayerUpdate(InventoryHolderEditData invd, PlayerWalletData fromid, PlayerWalletData toid) {
        List<String> fromMessage = new ArrayList<>();
        List<String> toMessage = new ArrayList<>();
        fromMessage.add(Main.SFTInfo + Main.prop.getProperty("nofityupdatefrom") + invd.id);
        fromMessage.add(Main.SFTInfo + Main.prop.getProperty("nofityfroma") + invd.id);
        toMessage.add(Main.SFTInfo + Main.prop.getProperty("nofityupdateto") + invd.id);
        toMessage.add(Main.SFTInfo + Main.prop.getProperty("nofitytoa") + invd.id);
        toMessage.add(Main.SFTInfo + Main.prop.getProperty("nofitytob") + invd.id);
        invd.nofity(fromMessage, toMessage, fromid, toid);
    }

    public static void sendMessageInfo(CommandSender commandSender, List<String> message, PlayerDealData pdd) {
        message.add(Main.SFTInfo + Main.prop.getProperty("dealid") + pdd.id);
        message.add(Main.SFTInfo + Main.prop.getProperty("dealtokentype") + pdd.type);
        message.add(Main.SFTInfo + Main.prop.getProperty("dealvalue") + pdd.value);
        message.add(Main.SFTInfo + Main.prop.getProperty("dealfromid") + pdd.fromid.playerid);
        message.add(Main.SFTInfo + Main.prop.getProperty("dealtoid") + pdd.toid.playerid);
        message.add(Main.SFTInfo + Main.prop.getProperty("dealitem") + String.join(",", pdd.strListItem()));
        APILibrary.playerSendMessage(commandSender, message);
    }

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
        commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("TransferSuccess") + Main.fileconfig.getString("ExplorerUrl") + transactionHash);
        return true;
    }

    public static String getVersion() {
        try {
            if (Objects.requireNonNull(Main.fileconfig.getString("Language")).contains("zh")) {
                return Main.SFTInfo + "§a Release1.6.2, 保留所有权利";
            } else {
                return Main.SFTInfo + "Release1.6.2, all rights reserved";
            }
        } catch (Exception ex) {
            return Main.SFTInfo + "Release1.6.2, all rights reserved";
        }
    }

    public static List<ItemStack> getItemData(Inventory inv) {
        List<ItemStack> details = new ArrayList<>();
        for (ItemStack is : inv.getContents()) {
            if (null != is) {
                if (!is.equals(APILibrary.getTargetItem(1)) &&
                        !is.equals(APILibrary.getTargetItem(2)) &&
                        !is.equals(APILibrary.getTargetItem(3))) {
                    details.add(is);
                }
            }
        }
        return details;
    }

    public static ItemStack getTargetItem(int i) {
        if (i == 1) {
            ItemStack barrier = new ItemStack(Material.BARRIER);
            ItemMeta meta = barrier.getItemMeta();
            meta.setDisplayName(Main.prop.getProperty("tradeguibarrier"));
            meta.addEnchant(Enchantment.DIG_SPEED, 10, true);
            barrier.setItemMeta(meta);
            return barrier;
        }
        if (i == 2) {
            ItemStack emerald_block = new ItemStack(Material.EMERALD_BLOCK);
            ItemMeta meta = emerald_block.getItemMeta();
            meta.setDisplayName(Main.prop.getProperty("tradeguiemeraldblock"));
            meta.addEnchant(Enchantment.DIG_SPEED, 10, true);
            emerald_block.setItemMeta(meta);
            return emerald_block;
        }
        if (i == 3) {
            ItemStack iron_fence = new ItemStack(Material.IRON_FENCE);
            ItemMeta meta = iron_fence.getItemMeta();
            meta.setDisplayName(Main.prop.getProperty("tradeguiironfence"));
            meta.addEnchant(Enchantment.DIG_SPEED, 10, true);
            iron_fence.setItemMeta(meta);
            return iron_fence;
        }
        return null;
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

    public static List<String> getTradeList(int type, String player, Connection connection) throws SQLException {
        List<String> result = new ArrayList<>();
        if (type == 1) {
            PreparedStatement statement = connection.prepareStatement("select ID from transactions where fromid = ? and toid != ?;");
            statement.setString(1, player);
            statement.setString(2, "NONE");
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                result.add(rs.getString("ID"));
            }
            rs.close();
            statement.close();
            return result;
        } else if (type == 2) {
            PreparedStatement statement = connection.prepareStatement("select ID from transactions where toid = ?;");
            statement.setString(1, player);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                result.add(rs.getString("ID"));
            }
            rs.close();
            statement.close();
            return result;
        } else if (type == 3) {
            PreparedStatement statement = connection.prepareStatement("select ID from transactions where fromid = ? and toid = ?;");
            statement.setString(1, player);
            statement.setString(2, "NONE");
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                result.add(rs.getString("ID"));
            }
            rs.close();
            statement.close();
            return result;
        }
        return null;
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

    public static Credentials getCredential(String PrivateKey) {
        return Credentials.create(PrivateKey);
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
            ERC20ContractData contractData = new ERC20ContractData(Main.ERC20ContractMap.get(ERC20Map.getKey()), Main.chainlibrary);
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
            ERC20ContractData contractData = new ERC20ContractData(Main.ERC20ContractMap.get(ERC20Map.getKey()), Main.chainlibrary);
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
            ERC20ContractData contractData = new ERC20ContractData(Main.ERC20ContractMap.get(ERC20Map.getKey()), Main.chainlibrary);
            if (TokenType.equals(contractData.symbol)) {
                rd.setGasLimit(contractData.gaslimit);
                if (APILibrary.sendApproveTransaction(commandSender, commander, rd, contractData)) return true;
            }
        }
        return false;
    }

    public static boolean checkDealAcceptDenyPermission(CommandSender commandSender, String type, String playerid, int id, Connection conn) throws SQLException {
        List<String> from = APILibrary.getTradeList(1, playerid, conn);
        List<String> to = APILibrary.getTradeList(2, playerid, conn);
        List<String> noneFrom = APILibrary.getTradeList(3, playerid, conn);
        String strid = String.valueOf(id);
        if (type.equals("all")) {
            if (from.contains(strid) || to.contains(strid) || noneFrom.contains(strid)) {
                return false;
            }
        } else if (type.equals("from")) {
            if (from.contains(strid) || noneFrom.contains(strid)) {
                return false;
            }
        } else if (type.equals("to")) {
            if (to.contains(strid)) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkTradeAvailable(@NotNull CommandSender commandSender, PlayerWalletData commander) {
        if (commandSender instanceof ConsoleCommandSender) {
            commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("consoledeny"));
            return true;
        }
        if (!Main.fileconfig.getBoolean("playerCanTradeEachOther")) {
            commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("featureunable"));
            return true;
        }
        if (!commander.has) {
            commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("Notreg"));
            return true;
        }
        if (Main.fileconfig.getBoolean("TradeWorldLimit")) {
            List<String> world = Main.fileconfig.getStringList("LimitedWorld");
            if (world.contains(Bukkit.getPlayer(commander.playerid).getWorld().getName())) {
                commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("worlderror"));
                return true;
            }
        }
        return false;
    }

    public static boolean checkSelf(@NotNull CommandSender commandSender, String parameters) {
        return commandSender.getName().equals(parameters);
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
        commandSender.sendMessage(Main.SFTInfo + Main.prop.getProperty("TransferSuccess") + Main.fileconfig.getString("ExplorerUrl") + transactionHash);
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

    public static boolean executeTrade(PlayerDealData pdd, PlayerWalletData playerWalletData, CommandSender commandSender) throws Exception {
        ReceiptData rd = new ReceiptData(pdd.type, pdd.toid.fromaddress, pdd.fromid.fromaddress, pdd.value, "null", String.valueOf(Main.chainlibrary.web3j.ethGasPrice().send().getGasPrice().divide(new BigInteger("1000000000"))));
        if (executeTransfer(playerWalletData, rd, commandSender)) {
            if (advancedUpdateData(pdd, Main.conn)) {
                return true;
            }
        }
        return false;
    }

    public static String checkLegalExchange(String Type, PlayerWalletData commander, CommandSender commandSender, String ToAddress) throws IOException {
        String DealType = CheckDealType(Type);
        for (Entry<String, Map<Integer, String>> DealMap : Main.ExchangeMap.entrySet()) {
            if (DealType.equals(DealMap.getKey())) {
                ExchangeData exchangeData = new ExchangeData(Main.ExchangeMap.get(DealMap.getKey()));
                String TokenType = CheckTokenType(exchangeData.tokentype);
                if (!TokenType.equals(exchangeData.tokentype)) {
                    return "errortokentype";
                }
                String value = exchangeData.price;
                String executecommand = exchangeData.executecommand.replace("{player}", commander.playerid);
                for (Entry<String, Map<Integer, String>> ERC20Map : Main.ERC20ContractMap.entrySet()) {
                    ERC20ContractData contractData = new ERC20ContractData(Main.ERC20ContractMap.get(ERC20Map.getKey()), Main.chainlibrary);
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