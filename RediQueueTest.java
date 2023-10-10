import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void testClearQueue() {
        // 入队
        MissionObject item1 = new MissionObject(1, "Mission 1");
        MissionObject item2 = new MissionObject(2, "Mission 2");
        rediQueue.enqueue("testQueue", item1);
        rediQueue.enqueue("testQueue", item2);

        // 清空队列
        rediQueue.clearQueue("testQueue");

        // 获取队列长度
        long length = rediQueue.getQueueLength("testQueue");

        // 断言队列已清空，长度应为0
        assertEquals(0, length);

        // 尝试从空队列出队
        MissionObject item = rediQueue.dequeue("testQueue");

        // 断言出队结果应该为null
        assertNull(item);
    }

    @Test
    void testReadQueueWithThreadPool() throws InterruptedException {
        // 创建一个线程池
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        // 创建一个对象用于线程之间的同步
        Object lock = new Object();

        // 入队一些元素
        MissionObject item1 = new MissionObject(1, "Mission 1");
        MissionObject item2 = new MissionObject(2, "Mission 2");
        rediQueue.enqueue("testQueue", item1);
        rediQueue.enqueue("testQueue", item2);

        // 使用线程池启动两个线程来读取队列
        for (int i = 0; i < 2; i++) {
            executorService.submit(() -> {
                while (true) {
                    synchronized (lock) {
                        MissionObject dequeuedItem = rediQueue.dequeue("testQueue");
                        if (dequeuedItem != null) {
                            // 在这里可以进行断言测试，例如检查是否成功出队元素
                            assertEquals(item1, dequeuedItem);
                            break; // 停止循环
                        }
                        try {
                            lock.wait(); // 等待通知
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            });
        }

        // 等待一段时间来确保线程有足够的时间读取队列
        Thread.sleep(5000); // 5秒

        // 停止线程池并等待其完成
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    
}
