# SFTFinance - Let you experience blockchain transactions in Minecraft.

[查看中文说明](/README_zh.md)

## What is SFTFinance ?

SFTFinance Is a minecraft server plug-in based on Bukkit/Spigot

Its features provide players and administrators with operations on the Ethereum blockchain and smart contracts

Tokens that allow interaction between original tokens and ERC20 standard tokens smart contracts.(Like Ether,USDT)

## End User License Agreement

To use SFTFinance (hereinafter referred to as this software) you need to agree to the following agreements:

- The player wallet data of this software is the user's personal privacy data.
- Using this software, all operations are caused by personal, such as asset loss the author does not assume any responsibility.
- Investment is risky and operation should be cautious. Please comply with local laws and regulations when using this software.
- The author reserves the right of final interpretation of this software, users and administrators can use this software free of charge.

## Features

- Connect your server to EVM-compatible blockchain (Ethereum Mainnet, Binance Smart Chain) and other test chains.
- Let players have their own digital wallets and transfer money to each other.
- Provide basic ways to interact with ERC20 token smart contracts such as USDT, DAI and any ERC20 token based standard contracts.
- Administrators can customize transaction pairs that allow players to interact with the server and execute them on the blockchain.

## Support

- Support custom ERC20 token contracts.
- Support EVM compatible blockchain and custom BLOCKCHAIN HTTP address.
- Supports administrators to customize transaction pairs.
- Supports generating digital wallets and exporting them to other wallets.
- Supports multiple token contracts and trading pairs.
- RPC connection blockchain is not currently supported.

## Installation

- Put the plugin into the server side's plugins folder
- If the server is started, files such as config.yml are automatically generated
- Plugin can only be configured when the server is disabled
- After the configuration is complete, it can be used normally.

## Command

- /wallet - View wallet balance, blockchain current GasPrice.
- /wallet help - Return to help page.
- /wallet blockchain - View current blockchain details, loaded ERC20 token contracts, etc.
- /wallet version - View the plug-in version.
- /wallet keys - View wallet private key, mnemonic.
- /wallet create - Creates a wallet (if there is one).
- /wallet player (name) - Check the wallet balance of the specified player.
- /wallet exchange (transaction pair) - Transactions with the server. (The administrator needs to configure the transaction pair.)
- /wallet transfer (name) (address) (amount) - Gas prices and limits are blockchain defaults.
- /wallet transfer (name) (address) (amount) (gasprice) - Transfer the specified gas price to the specified address, gas limit is default.
- /wallet transfer (name) (address) (amount) (gasprice) (gaslimit) - Transfer to the specified address with the specified gas price and limit.

## Configuration

[View main configuration files and configuration help](/src/main/resources/config.yml)
[View transaction pairs configuration files and configuration help](/src/main/resources/exchange.yml)
[View the ERC20 token contract configuration file and configuration help](/src/main/resources/contract.yml)

## Environment

This software has no absolute version limits and is developed using the Spigot-1.17 API

Here is the development environment (native support environment) in which this plug-in was tested

If there are bugs and other problems, please put forward to me in Issues.

In addition to the following environment, other versions of the bug problem, please contact me for assistance.

Java version :

- Java(TM) SE Runtime Environment (build 1.8.0_231-b11)
- Java(TM) SE Runtime Environment 18.9 (build 11.0.12+8-LTS-237)

Server version : 

- CraftBukkit version 3096-Spigot-9fb885e-296df56 (MC: 1.16.5) (Implementing API version 1.16.5-R0.1-SNAPSHOT)

Operating system : 

- Microsoft Windows 10 2004

## What the author wants to say to you

- This is my first Java project, if you like my project
- this is my ethereum address: 0x5b615f1a1989ee2636bfbfe471b1f66bca16f926
- I am Minecraft_652, a programmer who loves learning and is committed to network decentralization.
- Thank you very much for your support! My contact information is as follows:
- QQ : 919899140 , Telegram : https://t.me/SIXFIVETWO
- ENJOY TO USE!
