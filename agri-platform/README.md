# 农贸通 - 农产品交易平台

## 环境要求

| 软件 | 版本要求 | 说明 |
|------|---------|------|
| JDK | 11 或更高版本 | 后端运行环境 |
| Maven | 3.6+ | Java包管理工具 |
| Node.js | 16 或更高版本 | 前端运行环境 |
| npm | 8+ | Node包管理工具 |

## 快速启动

### Windows系统

双击运行 `start-all.bat` 即可一键启动前后端服务。

或分别启动：
- `start-backend.bat` - 启动后端服务
- `start-frontend.bat` - 启动前端服务

### 手动启动

**1. 启动后端**
```bash
cd agri-server
mvn spring-boot:run
```

**2. 启动前端**
```bash
cd agri-web
npm install    # 首次运行需要安装依赖
npm run dev
```

## 访问地址

| 服务 | 地址 |
|------|------|
| 前端页面 | http://localhost:5173 |
| 后端API | http://localhost:8888 |
| H2数据库控制台 | http://localhost:8888/h2-console |

## 测试账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | 123456 | 管理员 |
| farmer1 | 123456 | 农户 |
| buyer1 | 123456 | 采购商 |

## 项目结构

```
agri-platform/
├── agri-server/          # 后端项目 (Spring Boot)
│   ├── src/
│   │   └── main/
│   │       ├── java/     # Java源代码
│   │       └── resources/
│   │           ├── application.yml  # 配置文件
│   │           ├── schema.sql       # 数据库表结构
│   │           └── data.sql         # 初始数据
│   └── pom.xml           # Maven配置
│
├── agri-web/             # 前端项目 (Vue 3)
│   ├── src/
│   │   ├── api/          # API接口
│   │   ├── views/        # 页面组件
│   │   ├── router/       # 路由配置
│   │   └── utils/        # 工具函数
│   ├── package.json      # npm配置
│   └── vite.config.js    # Vite配置
│
├── start-all.bat         # 一键启动脚本
├── start-backend.bat     # 后端启动脚本
└── start-frontend.bat    # 前端启动脚本
```

## 常见问题

### 1. 端口被占用
- 后端默认端口: 8888
- 前端默认端口: 5173

如需修改端口：
- 后端: 修改 `agri-server/src/main/resources/application.yml` 中的 `server.port`
- 前端: 修改 `agri-web/vite.config.js` 中的 `server.port`

### 2. 依赖下载失败
- Maven: 检查网络连接，或配置国内镜像源
- npm: 可以使用 `npm config set registry https://registry.npmmirror.com` 切换国内镜像

### 3. Java版本不匹配
确保JAVA_HOME环境变量指向JDK 11或更高版本：
```bash
java -version
```

## 技术栈

**后端**
- Spring Boot 2.7.18
- MyBatis Plus 3.5.5
- H2 Database (内存数据库)
- JWT认证

**前端**
- Vue 3.4
- Element Plus 2.5
- Vue Router 4.2
- Axios
- ECharts 6.0
