# SFTFinance - A Ethereum linker of minecraft server plugin

## Introducing SFTFinance

SFTFinance 是一个基于Bukkit/Spigot开发的我的世界服务端插件

其功能提供了让玩家及管理员在以太坊区块链及智能合约的操作

并允许交互原链币及ERC20标准代币智能合约的代币。像 Ether , USDT

## Usage SFTFinance ?

举个例子：A 拥有 1 SFT (ERC20 Token) 要跟 B 玩家进行转账 (Transfer method)

B 玩家未知钱包地址 , 首先 A 需要查询 B 玩家的钱包地址即使用该命令: /wallet player B

查询到的 B 玩家钱包地址为 : 0x77751B52F993fD30042999F64Ed0C41A4eFa5Be8

然后 A 输入该命令完成操作: /wallet transfer SFT 0x77751B52F993fD30042999F64Ed0C41A4eFa5Be8 1
