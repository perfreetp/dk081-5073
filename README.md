# 平安校园一键报警联网平台

## 项目简介

平安校园一键报警联网平台是一套集校园报警接入、警情汇聚、督办流转、联动通知、考核报表于一体的智能化校园安全管理系统。通过整合校园报警设备、公安派出所、医疗机构等多方资源，实现校园安全事件的快速响应、高效处置和全程追溯。

### 核心功能

- **学校接入**: 学校信息管理、报警设备管理、学校分组管理
- **警情汇聚**: 报警接收、警情合并、警情查询、重复报警过滤
- **督办流转**: 警情派单、督办、响应、处置、催办、关闭全流程管理
- **联动通知**: 短信通知、APP推送、语音呼叫、多渠道联动
- **考核报表**: 学校风险画像、季度考核排名、数据统计分析
- **系统管理**: 用户管理、角色权限、部门管理、系统参数、值班排班

## 架构说明

### 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.2.5 | 核心框架 |
| Spring Security | 6.x | 安全认证 |
| MyBatis Plus | 3.5.5 | ORM框架 |
| MySQL | 8.x | 关系型数据库 |
| Redis | 7.x | 缓存中间件 |
| RabbitMQ | 3.x | 消息中间件 |
| Knife4j | 4.4.0 | 接口文档 |
| JWT | 0.11.5 | 令牌认证 |
| Hutool | 5.8.26 | 工具类库 |

### 系统架构

```
┌─────────────────────────────────────────────────────────────┐
│                        前端展示层                            │
│  PC管理端  │  移动APP  │  大屏展示  │  微信小程序            │
└─────────────────────────────┬───────────────────────────────┘
                              │
┌─────────────────────────────▼───────────────────────────────┐
│                        网关接入层                            │
│  Nginx  │  负载均衡  │  限流熔断  │  日志审计               │
└─────────────────────────────┬───────────────────────────────┘
                              │
┌─────────────────────────────▼───────────────────────────────┐
│                        应用服务层                            │
│  ┌──────────┬──────────┬──────────┬──────────┬──────────┐  │
│  │ 学校接入 │ 警情汇聚 │ 督办流转 │ 联动通知 │ 考核报表 │  │
│  └──────────┴──────────┴──────────┴──────────┴──────────┘  │
│  ┌──────────┐                                               │
│  │ 系统管理 │  安全认证  │  任务调度  │  消息队列           │
│  └──────────┘                                               │
└─────────────────────────────┬───────────────────────────────┘
                              │
┌─────────────────────────────▼───────────────────────────────┐
│                        数据存储层                            │
│  MySQL  │  Redis  │  RabbitMQ  │  文件存储                  │
└─────────────────────────────────────────────────────────────┘
```

### 模块划分

- `safetycampus-alarm` - 警情管理模块
- `safetycampus-school` - 学校接入模块
- `safetycampus-supervise` - 督办流转模块
- `safetycampus-notify` - 联动通知模块
- `safetycampus-report` - 考核报表模块
- `safetycampus-system` - 系统管理模块
- `safetycampus-common` - 公共模块
- `safetycampus-config` - 配置模块

### 核心业务流程

1. **报警接入流程**: 报警设备 → 消息队列 → 重复报警检测 → 警情入库 → 自动分级 → 通知发送
2. **督办流转流程**: 警情派单 → 首次响应 → 处置反馈 → 督办/催办 → 处置完成 → 警情关闭
3. **联动通知流程**: 警情产生 → 通知渠道选择 → 批量发送 → 状态跟踪 → 失败重试
4. **考核报表流程**: 数据采集 → 指标计算 → 风险评估 → 排名生成 → 报表导出

## 快速启动

### 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Redis 7.0+
- RabbitMQ 3.10+

### 数据库初始化

1. 创建数据库
```sql
CREATE DATABASE safety_campus_dev DEFAULT CHARACTER SET utf8mb4;
```

2. 执行建表脚本
```bash
mysql -uroot -p < src/main/resources/db/schema.sql
```

3. 执行初始化数据脚本
```bash
mysql -uroot -p < src/main/resources/db/data.sql
```

### 配置文件修改

修改 `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/safety_campus_dev?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
  data:
    redis:
      host: localhost
      port: 6379
      password: your_password
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
```

### 启动应用

