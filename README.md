# Task Schedule 任务调度系统

基于 Spring Boot 和 Quartz 的任务调度系统，支持动态创建、更新、删除定时任务，并提供持久化存储。

## 项目介绍

这是一个基于 Spring Boot 和 Quartz 的任务调度系统，支持动态任务管理、持久化存储和日志配置。

### 功能特性

- 动态任务管理：支持通过接口动态创建、更新、删除定时任务
- 持久化支持：任务信息存储在 MySQL 数据库中
- 日志配置：使用 logback 进行日志管理，支持任务独立日志文件
- Quartz 集成：基于 Quartz 实现任务调度
- 外部任务加载：支持在运行时动态加载外部jar包中的任务

## 系统要求

- JDK 21
- Maven 3.x
- MySQL 8.0+

## 快速开始

### 1. 构建项目

```bash
mvn clean package
```

### 2. 数据库配置

确保 MySQL 数据库可用，并在 `application.yml` 中配置正确的数据库连接信息。

### 3. 运行应用

```bash
mvn spring-boot:run
```

或者运行打包后的jar文件：

```bash
java -jar target/task_schedule-1.0-SNAPSHOT.jar
```

## 原生方式运行并加载外部任务

除了 Docker 方式，你也可以使用原生方式运行应用并加载外部任务jar包：

### 通过环境变量指定

```bash
export EXTERNAL_JOBS_JAR_PATH=/path/to/your/tasks.jar
java -jar target/task_schedule-1.0-SNAPSHOT.jar
```

### 通过配置文件指定

在 `application.yml` 中配置：

```yaml
external:
  jobs:
    jar:
      path: /path/to/your/tasks.jar
```

然后运行：

```bash
java -jar target/task_schedule-1.0-SNAPSHOT.jar
```

### 通过命令行参数指定

```bash
java -jar target/task_schedule-1.0-SNAPSHOT.jar --external-jobs-jar-path=/path/to/your/tasks.jar
```

或者：

```bash
java -jar target/task_schedule-1.0-SNAPSHOT.jar --external-jobs-jar-path /path/to/your/tasks.jar
```

## Docker 部署

### 1. 构建 Docker 镜像

```bash
docker build -t task-schedule:latest .
```

或者使用 Maven 插件构建：

```bash
mvn clean package dockerfile:build
```

### 2. 运行 Docker 容器

#### 基本运行

```bash
docker run -p 8080:8080 task-schedule:latest
```

#### 挂载外部任务jar包

```bash
docker run -p 8080:8080 \
  -v /path/to/your/tasks.jar:/app/external-jobs/tasks.jar \
  -e EXTERNAL_JOBS_JAR_PATH=/app/external-jobs/tasks.jar \
  task-schedule:latest
```

### 3. 使用 docker-compose

项目提供了 docker-compose.yml 文件，可以一键启动应用和数据库：

```bash
docker-compose up -d
```

## 外部任务加载机制

本系统支持在运行时动态加载外部jar包中的任务类。外部jar包中的任务类需要满足以下条件：

1. 实现 `org.quartz.Job` 接口
2. 有公共无参构造函数
3. 位于jar包的根目录或任意包结构下

### 配置外部任务jar包路径

可以通过以下方式指定外部任务jar包路径（优先级从高到低）：

1. 命令行参数: `--external-jobs-jar-path=/path/to/tasks.jar`
2. 系统属性: `-Dexternal.jobs.jar.path=/path/to/tasks.jar`
3. 环境变量: `EXTERNAL_JOBS_JAR_PATH=/path/to/tasks.jar`
4. 配置文件: `external.jobs.jar.path` in `application.yml`

### 示例外部任务类

```java
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class SampleExternalJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("执行外部任务: " + this.getClass().getSimpleName());
    }
}
```

## API 接口

- `POST /api/jobs/sample` - 创建示例任务
- `POST /api/jobs/five-minute` - 创建五分钟任务
- `DELETE /api/jobs/sample/{jobName}` - 删除示例任务

## 日志配置

系统支持为每个任务生成独立的日志文件，文件名为 `task_schedule_任务名.log`，并按天进行归档。

## 配置说明

所有 Quartz 相关配置位于 `task_schedule.yml` 文件中，支持以下配置项：

- 调度器配置
- 线程池配置
- 作业存储配置
- 数据源配置

## 开发指南

### 添加新的定时任务

