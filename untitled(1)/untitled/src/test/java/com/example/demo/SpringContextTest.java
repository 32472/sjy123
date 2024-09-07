package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(com.example.demo.SpringExtension.class)
@SpringBootTest
public class SpringContextTest {

    @Test
    public void contextLoads() {
        // 这个测试只是确认 Spring 应用上下文能够正确加载
        assertTrue(true);
    }
}