# leyou

## 使用技术：

前端技术：

- 基础的HTML、CSS、JavaScript（基于ES6标准）
- JQuery
- Vue.js 2.0以及基于Vue的框架：Vuetify
- 前端构建工具：WebPack
- 前端安装包工具：NPM
- Vue脚手架：Vue-cli
- Vue路由：vue-router
- ajax框架：axios
- 基于Vue的富文本框架：quill-editor

后端技术：

- 基础的SpringMVC、Spring 5.0和MyBatis3
- Spring Boot 2.0.1版本
- Spring Cloud 最新版 Finchley.RC1
- Redis-4.0
- RabbitMQ-3.4
- Elasticsearch-5.6.8
- nginx-1.10.2：
- FastDFS - 5.0.8
- Thymeleaf

springboot springcloud  mybatis注解开发

ES6 node.js npm

## 功能

### 后台管理系统

1. cors解决跨域问题

2. 使用axios 一款ajax请求框架

#### 一 商品分类管理

vue

#### 二 商品品牌管理

Nginx提供了rewrite指令，用于对地址进行重写，语法规则：

```
rewrite "用来匹配路径的正则" 重写后的路径 [指令];
```

```
 # 上传路径的映射
 location /api/upload {	
			
	rewrite "^/(.*)$" /zuul/$1 ; 
 }

```

rewrite "^/(.*)$" /zuul/$1 ;   就是把前面的任意值作为变量作为$1的值 也就是/api/upload 变成了/zuul/api/upload

商品品牌图片 搭建上传文件服务 **搭建FastDfs**

#### 三 商品列表 

lamada表达式

1. 商品添加

- Vue-Quill-Editor富文本编辑器
- 笛卡尔积  sku
- **使用rabbitMq发送消息  更新索引库和详情页静态页面**

2. 商品修改

   **使用rabbitMq发送消息  更新索引库和详情页静态页面**
   
   

### 前台portal

##### live-server 热部署前台项目

### 后台服务

#### 一 搜索服务：

elastic search 构建索引库 搜索服务

监听器监听消息  更新索引库

#### 二 商品详情页：

填充数据 使用thymeleaf  生成html静态页面  Nginx 先在本地查找 没有再请求详情页服务

```nginx
location /item {
	   # 先找本地
		root html;
	   if (!-f $request_filename) { #请求的文件不存在，就反向代理
	       proxy_pass http://192.168.200.1:8084;
	       break;
	   }
 }
```

请求到Nginx静态html文件 返回304状态码 表示服务端已经执行了GET，但文件未变化

**过滤功能：**

**过滤条件展示**：elastic search 聚合搜索数据

**过滤搜索功能实现**

监听器监听消息  创建或删除静态文件

#### 三 用户服务

**用户注册**  

1. 校验用户名和手机号是否已存在

2. 点击发送验证码  生成6位随机数 然后放入Redis中 然后通过MQ通知短信微服务。

3. 点击注册 把用户输入的验证码和Redis中作对比 相同表示注册成功 删除Redis中的验证码数据

4. 使用HIbernateValidator数据校验  在Bean上加注解


**用户服务**



#### 四 短信服务

监听短信  使用阿里大于短信服务发送短信 sms

#### 五 登录服务

无状态登录  

**私钥签名：**

