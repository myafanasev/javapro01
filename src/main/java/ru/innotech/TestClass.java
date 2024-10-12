package ru.innotech;

import ru.innotech.annotation.Test;

public class TestClass {

    @Test(priority = 3)
    public void methodTets01(){System.out.println("Обычный метод 01. Без параметров");}

    @Test(priority = 1)
    protected void methodTets02(int a){System.out.println("Обычный метод 02. Параметр 1 = " + a);}

    @Test(priority = 20)
    void methodTets03(){System.out.println("Обычный метод 03. Без параметров");}

    @Test
    private void methodTets04(int a, String s){System.out.println("Обычный метод 04. Параметр 1 = " + a + ", параметр 2 = " + s);}

    private static void methodStaticTets01(){System.out.println("Статический метод 01. Без параметров");}


}
