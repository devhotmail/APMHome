# dew
the GIT mirror of Dew project SVN

### 准备工作：

  - IDE(推荐NetBeans)
  - maven(NetBeans的安装中自带 maven)
  - JDK 1.8
  - git

### 安装步骤

1. 本地安装 PostgreSQL:

    连接帐号设置为 postgres/root, 
    如果口令不是 root, 那么要在下面第5步中，修改程序的配置文件 `database.properties`

1. 在PostgreSQL中创建数据库 `ge_apm`

1. 创建数据库表结构

    在geapm中执行脚本 `create_tables.sql`

1. 导入初始数据

    在geapm中执行脚本 `init_data.sql`

1. 设置 maven jetty 

    修改根目录的 jetty_run.bat, 按照你本机的情况设置 JDK 和 mvn的路径
    如果要修改数据库的连接信息，请去修改 `src/main/resources/database.properties`

1. 运行

    直接运行根目录的 jetty_run.bat 文件即可。
    或者执行 mvn jetty:run 亦可。
    初次运行，视网速，会需要较长的时间下载依赖的 jar 包

1. 登录到系统首页

  - http://localhost:8080/geapm
  - 登录口令 `admin/111`

1. IDE中建立项目

  - 若使用 Netbeans 开发，则可: File->Open Project, 直接打开本项目
  - 若使用 Eclipse 开发，则可: File->Import->Import Existing Maven Projects 来打开本项目

1. 生成部署war包

    运行 mvn package 即可，在 target 目录会生成可部署的 war 文件

1. 其他  mvn goals

    ```
    mvn clean
    mvn compile
    mvn test
    mvn package
    mvn jetty:run
    ```
