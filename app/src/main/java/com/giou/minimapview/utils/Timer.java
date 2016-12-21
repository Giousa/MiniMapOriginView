package com.giou.minimapview.utils;

import android.os.Message;

/**
 * Created by liuenbao on 1/23/16.
 */
public class Timer {

    private final int START_TIMER = 0;
    private final int STOP_TIMER  = 1;

    private AndroidChannel mAndroidChannel;
    private int mInterval=0;
    private OnTimer mOnTimer;

    volatile  boolean loop = false;

    public Timer(int interval, OnTimer onTimer) {
        this.mInterval = (interval < 0) ? (interval*-1) : (interval);
        this.mOnTimer = onTimer;

        mAndroidChannel = new AndroidChannel(new AndroidChannel.UiCallback() {
            @Override
            public boolean handleUiMessage(Message msg) {
                Timer.this.mOnTimer.onTime(Timer.this);
                return false;
            }
        }, new AndroidChannel.WorkerCallback() {

            Thread jobThread = null;

            @Override
            public boolean handleWorkerMessage(Message msg) {

                switch (msg.what) {
                    case START_TIMER:
                        loop = true;
                        break;
                    case STOP_TIMER:
                        loop = false;
                        jobThread = null;
                        break;
                }

                if(msg.what == START_TIMER && jobThread == null) {
                    jobThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (loop) {
                                try {
                                    Message msg = mAndroidChannel.toUI().obtainMessage();
                                    mAndroidChannel.toUI().sendMessage(msg);
                                    Thread.sleep(Timer.this.mInterval);
                                } catch (InterruptedException e) {
                                }
                            }
                        }
                    });
                }

                switch (msg.what) {
                    case START_TIMER:
                        loop = true;
                        jobThread.start();
                        break;
                    case STOP_TIMER:
                        loop = false;
                        jobThread = null;
                        break;
                }


                return false;
            }
        });

    }

    public void start() {
        mAndroidChannel.toWorker().sendEmptyMessage(START_TIMER);
    }

    public void stop() {
        mAndroidChannel.toWorker().sendEmptyMessage(STOP_TIMER);
    }

    public void resetInterval(int interval) {
        this.mInterval = interval;
    }

    public int getInterval() {
        return mInterval;
    }

    public boolean isAlive() {
        return loop;
    }

    public interface OnTimer {
        void onTime(Timer timer);
    }
}
