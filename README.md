# RediQueue

## 介绍

`RediQueue` 是一个用于操作 Redis 队列的 Java 类。<br>
在Redis中，List（列表）数据结构是一个有序的、可重复的数据集合，它允许在列表的两端执行添加（push）和删除（pop）操作。Redis的List是一个非常灵活和强大的数据结构，可实现队列、栈、发布与订阅等功能。<br>
这里考虑代码使用环境采用 Spring Framework 的 RedisTemplate 和 ListOperations 来提供基本的队列操作功能，包括入队、出队、获取队列长度和获取所有队列项。

## 使用

1. Maven 依赖：

```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-data-redis</artifactId>
   </dependency>
```

2. 测试用例：

    [RediQueueTest](RediQueueTest.java ':include :type=code')
```java
    @Autowired
    private RediQueue<MissionObject> rediQueue;

    // 入队
    MissionObject item1 = new MissionObject(1, "Mission 1");
    MissionObject item2 = new MissionObject(2, "Mission 2");
    rediQueue.enqueue("testQueue", item1);
    rediQueue.enqueue("testQueue", item2);

    // 出队
    MissionObject dequeuedItem1 = rediQueue.dequeue("testQueue");

    // 清空队列
    rediQueue.clearQueue("testQueue");

    // 获取队列长度
    long length = rediQueue.getQueueLength("testQueue");
```
## 联系我

* 微博: [@Ni永胜](https://weibo.com/u/7317805089)
* 邮箱: niyongsheng@Outlook.com