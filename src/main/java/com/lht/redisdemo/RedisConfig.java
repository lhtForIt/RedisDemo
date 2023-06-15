package com.lht.redisdemo;

import io.lettuce.core.RedisURI;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.sync.RedisAdvancedClusterCommands;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Arrays;

/**
 * @author lianght1
 * @date 2023/5/30
 */
@Configuration
public class RedisConfig {
//    @Bean
//    public RedisConnectionFactory connectionFactory() {
//        //这里要么不写，要么写的时候要把yml里面的配置自己配到redisconnectionfactory里面去，否则会出问题
//        return new LettuceConnectionFactory();
//    }
    //单节点
    @Bean
    public RedisTemplate getRedisTemplate(RedisConnectionFactory connectionFactory){
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericToStringSerializer<>(Object.class));
        template.setHashValueSerializer(new GenericToStringSerializer<>(Object.class));
        return template;
    }

//    public @Bean RedisConnectionFactory connectionFactory() {
//
//        RedisClusterConfiguration clusterConfiguration = new RedisClusterConfiguration(Arrays.asList("169.254.141.73:7000",
//                "169.254.141.73:7001",
//                "169.254.141.73:7002",
//                "169.254.141.73:7003",
//                "169.254.141.73:7004",
//                "169.254.141.73:7005"));
//        clusterConfiguration.setPassword(RedisPassword.of("123456"));
//        return new LettuceConnectionFactory(
//                clusterConfiguration);
//    }


    public static void main(String[] args) {
        RedisURI node1 = RedisURI.create("169.254.141.73", 7000);
        RedisURI node2 = RedisURI.create("169.254.141.73", 7001);
        RedisURI node3 = RedisURI.create("169.254.141.73", 7002);
        RedisURI node4 = RedisURI.create("169.254.141.73", 7003);
        RedisURI node5 = RedisURI.create("169.254.141.73", 7004);
        RedisURI node6 = RedisURI.create("169.254.141.73", 7005);
        node1.setPassword("123456");
        node2.setPassword("123456");
        node3.setPassword("123456");
        node4.setPassword("123456");
        node5.setPassword("123456");
        node6.setPassword("123456");

        RedisClusterClient clusterClient = RedisClusterClient.create(Arrays.asList(node1, node2, node3, node4, node5, node6));
        StatefulRedisClusterConnection<String, String> connection = clusterClient.connect();
        RedisAdvancedClusterCommands<String, String> syncCommands = connection.sync();

        syncCommands.set("test", "liang");

        System.out.println(syncCommands.get("test"));

        connection.close();
        clusterClient.shutdown();
    }





}
