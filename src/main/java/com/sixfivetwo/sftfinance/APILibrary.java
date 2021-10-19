package com.sixfivetwo.sftfinance;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import org.bitcoinj.crypto.*;
import org.bitcoinj.wallet.DeterministicSeed;
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
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

public class APILibrary {
	final static ImmutableList<ChildNumber> BIP44_ETH_ACCOUNT_ZERO_PATH =
			ImmutableList.of(new ChildNumber(44, true), new ChildNumber(60, true),
					ChildNumber.ZERO_HARDENED, ChildNumber.ZERO);

	public static String getVersion() {
		try {
			if (Objects.requireNonNull(Main.fileconfig.getString("Language")).contains("zh")) {
				return Main.SFTInfo + "Release1.1, \u4f5c\u8005\u4fdd\u7559\u6240\u6709\u6743\u5229";
			} else {
				return Main.SFTInfo + "Release1.1, Author all rights reserved";
			}
		} catch (Exception ex){
			return Main.SFTInfo + "Release1.1, Author all rights reserved";
		}
	}

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
				assert os != null;
				os.close();
				is.close();
			}
	}
	public static boolean FirstRun() {
		try {
			Statement statement = Main.conn.createStatement();
			statement.executeUpdate("create table wallets (UUID string, PlayerID string, Seed string, Address string, PrivateKey string, PublicKey string)");
			statement.close();
			CreateWallet(Main.consoleuuid, Main.consoleid);
			return false;
		} catch (Exception e) {
			return true;
		}
	}

	public static Web3j GetWeb3j(String HttpUrl) {
		Web3j web3j = Web3j.build(new HttpService(HttpUrl));
		try {
			Web3ClientVersion clientVersion = web3j.web3ClientVersion().send();
			EthBlockNumber blockNumber = web3j.ethBlockNumber().send();
			EthGasPrice gasPrice = web3j.ethGasPrice().send();
			System.out.println(Main.SFTInfo+ Main.prop.getProperty("ClientVersion") + clientVersion.getWeb3ClientVersion());
			System.out.println(Main.SFTInfo+ Main.prop.getProperty("CurrentBlock")  + blockNumber.getBlockNumber());
			System.out.println(Main.SFTInfo+ Main.prop.getProperty("DefaultGasPrice") + gasPrice.getGasPrice());
			return web3j;
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		return null;
	}

	public static boolean CreateWallet(String UUID,String PlayerID) {
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
			String Address = "0x"+address;
			String MasterPrivateKey = Joiner.on(",").join(str);
			String PrivateKey = "0x"+keyPair.getPrivateKey().toString(16);
			String PublicKey = keyPair.getPublicKey().toString(16);
			InsertData(UUID,PlayerID,MasterPrivateKey,Address,PrivateKey,PublicKey);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	public static void InsertData(String UUID,String PlayerID,String Seed,String Address,String PrivateKey,String PublicKey) {
		try {
			PreparedStatement statement = Main.conn.prepareStatement("insert into wallets values(?,?,?,?,?,?)");
			statement.setString(1, UUID);
			statement.setString(2, PlayerID);
			statement.setString(3, Seed);
			statement.setString(4, Address);
			statement.setString(5, PrivateKey);
			statement.setString(6, PublicKey);
			statement.executeUpdate();
			statement.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
	
	public static Credentials getCredential(String PrivateKey, String PublicKey) {
		return Credentials.create(PrivateKey, PublicKey);
	}

	public static PlayerWalletData getPlayerWallet(String playerid) {
		try {
			return new PlayerWalletData(playerid);
		} catch (Exception e) {
			return null;
		}
	}

	public static ERC20 getDefaultGasERC20(ERC20ContractData erc20ContractData, PlayerWalletData playerWalletData) {
		TransactionManager transactionmanager = new RawTransactionManager(Main.chainlibrary.web3j, playerWalletData.creds, Main.chainlibrary.chainid);
		return ERC20.load(erc20ContractData.address, Main.chainlibrary.web3j, transactionmanager, new DefaultGasProvider());
	}

	public static ERC20 getStaticGasERC20(ERC20ContractData erc20ContractData, PlayerWalletData playerWalletData, BigInteger gasPrice, BigInteger gasLimit) {
		TransactionManager transactionmanager = new RawTransactionManager(Main.chainlibrary.web3j, playerWalletData.creds, Main.chainlibrary.chainid);
		return ERC20.load(erc20ContractData.address, Main.chainlibrary.web3j, transactionmanager, new StaticGasProvider(gasPrice, gasLimit));
	}

	public static String getEthBanlance(String ToAddress) throws IOException {
		EthGetBalance balanceInWei = Main.chainlibrary.web3j.ethGetBalance(ToAddress,DefaultBlockParameterName.LATEST).send();
		return Convert.fromWei(balanceInWei.getBalance().toString(), Unit.ETHER).toString();
	}

	public static BigInteger getERC20Balance(ERC20ContractData erc20ContractData, PlayerWalletData playerWalletData, String ToAddress) throws Exception {
		ERC20 TokenERC20 = getDefaultGasERC20(erc20ContractData, playerWalletData);
		return TokenERC20.balanceOf(ToAddress).send();
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

	public static String CheckTokenType(String Type) {
		for (Entry<String, Map<Integer, String>> ERC20Map : Main.ERC20ContractMap.entrySet()) {
			String TokenSymbol = Main.ERC20ContractMap.get(ERC20Map.getKey()).get(2);
			if(Type.equals(Main.chainlibrary.symbol)) {
				return Main.chainlibrary.symbol;
			}
			else if(Type.equals(TokenSymbol)) {
				return TokenSymbol;
			}
		}
		return null;
	}

	public static String CheckDealType(String Type) {
		for (Entry<String, Map<Integer, String>> DealMap : Main.ExchangeMap.entrySet()) {
			if(Type.equals(DealMap.getKey())) {
				return DealMap.getKey();
			}
		}
		return null;
	}

	public static boolean CheckLegal(
		ERC20ContractData erc20ContractData,
		PlayerWalletData playerWalletData,
		Credentials creds, String FromAddress, 
		String ToAddress, String gasLimit, 
		String gasPrice, String value, boolean Type) {
		if(Type) {
			try {
				BigInteger FromCurrentBalance = Main.chainlibrary.web3j.ethGetBalance(FromAddress, DefaultBlockParameterName.LATEST).send().getBalance();
				BigInteger ToCurrentBalance = Main.chainlibrary.web3j.ethGetBalance(ToAddress, DefaultBlockParameterName.LATEST).send().getBalance();
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
				BigInteger FromCurrentBalance = Main.chainlibrary.web3j.ethGetBalance(FromAddress, DefaultBlockParameterName.LATEST).send().getBalance();
				BigInteger ToCurrentBalance = getERC20Balance(erc20ContractData, playerWalletData, ToAddress);
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

