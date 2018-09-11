package com.scanner.returm.retumscanner.utils.timer.util;

import android.os.CountDownTimer;

import com.scanner.returm.retumscanner.utils.timer.callback.TimeOutCallback;

public class Timer {
    private static final int COUNT_DOWN_INTERVAL = 1000;
    private TimeOutCallback timeOutCallback;
    private CountDownTimer countDownTimer;

    public Timer(long milliseconds, TimeOutCallback timeOutCallback) {
        this.timeOutCallback = timeOutCallback;
        initCountDown(milliseconds);
    }

    private void initCountDown(long milliseconds) {
        countDownTimer = new CountDownTimer(milliseconds, COUNT_DOWN_INTERVAL) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                timeOutCallback.onTimeOut();
            }
        };
    }

    public void start() {
        countDownTimer.start();
    }

    public void stop() {
        countDownTimer.cancel();
    }
}
