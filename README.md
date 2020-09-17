## TMC智慧消防云平台部署说明
### 数据库环境准备
- mysql 5.7
- redis 5.0.0
- elasticsearch 6.5.4
- rabbitmq 3.6.9

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
[示例配置文件地址](https://gitee.com/8090diy/fire_control_config_example)

修改gitee上各个微服务配置下的数据库链接信息，并对应修改微服务代码配置yml里面的gitee配置信息

### 代码编译打包
1. 编译各个微服务代码，生成对应jar包
2. 执行各个服务文件夹下Dockerfile文件，打包成docker镜像


### 启动服务
对应修改docker-compose.yml配置文件，执行`docker compose up -d` 即可启动服务