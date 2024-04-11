package com.example.workflow.Task;

import jakarta.annotation.PostConstruct;
import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
// Task Status Checker, tasks created and completed in a period of time.
@Component
@EnableScheduling
public class TaskStatusChecker {
    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;

    private Date lastCheckTime = new Date();


    @PostConstruct
    @Scheduled(fixedRate = 10000)
    public void checkTaskStatus() {
        Date currentTime = new Date();
        System.out.println("checkTaskChecker");
        List<Task> newTasks = taskService.createTaskQuery()
                .taskCreatedAfter(lastCheckTime)
                .list();
        List<HistoricTaskInstance> completedTasks = historyService.createHistoricTaskInstanceQuery()
                .finished()
                .startedAfter(lastCheckTime)
                .list();


        lastCheckTime = currentTime;

        for (Task task : newTasks) {
            System.out.println("New Task Created - ID: " + task.getId() +
                    ", Name: " + task.getName() +
                    ", Assignee: " + task.getAssignee() +
                    ", Start Time: " + task.getCreateTime());
        }

        for (HistoricTaskInstance task : completedTasks) {
            System.out.println("Task Completed - ID: " + task.getId() +
                    ", Name: " + task.getName() +
                    ", Assignee: " + task.getAssignee() +
                    ", Start Time: " + task.getStartTime() +
                    ", End Time: " + task.getEndTime());
        }
    }
}