1. 创建新的任务类，继承 `BaseJob` 类
2. 实现 `executeJob` 方法
3. 任务会自动获得独立的日志文件支持

### 修改 Quartz 配置

直接编辑 `src/main/resources/task_schedule.yml` 文件即可修改 Quartz 配置。

## 许可证

MIT

## Quartz 数据库表结构说明

Quartz 调度框架使用以下数据库表来存储任务、触发器及相关信息：

### 主要表结构

| 表名 | 说明 |
|------|------|
| [QRTZ_JOB_DETAILS](file:///D:/develop_pro/coffee/task_schedule/src/main/resources/quartz_tables_mysql.sql#L15-L27) | 存储作业的详细信息，包括作业名称、组名、实现类、是否持久化等 |
| [QRTZ_TRIGGERS](file:///D:/develop_pro/coffee/task_schedule/src/main/resources/quartz_tables_mysql.sql#L29-L51) | 存储触发器的基本信息，包括触发时间、状态、关联的作业等 |
| [QRTZ_SIMPLE_TRIGGERS](file:///D:/develop_pro/coffee/task_schedule/src/main/resources/quartz_tables_mysql.sql#L53-L64) | 存储简单类型的触发器信息，如重复次数和间隔 |
| [QRTZ_CRON_TRIGGERS](file:///D:/develop_pro/coffee/task_schedule/src/main/resources/quartz_tables_mysql.sql#L66-L76) | 存储 Cron 表达式类型的触发器信息 |
| [QRTZ_BLOB_TRIGGERS](file:///D:/develop_pro/coffee/task_schedule/src/main/resources/quartz_tables_mysql.sql#L78-L88) | 存储 Blob 类型的触发器数据 |
| [QRTZ_CALENDARS](file:///D:/develop_pro/coffee/task_schedule/src/main/resources/quartz_tables_mysql.sql#L90-L97) | 存储日历信息，用于排除某些时间点 |

### 状态和历史表

| 表名 | 说明 |
|------|------|
| [QRTZ_PAUSED_TRIGGER_GRPS](file:///D:/develop_pro/coffee/task_schedule/src/main/resources/quartz_tables_mysql.sql#L99-L104) | 存储暂停的触发器组信息 |
| [QRTZ_FIRED_TRIGGERS](file:///D:/develop_pro/coffee/task_schedule/src/main/resources/quartz_tables_mysql.sql#L106-L120) | 存储已触发的触发器信息（正在执行的触发器） |
| [QRTZ_SCHEDULER_STATE](file:///D:/develop_pro/coffee/task_schedule/src/main/resources/quartz_tables_mysql.sql#L122-L129) | 存储调度器实例的状态信息 |
| [QRTZ_LOCKS](file:///D:/develop_pro/coffee/task_schedule/src/main/resources/quartz_tables_mysql.sql#L131-L134) | 存储锁信息，用于集群环境同步 |

### 触发器状态说明

| 状态 | 说明 |
|------|------|
| WAITING | 等待触发 |
| PAUSED | 暂停 |
| ACQUIRED | 已获得（正在执行） |
| BLOCKED | 阻塞 |
| ERROR | 错误 |

### 锁类型说明

| 锁类型 | 说明 |
|--------|------|
| TRIGGER_ACCESS | 触发器访问锁 |
| JOB_ACCESS | 作业访问锁 |
| CALENDAR_ACCESS | 日历访问锁 |
| STATE_ACCESS | 状态访问锁 |
| MISFIRE_ACCESS | 错过触发访问锁 |

## 项目结构

```
src/
├── main/
│   ├── java/
│   │   └── org/poying/
│   │       ├── config/          # 配置类
│   │       ├── controller/      # 控制器
│   │       ├── entity/          # 实体类
│   │       ├── jobs/            # 任务实现类
│   │       ├── mapper/          # MyBatis Mapper
│   │       └── service/         # 服务层
│   └── resources/
│       ├── application.yml      # 配置文件
│       ├── logback-spring.xml   # 日志配置
│       └── quartz_tables_mysql.sql  # Quartz表结构
```

## 扩展功能

你可以通过以下方式扩展项目功能：

1. 实现更多的 Job 类，处理不同的定时任务逻辑
2. 在 Task 表中添加更多字段，支持更复杂的任务配置
3. 增加任务管理接口，支持任务的增删改查
4. 添加任务执行日志记录功能
5. 实现任务监控和报警功能