```bash
# 编译项目
mvn clean package

# 启动应用
java -jar target/safety-alarm-platform-1.0.0.jar

# 开发环境启动
mvn spring-boot:run
```

### 启动验证

应用启动后，访问以下地址验证：

- 健康检查: http://localhost:8080/api/actuator/health
- 接口文档: http://localhost:8080/api/doc.html

## 接口地址

### 接口文档

- **Knife4j文档**: http://localhost:8080/api/doc.html
- **OpenAPI JSON**: http://localhost:8080/api/v3/api-docs

### 接口分组

| 分组 | 路径前缀 | 说明 |
|------|----------|------|
| 学校接入 | `/school/**` | 学校、设备、分组管理 |
| 警情汇聚 | `/alarm/**` | 报警接收、警情查询、合并 |
| 督办流转 | `/supervise/**` | 警情派单、督办、处置 |
| 联动通知 | `/notify/**`, `/contact/**`, `/police/**` | 通知发送、通讯录、派出所管理 |
| 考核报表 | `/report/**`, `/assess/**`, `/risk/**` | 风险画像、考核排名、统计报表 |
| 系统管理 | `/system/**`, `/auth/**`, `/user/**` | 用户、角色、权限、参数管理 |

### 核心接口示例

#### 1. 用户登录
```
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "123456"
}
```

#### 2. 报警接收
```
POST /api/alarm/receive
Content-Type: application/json
Authorization: Bearer {token}

{
  "deviceCode": "DEV001",
  "alarmType": 1,
  "alarmLevel": 1,
  "reporterName": "张三",
  "reporterPhone": "13800000001",
  "location": "校门口",
  "longitude": 116.4074,
  "latitude": 39.9042,
  "alarmContent": "校门口有人斗殴"
}
```

#### 3. 警情查询
```
GET /api/alarm/list?pageNum=1&pageSize=10&alarmLevel=1&status=1
Authorization: Bearer {token}
```

#### 4. 警情处置
```
POST /api/supervise/handle
Content-Type: application/json
Authorization: Bearer {token}

{
  "alarmId": 1,
  "flowType": 5,
  "remark": "已派民警前往处置，嫌疑人已控制",
  "attachUrl": "/files/evidence.jpg"
}
```

## 默认账号

### 系统管理员
- **用户名**: `admin`
- **密码**: `123456`
- **角色**: 系统管理员
- **权限**: 全部权限

### 教育局管理员
- **用户名**: `edu_admin`
- **密码**: `123456`
- **角色**: 教育局管理员
- **权限**: 教育局管理相关权限

### 教育局值班
- **用户名**: `edu_duty`
- **密码**: `123456`
- **角色**: 教育局值班
- **权限**: 警情查看、督办、通知

### 密码加密说明

系统使用 BCrypt 加密存储密码，默认密码 `123456` 的加密值为：
```
$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2
```

如需修改默认密码，可通过以下方式生成新的加密值：

```java
BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
String encodedPassword = encoder.encode("new_password");
```

## 部署说明

### Docker部署

```dockerfile
FROM openjdk:17-jdk-slim
VOLUME /tmp
COPY target/safety-alarm-platform-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
```

### 生产环境配置建议

1. **数据库**: 主从复制，读写分离
2. **Redis**: 哨兵模式或集群模式
3. **RabbitMQ**: 镜像队列，高可用部署
4. **应用**: 多实例部署，负载均衡
5. **日志**: ELK日志收集分析
6. **监控**: Prometheus + Grafana 监控告警

## 开发规范

### 代码规范
- 遵循阿里巴巴Java开发规范
- 使用Lombok简化代码
- 统一异常处理和返回格式
- 接口必须加上Knife4j注解

### 命名规范
- 数据库表名: `模块_表名`，如 `alarm_record`
- 实体类名: 大驼峰，如 `AlarmRecord`
- 常量类: 全大写，下划线分隔
- 方法名: 小驼峰，动词开头

### 配置规范
- 环境区分: dev/test/prod
- 敏感配置: 使用环境变量或配置中心
- 配置变更: 需要提交变更记录

## 联系方式

- 项目地址: https://github.com/safetycampus/safety-alarm-platform
- 技术支持: dev@safetycampus.com
- 问题反馈: https://github.com/safetycampus/safety-alarm-platform/issues

## License

Apache License 2.0
