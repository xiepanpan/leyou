# leyou

使用技术：
springboot springcloud  mybatis注解开发

elastic搜索 kibana视图化

阿里大于短信服务 

rabbitmq：  

1. 在后台商品修改时发送消息 创建或删除elastic search 文档  创建或删除商品详情页的静态html文件  

2. 短信验证码  发送消息

先启动register 再启动其他的

全局异常拦截处理

Hibernate validation  对实体类校验  

无状态登录：jwt+RSA非对称加密

网关拦截处理 使用公钥解析token 



商品详情页登陆状态下要把购物车信息放入Redis中 Redis的key就是用户名  因此要解析token 取得用户信息 

实用技术：拦截器 前置拦截



threadlocal  线程域 底层就是map结构 key是当前线程 value是需要存的值 不同线程之间不共享 保证线程安全 



购物车：
前台js对localStoreage 操作

elasticsearch 地址：http://192.168.200.128:9200/



kibana安装在到Windows中：http://127.0.0.1:5601

### 模块
| upload       | astDFS  图片服务                |
| ------------ | ------------------------------- |
| sms          | short message service  短信服务 |
| register     | 注册中心                        |
| gateway      | 网关                            |
| user-service | 用户服务                        |


