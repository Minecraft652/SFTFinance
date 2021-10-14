# SFTFinance - 让你在 Minecraft 里体验区块链上交易。

[View English instructions](/README.md)

## 什么是 SFTFinance

SFTFinance 是一个基于Bukkit/Spigot开发的我的世界服务端插件

其功能提供了让玩家及管理员在以太坊区块链及智能合约的操作

并允许交互原链币及ERC20标准代币智能合约的代币。像 Ether , USDT

## 功能

- 让你的服务器连接至 EVM 标准区块链（Ethereum Mainnet, Ropsten, Binance Smart Chain等）
- 让玩家拥有自己的数字钱包，并且可以相互转账。
- 提供基本交互 ERC20 代币智能合约，如（USDT, DAI及任何基于 ERC20 代币标准合约）的方法。
- 管理员可以自定义玩家与服务器之间的交易，并且在区块链上执行。

## 支持

- 支持自定义 ERC20 代币合约
- 支持EVM 区块链及自定义区块链 HTTP 地址
- 支持自定义管理员自定义交易对
- 支持生成数字钱包导出到其他钱包

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

[查看默认配置文件及配置帮助](/src/main/resources/config.yml)
