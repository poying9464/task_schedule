package org.poying.controller;

import org.poying.jobs.TestJob;
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

    @PostMapping("/test")
    public ResponseEntity<Map<String, Object>> scheduleTestJob() throws SchedulerException {
        Map<String, Object> response = new HashMap<>();

        JobDetail jobDetail = JobBuilder.newJob(TestJob.class)
                .withIdentity("testJob", "group1")
                .usingJobData("name", "test")
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity("testJobTrigger", "group1")
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(30)
                        .repeatForever())
                .build();

        Date date = scheduler.scheduleJob(jobDetail, trigger);

        response.put("message", "Test job scheduled successfully");
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
}