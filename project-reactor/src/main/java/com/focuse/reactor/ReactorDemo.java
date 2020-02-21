package com.focuse.reactor;

import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author ：focuse
 * @date ：Created in 2020/2/19 下午12:36
 * @description：
 * @modified By：
 */
public class ReactorDemo {

    public static void main(String[] args) {
        Flux<String> seq1 = Flux.just("foo", "bar", "foobar");
        seq1.subscribe(i -> {
            System.out.println(i + "1");
        });
        seq1.subscribe(i -> {
            System.out.println(i);
        });

        ReentrantLock lock = new ReentrantLock();
        lock.lock();
        try {
            lock.wait();
        }catch (InterruptedException e) {

        }
    }

}
