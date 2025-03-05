



# Pomelo SSO [![MIT License](https://img.shields.io/badge/License-MIT-green.svg)](https://choosealicense.com/licenses/mit/)

**Pomelo SSO** 是一款基于 OAuth 2.0 和 OpenID Connect (OIDC) 标准实现的轻量级单点登录系统，采用基于 HTTP Cookie 的 SSO 网页会话管理，并基于 Vert.x 框架专为高并发场景设计。

[English](https://github.com/feellmoose/core-sso/blob/main/README.md)/中文

## ✨ 核心特性

- **标准协议支持**：完整实现 OAuth 2.0 和 OpenID Connect (OIDC) 协议
- **会话管理**：基于 Cookie 的有状态会话机制
- **高性能架构**：基于 Vert.x 的异步非阻塞 I/O 模型
- **数据存储支持**：集成 PostgreSQL 与 Hibernate ORM
- **可扩展设计**：模块化架构支持自定义认证流程

## 🚀 快速入门

### 环境要求

- Java 17+
- PostgreSQL 13+

### 配置说明

创建 `config.json` 配置文件模板：

```json
{
  "server" : {
    "host" : "0.0.0.0",
    "port" : 8080
  },
  "database": {
    "url" : "jdbc:postgresql://localhost:5432/sso",
    "user" : "postgres",
    "password" : "passw0rd",
    "connection" : {
      "poolSize" : 10
    },
    "hibernate": {
      "showSql" : true,
      "action": "update"
    }
  },
  "security": {
      "jwt": {
        "secret" : "jwt_secret"
      },
      "cookie": {
        "name" : "session_id",
        "maxAge" : 60000000,
        "timeout" : 60000000,
        "path" : "/",
        "domain" : "localhost",
        "secure" : false,
        "httpOnly" : false
      }
  },
  "mail": {
    "expire": 60000,
    "username": "user@example.com",
    "password": "",
    "host" : "",
    "port" : "",
    "from" : "user@example.com",
    "subject" : "[Pomelo SSO] One-Time Passcode (OTP)",
    "pattern" : "Verification Code: %s"
  }
}
```

### 单机启动服务

```bash
java -jar target/pomelo-sso-1.0.0.jar -conf config.json
```

**安全提示**：生产环境请使用高强度密钥并关闭调试配置。

### 🌀 集群部署 (高可用模式)

> 请参阅 vertx 官方文档

**集群配置文件** `cluster.xml`：

```xml
<vertx-cluster>
  <hazelcast>
    <network>
      <port auto-increment="true">5701</port>
      <join>
        <multicast enabled="false"/>
        <tcp-ip enabled="true">
          <member>node1.example.com</member>
          <member>node2.example.com</member>
        </tcp-ip>
      </join>
    </network>
  </hazelcast>
</vertx-cluster>
```

**启动参数**：

```bash
java -jar target/pomelo-sso-1.0.0.jar \
-conf config.json \
-cluster -cluster-host 192.168.1.100 \
-cluster-port 15701 \
-cluster-config cluster.xml
```

## 📌 Roadmap

### 已实现功能

- 核心 OAuth 2.0/OIDC 协议实现
- JWT & Cookie 会话管理
- PostgreSQL 集成支持
- 邮件 OTP 认证流程

### 规划功能

- Docker & Docker compose 支持
- Vert.x 集群配置
- 管理控制台
- 多数据库支持（MySQL/MongoDB）
- 性能基准测试
- 社交登录扩展
- 配置向导界面

## 🤝 参与贡献

欢迎通过以下流程参与贡献：

1. Fork 项目仓库
2. 创建特性分支 (`git checkout -b feature/新功能`)
3. 提交规范化的代码变更
4. 推送分支到远程仓库 (`git push origin feature/新功能`)
5. 发起 Pull Request

**贡献规范：**

- 遵循现有代码风格
- 新增功能需包含单元测试
- 及时更新相关文档
- 使用约定式提交规范

发现漏洞？[提交 issue](https://github.com/feellmoose/core-sso/issues)

## ⚖️ 开源协议

本项目采用 MIT 开源协议，详见 `LICENSE` 文件。

------

**觉得不错？** ⭐ 给项目加星支持开发！