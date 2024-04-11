package com.example.workflow.TimerBoundary;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.task.Task;
import org.springframework.stereotype.Component;

@Component
public class UserListen implements TaskListener {
    @Override
    public void notify(DelegateTask delegateTask) {
        String taskId = delegateTask.getId();
        System.out.println("Current Task ID: " + taskId);
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        String processInstanceId = delegateTask.getProcessInstanceId();
        Task task = processEngine.getTaskService().createTaskQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        System.out.println(task.getId());
        System.out.println(task.isSuspended());
        // TaskId for delegatetask sucessful in event = create and event = complete
        // Task Id from taskService failed in event = create and successful in event = complete.
    }
}
