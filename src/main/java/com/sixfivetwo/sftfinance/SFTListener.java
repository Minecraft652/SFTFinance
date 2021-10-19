package com.sixfivetwo.sftfinance;

import org.bitcoinj.crypto.MnemonicException.MnemonicLengthException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.web3j.crypto.CipherException;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SFTListener implements Listener {
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent e) throws ClassNotFoundException, SQLException, MnemonicLengthException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, CipherException, IOException {
		String UUID = e.getPlayer().getUniqueId().toString();
		String PlayerID = e.getPlayer().getName();
		PreparedStatement statement = Main.conn.prepareStatement("select * from wallets");
		ResultSet rs = statement.executeQuery();
		while (rs.next()) {
			if (rs.getString("UUID").contains(UUID)) {
				rs.close();
				statement.close();
				return;
			}
		}
		APILibrary.CreateWallet(UUID,PlayerID);
		rs.close();
		statement.close();
	}
}