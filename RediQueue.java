import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * Redis队列服务
 * 
 * https://github.com/niyongsheng/RediQueue
 * 
 * @param <T> 队列中的元素类型
 */
@Component
public class RediQueue<T> {

    @Resource
    private RedisTemplate<String, T> redisTemplate;
    private final ListOperations<String, T> listOps;

    public RediQueue() {
        // 获取ListOperations实例以提高性能
        listOps = redisTemplate.opsForList();
    }

    /**
     * 入队操作：将元素添加到队列的尾部。
     *
     * @param queueName 队列的名称
     * @param item      要入队的元素
     * @throws IllegalArgumentException 如果队列名称或元素为null
     */
    public void enqueue(String queueName, T item) {
        if (queueName == null || item == null) {
            throw new IllegalArgumentException("队列名称和元素不能为空");
        }
        try {
            listOps.rightPush(queueName, item);
        } catch (DataAccessException e) {
            // 处理Redis操作异常
            throw new RuntimeException("无法将元素添加到队列：" + e.getMessage(), e);
        }
    }

    /**
     * 出队操作：从队列的头部弹出一个元素。
     *
     * @param queueName 队列的名称
     * @return 出队的元素，如果队列为空则返回null
     * @throws IllegalArgumentException 如果队列名称为null
     */
    public T dequeue(String queueName) {
        if (queueName == null) {
            throw new IllegalArgumentException("队列名称不能为空");
        }
        try {
            return listOps.leftPop(queueName);
        } catch (DataAccessException e) {
            // 处理Redis操作异常，例如队列为空
            return null;
        }
    }

    /**
     * 获取队列长度。
     *
     * @param queueName 队列的名称
     * @return 队列的长度
     * @throws IllegalArgumentException 如果队列名称为null
     */
    public long getQueueLength(String queueName) {
        if (queueName == null) {
            throw new IllegalArgumentException("队列名称不能为空");
        }
        try {
            return listOps.size(queueName);
        } catch (DataAccessException e) {
            // 处理Redis操作异常
            throw new RuntimeException("无法获取队列长度：" + e.getMessage(), e);
        }
    }

    /**
     * 获取队列中的所有元素。
     *
     * @param queueName 队列的名称
     * @return 包含所有队列项的列表
     * @throws IllegalArgumentException 如果队列名称为null
     */
    public List<T> getAllItems(String queueName) {
        if (queueName == null) {
            throw new IllegalArgumentException("队列名称不能为空");
        }
        try {
            return listOps.range(queueName, 0, -1);
        } catch (DataAccessException e) {
            // 处理Redis操作异常
            throw new RuntimeException("无法获取队列中的所有元素：" + e.getMessage(), e);
        }
    }
}
