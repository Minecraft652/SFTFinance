# SFTFinance - 让你在 Minecraft 里体验区块链上交易。

[View English instructions](/README.md)

## Introducing SFTFinance

SFTFinance 是一个基于Bukkit/Spigot开发的我的世界服务端插件

其功能提供了让玩家及管理员在以太坊区块链及智能合约的操作

并允许交互原链币及ERC20标准代币智能合约的代币。像 Ether , USDT

## Features

- 让你的服务器连接至 EVM 标准区块链（Ethereum Mainnet, Ropsten, Binance Smart Chain等）
- 让玩家拥有自己的数字钱包，并且可以相互转账。
- 提供基本交互 ERC20 代币智能合约，如（USDT, DAI及任何基于 ERC20 代币标准合约）的方法。
- 管理员可以自定义玩家与服务器之间的交易，并且在区块链上执行。

## Support

- 支持自定义 ERC20 代币合约
- 支持EVM 区块链及自定义区块链 HTTP 地址
- 支持自定义管理员自定义交易对
- 支持生成数字钱包导出到其他钱包

## Installation

- 把插件放进服务端的 plugins 文件夹
- 启动服务端，将会自动生成 config.yml 等文件
- 插件目前只有在服务端关闭状态下才能进行配置
- 配置完毕后，就可以正常使用。

## Commands

/wallet - 查看原链代币余额

## Usage SFTFinance ?

举个例子：A 拥有 1 SFT (ERC20 Token) 要跟 B 玩家进行转账 (Transfer method)

B 玩家未知钱包地址 , 首先 A 需要查询 B 玩家的钱包地址即使用该命令: /wallet player B

查询到的 B 玩家钱包地址为 : 0x77751B52F993fD30042999F64Ed0C41A4eFa5Be8

然后 A 输入该命令完成操作: /wallet transfer SFT 0x77751B52F993fD30042999F64Ed0C41A4eFa5Be8 1
