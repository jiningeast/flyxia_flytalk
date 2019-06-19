package com.flyxia.flytalk.redisDomain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author automannn@163.com
 * @time 2019/5/6 16:41
 */
@Configuration
public class RedisConfig {

    @Value("${spring.redis.pool.max-idle}")
    private Integer maxIdle;


    @Value("${spring.redis.pool.max-wait}")
    private Integer maxWaitMillis;

    @Value("${spring.redis.host}")
    private String hostname;

    @Value("${spring.redis.port}")
    private Integer port;

    @Value("${spring.redis.timeout}")
    private Integer timeout;

    @Bean
    public JedisPoolConfig jedisPoolConfig(){
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();

        jedisPoolConfig.setMaxIdle(maxIdle);

        jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);

        return jedisPoolConfig;
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory(JedisPoolConfig jedisPoolConfig){
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(jedisPoolConfig);

        jedisConnectionFactory.setHostName(hostname);

        jedisConnectionFactory.setPort(port);

        jedisConnectionFactory.setTimeout(timeout);

        return jedisConnectionFactory;
    }

    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory redisConnectionFactory){
        RedisTemplate<String,Object> redisTemplate = new RedisTemplate<>();
        initDomainRedisTemplate(redisTemplate,redisConnectionFactory);

        return redisTemplate;
    }

    //设置数据村如redis的序列化方式，并开启事务
    private void initDomainRedisTemplate(RedisTemplate<String,Object> redisTemplate,RedisConnectionFactory factory){
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        //开启事务
        redisTemplate.setEnableTransactionSupport(true);
        redisTemplate.setConnectionFactory(factory);
    }

    @Bean
    public JedisUtil jedisUtil(RedisTemplate<String,Object> redisTemplate){
        JedisUtil jedisUtil = new JedisUtil();
        jedisUtil.setRedisTemplate(redisTemplate);
        return jedisUtil;
    }
}