![å¨è¿éæå¥å¾çæè¿°](https://img-blog.csdnimg.cn/20190824142856509.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3NkYXVqc2ox,size_16,color_FFFFFF,t_70)

- 我们首先利用RSA生成公钥和私钥。私钥保存在授权中心，公钥保存在Zuul和各个微服务
- 用户请求登录
- 授权中心校验，通过后用私钥对JWT进行签名加密 
- 返回jwt给用户 写入cookie中
- 用户携带JWT访问
- Zuul直接通过公钥解密JWT，进行验证，验证通过则放行
- 请求到达微服务，微服务直接用公钥解析JWT，获取用户信息，无需访问授权中心

#### 六 购物车服务

未登录使用localstorage存储



web本地存储主要有两种方式：

- LocalStorage：localStorage 方法存储的数据没有时间限制。第二天、第二周或下一年之后，数据依然可用。 
- SessionStorage：sessionStorage 方法针对一个 session 进行数据存储。当用户关闭浏览器窗口后，数据会被删除。 

新增商品：

- 判断是否登录
  - 是：则添加商品到后台Redis中
  - 否：则添加商品到本地的Localstorage

因为很多接口都需要进行登录，我们直接编写SpringMVC拦截器，进行统一登录校验。同时，我们还要把解析得到的用户信息保存起来，以便后续的接口可以使用。



## 相关配置

本机ip： 192.128.200.1

虚拟机ip：192.168.200.128


fdfs中配置文件 /ect/fdfs

Nginx   /opt/nginx/conf

Nginx switchhost该域名 然后本地解析出虚拟机地址

虚拟机Nginx拦截再转发到本地。。



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

### 订单模块

创建订单：

雪花算法实现订单号的唯一

使用乐观锁减库存 update操作



先创建订单 完成后再库存 巧妙解决了分布式事务。。。

如果先减库存 减库存成功了 但是创建订单失败 这时候减库存无法回滚。。





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



### 环境：

使用switchhost 配置hosts文件

```
# leyou
192.168.200.128 manage.leyou.com

192.168.200.128 www.leyou.com

192.168.200.128 api.leyou.com

192.168.200.128 image.leyou.com

```

nginx配置文件：

```


#user  nobody;
worker_processes  1;

#error_log  logs/error.log;
#error_log  logs/error.log  notice;
#error_log  logs/error.log  info;

#pid        logs/nginx.pid;


events {
    worker_connections  1024;
}


http {
    include       mime.types;
    default_type  application/octet-stream;
    client_max_body_size 10m; 

    #log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
    #                  '$status $body_bytes_sent "$http_referer" '
    #                  '"$http_user_agent" "$http_x_forwarded_for"';

    #access_log  logs/access.log  main;

    sendfile        on;
    #tcp_nopush     on;

    #keepalive_timeout  0;
    keepalive_timeout  65;

    #gzip  on;

    server {
        listen       80;
        server_name  www.leyou.com;

        proxy_set_header X-Forwarded-Host $host;
        proxy_set_header X-Forwarded-Server $host;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;

        location /item {
            proxy_pass http://192.168.200.1:8084;
            proxy_connect_timeout 600;
            proxy_read_timeout 600;
        }

        location / {
            proxy_pass http://192.168.200.1:9002;
            proxy_connect_timeout 600;
            proxy_read_timeout 600;
        }
    }

    server {
        listen       80;
        server_name  manage.leyou.com;

        proxy_set_header X-Forwarded-Host $host;
        proxy_set_header X-Forwarded-Server $host;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;

        location / {
			proxy_pass http://192.168.200.1:9001;
			proxy_connect_timeout 600;
			proxy_read_timeout 600;
        }
    }

	server {
        listen       80;
        server_name  api.leyou.com;

        proxy_set_header X-Forwarded-Host $host;
        proxy_set_header X-Forwarded-Server $host;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header Host $host;
       
       # 上传路径的映射
        location /api/upload {	
			
		rewrite "^/(.*)$" /zuul/$1 ; 
        }

        location / {
			proxy_pass http://192.168.200.1:10010;
			proxy_connect_timeout 600;
			proxy_read_timeout 600;
        }
    }

    server {
        listen       80;
        server_name  image.leyou.com;

    	# 监听域名中带有group的，交给FastDFS模块处理
        location ~/group([0-9])/ {
            ngx_fastdfs_module;
        }

        location / {
            root   /leyou/static/;
            index  index.html index.htm;
        }

        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   html;
        }
        
    }

    server {
        listen       80;
        server_name  localhost;

        #charset koi8-r;

        #access_log  logs/host.access.log  main;

        location / {
            root   html;
            index  index.html index.htm;
        }

        #error_page  404              /404.html;

        # redirect server error pages to the static page /50x.html
        #
        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   html;
        }

        # proxy the PHP scripts to Apache listening on 127.0.0.1:80
        #
        #location ~ \.php$ {
        #    proxy_pass   http://127.0.0.1;
        #}

        # pass the PHP scripts to FastCGI server listening on 127.0.0.1:9000
        #
        #location ~ \.php$ {
        #    root           html;
        #    fastcgi_pass   127.0.0.1:9000;
        #    fastcgi_index  index.php;
        #    fastcgi_param  SCRIPT_FILENAME  /scripts$fastcgi_script_name;
        #    include        fastcgi_params;
        #}

        # deny access to .htaccess files, if Apache's document root
        # concurs with nginx's one
        #
        #location ~ /\.ht {
        #    deny  all;
        #}
    }


    # another virtual host using mix of IP-, name-, and port-based configuration
    #
    #server {
    #    listen       8000;
    #    listen       somename:8080;
    #    server_name  somename  alias  another.alias;

    #    location / {
    #        root   html;
    #        index  index.html index.htm;
    #    }
    #}


    # HTTPS server
    #
    #server {
    #    listen       443 ssl;
    #    server_name  localhost;

    #    ssl_certificate      cert.pem;
    #    ssl_certificate_key  cert.key;

    #    ssl_session_cache    shared:SSL:1m;
    #    ssl_session_timeout  5m;

    #    ssl_ciphers  HIGH:!aNULL:!MD5;
    #    ssl_prefer_server_ciphers  on;

    #    location / {
    #        root   html;
    #        index  index.html index.htm;
    #    }
    #}

}


```

