# mrpc

#### 介绍
m-rpc , 基于netty开发的rpc框架
使用spring、zookeeper、netty、cglib、fastjson技术
#### 软件架构
- zookeeper负责服务的注册和发现
- spring容器管理框架的bean和功能注解，以及完成框架的初始化
- netty作为通信手段，实现协议包的传输，编解码问题和拆包粘包问题
- cglib动态代理和FastAPI的反射技术，使得rpc调用对客户端透明，以及服务端更加高效的使用反射
- fastjson实现json序列化

#### 使用说明
在test文件夹下面的JunitTest中，依次启动server()和client()，即可


