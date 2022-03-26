# SFTFinance - 让你在 Minecraft 里体验区块链上交易。

![](/bannerzh.png)

[View English instructions](/README.md)

## 什么是 SFTFinance

SFTFinance 是一个基于Bukkit/Spigot开发的我的世界服务端插件

其功能提供了让玩家及管理员在兼容EVM标准区块链及智能合约的操作

并允许交互原链币及ERC20标准代币智能合约的代币。像 Ether , USDT

## 最终用户许可协议

版权(c) Github: Minecraft652。保留所有权利。(翻译来自 英文版本)
https://github.com/Minecraft652

谨此同意，只免费向任何人提供使用和复制本软件及相关文件的权利。
不包括任何修改、合并、分发、再授权和/或出售“软件”副本的权利或其他特殊权利。
如果您需要额外的权利，请联系作者的许可。
上述版权声明和本许可声明应包含在软件的所有副本或主要部分中。

本软件是“按现状”提供的，没有任何形式的明示或暗示的保证，包括但不限于适销性、适合特定用途和不侵权的保证。在任何情况下，作者或版权所有人都不应对任何索赔、损害赔偿或其他责任负责，无论是在合同、侵权或其他行为中，或与软件或软件的使用或其他交易有关的行为。

## 使用 SFTFinance 你需要知道的

- 本软件的玩家钱包数据均为用户个人隐私数据。
- 使用本软件，所有操作均为个人所致，如有资产损失作者不承担任何责任。
- 投资有风险，操作需谨慎。使用本软件请您遵守当地法律规范。
- 作者保留本软件的所有权利，请阅读许可协议，如果您继续使用本软件，将默认您已经同意该协议。

## 功能

- 让你的服务器连接至兼容 EVM 的区块链（Ethereum Mainnet, Binance Smart Chain）等测试网链。
- 让玩家拥有自己的数字钱包，并且可以相互转账。
- 提供基本交互 ERC20 代币智能合约，如（USDT, DAI及任何基于 ERC20 代币标准合约）的方法。
- 管理员可以自定义交易对，让玩家与服务器进行交互，并且在区块链上执行。

## 支持

- 支持 MySQL , SQLite 数据库。
- 支持自定义 ERC20 代币合约。
- 支持兼容 EVM 的区块链及自定义区块链 HTTP 地址。
- 支持所有基于以太坊网络的区块链钱包, 以及玩家可以自行导入。
- 支持管理员自定义交易对。
- 支持玩家使用游戏物品数字货币定价进行交易。
- 支持生成数字钱包导出到其他钱包。
- 支持多个代币合约和交易对。
- 支持老版本 Minecraft 至 1.8。

## 安装

- 把插件放进服务端的 plugins 文件夹
- 启动服务端，将会自动生成 config.yml 等文件
- 插件目前只有在服务端关闭状态下才能进行配置
- 配置完毕后，就可以正常使用。

## 命令

- /wallet - 查看您的钱包地址,余额.
- /wallet help - 查看SFTFinance命令帮助.
- /wallet help <页面> - 查看SFTFinance命令帮助.
- /wallet version - 查看当前插件版本.
- /wallet blockchain - 查看当前连接的区块链详细信息.
- /wallet keys - 查看您的钱包助记词，密钥等.
- /wallet create - 创建一个钱包(前提是没有).
- /wallet player <玩家名称> - 查看指定玩家的钱包地址,余额.
- /wallet import <私钥> - 导入钱包至SFTFinance.
- /wallet gas - 查看默认Gas限制,价格等设置.
- /wallet delete - 删除当前钱包.
- /wallet exchange - 查看当前活跃的交易对.
- /wallet exchange <交易对> - 与管理员预设的交易对向服务器发起交易.
- /wallet exchange <交易对> info - 查看该交易对详细信息.
- /wallet trade <玩家ID> <代币名称> <金额> - 向目标玩家发送一笔物品交易. 需要打开 playerCanTradeEachOther 功能在 config.yml 文件里.
- /wallet trade list - 查看你的所有交易.
- /wallet trade edit <交易ID> - 编辑这个交易, 只允许发送方或已经被接受过的接收方, 拿走全部物品将自动删除交易或者修改等操作.
- /wallet trade accept <交易ID> - 接受这个交易, 只允许接收方, 又接收方出钱 (币)
- /wallet trade deny <交易ID> - 拒绝这个交易, 只允许接收方, 拒绝后发送方可使用 /wallet trade edit <交易ID> 取回物品
- /wallet trade info <交易ID> - 查看这个交易的详细信息.
- /wallet transfer <代币名称> <目标地址> <金额> - 向目标地址发送指定代币.
- /wallet transfer <代币名称> <目标地址> <金额> <燃油价格> - 指定燃料价格向目标地址发送代币.
- /wallet transfer <代币名称> <目标地址> <金额> <燃油价格> <燃油限制> - 指定燃料价格和燃油限制向目标地址发送代币.
- /wallet approve <代币类型> <目标地址> <数量> - 向目标地址许可使用已指定自己的ERC20代币,详情请查看Solidity-Approve方法.
- /wallet approve <代币类型> <目标地址> <数量> <燃料价格> - 指定燃料价格,向目标地址许可使用已指定自己的ERC20代币,详情请查看Solidity-Approve方法.
- /wallet approve <代币类型> <目标地址> <数量> <燃料价格> <燃料限制> - 指定燃料价格和限制,向目标地址许可使用已指定自己的ERC20代币,详情请查看Solidity-Approve方法.
- /wallet transferfrom <代币类型> <付款地址> <收款地址> <数量> Solidity-Transferfrom 方法.
- /wallet transferfrom <代币类型> <付款地址> <收款地址> <数量> <燃料价格> Solidity-Transferfrom 方法.
- /wallet transferfrom <代币类型> <付款地址> <收款地址> <数量> <燃料价格> <燃料限制> Solidity-Transferfrom 方法.

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
- CraftBukkit version git-Spigot-21fe707-741a1bd (MC: 1.8.8) (Implementing API version 1.8.8-R0.1-SNAPSHOT)

操作系统 : 

- Microsoft Windows 10 2004

## 作者想跟各位说的话

- 这是我的第一个 Java 项目，如果您喜欢我的项目
- 这是我的以太坊钱包地址 : 0x5b615F1a1989ee2636BfbFe471B1F66bCa16F926
- 我是 Minecraft_652 一名热爱学习且致力于网络去中心化的程序猿。
- 十分感谢您的支持！以下是我的联系方式：
- QQ : 919899140 , Telegram : https://t.me/SIXFIVETWO
