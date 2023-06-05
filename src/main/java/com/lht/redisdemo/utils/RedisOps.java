package com.lht.redisdemo.utils;

/**
 * @author lianght1
 * @date 2023/5/31
 */
public interface RedisOps {

    Object get(String key);

    void set(String key, Object value);

    Object getSet(String key, Object newValue);
    //没有
    void deleteKey(String key);

    //好像没有这个方法
    boolean exists(String key);

    int append(String key);

    int incrBy(String key);

    //没有incr,没有dec,decrby，我理解都可以用incrby做
















}
