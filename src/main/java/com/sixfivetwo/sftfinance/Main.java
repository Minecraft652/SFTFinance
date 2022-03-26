package com.sixfivetwo.sftfinance;

import com.sixfivetwo.sftfinance.datalibrary.*;
import com.sixfivetwo.sftfinance.listener.ContainerListener;
import com.sixfivetwo.sftfinance.listener.InventoryCloseListener;
import com.sixfivetwo.sftfinance.listener.SFTListener;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
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

    public void onEnable() {

        try {
            ERC20ContractMap = new HashMap<>();
            ExchangeMap = new HashMap<>();
            HelpPageMap = new HashMap<>();
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

            legacyDirectory = new File(getDataFolder().toString() + "/Legacy");

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

            fileconfig = YamlConfiguration.loadConfiguration(configfile);
            fileexchange = YamlConfiguration.loadConfiguration(exchangefile);
            filecontract = YamlConfiguration.loadConfiguration(contractfile);

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
                ExchangeMap.put(exchangeroot, FileMapExchange);
            }

            if (Objects.requireNonNull(fileconfig.getString("Language")).contains("zh")) {
                APILibrary.loadHelpFile(zhhelpfile, filehelp, HelpPageMap);
            } else if (Objects.requireNonNull(fileconfig.getString("Language")).contains("ru")) {
                APILibrary.loadHelpFile(ruhelpfile, filehelp, HelpPageMap);
            } else {
                APILibrary.loadHelpFile(enhelpfile, filehelp, HelpPageMap);
            }

            prop = new Properties();
            InputStream in = new FileInputStream(new File(getDataFolder(), fileconfig.getString("Language")));
            prop.load(in);

            try {
                if (fileconfig.getBoolean("IsMysql")) {
                    String user = fileconfig.getString("MysqlUser");
                    String pass = fileconfig.getString("MysqlPassword");
                    String url = "jdbc:" + fileconfig.getString("MysqlUrl");
                    conn = APILibrary.getConnection("mysql", url, user, pass);
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
                    conn = APILibrary.getConnection("sqlite", url, "null", "null");
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

            chainlibrary = new BlockchainData(fileconfig.getString("ChainName"), fileconfig.getString("HttpUrl"), fileconfig.getLong("ChainID"), fileconfig.getString("Symbol"));
            ConsoleWallet = new PlayerWalletData("CONSOLE");

            if (fileconfig.getBoolean("OnPlayerLoginRegisterWallet")) {
                Bukkit.getServer().getPluginManager().registerEvents(new SFTListener(), this);
            }

            Bukkit.getServer().getPluginManager().registerEvents(new ContainerListener(), this);
            Bukkit.getServer().getPluginManager().registerEvents(new InventoryCloseListener(), this);

            Objects.requireNonNull(Bukkit.getPluginCommand("wallet")).setExecutor(new SFTCommand());

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
    }
}