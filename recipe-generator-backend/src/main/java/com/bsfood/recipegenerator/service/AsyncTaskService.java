package com.bsfood.recipegenerator.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 异步任务服务 - 管理异步生成食谱任务
 */
@Service
public class AsyncTaskService {

    /**
     * 任务状态枚举
     */
    public enum TaskStatus {
        PENDING,    // 待处理
        PROCESSING, // 处理中
        COMPLETED,  // 已完成
        FAILED      // 失败
    }

    /**
     * 异步任务结果
     */
    public static class AsyncTaskResult {
        public TaskStatus status;
        public Object data;
        public String error;
        public long createTime;

        public AsyncTaskResult() {
            this.status = TaskStatus.PENDING;
            this.createTime = System.currentTimeMillis();
        }
    }

    // 存储异步任务，key为taskId
    private final ConcurrentHashMap<String, AsyncTaskResult> tasks = new ConcurrentHashMap<>();

    /**
     * 创建异步任务
     * @param taskId 任务ID
     * @return 任务结果对象
     */
    public AsyncTaskResult createTask(String taskId) {
        AsyncTaskResult result = new AsyncTaskResult();
        tasks.put(taskId, result);
        return result;
    }

    /**
     * 获取任务结果
     * @param taskId 任务ID
     * @return 任务结果，可能为null
     */
    public AsyncTaskResult getTask(String taskId) {
        return tasks.get(taskId);
    }

    /**
     * 更新任务状态为处理中
     * @param taskId 任务ID
     */
    public void markProcessing(String taskId) {
        AsyncTaskResult result = tasks.get(taskId);
        if (result != null) {
            result.status = TaskStatus.PROCESSING;
        }
    }

    /**
     * 设置任务成功结果
     * @param taskId 任务ID
     * @param data 结果数据
     */
    public void setSuccess(String taskId, Object data) {
        AsyncTaskResult result = tasks.get(taskId);
        if (result != null) {
            result.status = TaskStatus.COMPLETED;
            result.data = data;
        }
    }

    /**
     * 设置任务失败结果
     * @param taskId 任务ID
     * @param error 错误信息
     */
    public void setFailed(String taskId, String error) {
        AsyncTaskResult result = tasks.get(taskId);
        if (result != null) {
            result.status = TaskStatus.FAILED;
            result.error = error;
        }
    }

    /**
     * 移除任务（清理内存）
     * @param taskId 任务ID
     */
    public void removeTask(String taskId) {
        tasks.remove(taskId);
    }
}
