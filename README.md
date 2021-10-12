# SFTFinance - Let you experience blockchain transactions in Minecraft.

[查看中文说明](/README_ZH.md)

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

## Usage SFTFinance ?

举个例子：A 拥有 1 SFT (ERC20 Token) 要跟 B 玩家进行转账 (Transfer method)

B 玩家未知钱包地址 , 首先 A 需要查询 B 玩家的钱包地址即使用该命令: /wallet player B

查询到的 B 玩家钱包地址为 : 0x77751B52F993fD30042999F64Ed0C41A4eFa5Be8

然后 A 输入该命令完成操作: /wallet transfer SFT 0x77751B52F993fD30042999F64Ed0C41A4eFa5Be8 1
