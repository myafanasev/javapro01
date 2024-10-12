package ru.innotech;

import lombok.SneakyThrows;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TestRunner {
    public static void main(String[] args) {
        TestClass testClass = new TestClass();
        Class<?> cls = testClass.getClass();
        runTests(cls);
    }
    @SneakyThrows
    static void runTests(Class c) {
        Object obj = c.getDeclaredConstructor().newInstance(); // создадим объект
        Method[] methods = c.getDeclaredMethods();    // получим список методов класса

        for (Method m : methods) {
            System.out.println(m.getName());
            m.setAccessible(true);
            try {
                m.invoke(obj);
            } catch (InvocationTargetException e) {
                System.out.println(e.getMessage());
            }
        }
        System.out.println(c.getName());
    }
}
