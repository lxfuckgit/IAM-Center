# IAM-Center

Identity and Access Management Center - 身份认证与权限管理中心

## 技术栈

- **框架**: Spring Boot 3.2.5
- **语言**: Java 17
- **数据库**: MySQL 8.0+
- **缓存**: Redis
- **ORM**: Spring Data JPA
- **安全**: Spring Security + JWT

## 项目结构

```
IAM-Center/
├── pom.xml                           # Maven配置
├── src/
│   └── main/
│       ├── java/
│       │   └── com/iamcenter/
│       │       └── IamCenterApplication.java    # 启动类
│       └── resources/
│           ├── application.yml                   # 公共配置
│           ├── application-test.yml              # 测试环境配置
│           └── application-prod.yml              # 生产环境配置
```

## 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+

## 快速开始

### 1. 创建数据库

```sql
-- 测试环境
CREATE DATABASE iam_test CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 生产环境
CREATE DATABASE iam_prod CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. 修改配置

根据实际环境修改 `application-test.yml` 或 `application-prod.yml` 中的数据库和Redis配置。

### 3. 启动项目

```bash
# 编译
mvn clean compile

# 运行（默认使用test环境）
mvn spring-boot:run

# 指定环境运行
mvn spring-boot:run -Dspring.profiles.active=prod

# 打包
mvn clean package

# 运行打包后的jar（指定环境）
java -jar target/IAM-Center-v20260701.jar --spring.profiles.active=prod
```

## 配置说明

### 多环境配置

| 环境 | 配置文件 | 数据库 | show-sql |
|------|---------|--------|----------|
| test | application-test.yml | iam_test | true |
| prod | application-prod.yml | iam_prod | false |

### 默认端口

- 服务端口: 8080
- MySQL端口: 3306
- Redis端口: 6379

## Maven依赖

| 依赖 | 说明 |
|------|------|
| spring-boot-starter-web | Web开发 |
| spring-boot-starter-data-jpa | JPA数据访问 |
| spring-boot-starter-security | Spring Security安全框架 |
| spring-boot-starter-validation | 参数校验 |
| spring-boot-starter-data-redis | Redis缓存 |
| mysql-connector-j | MySQL驱动 |
| jjwt-api/impl/jackson | JWT令牌 |
| spring-boot-starter-test | 测试支持 |

## 许可证

MIT License
