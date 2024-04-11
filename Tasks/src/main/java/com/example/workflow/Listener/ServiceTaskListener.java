package com.example.workflow.Listener;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.task.Task;
import org.springframework.stereotype.Component;

// Code to complete task using Java Delegate - 4th line
@Component
public class ServiceTaskListener implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        String processInstanceId = execution.getProcessInstanceId();
        Task task = processEngine.getTaskService().createTaskQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        String taskId = task.getId();
        execution.getProcessEngineServices().getTaskService().complete(taskId);
    }
}
