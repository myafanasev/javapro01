package ru.innotech;

import lombok.SneakyThrows;
import ru.innotech.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;

public class TestRunner {
    public static void main(String[] args) {
        TestClass testClass = new TestClass();
        Class<?> cls = testClass.getClass();
        runTests(cls);
    }

    @SneakyThrows
    static void invokeMethod(Method m, Object obj, List<String> errors) {

        Class[] classParams = m.getParameterTypes(); // получим список типов параметров метода
        List<String> params = new ArrayList<>();
        Object[] arguments = new Object[classParams.length]; // подготовим массив аргументов

        if (m.isAnnotationPresent(CsvSource.class)) // если есть аннотация CsvSource, обработаем её
            params = Arrays.stream(m.getAnnotation(CsvSource.class).value().split(",")).toList();

        if (classParams.length != params.size())
            errors.add("Для метода " + m.getName() + " передано неверное количество аргументов в аннотации @CsvSource");
        else {
            // формируем список аргументов
            for (int i = 0; i < classParams.length; i++) {
                switch (classParams[i].getSimpleName()) {
                    // для примитивных типов и их обвёрток нужно использовать valueOf
                    case "boolean", "Boolean": arguments[i] = Boolean.valueOf(params.get(i)); break;
                    case "int", "Integer": arguments[i] = Integer.valueOf(params.get(i)); break;
                    case "long", "Long": arguments[i] = Long.valueOf(params.get(i)); break;
                    case "double", "Double": arguments[i] = Double.valueOf(params.get(i)); break;
                    case "float", "Float": arguments[i] = Float.valueOf(params.get(i)); break;
                    // для остальных просто кастуем
                    default: arguments[i] = classParams[i].cast(params.get(i));
                }
            }

            m.setAccessible(true);

            try {
                m.invoke(obj, arguments);
            } catch (InvocationTargetException e) {
                errors.add("Ошибка при выполнении метода " + m.getName() + ": " + e.getMessage());
            }
        }
    }
    @SneakyThrows
    static void runTests(Class c) {
        Method methodBeforeSuite = null;
        Method methodAfterSuite = null;
        TreeMap<Integer, Method> methodsTest = new TreeMap<>(Comparator.reverseOrder()); // отсортированный в обратном порядке список для тестовых методов
        List<String> errors = new ArrayList<>(); // список ошибок
        List<Method> methodsBeforeTest = new ArrayList<>(); // список методов, которые должны быть выполнены перед тестом
        List<Method> methodsAfterTest = new ArrayList<>(); // список методов, которые должны быть выполнены после теста

        Object obj = c.getDeclaredConstructor().newInstance(); // создадим объект
        Method[] methods = c.getDeclaredMethods();    // получим список методов класса

        for (Method m : methods) { // выполним анализ и проверку методов
            if (m.isAnnotationPresent(Test.class))
                if (m.getAnnotation(Test.class).priority() >= 1 && m.getAnnotation(Test.class).priority() <= 10)
                    if (!Modifier.isStatic(m.getModifiers())) methodsTest.put(m.getAnnotation(Test.class).priority(), m);
                    else errors.add("Аннотация @Test не может быть применена к методу " + m.getName() + ", так как он статичный");
                else
                    errors.add("У метода " + m.getName() + " установлен некорректный приоритет");

            if (m.isAnnotationPresent(BeforeSuite.class))
                if (methodBeforeSuite == null)
                    if (Modifier.isStatic(m.getModifiers())) methodBeforeSuite = m;
                    else errors.add("Аннотация @BeforeSuite не может быть применена к методу " + m.getName() + ", так как он не статичный");
                else errors.add("Для метода " + m.getName() + " не может быть применена аннотация @BeforeSuite, так как уже есть метод " + methodBeforeSuite.getName() + " с такой аннотацией");

            if (m.isAnnotationPresent(AfterSuite.class))
                if (methodAfterSuite == null)
                    if (Modifier.isStatic(m.getModifiers()))  methodAfterSuite = m;
                    else errors.add("Аннотация @AfterSuite не может быть применена к методу " + m.getName() + ", так как он не статичный");
                else errors.add("Для метода " + m.getName() + " не может быть применена аннотация @AfterSuite, так как уже есть метод " + methodAfterSuite.getName() + " с такой аннотацией");

            if (m.isAnnotationPresent(BeforeTest.class)) methodsBeforeTest.add(m);

            if (m.isAnnotationPresent(AfterTest.class)) methodsAfterTest.add(m);
        }

        // выполнение методов
        if (methodBeforeSuite != null) invokeMethod(methodBeforeSuite, obj, errors); // метод с аннотацией @BeforeSuite

        for(Integer key : methodsTest.keySet()) { // выполняем методы с аннотациями @Test, @BeforeTest и @AfterTest
            for (Method m : methodsBeforeTest)
                invokeMethod(m, obj, errors); // методы с аннотацией @BeforeTest

            invokeMethod(methodsTest.get(key), obj, errors); // метод с аннотацией @Test

            for (Method m : methodsAfterTest)
                invokeMethod(m, obj, errors); // методы с аннотацией @AfterTest
        }

        if (methodAfterSuite != null) invokeMethod(methodAfterSuite, obj, errors); // метод с аннотацией @AfterSuite

        // выводим информацию об ошибках
        System.out.println("Информация об ошибках:");
        for(String s : errors)
            System.out.println(s);
    }
}
