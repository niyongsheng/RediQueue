package com.qas.common.core.redis;

import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 *
 * @param <T>
 */
@Component
public class RedisQueue<T> {
    @Resource
    private RedisTemplate<String, T> redisTemplate;

    public void enqueue(String queueName, T item) {
        ListOperations<String, T> listOps = redisTemplate.opsForList();
        listOps.rightPush(queueName, item);
    }

    public T dequeue(String queueName) {
        ListOperations<String, T> listOps = redisTemplate.opsForList();
        return listOps.leftPop(queueName);
    }

    public long getQueueLength(String queueName) {
        ListOperations<String, T> listOps = redisTemplate.opsForList();
        return listOps.size(queueName);
    }

    public List<T> getAllItems(String queueName) {
        ListOperations<String, T> listOps = redisTemplate.opsForList();
        return listOps.range(queueName, 0, -1);
    }

}
