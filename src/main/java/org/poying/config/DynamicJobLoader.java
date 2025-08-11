package org.poying.config;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Component
public class DynamicJobLoader implements ApplicationListener<ContextRefreshedEvent> {
    
    private static final Logger logger = LoggerFactory.getLogger(DynamicJobLoader.class);
    
    @Autowired
    private Scheduler scheduler;
    
    @Value("${external.jobs.jar.path:}")
    private String externalJobsJarPath;
    
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // 首先尝试从环境变量或配置文件获取路径
        String jarPath = externalJobsJarPath;
        
        // 如果环境变量或配置文件中没有配置，则尝试从系统属性获取
        if (jarPath == null || jarPath.isEmpty()) {
            jarPath = System.getProperty("external.jobs.jar.path", "");
        }
        
        // 如果系统属性中也没有配置，则尝试从命令行参数获取
        if (jarPath == null || jarPath.isEmpty()) {
            jarPath = getJarPathFromCommandLineArgs();
        }
        
        if (jarPath != null && !jarPath.isEmpty()) {
            logger.info("开始加载外部任务jar包: {}", jarPath);
            try {
                loadJobsFromJar(jarPath);
            } catch (Exception e) {
                logger.error("加载外部任务jar包失败: {}", jarPath, e);
            }
        } else {
            logger.info("未配置外部任务jar包路径，跳过动态任务加载");
        }
    }
    
    private String getJarPathFromCommandLineArgs() {
        // 获取命令行参数
        String[] args = System.getProperty("sun.java.command", "").split(" ");
        
        for (int i = 0; i < args.length; i++) {
            if ("--external-jobs-jar-path".equals(args[i]) && i + 1 < args.length) {
                return args[i + 1];
            }
            
            if (args[i].startsWith("--external-jobs-jar-path=")) {
                return args[i].substring("--external-jobs-jar-path=".length());
            }
        }
        
        return "";
    }
    
    private void loadJobsFromJar(String jarPath) throws Exception {
        File jarFile = new File(jarPath);
        if (!jarFile.exists()) {
            logger.warn("外部任务jar包不存在: {}", jarPath);
            return;
        }
        
        URL jarUrl = jarFile.toURI().toURL();
        URLClassLoader classLoader = new URLClassLoader(new URL[]{jarUrl}, this.getClass().getClassLoader());
        
        try (JarFile jar = new JarFile(jarFile)) {
            Enumeration<JarEntry> entries = jar.entries();
            List<Class<? extends Job>> jobClasses = new ArrayList<>();
            
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.getName().endsWith(".class")) {
                    String className = entry.getName()
                            .replace("/", ".")
                            .replace(".class", "");
                    
                    try {
                        Class<?> clazz = classLoader.loadClass(className);
                        if (Job.class.isAssignableFrom(clazz) && clazz != Job.class) {
                            @SuppressWarnings("unchecked")
                            Class<? extends Job> jobClass = (Class<? extends Job>) clazz;
                            jobClasses.add(jobClass);
                            logger.info("发现任务类: {}", className);
                        }
                    } catch (ClassNotFoundException e) {
                        logger.warn("无法加载类: {}", className, e);
                    } catch (NoClassDefFoundError e) {
                        logger.warn("类定义未找到: {}", className, e);
                    }
                }
            }
            
            // 注册发现的任务类
            for (Class<? extends Job> jobClass : jobClasses) {
                registerJob(jobClass);
            }
            
            logger.info("成功从jar包 {} 中加载了 {} 个任务类", jarPath, jobClasses.size());
        } finally {
            try {
                classLoader.close();
            } catch (Exception e) {
                logger.warn("关闭类加载器时出错", e);
            }
        }
    }
    
    private void registerJob(Class<? extends Job> jobClass) {
        try {
            String jobName = jobClass.getSimpleName();
            String jobGroup = "DYNAMIC";
            
            JobDetail jobDetail = org.quartz.JobBuilder.newJob(jobClass)
                    .withIdentity(jobName, jobGroup)
                    .storeDurably()
                    .build();
            
            scheduler.addJob(jobDetail, true);
            logger.info("成功注册动态任务: {}.{}", jobGroup, jobName);
        } catch (SchedulerException e) {
            logger.error("注册动态任务失败: {}", jobClass.getName(), e);
        }
    }
}