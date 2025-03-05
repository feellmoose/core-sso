# Pomelo SSO [![MIT License](https://img.shields.io/badge/License-MIT-green.svg)](https://choosealicense.com/licenses/mit/)

**Pomelo SSO** is a lightweight single sign-on (SSO) system built on the OAuth 2.0 and OpenID Connect (OIDC) standards. It utilizes HTTP Cookie-based SSO session management and is designed for high-concurrency scenarios based on the Vert.x framework.

English/[中文](https://github.com/feellmoose/core-sso/blob/main/README_CN.md)

## ✨ Core Features

- **Standards Compliance**: Full implementation of OAuth 2.0 and OpenID Connect (OIDC) protocols
- **Session Management**: Stateful cookie-based session mechanism
- **High-Performance Architecture**: Asynchronous non-blocking I/O model powered by Vert.x
- **Database Support**: PostgreSQL integration with Hibernate ORM
- **Extensible Design**: Modular architecture supporting custom authentication flows

## 🚀 Quick Start

### Prerequisites

- Java 17+
- PostgreSQL 13+

### Configuration

Create `config.json` configuration file:

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

### Standalone Deployment

```bash
java -jar target/pomelo-sso-1.0.0.jar -conf config.json
```

**Security Note**: Use strong secrets in production and disable debug configurations.

### 🌀 Cluster Deployment (High Availability)

> Please refer to the official Vert.x documentation

**Cluster configuration file** `cluster.xml`:

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

**Startup command**:

```bash
java -jar target/pomelo-sso-1.0.0.jar \
-conf config.json \
-cluster -cluster-host 192.168.1.100 \
-cluster-port 15701 \
-cluster-config cluster.xml
```

## 📌 Roadmap

### Implemented Features

- Core OAuth 2.0/OIDC protocol implementation
- JWT & Cookie session management
- PostgreSQL integration
- Email OTP authentication flow

### Planned Features

- Docker & Docker Compose support
- Vert.x cluster configuration
- Admin console
- Multi-database support (MySQL/MongoDB)
- Performance benchmarking
- Social login extensions
- Configuration wizard UI

## 🤝 Contributing

We welcome contributions through the following process:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/new-feature`)
3. Commit standardized code changes
4. Push to the branch (`git push origin feature/new-feature`)
5. Open a Pull Request

**Contribution Guidelines:**

- Follow existing code style
- Include unit tests for new features
- Update relevant documentation
- Use conventional commit messages

Found a vulnerability? [Open an issue](https://github.com/feellmoose/core-sso/issues)

## ⚖️ License

Distributed under the MIT License. See `LICENSE` for details.

------

**Like what you see?** ⭐ Star the repository to support development!

