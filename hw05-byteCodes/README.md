# Домашнее задание к лекции 10: Автоматическое логирование

## Цель: Понять как реализуется AOP, какие для этого есть технические средства

## Описание/Пошаговая инструкция выполнения домашнего задания:

1) Разработайте такой функционал:
   метод класса можно пометить самодельной аннотацией @Log, например, так:
   ```
   class TestLogging implements TestLoggingInterface {
       @Log
       public void calculation(int param) {};
   }
2) При вызове этого метода "автомагически" в консоль должны логироваться значения параметров.
   Например так:
   ```
   class Demo {
       public void action() {
           new TestLogging().calculation(6);
       }
   }
3) В консоле дожно быть:
   ```
   executed method: calculation, param: 6
4) Обратите внимание: явного вызова логирования быть не должно.
5) Учтите, что аннотацию можно поставить, например, на такие методы:
   ```
   public void calculation(int param1)
   public void calculation(int param1, int param2)
   public void calculation(int param1, int param2, String param3)
6) P.S.: Выбирайте реализацию с ASM, если действительно этого хотите и уверены в своих силах

