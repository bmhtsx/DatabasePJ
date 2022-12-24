# Database Project(H)

This is the project of 2022 Autumn Database Design.

### 文件结构

DatabasePJ
│── doc：项目相关文档
│── lib：手动引入的依赖
│── sql：MySQL相关文件目录：
│　　├── init.sql：创建数据库表结构脚本
│　　└── *.csv：测试数据
│── src：源代码
│　　├── main.java.cn.edu.fudan：业务逻辑代码
│　　　　　├── dao：数据持久层，进行数据库查询
│　　　　　├── data：获取原始数据，扫描入库相关
│　　　　　　　　├── 
│　　　　　├── entity：实体层，数据表对应的实体类
│　　　　　├── BugTrack.java：主函数
│　　　　　├── CmdExecute.java：执行命令行函数
│　　　　　├── DBConnection.java：连接数据库函数
│　　　　　├── Read.java：命令行读入交互函数
│　　　　　└── Scan.java：扫描入口函数
│　　├── jdbc.properties：数据库配置文件
│　　└── pjInfo.properties：sonar及待扫描项目配置文件
