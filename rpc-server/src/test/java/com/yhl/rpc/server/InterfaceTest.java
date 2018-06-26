package com.yhl.rpc.server;

import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by yuhongliang on 18-7-25.
 */
public class InterfaceTest {
    public interface ITestInterface {
        void hello(String data);
    }

    public static class TestClass {
        public void hello(String data, Map<String, Integer> map) {

        }

        private void prvHello() {

        }
    }

    @Test
    public void testGetMethods() {
        Method[] methods = ITestInterface.class.getMethods();
        Method[] declaredMethods = ITestInterface.class.getDeclaredMethods();
        System.out.println(methods.length);
        System.out.println(declaredMethods.length);
    }

    @Test
    public void testGetMethods2() {
        Method[] methods = TestClass.class.getMethods();
        Method[] declaredMethods = TestClass.class.getDeclaredMethods();
        printMethod(methods);
        System.out.println("===============");
        printMethod(declaredMethods);
    }

    @Test
    public void testMethodParamType() {
        Method[] declaredMethods = TestClass.class.getDeclaredMethods();
        printMethod(declaredMethods);
    }

    public void printMethod(Method[] methods) {
        for (Method method : methods) {
            System.out.println("----------");
            System.out.println(method.getName());
            Class<?>[] paramTypes = method.getParameterTypes();
            for (Class<?> one : paramTypes) {
                System.out.println(one.getName());
            }
        }
    }
}
