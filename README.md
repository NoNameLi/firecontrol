## TMC智慧消防云平台部署说明
### 数据库环境准备
- mysql 5.7
- redis 5.0.0
- elasticsearch 6.5.4
- rabbitmq 3.6.9
使用根目录下maven 配置文件
mysql数据库资源创建好后，执行如下sql创建初始化数据库
```
CREATE DATABASE `turing-admin` CHARACTER SET 'utf8' COLLATE 'utf8_general_ci';
CREATE DATABASE `turing-auth` CHARACTER SET 'utf8' COLLATE 'utf8_general_ci';
CREATE DATABASE `turing_datahandler` CHARACTER SET 'utf8' COLLATE 'utf8_general_ci';
CREATE DATABASE `turing_device` CHARACTER SET 'utf8' COLLATE 'utf8_general_ci';
```
并导入sql目录下的各个sql文件来创建初始化表和初始化数据

### 基础配置准备
SpringCloud使用gitee来作为配置中心
[示例配置文件地址](https://gitee.com/zkturing-tmc/fire_control_config_example)

修改gitee上各个微服务配置下的数据库链接信息，并对应修改微服务代码配置yml里面的gitee配置信息

### 代码编译打包
1. 编译各个微服务代码，生成对应jar包
2. 执行各个服务文件夹下Dockerfile文件，打包成docker镜像


### 启动服务
对应修改docker-compose.yml配置文件，执行`docker compose up -d` 即可启动服务

### 前端代码链接
[https://gitee.com/zkturing-tmc/big_fire_data_vue](https://gitee.com/zkturing-tmc/big_fire_data_vue)

### Nginx配置示例
```
user www-data;
worker_processes auto;

worker_rlimit_nofile 51200;

events {

  use epoll;
  worker_connections 51200;
  multi_accept on;
}
http {
    include       mime.types;
    default_type  application/octet-stream;

    server_names_hash_bucket_size   128;

    client_header_buffer_size   32k;

    large_client_header_buffers 4 32k;
    log_format access '$remote_addr - $remote_user [$time_local] "$request" '
            '$status $body_bytes_sent "$http_referer" '
            '"$http_user_agent" "$http_x_forwarded_for"';
    access_log  /var/log/nginx/access.log   access;
    sendfile    on;
    tcp_nopush  on;
    tcp_nodelay on;
    keepalive_timeout   65;
    server_tokens   off;
    client_body_buffer_size 1024k;
   
    proxy_connect_timeout   150s;
    proxy_send_timeout      150s;
    proxy_read_timeout      150s;

    proxy_buffer_size       512k;
    proxy_buffers           64 64k;
    proxy_busy_buffers_size 2048k;
    proxy_temp_file_write_size 2048k;


    gzip    on;
    gzip_min_length 1k;
    gzip_buffers 4 16k;
    gzip_http_version 1.1;
    gzip_comp_level 2;
    gzip_types text/plain application/x-javascript text/css application/xml;
    gzip_vary   on;    

    client_header_timeout 120s;     
    client_body_timeout 120s;  

    client_max_body_size 300M; 

    upstream mycluster{
        # 对应后端服务地址
        server turing-gate.tmc-v1:8765 weight=1;
    }

    upstream websocket{
        # 对应后端服务地址
        server turing-datahandler.tmc-v1:2345  weight=1;
    }

    server {
        listen 80;
        server_name _;
        #charset koi8-r;
        #access_log  /var/log/nginx/host.access.log  main;

        location / {
            root   /usr/share/nginx/html;
            index  index.html index.htm;
        }

        location /threed/ {
            root  /usr/share/nginx/threed;
        }

        location /api/ {
            #proxy_next_upstream http_502 http_504 error timeout invalid_header;
            proxy_set_header Connection "Keep-Alive";
            proxy_pass http://mycluster;
            proxy_set_header   X-Real-IP        $remote_addr:8765;
            proxy_set_header   Host             $host;
            proxy_set_header   X-Forwarded-For  $proxy_add_x_forwarded_for;
            proxy_set_header   X-Forwarded-Proto $scheme;
        }

        location /alarm/websocket {
            proxy_pass http://websocket;
            proxy_redirect off;
            proxy_http_version 1.1;
            proxy_set_header Upgrade $http_upgrade;
            proxy_set_header Connection "upgrade";
            proxy_set_header Host $host:$server_port;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        }

    }

}
```