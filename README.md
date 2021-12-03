# SFTFinance - Let you experience blockchain transactions in Minecraft.

![](/banner.png)

[查看中文说明](/README_zh.md)

## What is SFTFinance ?

SFTFinance Is a minecraft server plug-in based on Bukkit/Spigot

Its features provide players and administrators with operations on the Ethereum blockchain and smart contracts

Tokens that allow interaction between original tokens and ERC20 standard tokens smart contracts.(Like Ether,USDT)

## End User License Agreement

To use SFTFinance (hereinafter referred to as this software),(author is https://github.com/Minecraft652) you need to agree to the following agreements:

- The player wallet data of this software is the user's personal privacy data.
- Using this software, all operations are caused by personal, such as asset loss the author does not assume any responsibility.
- Investment is risky and operation should be cautious. Please comply with local laws and regulations when using this software.
- The author reserves all rights of this software, users and administrators can use this software for free.

## Features

- Connect your server to EVM-compatible blockchain (Ethereum Mainnet, Binance Smart Chain) and other test chains.
- Let players have their own digital wallets and transfer money to each other.
- Provide basic ways to interact with ERC20 token smart contracts such as USDT, DAI and any ERC20 token based standard contracts.
- Administrators can customize transaction pairs that allow players to interact with the server and execute them on the blockchain.

## Support

- Support MySQL Database , SQLite.
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

- /wallet - View your wallet address, balance.
- /wallet help - See the SFTFinance command help.
- /wallet help (page) - See the SFTFinance command help.
- /wallet version - View the current plugin version.
- /wallet blockchain - View details of the currently connected blockchain.
- /wallet keys - View your wallet mnemonics, keys, etc.
- /wallet create - Create a wallet (if you don't have one).
- /wallet gas - View default Gas limits, prices, etc.
- /wallet delete - Delete the current wallet.
- /wallet player (name) - View the specified player's wallet address, balance.
- /wallet exchange - View the currently active deals.
- /wallet exchange (transaction pair) - Transactions with the server. (The administrator needs to configure the transaction pair.)
- /wallet exchange (transaction pair) info - View this deal details.
- /wallet transfer (name) (address) (amount) - Sends the specified token to the destination address.
- /wallet transfer (name) (address) (amount) (gasprice) - Sends tokens to target addresses for specified gas prices.
- /wallet transfer (name) (address) (amount) (gasprice) (gaslimit) - Sends tokens to target addresses specifying gas prices and gas limits.
- /wallet approve (TokenType) (TargetAddress) (amount) - Access target address to approve your ERC20 token, please see info Solidity-Approve method.
- /wallet approve (TokenType) (TargetAddress) (amount) (gasprice) - Access target address to approve your ERC20 token for specified gas price.
- /wallet approve (TokenType) (TargetAddress) (amount) (gasprice) (gaslimit) - Access target address to approve your ERC20 token for specified gas price and gas limit.
- /wallet transferfrom (TokenType) (FromAddress) (TargetAddress) (amount) - Solidity-Transferfrom method.
- /wallet transferfrom (TokenType) (FromAddress) (TargetAddress) (amount) (gasprice) - Solidity-Transferfrom method.
- /wallet transferfrom (TokenType) (FromAddress) (TargetAddress) (amount) (gasprice) (gaslimit) - Solidity-Transferfrom method.
  
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
- ENJOY TO USE! :Smile
