# SFTFinance - Let you experience blockchain transactions in Minecraft.

[查看中文说明](/README_zh.md)

## Introduction

SFTFinance Is a minecraft server plug-in based on Bukkit/Spigot

Its features provide players and administrators with operations on the Ethereum blockchain and smart contracts

Tokens that allow interaction between original tokens and ERC20 standard tokens smart contracts.(Like Ether,USDT)

## Features

- Connect your server to the EVM standard blockchain（Ethereum Mainnet, Ropsten, Binance Smart Chain）
- Let players have their own crypto wallets and transfer money to each other.
- Provide basic ways to interact with ERC20 token smart contracts such as USDT, DAI and any standard contracts based on ERC20 tokens.
- Administrators can customize transactions between players and servers and execute them on the blockchain.

## Support

- Support custom ERC20 token contracts.
- Support EVM blockchain and custom blockchain HTTP address.
- Supports custom administrators to customize transaction pairs.
- Supports the generation of crypto wallets and export to other wallets. (Like Metamask)

## Installation

- Put the plug-in into the server side's plugins folder.
- If the server is started, files such as config.yml are automatically generated.
- Plug-ins can only be configured when the server is off.
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

## Config
