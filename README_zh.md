# SFTFinance - 让你在 Minecraft 里体验区块链上交易。

[View English instructions](/README.md)

## 什么是 SFTFinance

SFTFinance 是一个基于Bukkit/Spigot开发的我的世界服务端插件

其功能提供了让玩家及管理员在以太坊区块链及智能合约的操作

并允许交互原链币及ERC20标准代币智能合约的代币。像 Ether , USDT

## 最终用户许可协议

使用 SFTFinance（以下称为本插件）您需要同意以下协议：

- 本插件的玩家钱包数据均为用户个人隐私数据。
- 使用本插件，所有操作均为个人所致，如有资产损失作者不承担任何责任。
- 投资有风险，操作需谨慎。使用本插件请您遵守当地法律规范。
- 作者保留本插件的最终解释权，用户及管理员均可以免费使用本插件。

## 功能

- 让你的服务器连接至 EVM 标准区块链（Ethereum Mainnet, Ropsten, Binance Smart Chain等）
- 让玩家拥有自己的数字钱包，并且可以相互转账。
- 提供基本交互 ERC20 代币智能合约，如（USDT, DAI及任何基于 ERC20 代币标准合约）的方法。
- 管理员可以自定义交易对，让玩家与服务器进行交互，并且在区块链上执行。

## 支持

- 支持自定义 ERC20 代币合约
- 支持EVM 区块链及自定义区块链 HTTP 地址
- 支持管理员自定义交易对
- 支持生成数字钱包导出到其他钱包
- 支持多个代币合约和交易对

## 安装

- 把插件放进服务端的 plugins 文件夹
- 启动服务端，将会自动生成 config.yml 等文件
- 插件目前只有在服务端关闭状态下才能进行配置
- 配置完毕后，就可以正常使用。

## 命令

- /wallet - 查看钱包余额,区块链当前GasPrice。
- /wallet help - 返回帮助页面。
- /wallet blockchain - 查看当前区块链详细信息，以及已经加载的 ERC20 代币合约等。
- /wallet version - 查看插件版本。
- /wallet keys - 查看钱包的私钥，助记词。
- /wallet create - 创建一个钱包（如果没有）。
- /wallet player <玩家名称> - 查看指定玩家的钱包余额。
- /wallet exchange <交易对> - 与服务器进行交易。（需要管理员自行配置交易对）
- /wallet transfer <代币名称> <目标地址> <金额> - 向指定地址转账。燃油价格和限制均为区块链默认。
- /wallet transfer <代币名称> <目标地址> <金额> <燃油价格> - 以指定的燃油价格向指定地址转账，燃油限制为区块链默认。
- /wallet transfer <代币名称> <目标地址> <金额> <燃油价格> <燃油限制> - 以指定的燃油价格和限制向指定地址转账。

## 配置

[查看主要配置文件及配置帮助](/src/main/resources/config.yml)

[查看交易对配置文件及配置帮助](/src/main/resources/exchange.yml)

[查看ERC20代币合约配置文件及配置帮助](/src/main/resources/contract.yml)

## 环境

本插件没有绝对的版本界限，使用 spigot-1.17 api 进行开发

以下是测试本插件的开发环境（原生支持环境）

如果出现 bug 等问题请在 Issues 给我提出。

除以下环境外的其他版本出现 bug 问题可以联系我协助处理。

Java 版本 :

- Java(TM) SE Runtime Environment (build 1.8.0_231-b11)
- Java(TM) SE Runtime Environment 18.9 (build 11.0.12+8-LTS-237)

Spigot 版本 : 

- CraftBukkit version 3096-Spigot-9fb885e-296df56 (MC: 1.16.5) (Implementing API version 1.16.5-R0.1-SNAPSHOT)

操作系统 : 

- Microsoft Windows 10 2004
