package com.utils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ACT-NJ on 2017/7/25.
 */
public class TimerWorker {
    public static void main(String[] args) {
        new TimerWorker().timer();
    }

    private void timer() {
        long period = 6*3600*1000;    //8小时为间隔
        long delay = 2*1000;    //2'延迟
        Timer _timer = new Timer();
        TimerTask _task =new TimerTask() {
            @Override
            public void run() {
                //do something here
            }
        };
        _timer.schedule(_task, delay, period);
    }
}
