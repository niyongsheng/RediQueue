import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
class RediQueueTest {

    @Autowired
    private RediQueue<MissionObject> rediQueue;

    @Autowired
    private RedisTemplate<String, MissionObject> redisTemplate;

    @BeforeEach
    void setUp() {
        // 清空队列，以确保测试前队列为空
        String queueName = "testQueue";
        redisTemplate.delete(queueName);
    }

    @Test
    void testEnqueueAndDequeue() {
        // 入队
        MissionObject item1 = new MissionObject(1, "Mission 1");
        MissionObject item2 = new MissionObject(2, "Mission 2");
        rediQueue.enqueue("testQueue", item1);
        rediQueue.enqueue("testQueue", item2);

        // 出队
        MissionObject dequeuedItem1 = rediQueue.dequeue("testQueue");
        MissionObject dequeuedItem2 = rediQueue.dequeue("testQueue");

        // 断言出队的顺序和内容是否正确
        assertEquals(item1, dequeuedItem1);
        assertEquals(item2, dequeuedItem2);
    }

    @Test
    void testGetQueueLength() {
        // 入队
        MissionObject item1 = new MissionObject(1, "Mission 1");
        MissionObject item2 = new MissionObject(2, "Mission 2");
        rediQueue.enqueue("testQueue", item1);
        rediQueue.enqueue("testQueue", item2);

        // 获取队列长度
        long length = rediQueue.getQueueLength("testQueue");

        // 断言队列长度是否正确
        assertEquals(2, length);
    }

    @Test
    void testGetAllItems() {
        // 入队
        MissionObject item1 = new MissionObject(1, "Mission 1");
        MissionObject item2 = new MissionObject(2, "Mission 2");
        rediQueue.enqueue("testQueue", item1);
        rediQueue.enqueue("testQueue", item2);

        // 获取所有队列项
        var items = rediQueue.getAllItems("testQueue");

        // 断言队列项是否正确
        assertEquals(2, items.size());
        assertEquals(item1, items.get(0));
        assertEquals(item2, items.get(1));
    }

    @Test
    void testDequeueFromEmptyQueue() {
        // 尝试从空队列出队
        MissionObject item = rediQueue.dequeue("testQueue");

        // 断言出队结果应该为null
        assertNull(item);
    }
}
