package com.example.workflow.TimerBoundary;

import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TaskServiceComplete implements ExecutionListener {
    @Autowired
    private TaskService taskService;

    @Override
    public void notify(DelegateExecution execution) throws Exception {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        String processInstanceId = execution.getProcessInstanceId();
        Task task = processEngine.getTaskService().createTaskQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
// Can complete tasks using taskService.complete(taskId);
        // Task should be registered into task service, couldnot find a proper use-case
        if (task != null) {
            String taskId = task.getId();
            System.out.println(taskId);
            System.out.println(task.isSuspended());
            try {
                taskService.complete(taskId);
                System.out.println("Done: " + execution.getId());
            } catch (ProcessEngineException e) {
                e.printStackTrace();
            }
        }


    }
}
