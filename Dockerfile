# 使用官方OpenJDK 21镜像作为基础镜像
FROM openjdk:21-jdk-slim

# 设置维护者信息
LABEL maintainer="poying"

# 设置工作目录
WORKDIR /app

# 创建目录用于存放外部任务jar包
RUN mkdir -p /app/external-jobs

# 复制打包好的jar文件到容器中
COPY target/task_schdule-1.0-SNAPSHOT.jar app.jar

# 暴露端口
EXPOSE 8080

# 设置环境变量默认值
ENV EXTERNAL_JOBS_JAR_PATH=""

# 启动应用
ENTRYPOINT ["sh", "-c", "java -jar app.jar --external-jobs-jar-path=${EXTERNAL_JOBS_JAR_PATH}"]