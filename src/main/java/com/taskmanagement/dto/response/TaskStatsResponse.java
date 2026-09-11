package com.taskmanagement.dto.response;

public class TaskStatsResponse {
    private long totalTasks;
    private long todoCount;
    private long inProgressCount;
    private long completedCount;
    private long cancelledCount;
    private long lowPriorityCount;
    private long mediumPriorityCount;
    private long highPriorityCount;
    private long urgentPriorityCount;
    private long overdueCount;

    public TaskStatsResponse() {
    }

    public TaskStatsResponse(long totalTasks, long todoCount, long inProgressCount,
                             long completedCount, long cancelledCount,
                             long lowPriorityCount, long mediumPriorityCount,
                             long highPriorityCount, long urgentPriorityCount,
                             long overdueCount) {
        this.totalTasks = totalTasks;
        this.todoCount = todoCount;
        this.inProgressCount = inProgressCount;
        this.completedCount = completedCount;
        this.cancelledCount = cancelledCount;
        this.lowPriorityCount = lowPriorityCount;
        this.mediumPriorityCount = mediumPriorityCount;
        this.highPriorityCount = highPriorityCount;
        this.urgentPriorityCount = urgentPriorityCount;
        this.overdueCount = overdueCount;
    }

    public long getTotalTasks() {
        return totalTasks;
    }

    public void setTotalTasks(long totalTasks) {
        this.totalTasks = totalTasks;
    }

    public long getTodoCount() {
        return todoCount;
    }

    public void setTodoCount(long todoCount) {
        this.todoCount = todoCount;
    }

    public long getInProgressCount() {
        return inProgressCount;
    }

    public void setInProgressCount(long inProgressCount) {
        this.inProgressCount = inProgressCount;
    }

    public long getCompletedCount() {
        return completedCount;
    }

    public void setCompletedCount(long completedCount) {
        this.completedCount = completedCount;
    }

    public long getCancelledCount() {
        return cancelledCount;
    }

    public void setCancelledCount(long cancelledCount) {
        this.cancelledCount = cancelledCount;
    }

    public long getLowPriorityCount() {
        return lowPriorityCount;
    }

    public void setLowPriorityCount(long lowPriorityCount) {
        this.lowPriorityCount = lowPriorityCount;
    }

    public long getMediumPriorityCount() {
        return mediumPriorityCount;
    }

    public void setMediumPriorityCount(long mediumPriorityCount) {
        this.mediumPriorityCount = mediumPriorityCount;
    }

    public long getHighPriorityCount() {
        return highPriorityCount;
    }

    public void setHighPriorityCount(long highPriorityCount) {
        this.highPriorityCount = highPriorityCount;
    }

    public long getUrgentPriorityCount() {
        return urgentPriorityCount;
    }

    public void setUrgentPriorityCount(long urgentPriorityCount) {
        this.urgentPriorityCount = urgentPriorityCount;
    }

    public long getOverdueCount() {
        return overdueCount;
    }

    public void setOverdueCount(long overdueCount) {
        this.overdueCount = overdueCount;
    }
}
