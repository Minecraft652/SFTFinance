package com.sixfivetwo.sftfinance;

import org.bukkit.event.Listener;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bitcoinj.crypto.MnemonicException.MnemonicLengthException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLoginEvent;
import org.web3j.crypto.CipherException;

public class SFTListener implements Listener {
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent e) throws ClassNotFoundException, SQLException, MnemonicLengthException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, CipherException, IOException {
		String UUID = e.getPlayer().getUniqueId().toString();
		String PlayerID = e.getPlayer().getName();
		ResultSet rs = Main.statement.executeQuery("select * from wallets");
		do {
			String CheckAvaString = rs.getString("UUID");
			if (CheckAvaString.contains(UUID)) {
				rs.close();
				return;
			}
		} while (rs.next());
		APILibrary.CreateWallet(UUID,PlayerID);
		rs.close();
	}
}