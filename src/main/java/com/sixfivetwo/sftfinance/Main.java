package com.sixfivetwo.sftfinance;

import com.sixfivetwo.sftfinance.datalibrary.*;
import com.sixfivetwo.sftfinance.expansion.PlaceHolderAPIExpansion;
import com.sixfivetwo.sftfinance.listener.ContainerListener;
import com.sixfivetwo.sftfinance.listener.InventoryCloseListener;
import com.sixfivetwo.sftfinance.listener.SFTListener;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.apache.logging.log4j.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

public class Main extends JavaPlugin {
    public final static String SFTInfo = "§a[§6SFT§cFinance§a] §r";
    public final static String Thanks = "————————————————\nThank you for using SFTFinance,It is my first java project\nIf you want to support me,This is my ethereum address : 0x5b615F1a1989ee2636BfbFe471B1F66bCa16F926\nSupport link: https://github.com/Minecraft652/SFTFinance\nI would love to be your friend!Enjoy your use![Smile]\nDear ServerManager —— Minecraft_652n————————————————";
    public final static String ThanksZh = "————————————————\n感谢您使用 SFTFinance,这是我的第一个Java项目\n如果您想支持我一下,这是我的以太坊地址 : 0x5b615F1a1989ee2636BfbFe471B1F66bCa16F926 \n支持链接 : https://github.com/Minecraft652/SFTFinance\n我很乐意跟你交朋友！享受SFTFinance吧[微笑]\n亲爱的服务器管理员 —— Minecraft_652\n——————————————";
    public static Connection conn;
    public static Properties prop;
    public static BlockchainData chainlibrary;
    public static FileConfiguration fileconfig;
    public static FileConfiguration fileexchange;
    public static FileConfiguration filecontract;
    public static FileConfiguration filehelp;
    public static File legacyDirectory;
    public static Map<String, Map<Integer, String>> ERC20ContractMap;
    public static Map<String, Map<Integer, String>> ExchangeMap;
    public static Map<String, Map<Integer, String>> HelpPageMap;
    public static PlayerWalletData ConsoleWallet;
    public static CommandFilter commandFilter;
    public static SFTCommand sftCommand;
    public static SFTListener sftListener;
    public static ContainerListener containerListener;
    public static InventoryCloseListener inventoryCloseListener;
    public boolean isReload;
    public CommandSender theReloader;

