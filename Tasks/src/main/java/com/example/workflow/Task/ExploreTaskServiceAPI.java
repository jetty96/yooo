package com.example.workflow.Task;


import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.runtime.ProcessInstanceQuery;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.task.TaskQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.security.PublicKey;
import java.util.*;

@Component
@EnableScheduling
public class ExploreTaskServiceAPI {

    @Autowired
    private TaskService taskService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private HistoryService historyService;


    @Scheduled(fixedRate = 15000)
    public void printUserTasks() {
        // Print all Tasks associated to a User at a point of time.
        int numUsers = 5;
        for (int i = 1; i <= numUsers; i++) {
            String userName = "UserName" + i;
            List<Task> tasks = taskService.createTaskQuery().taskAssignee(userName).list();
            if (!tasks.isEmpty()) {
                System.out.println("Tasks assigned to user " + userName + ":");
                for (Task task : tasks) {
                    System.out.println("Task Id: " + task.getId() + ", Name: " + task.getName());
                    // Whether the task is active or not
                    System.out.println(task.isSuspended());
                }
            } else {
                System.out.println("No tasks assigned to user " + userName);
            }
        }
    }

    public String taskStatus(String taskId){
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        // can use task.isSuspended() as well. Will tell whether the task is active or not
        if(task != null){
            if(task.getAssignee()!=null){
                return "Pending";
            }else{
                return "created";
            }
        }else{
            return "Completed";
        }

    }

    public void PrintCurrentTasksUsingProcessInstance() {
        //query tasks based on the process instance ID. retrieving all tasks associated with a specific process instance
        List<String> processInstanceIds = GetProcessInstanceIDs();
        List<Task> allTasks = new ArrayList<>();
        // Iterate over each process instance ID
        for (String processInstanceId : processInstanceIds) {
            List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
            // Add the tasks to the list of all tasks
            allTasks.addAll(tasks);
        }

    }


    public List<Task> getTasksByNameOrDescription(String name, String description) {
        // get Tasks By Name And Description
        // filter tasks based on their name or description using taskName() and taskDescription() methods in the task query.
        // This allows to search for tasks with specific names or descriptions

        // Create a task query
        TaskQuery query = taskService.createTaskQuery();

        // Filter tasks by name
        if (name != null && !name.isEmpty()) {
            query.taskName(name);
        }

        // Filter tasks by description
        if (description != null && !description.isEmpty()) {
            query.taskDescription(description);
        }

        // Retrieve tasks matching the query criteria
        List<Task> tasks = query.list();

        return tasks;
    }


    public List<Task> getUnassignedTasks() {
        // query tasks that are not assigned to any user using the taskUnassigned()
        // retrieving tasks that are available for claim

        // Create a task query
        TaskQuery query = taskService.createTaskQuery();

        // Filter tasks to include only unassigned tasks
        query.taskUnassigned();

        // Retrieve unassigned tasks
        List<Task> unassignedTasks = query.list();

        return unassignedTasks;
    }


    public void claimAndAssignTask(String taskId, String userId) {

        // use the claim() method to claim a task for a specific user and
        // the setAssignee() method to assign a task to a user.
        // tasks need to be assigned to users for processing.


        // Claim the task for the user
        taskService.claim(taskId, userId);

        // Assign the task to the user
        taskService.setAssignee(taskId, userId);
    }


    public List<String> GetProcessInstanceIDs() {
        // Get Active Process InstanceIDs

//        Create a query to retrieve historic process instances
//        HistoricProcessInstanceQuery query = historyService.createHistoricProcessInstanceQuery();
//        List<HistoricProcessInstance> historicProcessInstances = query.list();

        ProcessInstanceQuery query = runtimeService.createProcessInstanceQuery();
        List<ProcessInstance> activeProcessInstances = query.list();
        List<String> processInstanceIds = new ArrayList<>();

        // To get only active Process Instances, use RunTimeService not history service like above
        for (ProcessInstance processInstance : activeProcessInstances) {
            processInstanceIds.add(processInstance.getProcessInstanceId());
        }
        return processInstanceIds;
    }

    public void completeTaskWithApprovalStatus(String taskId) {
        // Create a map to store process variables
        Map<String, Object> variables = new HashMap<>();
        variables.put("approvalStatus", "approved");

        // Complete the task with the provided variables
        taskService.complete(taskId, variables);
    }
    public void deleteTask(String taskId) {
        // delete tasks using the deleteTask() method. This removes the task from the Camunda database.

        // Delete the task by its ID
        taskService.deleteTask(taskId);
    }


    public List<Task> getTasksByPriority(int priority) {
        // set due dates and priorities for tasks using the
        // dueDate() and taskPriority() methods in the task query
        // Create a task query
        TaskQuery query = taskService.createTaskQuery();

        // Filter tasks by priority
        query.taskPriority(priority);

        // Retrieve tasks matching the query criteria
        List<Task> tasks = query.list();

        return tasks;
    }


    public void delegateTask(TaskService taskService, String taskId, String newAssigneeUserId) {
        // Task Delegation
        taskService.delegateTask(taskId, newAssigneeUserId);
    }


    public String getTaskFormKey(TaskService taskService, String taskId) {
        // Retrieving Task Forms
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task != null) {
            return task.getFormKey();
        } else {
            return null; // Task not found or no form key defined
        }
    }


    public void addCommentToTask(TaskService taskService, String taskId, String processInstanceId, String comment) {
        // Adding Comments to Tasks
        taskService.addComment(taskId, processInstanceId, comment);
    }


    public Map<String, Object> getTaskVariables(TaskService taskService, String taskId) {
        // Retrieving Task Variables
        return taskService.getVariables(taskId);
    }


    public void setDueDateForTask(TaskService taskService, String taskId, Date newDueDate) {
        // Task Escalation
        taskService.setDueDate(taskId, newDueDate);
    }


    public void setFollowUpDateForTask(TaskService taskService, String taskId, Date followUpDate) {
        // Task Follow-Up
        taskService.setFollowUpDate(taskId, followUpDate);
    }

    public void addSubscriptionForUser(TaskService taskService, String taskId, String userId) {
        // Task Subscriptions

        taskService.addCandidateUser(taskId, userId);
    }

    public void escalateTask(TaskService taskService, String taskId, Date newDueDate) {
        // Set the new due date for the task
        taskService.setDueDate(taskId, newDueDate);

        System.out.println("Due date for task with ID " + taskId + " has been updated to: " + newDueDate);
    }
}
