package com.sixfivetwo.sftfinance;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;


public class Main extends JavaPlugin {
	final static String consoleuuid = "00000000-0000-0000-0000-000000000000";
	final static String consoleid = "CONSOLE";
	final static String SFTInfo = "\u00a7a[\u00a76SFT\u00a7cFinance\u00a7a] \u00a7r";
	final static String Thanks = "\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\nThank you for using SFTFinance,It is my first java project\nIf you want to support me,This is my ethereum address : 0x5b615F1a1989ee2636BfbFe471B1F66bCa16F926\nSupport link: https://github.com/Minecraft652/SFTFinance\nI would love to be your friend!Enjoy your use![Smile]\nDear ServerManager \u2014\u2014 Minecraft_652\n\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014";
	final static String ThanksZh = "\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\n\u611f\u8c22\u60a8\u4f7f\u7528 SFTFinance,\u8fd9\u662f\u6211\u7684\u7b2c\u4e00\u4e2aJava\u9879\u76ee\n\u5982\u679c\u60a8\u60f3\u652f\u6301\u6211\u4e00\u4e0b,\u8fd9\u662f\u6211\u7684\u4ee5\u592a\u574a\u5730\u5740 : 0x5b615F1a1989ee2636BfbFe471B1F66bCa16F926\n\u652f\u6301\u94fe\u63a5 : https://github.com/Minecraft652/SFTFinance\n\u6211\u5f88\u4e50\u610f\u8ddf\u4f60\u4ea4\u670b\u53cb\uff01\u4eab\u53d7SFTFinance\u5427[\u5fae\u7b11]\n\u4eb2\u7231\u7684\u670d\u52a1\u5668\u7ba1\u7406\u5458 \u2014\u2014 Minecraft_652\n\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014";
	public static Connection conn;
	public static Properties prop;
	public static BlockchainData chainlibrary;
	public static FileConfiguration fileconfig;
	public static FileConfiguration fileexchange;
	public static FileConfiguration filecontract;
	public static Map<String, Map<Integer, String>> ERC20ContractMap;
	public static Map<String, Map<Integer, String>> ExchangeMap;
	public static PlayerWalletData ConsoleWallet;

	public void onEnable() {

		try {
			ERC20ContractMap = new HashMap<String, Map<Integer, String>>();
			ExchangeMap = new HashMap<String, Map<Integer, String>>();
			Map<String, File> FileMaps = new HashMap<String, File>();
			Map<String, InputStream> InternalFileMaps = new HashMap<String, InputStream>();
			File configfile = new File(getDataFolder(), "config.yml");
			File exchangefile = new File(getDataFolder(), "exchange.yml");
			File contractfile = new File(getDataFolder(), "contract.yml");
			File walletfile = new File(getDataFolder(), "wallets.db");
			FileMaps.put("config.yml", configfile);
			FileMaps.put("exchange.yml", exchangefile);
			FileMaps.put("contract.yml", contractfile);
			FileMaps.put("wallets.db", walletfile);
			InternalFileMaps.put("config.yml", getResource("config.yml"));
			InternalFileMaps.put("exchange.yml", getResource("exchange.yml"));
			InternalFileMaps.put("contract.yml", getResource("contract.yml"));
			InternalFileMaps.put("wallets.db", getResource("wallets.db"));

			if (!getDataFolder().exists()) {
				getDataFolder().mkdir();
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
				Map<Integer, String> FileMapContract = new HashMap<Integer, String>();
				FileMapContract.put(1, filecontract.getString(contractroot + ".Address"));
				FileMapContract.put(2, filecontract.getString(contractroot + ".Symbol"));
				FileMapContract.put(3, filecontract.getString(contractroot + ".GasLimit"));
				FileMapContract.put(4, filecontract.getString(contractroot + ".Decimal"));
				ERC20ContractMap.put(contractroot, FileMapContract);
			}

			for (String exchangeroot : fileexchange.getKeys(false)) {
				Map<Integer, String> FileMapExchange = new HashMap<Integer, String>();
				FileMapExchange.put(1, fileexchange.getString(exchangeroot + ".Tokentype"));
				FileMapExchange.put(2, fileexchange.getString(exchangeroot + ".Price"));
				FileMapExchange.put(3, fileexchange.getString(exchangeroot + ".Executecommand"));
				ExchangeMap.put(exchangeroot, FileMapExchange);
			}

			prop = new Properties();
			InputStream in = Main.class.getClassLoader().getResourceAsStream(fileconfig.getString("Language"));
			prop.load(in);

			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:"+walletfile.toString());

			APILibrary.FirstRun();
			chainlibrary = new BlockchainData(fileconfig.getString("ChainName"), fileconfig.getString("HttpUrl"), fileconfig.getLong("ChainID"), fileconfig.getString("Symbol"));
			ConsoleWallet = new PlayerWalletData("CONSOLE");

			if (fileconfig.getBoolean("OnPlayerLoginRegisterWallet")) {
				Bukkit.getServer().getPluginManager().registerEvents(new SFTListener(),this);
			}

			Objects.requireNonNull(Bukkit.getPluginCommand("wallet")).setExecutor(new SFTCommand());

			if (Objects.requireNonNull(fileconfig.getString("Language")).contains("zh")) {
				System.out.println(ThanksZh);
			} else {
				System.out.println(Thanks);
			}

			System.out.println(APILibrary.getVersion());

		} catch (Exception e) {
			e.printStackTrace();
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