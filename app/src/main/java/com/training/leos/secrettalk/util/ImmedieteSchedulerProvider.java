package com.training.leos.secrettalk.util;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 *
 */

public class ImmedieteSchedulerProvider implements BaseSchedulerProvider {
    private static ImmedieteSchedulerProvider INSTANCE;

    private ImmedieteSchedulerProvider(){

    }

    public static synchronized  ImmedieteSchedulerProvider getInstance(){
        if (INSTANCE == null){
            INSTANCE = new ImmedieteSchedulerProvider();
        }
        return INSTANCE;
    }

    @Override
    public Scheduler computation() {
        return Schedulers.trampoline();
    }

    @Override
    public Scheduler io() {
        return Schedulers.trampoline();
    }

    @Override
    public Scheduler ui() {
        return Schedulers.trampoline();
    }
}
