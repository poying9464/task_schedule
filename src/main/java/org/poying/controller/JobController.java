package org.poying.controller;

import org.poying.jobs.SampleJob;
import org.poying.jobs.FiveMinuteJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    @Autowired
    private Scheduler scheduler;

    @PostMapping("/sample")
    public ResponseEntity<Map<String, Object>> scheduleSampleJob() throws SchedulerException {
        Map<String, Object> response = new HashMap<>();
        
        JobDetail jobDetail = JobBuilder.newJob(SampleJob.class)
                .withIdentity("dynamicSampleJob", "group1")
                .usingJobData("name", "dynamicSample")
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity("dynamicSampleTrigger", "group1")
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(15)
                        .repeatForever())
                .build();

        Date date = scheduler.scheduleJob(jobDetail, trigger);
        
        response.put("message", "Job scheduled successfully");
        response.put("nextFireTime", date);
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/five-minute")
    public ResponseEntity<Map<String, Object>> scheduleFiveMinuteJob() throws SchedulerException {
        Map<String, Object> response = new HashMap<>();
        
        JobDetail jobDetail = JobBuilder.newJob(FiveMinuteJob.class)
                .withIdentity("fiveMinuteJob", "group1")
                .usingJobData("name", "fiveMinute")
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity("fiveMinuteTrigger", "group1")
                .startNow()
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0/5 * * * ?"))
                .build();

        Date date = scheduler.scheduleJob(jobDetail, trigger);
        
        response.put("message", "Five minute job scheduled successfully");
        response.put("nextFireTime", date);
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/sample/{jobName}")
    public ResponseEntity<Map<String, Object>> deleteSampleJob(@PathVariable String jobName) throws SchedulerException {
        Map<String, Object> response = new HashMap<>();
        
        JobKey jobKey = new JobKey(jobName, "group1");
        scheduler.deleteJob(jobKey);
        
        response.put("message", "Job deleted successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/scheduler")
    public ResponseEntity<Map<String, Object>> configureScheduler(@RequestBody Map<String, Object> config) throws SchedulerException {
        Map<String, Object> response = new HashMap<>();

        // Extract configuration parameters
        int threadPoolSize = Integer.parseInt(config.getOrDefault("threadPoolSize", "10").toString());
        String schedulerName = config.getOrDefault("schedulerName", "ManualScheduler").toString();

        // Create a new SchedulerFactory
        SchedulerFactory schedulerFactory = new org.quartz.impl.StdSchedulerFactory();
        
        // Optionally configure the scheduler factory if needed
        // e.g., through properties or other means

        // Get or create a new Scheduler instance
        Scheduler manualScheduler = schedulerFactory.getScheduler();

        // Start the scheduler
        manualScheduler.start();

        // Store or manage manualScheduler as needed, e.g., in a service or cache
        // For demonstration, we'll just return a success message
        response.put("message", "Scheduler configured successfully");
        response.put("schedulerName", schedulerName);
        response.put("threadPoolSize", threadPoolSize);

        return ResponseEntity.ok(response);
    }
}