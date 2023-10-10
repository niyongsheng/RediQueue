import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
class RediQueueTest {

    @Autowired
    private RediQueue<String> rediQueue;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @BeforeEach
    void setUp() {
        // 清空队列，以确保测试前队列为空
        stringRedisTemplate.delete("testQueue");
    }

    @Test
    void testEnqueueAndDequeue() {
        // 入队
        rediQueue.enqueue("testQueue", "Item1");
        rediQueue.enqueue("testQueue", "Item2");

        // 出队
        String item1 = rediQueue.dequeue("testQueue");
        String item2 = rediQueue.dequeue("testQueue");

        // 断言出队的顺序和内容是否正确
        assertEquals("Item1", item1);
        assertEquals("Item2", item2);
    }

    @Test
    void testGetQueueLength() {
        // 入队
        rediQueue.enqueue("testQueue", "Item1");
        rediQueue.enqueue("testQueue", "Item2");

        // 获取队列长度
        long length = rediQueue.getQueueLength("testQueue");

        // 断言队列长度是否正确
        assertEquals(2, length);
    }

    @Test
    void testGetAllItems() {
        // 入队
        rediQueue.enqueue("testQueue", "Item1");
        rediQueue.enqueue("testQueue", "Item2");

        // 获取所有队列项
        List<String> items = rediQueue.getAllItems("testQueue");

        // 断言队列项是否正确
        assertEquals(2, items.size());
        assertEquals("Item1", items.get(0));
        assertEquals("Item2", items.get(1));
    }

    @Test
    void testDequeueFromEmptyQueue() {
        // 尝试从空队列出队
        String item = rediQueue.dequeue("testQueue");

        // 断言出队结果应该为null
        assertNull(item);
    }
}
