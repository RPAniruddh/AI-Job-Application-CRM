package com.jobcrm.workflow.engine;

import com.jobcrm.workflow.model.WorkflowTask;
import com.jobcrm.workflow.repository.WorkflowTaskRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class WorkflowEngine {

    private final WorkflowTaskRepository workflowTaskRepository;
    private final TaskExecutor taskExecutor;
    private final RedisTemplate<String, String> redisTemplate;

    public WorkflowEngine(WorkflowTaskRepository workflowTaskRepository,
                          TaskExecutor taskExecutor,
                          RedisTemplate<String, String> redisTemplate) {
        this.workflowTaskRepository = workflowTaskRepository;
        this.taskExecutor = taskExecutor;
        this.redisTemplate = redisTemplate;
    }

    // Runs every hour — 3,600,000 milliseconds
    @Scheduled(fixedRate = 300000)
    public void processPendingTasks() {
        System.out.println("WORKFLOW ENGINE: Tick at " + LocalDateTime.now());

        List<WorkflowTask> dueTasks = workflowTaskRepository
                .findPendingTasksDue(LocalDateTime.now());

        System.out.println("WORKFLOW ENGINE: Found " + dueTasks.size() + " tasks due");

        for (WorkflowTask task : dueTasks) {
            String lockKey = "lock:task:" + task.getId();

            // Try to acquire Redis lock — SET key value NX EX 30
            Boolean acquired = redisTemplate.opsForValue()
                    .setIfAbsent(lockKey, "locked", 30, TimeUnit.SECONDS);

            if (Boolean.TRUE.equals(acquired)) {
                try {
                    taskExecutor.execute(task);
                } finally {
                    // Always release lock after execution
                    redisTemplate.delete(lockKey);
                }
            } else {
                System.out.println("WORKFLOW ENGINE: Task " + task.getId()
                        + " already locked — skipping");
            }
        }
    }
}