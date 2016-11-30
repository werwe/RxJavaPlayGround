package com.werwe.test.format;

import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.TestScheduler;

import java.util.concurrent.TimeUnit;

public class Main {

    public <T> TestSubscriber<T> testSubscriber(){
        return new TestSubscriber<T>(){
            @Override
            public void onNext(T t) {
                super.onNext(t);
                System.out.println("onNext");
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                System.out.println("onError");
            }

            @Override
            public void onCompleted() {
                super.onCompleted();
                System.out.println("onComplete");
            }
        };
    }

    @Test
    public void testInterval() {
        TestSubscriber<Long> testSubscriber = testSubscriber();
        TestScheduler scheduler = new TestScheduler();

        Observable.interval(1000, TimeUnit.MILLISECONDS,scheduler)
                .observeOn(scheduler)
                .subscribeOn(scheduler)
                .subscribe(testSubscriber);
        scheduler.advanceTimeBy(1,TimeUnit.SECONDS);

    }

    @Test
    public void test1_ContrivedExample() {
        TestSubscriber<Long> testSubscriber = testSubscriber();
        TestScheduler scheduler = new TestScheduler();
        Observable.interval(100, TimeUnit.MILLISECONDS,scheduler)
                .takeUntil(Observable.timer(1, TimeUnit.SECONDS))
                .observeOn(scheduler)
                .subscribeOn(scheduler)
                .subscribe(
                        testSubscriber
                );
        scheduler.advanceTimeBy(10,TimeUnit.SECONDS);
    }

    @Test
    public void test2_ContrivedExample() {
        TestSubscriber<Long> testSubscriber = testSubscriber();
        TestScheduler scheduler = new TestScheduler();
        Observable.interval(100, TimeUnit.MILLISECONDS , scheduler)
                .lift(new OperatorSubscribeUntil(Observable.timer(1, TimeUnit.SECONDS)))
                .observeOn(scheduler)
                .subscribeOn(scheduler)
                .subscribe(
                        i -> System.out.println("onNext"),
                        e -> System.out.println("onError"),
                        () -> System.out.println("onComplete")
                );
        scheduler.advanceTimeBy(10,TimeUnit.SECONDS);
    }
}
