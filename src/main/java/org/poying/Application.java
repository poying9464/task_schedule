package org.poying;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("org.poying.mapper")
public class Application {

    public static void main(String[] args) {
        // 解析命令行参数
        parseCommandLineArgs(args);
        
        SpringApplication.run(Application.class, args);
    }
    
    private static void parseCommandLineArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if ("--external-jobs-jar-path".equals(args[i]) && i + 1 < args.length) {
                System.setProperty("external.jobs.jar.path", args[i + 1]);
                i++; // 跳过参数值
            } else if (args[i].startsWith("--external-jobs-jar-path=")) {
                String jarPath = args[i].substring("--external-jobs-jar-path=".length());
                System.setProperty("external.jobs.jar.path", jarPath);
            }
        }
    }
}