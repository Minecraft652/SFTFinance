package com.sixfivetwo.sftfinance.expansion;

import com.sixfivetwo.sftfinance.APILibrary;
import com.sixfivetwo.sftfinance.Main;
import com.sixfivetwo.sftfinance.datalibrary.ERC20ContractData;
import com.sixfivetwo.sftfinance.datalibrary.PlayerWalletData;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.Map;

public class PlaceHolderAPIExpansion extends PlaceholderExpansion {

    @Override
    public String getAuthor() {
        return "sixfivetwo";
    }

    @Override
    public String getIdentifier() {
        return "sftfinance";
    }

    @Override
    public String getVersion() {
        return "Release1.7.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        try {
            if (identifier.equals("address")) {
                PlayerWalletData commander = new PlayerWalletData(player.getName());
                return commander.fromaddress;
            }
            if (identifier.contains("balance")) {
                for (Map.Entry<String, Map<Integer, String>> ERC20Map : Main.ERC20ContractMap.entrySet()) {
                    ERC20ContractData ERC20Data = new ERC20ContractData(Main.ERC20ContractMap.get(ERC20Map.getKey()), Main.chainlibrary);
                    PlayerWalletData commander = new PlayerWalletData(player.getName());
                    if (identifier.contains(ERC20Data.symbol)) {
                        return new BigDecimal(APILibrary.getERC20Balance(ERC20Data, commander, commander.fromaddress)).divide(new BigDecimal(ERC20Data.decimal)).toString();
                    } else {
                        if (identifier.contains(Main.chainlibrary.symbol)) {
                            return String.valueOf(APILibrary.getEthBalance(commander.fromaddress, true));
                        }
                    }
                }
            }
            return null;
        } catch (Exception ex) {
            return null;
        }
    }
}
