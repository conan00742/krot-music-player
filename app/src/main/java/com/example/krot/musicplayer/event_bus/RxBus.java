package com.example.krot.musicplayer.event_bus;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by Krot on 1/29/18.
 */

public class RxBus {
    private static RxBus bus;
    private final Subject<Object> mSubjectBus;

    private RxBus() {
        mSubjectBus = PublishSubject.create().toSerialized();
    }

    public static RxBus getInstance() {
        if (bus == null) {
            bus = new RxBus();
        }

        return bus;
    }

    public void send(Object o) {
        mSubjectBus.onNext(o);
    }

    public Observable<Object> toObserverable() {
        return mSubjectBus;
    }
}