    public void onEnable() {

        try {
            setERC20ContractMap(new HashMap<>());
            setExchangeMap(new HashMap<>());
            setHelpPageMap(new HashMap<>());
            Map<String, File> FileMaps = new HashMap<>();
            Map<String, InputStream> InternalFileMaps = new HashMap<>();
            File configfile = new File(getDataFolder(), "config.yml");
            File exchangefile = new File(getDataFolder(), "exchange.yml");
            File contractfile = new File(getDataFolder(), "contract.yml");
            File walletfile = new File(getDataFolder(), "wallets.db");
            File zhhelpfile = new File(getDataFolder(), "help_zh_CN.yml");
            File enhelpfile = new File(getDataFolder(), "help_en_US.yml");
            File ruhelpfile = new File(getDataFolder(), "help_ru_RU.yml");
            File zhlanfile = new File(getDataFolder(), "zh_CN.properties");
            File enlanfile = new File(getDataFolder(), "en_US.properties");
            File rulanfile = new File(getDataFolder(), "ru_RU.properties");
            FileMaps.put("config.yml", configfile);
            FileMaps.put("exchange.yml", exchangefile);
            FileMaps.put("contract.yml", contractfile);
            FileMaps.put("wallets.db", walletfile);
            FileMaps.put("help_zh_CN.yml", zhhelpfile);
            FileMaps.put("help_en_US.yml", enhelpfile);
            FileMaps.put("help_ru_RU.yml", ruhelpfile);
            FileMaps.put("zh_CN.properties", zhlanfile);
            FileMaps.put("en_US.properties", enlanfile);
            FileMaps.put("ru_RU.properties", rulanfile);
            InternalFileMaps.put("config.yml", getResource("config.yml"));
            InternalFileMaps.put("exchange.yml", getResource("exchange.yml"));
            InternalFileMaps.put("contract.yml", getResource("contract.yml"));
            InternalFileMaps.put("wallets.db", getResource("wallets.db"));
            InternalFileMaps.put("help_zh_CN.yml", getResource("help_zh_CN.yml"));
            InternalFileMaps.put("help_en_US.yml", getResource("help_en_US.yml"));
            InternalFileMaps.put("help_ru_RU.yml", getResource("help_ru_RU.yml"));
            InternalFileMaps.put("zh_CN.properties", getResource("zh_CN.properties"));
            InternalFileMaps.put("en_US.properties", getResource("en_US.properties"));
            InternalFileMaps.put("ru_RU.properties", getResource("ru_RU.properties"));

            setLegacyDirectory(new File(getDataFolder().toString() + "/Legacy"));

            if (!getDataFolder().exists()) {
                getDataFolder().mkdir();
            }
            if (!legacyDirectory.exists()) {
                legacyDirectory.mkdir();
            }

            for (String keySet : FileMaps.keySet()) {
                File externalfile = FileMaps.get(keySet);
                if (!externalfile.exists()) {
                    InputStream internalfiles = InternalFileMaps.get(keySet);
                    APILibrary.inputStream2File(internalfiles, externalfile);
                }
            }

            setFileconfig(YamlConfiguration.loadConfiguration(configfile));
            setFileexchange(YamlConfiguration.loadConfiguration(exchangefile));
            setFilecontract(YamlConfiguration.loadConfiguration(contractfile));

            for (String contractroot : filecontract.getKeys(false)) {
                Map<Integer, String> FileMapContract = new HashMap<>();
                FileMapContract.put(1, filecontract.getString(contractroot + ".Address"));
                FileMapContract.put(2, filecontract.getString(contractroot + ".Symbol"));
                FileMapContract.put(3, filecontract.getString(contractroot + ".GasLimit"));
                FileMapContract.put(4, filecontract.getString(contractroot + ".Decimal"));
                ERC20ContractMap.put(contractroot, FileMapContract);
            }

            for (String exchangeroot : fileexchange.getKeys(false)) {
                Map<Integer, String> FileMapExchange = new HashMap<>();
                FileMapExchange.put(1, fileexchange.getString(exchangeroot + ".Tokentype"));
                FileMapExchange.put(2, fileexchange.getString(exchangeroot + ".Price"));
                FileMapExchange.put(3, fileexchange.getString(exchangeroot + ".Executecommand"));
                FileMapExchange.put(4, fileexchange.getString(exchangeroot + ".ExecutorIsConsole"));
                ExchangeMap.put(exchangeroot, FileMapExchange);
            }

            if (Objects.requireNonNull(fileconfig.getString("Language")).contains("zh")) {
                APILibrary.loadHelpFile(zhhelpfile, HelpPageMap);
            } else if (Objects.requireNonNull(fileconfig.getString("Language")).contains("ru")) {
                APILibrary.loadHelpFile(ruhelpfile, HelpPageMap);
            } else {
                APILibrary.loadHelpFile(enhelpfile, HelpPageMap);
            }

            setProp(new Properties());

            InputStream in = Files.newInputStream(new File(getDataFolder(), fileconfig.getString("Language")).toPath());
            prop.load(in);

            try {
                if (fileconfig.getBoolean("IsMysql")) {
                    String user = fileconfig.getString("MysqlUser");
                    String pass = fileconfig.getString("MysqlPassword");
                    String url = "jdbc:" + fileconfig.getString("MysqlUrl");
                    setConn(APILibrary.getConnection("mysql", url, user, pass));
                    if (Main.fileconfig.getBoolean("LegacyWalletGenerator")) {
                        APILibrary.legacyCreateWallet("00000000-0000-0000-0000-000000000000", "CONSOLE", Main.legacyDirectory);
                    } else {
                        APILibrary.createWallet("00000000-0000-0000-0000-000000000000", "CONSOLE");
                    }
                    if (fileconfig.getBoolean("playerCanTradeEachOther")) {
                        APILibrary.createTradeTable("mysql", conn);
                    }
                } else {
                    String url = "jdbc:sqlite:" + walletfile.toString();
                    setConn(conn = APILibrary.getConnection("sqlite", url, "null", "null"));
                    if (Main.fileconfig.getBoolean("LegacyWalletGenerator")) {
                        APILibrary.legacyCreateWallet("00000000-0000-0000-0000-000000000000", "CONSOLE", Main.legacyDirectory);
                    } else {
                        APILibrary.createWallet("00000000-0000-0000-0000-000000000000", "CONSOLE");
                    }
                    if (fileconfig.getBoolean("playerCanTradeEachOther")) {
                        APILibrary.createTradeTable("sqlite", conn);
                    }
                }
            } catch (NoSuchMethodError ex) {
                System.out.println(Main.prop.getProperty("error"));
                System.out.println(Main.prop.getProperty("error"));
                System.out.println(Main.prop.getProperty("error"));
                System.out.println(Main.prop.getProperty("error"));
                System.out.println(Main.prop.getProperty("error"));
            }

            setChainlibrary(new BlockchainData(fileconfig.getString("ChainName"), fileconfig.getString("HttpUrl"), fileconfig.getLong("ChainID"), fileconfig.getString("Symbol"), APILibrary.getWeb3j(fileconfig.getString("HttpUrl"))));
            setConsoleWallet(new PlayerWalletData("CONSOLE"));

            if (fileconfig.getBoolean("OnPlayerLoginRegisterWallet")) {
                setSftListener(new SFTListener());
                Bukkit.getServer().getPluginManager().registerEvents(sftListener, this);
            }

            if (fileconfig.getBoolean("playerCanTradeEachOther")) {
                setContainerListener(new ContainerListener());
                setInventoryCloseListener(new InventoryCloseListener());
                Bukkit.getServer().getPluginManager().registerEvents(containerListener, this);
                Bukkit.getServer().getPluginManager().registerEvents(inventoryCloseListener, this);
            }

            setSftCommand(new SFTCommand());
            Objects.requireNonNull(Bukkit.getPluginCommand("wallet")).setExecutor(sftCommand);

            if (Objects.requireNonNull(fileconfig.getString("Language")).contains("zh")) {
                System.out.println(ThanksZh);
            } else {
                System.out.println(Thanks);
            }

            System.out.println(APILibrary.getVersion());

            if ("".equals(fileconfig.getString("Version")) ||
                    "".equals(fileconfig.getString("Language")) ||
                    "".equals(fileconfig.getString("HttpUrl")) ||
                    "".equals(fileconfig.getString("ExplorerUrl")) ||
                    "".equals(fileconfig.getString("Symbol")) ||
                    "".equals(fileconfig.getString("ChainName")) ||
                    "".equals(fileconfig.getString("ChainID")) ||
                    "".equals(fileconfig.getString("OnPlayerLoginRegisterWallet")) ||
                    "".equals(fileconfig.getString("LegacyWalletGenerator")) ||
                    "".equals(fileconfig.getString("playerCanImportTheyOwnWallet")) ||
                    "".equals(fileconfig.getString("playerCanTradeEachOther")) ||
                    "".equals(fileconfig.getString("TradeWorldLimit")) ||
                    "".equals(fileconfig.getString("LimitedWorld")) ||
                    "".equals(fileconfig.getString("TradeAmountLimit")) ||
                    "".equals(fileconfig.getString("EnableCommandFilter")) ||
                    "".equals(fileconfig.getString("EnableErrorPrint")) ||
                    "".equals(fileconfig.getString("EnableGeneralChecker")) ||
                    "".equals(fileconfig.getString("EnablePlaceHolderAPI")) ||
                    "".equals(fileconfig.getString("IsMysql")) ||
                    "".equals(fileconfig.getString("MysqlUrl")) ||
                    "".equals(fileconfig.getString("MysqlUser")) ||
                    "".equals(fileconfig.getString("MysqlPassword")) ||
                    null == fileconfig.getString("Version") ||
                    null == fileconfig.getString("Language") ||
                    null == fileconfig.getString("HttpUrl") ||
                    null == fileconfig.getString("ExplorerUrl") ||
                    null == fileconfig.getString("Symbol") ||
                    null == fileconfig.getString("ChainName") ||
                    null == fileconfig.getString("ChainID") ||
                    null == fileconfig.getString("OnPlayerLoginRegisterWallet") ||
                    null == fileconfig.getString("LegacyWalletGenerator") ||
                    null == fileconfig.getString("playerCanImportTheyOwnWallet") ||
                    null == fileconfig.getString("playerCanTradeEachOther") ||
                    null == fileconfig.getString("TradeWorldLimit") ||
                    null == fileconfig.getString("LimitedWorld") ||
                    null == fileconfig.getString("TradeAmountLimit") ||
                    null == fileconfig.getString("EnableCommandFilter") ||
                    null == fileconfig.getString("EnableErrorPrint") ||
                    null == fileconfig.getString("EnableGeneralChecker") ||
                    null == fileconfig.getString("EnablePlaceHolderAPI") ||
                    null == fileconfig.getString("IsMysql") ||
                    null == fileconfig.getString("MysqlUrl") ||
                    null == fileconfig.getString("MysqlUser") ||
                    null == fileconfig.getString("MysqlPassword")) {
                System.out.println(Main.prop.getProperty("updateconfig"));
                System.out.println(Main.prop.getProperty("updateconfig"));
                System.out.println(Main.prop.getProperty("updateconfig"));
                System.out.println(Main.prop.getProperty("updateconfig"));
                System.out.println(Main.prop.getProperty("updateconfig"));
            }

            if (Bukkit.getVersion().contains("1.8")) {
                if (!fileconfig.getBoolean("LegacyWalletGenerator")) {
                    System.out.println(Main.prop.getProperty("legacyversion"));
                    System.out.println(Main.prop.getProperty("legacyversion"));
                    System.out.println(Main.prop.getProperty("legacyversion"));
                    System.out.println(Main.prop.getProperty("legacyversion"));
                    System.out.println(Main.prop.getProperty("legacyversion"));
                }
            }

            if (fileconfig.getBoolean("EnableCommandFilter")) {
                Class.forName("org.apache.logging.log4j.core.filter.AbstractFilter");
                org.apache.logging.log4j.core.Logger logger;
                logger = (org.apache.logging.log4j.core.Logger) LogManager.getRootLogger();
                setCommandFilter(new CommandFilter());
                logger.addFilter(commandFilter);
            }

            if (isReload) {
                System.out.println(Main.SFTInfo + prop.getProperty("reload"));
                theReloader.sendMessage(Main.SFTInfo + prop.getProperty("reload"));
                setReload(false);
                setTheReloader(null);
            }

            if (fileconfig.getBoolean("EnablePlaceHolderAPI")) {
                if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                    new PlaceHolderAPIExpansion().register();
                } else {
                    System.out.println(prop.getProperty("noplaceholderapi"));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onDisable() {
        if (Objects.requireNonNull(fileconfig.getString("Language")).contains("zh")) {
            System.out.println(ThanksZh);
        } else {
            System.out.println(Thanks);
        }
        System.out.println(APILibrary.getVersion());

        setConn(null);
        setProp(null);
        setChainlibrary(null);
        setFileconfig(null);
        setFilecontract(null);
        setFilehelp(null);
        setFileexchange(null);
        setLegacyDirectory(null);
        setERC20ContractMap(null);
        setExchangeMap(null);
        setHelpPageMap(null);
        setConsoleWallet(null);
        setCommandFilter(null);
        setContainerListener(null);
        setInventoryCloseListener(null);
        setSftCommand(null);
        setSftListener(null);
        APILibrary.setBip44EthAccountZeroPath(null);
    }

    public void reloadWithCommand(CommandSender commandSender) {
        setReload(true);
        setTheReloader(commandSender);
        onDisable();
        onEnable();
    }

    public static BlockchainData getChainlibrary() {
        return chainlibrary;
    }

    public static Connection getConn() {
        return conn;
    }

    public static File getLegacyDirectory() {
        return legacyDirectory;
    }

    public static FileConfiguration getFileconfig() {
        return fileconfig;
    }

    public static FileConfiguration getFilecontract() {
        return filecontract;
    }

    public static FileConfiguration getFileexchange() {
        return fileexchange;
    }

    public static FileConfiguration getFilehelp() {
        return filehelp;
    }

    public static Map<String, Map<Integer, String>> getERC20ContractMap() {
        return ERC20ContractMap;
    }

    public static Map<String, Map<Integer, String>> getExchangeMap() {
        return ExchangeMap;
    }

    public static Map<String, Map<Integer, String>> getHelpPageMap() {
        return HelpPageMap;
    }

    public static PlayerWalletData getConsoleWallet() {
        return ConsoleWallet;
    }

    public static Properties getProp() {
        return prop;
    }

    public static CommandFilter getCommandFilter() {
        return commandFilter;
    }

    public static ContainerListener getContainerListener() {
        return containerListener;
    }

    public static InventoryCloseListener getInventoryCloseListener() {
        return inventoryCloseListener;
    }

    public static SFTCommand getSftCommand() {
        return sftCommand;
    }

    public static SFTListener getSftListener() {
        return sftListener;
    }

    public CommandSender getTheReloader() {
        return theReloader;
    }


    public boolean isReload() {
        return isReload;
    }

    public static void setChainlibrary(BlockchainData chainlibrary) {
        Main.chainlibrary = chainlibrary;
    }

    public static void setConn(Connection conn) {
        Main.conn = conn;
    }

    public static void setConsoleWallet(PlayerWalletData consoleWallet) {
        ConsoleWallet = consoleWallet;
    }

    public static void setERC20ContractMap(Map<String, Map<Integer, String>> ERC20ContractMap) {
        Main.ERC20ContractMap = ERC20ContractMap;
    }

    public static void setExchangeMap(Map<String, Map<Integer, String>> exchangeMap) {
        ExchangeMap = exchangeMap;
    }

    public static void setFileconfig(FileConfiguration fileconfig) {
        Main.fileconfig = fileconfig;
    }

    public static void setFilecontract(FileConfiguration filecontract) {
        Main.filecontract = filecontract;
    }

    public static void setFileexchange(FileConfiguration fileexchange) {
        Main.fileexchange = fileexchange;
    }

    public static void setFilehelp(FileConfiguration filehelp) {
        Main.filehelp = filehelp;
    }

    public static void setHelpPageMap(Map<String, Map<Integer, String>> helpPageMap) {
        HelpPageMap = helpPageMap;
    }

    public static void setLegacyDirectory(File legacyDirectory) {
        Main.legacyDirectory = legacyDirectory;
    }

    public static void setProp(Properties prop) {
        Main.prop = prop;
    }

    public static void setCommandFilter(CommandFilter commandFilter) {
        Main.commandFilter = commandFilter;
    }

    public static void setContainerListener(ContainerListener containerListener) {
        Main.containerListener = containerListener;
    }

    public static void setInventoryCloseListener(InventoryCloseListener inventoryCloseListener) {
        Main.inventoryCloseListener = inventoryCloseListener;
    }

    public static void setSftCommand(SFTCommand sftCommand) {
        Main.sftCommand = sftCommand;
    }

    public static void setSftListener(SFTListener sftListener) {
        Main.sftListener = sftListener;
    }

    public void setReload(boolean reload) {
        isReload = reload;
    }

    public void setTheReloader(CommandSender theReloader) {
        this.theReloader = theReloader;
    }
}