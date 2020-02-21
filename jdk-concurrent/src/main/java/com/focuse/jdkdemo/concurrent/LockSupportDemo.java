package com.focuse.jdkdemo.concurrent;

import java.util.concurrent.locks.LockSupport;

/**
 * @author ：focuse
 * @date ：Created in 2020/2/16 上午11:10
 * @description：
 * @modified By：
 */
public class LockSupportDemo {
    private static Thread threadA1 = new Thread() {
        public void  run() {
            System.out.println("A1: before park");
            LockSupport.park();
            System.out.println("A1: after unpark ");
        }
    };


    private static Thread threadA2 = new Thread() {
        public void  run() {
            //睡一小会 保证A1先执行
            try {
                Thread.currentThread().sleep(50);
            }catch (InterruptedException e) {
                System.out.println("A2 interrupted");
            }
            System.out.println("A2: before unpark A1");
            try {
                Thread.currentThread().sleep(2000);
            }catch (InterruptedException e) {
                System.out.println("A2 interrupted");
            }
            System.out.println("A2: 2s later");
        }
    };


    /**
     *  A1 call park, then main call unpark(A1)
     *  期望:
     *     A1: before park
     *     A2: before unpark A1
     *     A2: 2s later
     *     A1: after park
     */
    protected static void testPark1() {
        threadA1.start();
        threadA2.start();
        try {
            Thread.currentThread().sleep(5000);
        }catch (InterruptedException e) {
            System.out.println("main interrupted");
        }
        LockSupport.unpark(threadA1);
    }

    private static Thread threadB1 = new Thread() {
        public void  run() {
            //睡一小会 保证main可以先执行unpark
            try {
                Thread.currentThread().sleep(50);
            }catch (InterruptedException e) {
                System.out.println("B1 interrupted");
            }
            System.out.println("B1: before park");
            LockSupport.park();
            //LockSupport.park();
            System.out.println("B1: after unpark ");
        }
    };


    private static Thread threadB2 = new Thread() {
        public void  run() {
            //比B1多睡50ms 保证B1先执行
            try {
                Thread.currentThread().sleep(100);
            }catch (InterruptedException e) {
                System.out.println("B2 interrupted");
            }
            System.out.println("B2: before unpark B1");
            try {
                Thread.currentThread().sleep(2000);
            }catch (InterruptedException e) {
                System.out.println("B2 interrupted");
            }
            System.out.println("B2: 2s later");
        }
    };
    /**
     *  main call unpark(B1), then B1 call park
     *  期望：
     *  B1: before park
     *  B1: after unpark
     *  B2: before unpark B1
     *  B2: 2s later
     */
    protected static void testPark2() {
        threadB1.start();
        //C睡50ms 保证unpark先调用
        LockSupport.unpark(threadB1);
        //LockSupport.unpark(threadC);
        threadB2.start();
    }


    private static Thread threadC1 = new Thread() {
        public void  run() {
            System.out.println("C1: before park");
            LockSupport.parkNanos(3000000000l);
            System.out.println("C1: after unpark 3s later");
        }
    };


    private static Thread threadC2 = new Thread() {
        public void  run() {
            //比C1多睡50ms 保证C1先执行
            try {
                Thread.currentThread().sleep(50);
            }catch (InterruptedException e) {
                System.out.println("C2 interrupted");
            }
            System.out.println("C2: before unpark C1");
            try {
                Thread.currentThread().sleep(2000);
            }catch (InterruptedException e) {
                System.out.println("C2 interrupted");
            }
            System.out.println("C2: 2s later");
        }
    };

    /**
     * C1 call parkNanos(3000000), then main call unpark(C1) 5s later
     *  期望：
     *  C1: before park
     *  C2: before unpark C1
     *  C2: 2s later
     *  C1: after unpark 3s later
     *  main: 5s later
     */
    protected static void testParkNanos() {
        threadC1.start();
        threadC2.start();
        try {
            Thread.currentThread().sleep(5000);
        }catch (InterruptedException e) {
            System.out.println("main interrupted");
        }
        System.out.println("main: 5s later");
        LockSupport.unpark(threadC1);
    }

    private static Thread threadD1 = new Thread() {
        public void  run() {
            System.out.println("D1: before park");
            //3s后
            Long deadLine = System.currentTimeMillis() + 3000;
            //过去的一个时间 则立即park不阻塞
            //Long deadLine = System.currentTimeMillis() - 1000;
            LockSupport.parkUntil(deadLine);
            System.out.println("D1: after unpark 3s later");
        }
    };


    private static Thread threadD2 = new Thread() {
        public void  run() {
            //比D1多睡50ms 保证D1先执行
            try {
                Thread.currentThread().sleep(50);
            }catch (InterruptedException e) {
                System.out.println("D2 interrupted");
            }
            System.out.println("D2: before unpark D1");
            try {
                Thread.currentThread().sleep(2000);
            }catch (InterruptedException e) {
                System.out.println("D2 interrupted");
            }
            System.out.println("D2: 2s later");
        }
    };

    /**
     * D1 call parkUtil(currentTime + 3000), then main call unpark(D1) 5s later
     *  期望：
     *  D1: before park
     *  D2: before unpark D1
     *  D2: 2s later
     *  D1: after unpark 3s later
     *  main: 5s later
     */
    protected static void testParkUtil() {
        threadD1.start();
        threadD2.start();
        try {
            Thread.currentThread().sleep(5000);
        }catch (InterruptedException e) {
            System.out.println("main interrupted");
        }
        System.out.println("main: 5s later");
        LockSupport.unpark(threadD1);
    }

    public static void main(String[] args) {
        System.out.println("**************");
        LockSupportDemo.testPark1();

        //睡6s保证 下面test不影响前面的test输出
        try {
            Thread.currentThread().sleep(6000);
        }catch (InterruptedException e) {
            System.out.println("main interrupted");
        }

        System.out.println("**************");
        LockSupportDemo.testPark2();

        //睡6s保证 下面test不影响前面的test输出
        try {
            Thread.currentThread().sleep(6000);
        }catch (InterruptedException e) {
            System.out.println("main interrupted");
        }

        System.out.println("**************");
        LockSupportDemo.testParkNanos();

        //睡6s保证 下面test不影响前面的test输出
        try {
            Thread.currentThread().sleep(6000);
        }catch (InterruptedException e) {
            System.out.println("main interrupted");
        }

        System.out.println("**************");
        LockSupportDemo.testParkUtil();
    }
}
