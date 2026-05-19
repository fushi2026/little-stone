package com.fushi;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class LombokTest {
    private String testField;
    
    public static void main(String[] args) {
        LombokTest test = new LombokTest();
        test.setTestField("Hello");
        System.out.println(test.getTestField());
        log.info("Lombok is working!");
    }
}
