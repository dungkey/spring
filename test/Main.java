package test;

import core.AutoWired;
import core.BeanFactory;

public class Main {

    @AutoWired
    private TestService testService;

    public static void main(String[] args) {
        BeanFactory.scan("test");
        TestService testService = (TestService) BeanFactory.getBean("TestService");
        testService.test();
    }
}