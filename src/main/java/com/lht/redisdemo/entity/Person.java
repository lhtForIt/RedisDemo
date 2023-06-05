package com.lht.redisdemo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author lianght1
 * @date 2023/6/1
 */
@Data
@AllArgsConstructor
public class Person {
    private String name;
    private int age;
    private String sex;
}
