### Easy-Java-Raft
基于Java实现的Raft，数据库存储

- [x] 基本的数据格式（各种通信的定义）
- [x] mysql数据库的基本操作
- [x] logEntity和KVEntity操作的基本实现 
- [x] rpc通信的基本实现
- [x] leader发送消息以及发送心跳的基本实现
- [x] follower接收消息和心跳的动作实现
- [x] 选举的基本实现
- [x] 选举过程的测试文件
- [x] 客户端发送请求的基本实现
- [x] 节点接收客户端请求进行回应实现 
- [x] 日志复制的实现 
- [x] 安全性的实现
- [x] 集群成员变更的实现 

### 例程
- 领导人选举测试文件: `server/MultiVoteTestServer.java`
- 追加日志测试文件：`server/EntryTest.java`
- 集群节点更改测试文件：`server/cOldNewTest.java`

### 文件说明  
主要实现raft逻辑的部分为

> -server
> &ensp;&ensp; -msg&ensp;&ensp;&ensp;&ensp;(根据接收到的不同的消息，做出动作以及回应)
> &ensp;&ensp; -model&ensp;&ensp;&ensp;&ensp;(一些改变服务器状态时做出的动作，以及一些常用的函数)
> &ensp;&ensp; -action&ensp;&ensp;&ensp;&ensp;(服务器后台线程，比如心跳和超时机制)

