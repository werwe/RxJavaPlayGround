package com.werwe.test.format;

import rx.Observable;
import rx.exceptions.Exceptions;
import rx.schedulers.Schedulers;

/**
 * Created by werwe on 2016. 11. 29..
 */
public class MapTest {

    public static void main(String[] args) {
        conCatTest();
    }

    public static void mapTest() {
        final int[] i = {0};
        final int[] ir = {0};
        Observable.just("hello")
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .map(s -> {
                    i[0]++;
                    System.out.println("s:" + s);
                    System.out.println("pass:" + i[0] + "/" + Thread.currentThread().getName());
                    if (i[0] >= 0 && i[0] < 3) {
                        Exceptions.propagate(new Exception("test ex"));
                    }
                    return Observable.from(new String[]{"pass:" + i[0]});

                })
                .retry(5)
                //.onErrorResumeNext(throwable -> Observable.just("test1","test2"))
                .map(r -> {

                    ir[0]++;
                    System.out.println("r pass:" + ir[0] + "/" + Thread.currentThread().getName());
                    if (ir[0] < 2)
                        Exceptions.propagate(new Exception("test ex1"));
                    return Observable.just("r pass:" + ir[0]);
                }).retry(5)
                .subscribe(stringObservable -> {
                    System.out.println("sub:" + stringObservable);
                }, throwable -> {

                });
    }

    public static void conCatTest() {
        firstOperation(false)
                .concatMap(o -> Observable.just(listOperation().toBlocking().last()))
//                .concatMap(o -> listOperation())
                .subscribe(o -> {
                    System.out.println("result:" + o);
                }, throwable -> {
                    System.out.println(throwable.toString());
                });
    }

    public static Observable firstOperation(boolean error) {
        return Observable.create(subscriber -> {
            if (error)
                subscriber.onError(new Exception("error"));
            else
                subscriber.onNext("normal");

            subscriber.onCompleted();
        });
    }

    public static Observable listOperation() {
        return Observable.from(new String[]{"1", "2", "3"})
                .concatMap(s -> longOperation(s).onErrorResumeNext(throwable -> Observable.empty()));


    }

    public static Observable<String> longOperation(String s) {
        return Observable.create(subscriber -> {
            subscriber.onStart();

            if (s.equals("2")) {
                subscriber.onError(new Exception("dubby"));
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            subscriber.onNext("timer task : " + s);
            subscriber.onCompleted();


        });
    }

    ;


}
