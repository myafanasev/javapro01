package ru.innotech;

import ru.innotech.annotation.*;

public class TestClass {

    @Test(priority = 3)
    public void methodTets01(){System.out.println("Обычный метод 01. Без параметров");}

    @Test(priority = 1)
    @BeforeSuite
    protected void methodTets02(){System.out.println("Обычный метод 02. Без параметров");}

    @Test(priority = 20)
    @AfterSuite
    void methodTets03(){System.out.println("Обычный метод 03. Без параметров");}

    @Test
    private void methodTets04(){System.out.println("Обычный метод 04. Без параметров");}

    @Test
    @BeforeSuite
    private static void methodStaticTets01(){System.out.println("Статический метод 01. Без параметров");}

    @BeforeSuite
    private static void methodStaticTets02(){System.out.println("Статический метод 02. Без параметров");}

    @AfterSuite
    private static void methodStaticTets03(){System.out.println("Статический метод 03. Без параметров");}

    @AfterSuite
    private static void methodStaticTets04(){System.out.println("Статический метод 04. Без параметров");}

    @BeforeTest
    private static void methodStaticTets05(){System.out.println("Статический метод 05. Без параметров. BeforeTest");}
    @AfterTest
    private void methodTets05(){System.out.println("Обычный метод 05. Без параметров. AfterTest");}

    @AfterTest
    private static void methodStaticTets06(){System.out.println("Статический метод 06. Без параметров. AfterTest");}
    @BeforeTest
    @CsvSource("10,Тест,2.15")
    private void methodTets06(int a, String s, float d){System.out.println("Обычный метод 06. С параметрами: a = " + a + ", s = " + s + ", d = "+ d +". BeforeTest");}


}
