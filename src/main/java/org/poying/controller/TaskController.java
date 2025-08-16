package org.poying.controller;

import org.poying.service.TaskService;
import org.poying.vo.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    /**
     * 获取所有任务列表
     */
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        List<Task> tasks = taskService.list();
        return ResponseEntity.ok(tasks);
    }

    /**
     * 获取任务统计信息
     */
    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getTaskMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        // 获取所有任务
        List<Task> tasks = taskService.list();
        
        // 计算统计信息
        long totalTasks = tasks.size();
        long activeTasks = tasks.stream().filter(task -> task.getStatus() == 1).count();
        
        // TODO: 从TaskResultDetail表中获取成功和失败次数
        long successCount = 0;
        long failCount = 0;
        
        metrics.put("totalTasks", totalTasks);
        metrics.put("activeTasks", activeTasks);
        metrics.put("successCount", successCount);
        metrics.put("failCount", failCount);
        
        return ResponseEntity.ok(metrics);
    }

    /**
     * 创建或更新任务
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> saveTask(@RequestBody Task task) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean success = taskService.saveOrUpdate(task);
            response.put("success", success);
            response.put("message", success ? "保存成功" : "保存失败");
            if (success) {
                response.put("data", task);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "保存失败: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * 删除任务
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteTask(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean success = taskService.removeById(id);
            response.put("success", success);
            response.put("message", success ? "删除成功" : "删除失败");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "删除失败: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * 执行任务
     */
    @PostMapping("/{id}/execute")
    public ResponseEntity<Map<String, Object>> executeTask(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // TODO: 实现任务执行逻辑
            response.put("success", true);
            response.put("message", "任务已触发执行");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "任务执行失败: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
}