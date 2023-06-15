package com.lht.redisdemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConnection;
import org.springframework.data.redis.core.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class RedisdemoApplication {

    public static void main(String[] args) throws ParseException {
        ConfigurableApplicationContext context = SpringApplication.run(RedisdemoApplication.class, args);
        RedisTemplate redisTemplate = (RedisTemplate) context.getBean("getRedisTemplate");

        /**
         * 哨兵和集群可以通过这种方式得到连接
         */
//        RedisSentinelConnection redisSentinelConnection = ((RedisConnectionFactory) context.getBean("redisConnectionFactory")).getSentinelConnection();
//        RedisClusterConnection redisClusterConnection = ((RedisConnectionFactory) context.getBean("redisConnectionFactory")).getClusterConnection();

        ValueOperations vs = redisTemplate.opsForValue();
        HashOperations hs = redisTemplate.opsForHash();
        ListOperations ls = redisTemplate.opsForList();
        SetOperations ss = redisTemplate.opsForSet();
        ZSetOperations zs = redisTemplate.opsForZSet();

        /**
         * string
         */
        vs.set("liang", 111);
        //设置过期
        vs.set("li", 121, 3, TimeUnit.SECONDS);
        String str = (String) vs.get("liang");
        Object liang = vs.getAndSet("liang", 12);
        long increment = vs.increment("liang", 3);
        double increment1 = vs.increment("liang", 3.2);
        int t = vs.append("liang", "23");
        Map<String, Object> map = new HashMap<>();
        map.put("1", "1");
        map.put("2", "2");
        map.put("3", "3");
        //批量设置，批量获取数据，多个key返回一个list
        vs.multiSet(map);
        System.out.println(Arrays.toString(vs.multiGet(map.keySet()).toArray(new String[]{})));
        System.out.println(str);
        System.out.println(increment);
        /**
         * hash
         */
        hs.put("wang", "liang", "333");
        String o = (String) hs.get("wang", "liang");
        System.out.println(o);
        hs.putAll("kai", map);
        //对应hmget
        List kai1 = hs.multiGet("kai", map.keySet());
        //对应hgetall
        Map kai2 = hs.entries("kai");
        //对应hvals
        List kai = hs.values("kai");
        System.out.println(Arrays.toString(kai.toArray(new String[]{})));

        //对应hdel
        hs.delete("kai", "1", "2");

        //这里我一开始用的hs.increment("liang", "li", 3);因为liang已经再string里面设置过key了，
        //所以会报WRONGTYPE Operation against a key holding the wrong kind of value错误，其实就是
        //这个类型前面有用过了，你现在用一个其他类型在使用，报错
        hs.increment("du", "li", 3);
        Object o1 = hs.get("du", "li");

        //hexists
        Boolean aBoolean = hs.hasKey("du", "li");

        //hlen
        long l = hs.size("du");
        hs.put("du", "liang", 212);
        l = hs.size("du");
        //hkeys
        Set liang1 = hs.keys("du");

        /**
         * list
         *
         * 这个只有pop会删除元素,range只是查看，set可以修改元素
         * 当删除元素之后链表为null，会删除这个key
         *
         */
        ls.rightPush("fang", "123");
        List<String> list = new ArrayList<>();
        list.add("122");
        list.add("121");
        list.add("120");
        ls.rightPushAll("fang", list);
        ls.rightPushAll("fang", "119", "118");

        ls.leftPush("fang", "117");
        Object fang = ls.leftPop("fang");

        //对应lrange
        List fang1 = ls.range("fang", 0, -1);
        //下标是从0开始的
        ls.set("fang", 3, "333");
        Long fang2 = ls.size("fang");
        Object fang3 = ls.rightPop("fang");

        /**
         * set
         *
         * set需要注意如果add时传List会将list里所有元素当成一个value存储，如果想是多个value存储，
         * 需要转成数组，看下面的例子。
         */
        ss.add("ss", "1", "2", "3");
        ss.add("ss", list);
        ss.add("ss",list.toArray(new String[]{}));

        //对应srem，可以删除多个
        ss.remove("ss", "2", "3");

        Set ss1 = ss.members("ss");
        //是否有这个元素
        Boolean ss2 = ss.isMember("ss", "1");

        ss.add("ss1", "2", "121", "1","7","8");

        //求a和b差集，列出a和b两个set里面不一样的部分，a在前列a和b不一样的，b在前列b和a不一样的
        Set difference = ss.difference("ss", "ss1");

        //可以求出交集并存到一个key里
        ss.intersectAndStore("ss", "ss1", "ss2");

        //求并集,这里所有的交集，差集，并集都可以存在一个key里
        Set union = ss.union("ss", "ss1");

        /**
         * zset(sort set)
         */

        zs.add("zs", "112", 0);
        DefaultTypedTuple typedTuple = new DefaultTypedTuple("113", 1d);
        DefaultTypedTuple typedTuple1 = new DefaultTypedTuple("114", 3d);
        DefaultTypedTuple typedTuple2 = new DefaultTypedTuple("115", 2d);
        DefaultTypedTuple typedTuple3 = new DefaultTypedTuple("116", 5d);
        DefaultTypedTuple typedTuple4 = new DefaultTypedTuple("117", 4d);
        Set<ZSetOperations.TypedTuple> set = new HashSet<>();
        set.add(typedTuple);
        set.add(typedTuple1);
        set.add(typedTuple2);
        set.add(typedTuple3);
        set.add(typedTuple4);
        zs.add("zs1", set);

        //查询成员分数
        Double zs1 = zs.score("zs1", "117");

        //某个key的成员数量
        Long zs11 = zs.zCard("zs1");

        Long zs12 = zs.remove("zs1", "115");

        //通过索引下标查
        Set zs13 = zs.range("zs1", 0, -1);
        //返回带分数
        Set zs15 = zs.rangeWithScores("zs1", 0, -1);
        //通过分数查找范围不返回分数
        Set zs14 = zs.rangeByScore("zs1", 0, 3);
        //通过分数查找范围返回分数(正序)
        Set zs16 = zs.rangeByScoreWithScores("zs1", 0, 10);

        //反序排列
        //通过索引
        Set zs17 = zs.reverseRange("zs1", 0, -1);
        //返回分数
        Set zs18 = zs.reverseRangeWithScores("zs1", 0, -1);
        //通过分数
        Set zs19 = zs.reverseRangeByScore("zs1", 0, 3);
        Set zs110 = zs.reverseRangeByScoreWithScores("zs1", 0, 10);

        //按分数范围删除
        zs.removeRangeByScore("zs1", 0, 1);
        //按索引删除，注意从0开始删除到1下标的元素，0,1删除的是两个元素，如果想删除一个元素是0,0
        zs.removeRange("zs1", 0, 0);

        //返回该元素索引，感觉没啥用
        Long zs111 = zs.rank("zs1", "113");

        /**
         * 过期时间
         */
        vs.set("lliang", "1222");
        vs.set("liliang", "1222");
        //设置多久时间
        redisTemplate.expire("lliang", 10, TimeUnit.SECONDS);
        //还剩多少时间过期
        redisTemplate.getExpire("lliang");
        redisTemplate.getExpire("zs1");
        redisTemplate.expire("zs1", 100, TimeUnit.SECONDS);
        //取消过期时间设定
        redisTemplate.persist("zs1");
        //设置时间为-1会直接删掉
        redisTemplate.expire("zs1", -1, TimeUnit.SECONDS);
        //设置到什么时间过期
        redisTemplate.expireAt("liliang", new SimpleDateFormat("yy-MM-dd").parse("2023-06-01"));
    }

}
