package com.training.leos.secrettalk.util;

import io.reactivex.Scheduler;

/**
 * Created by Leo on 04/12/2017.
 */

public interface BaseSchedulerProvider {
    Scheduler computation();
    Scheduler io();
    Scheduler ui();
}
