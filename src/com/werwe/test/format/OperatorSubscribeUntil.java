package com.werwe.test.format;

import rx.Observable;
import rx.Subscriber;
import rx.observers.SerializedSubscriber;

/**
 * Created by werwe on 2016. 6. 17..
 */
public class OperatorSubscribeUntil<T, R> implements Observable.Operator<T, T> {

    private final Observable<? extends R> other;

    public OperatorSubscribeUntil(final Observable<? extends R> other) {
        this.other = other;
    }

    @Override
    public Subscriber<? super T> call(Subscriber<? super T> child) {

        final Subscriber<T> parent = new SerializedSubscriber<T>(child);
        other.unsafeSubscribe(new Subscriber<R>(child) {
            @Override
            public void onCompleted() {
                parent.unsubscribe();
            }

            @Override
            public void onError(Throwable throwable) {
                parent.onError(throwable);
            }

            @Override
            public void onNext(R r) {
                parent.unsubscribe();
            }
        });
        return parent;
    }
}
