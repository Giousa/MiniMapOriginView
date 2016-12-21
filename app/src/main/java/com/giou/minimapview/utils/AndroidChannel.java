package com.giou.minimapview.utils;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

/**
 * Created by liuenbao on 1/23/16.
 */
public class AndroidChannel {

    public interface UiCallback {
        boolean handleUiMessage(Message msg);
    }

    public interface WorkerCallback {
        boolean handleWorkerMessage(Message msg);
    }

    HandlerThread mWorkerThread            = null;

    Handler mMainThreadHandler       = null;
    Handler mWorkerThreadHandler     = null;

    UiCallback      mUiCallback              = null;
    WorkerCallback  mWorkerCallback          = null;

    boolean mIsChannelOpened                 = false;

    /**
     * Create channel and open channel
     * @param uiCallback        handler callback for ui messages
     * @param workerCallback    handler callback for worker messages
     */
    public AndroidChannel(final UiCallback uiCallback, final WorkerCallback workerCallback) {

        this.mUiCallback = uiCallback;
        this.mWorkerCallback = workerCallback;

        open();
    }

    /**
     * To send message to ui thread, You should get mainThreadHandler by using toUI() method
     * @return main thread handler
     */
    public Handler toUI() {
        return mMainThreadHandler;
    }

    /**
     * To send message to worker thread, You should get workerThreadHandler by using toWorker() method
     * @return worker thread handler
     */
    public Handler toWorker() {
        return mWorkerThreadHandler;
    }

    /**
     * Open channel
     * @return true if success to open channel, or return false
     */
    public boolean open() {

        if(mIsChannelOpened == true) {
            return true;
        }

        if(mUiCallback == null || mWorkerCallback == null)
            return false;

        mMainThreadHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                return mUiCallback.handleUiMessage(msg);
            }
        });

        mWorkerThread = new HandlerThread("channel-worker-thread");
        mWorkerThread.start();
        mWorkerThreadHandler = new Handler(mWorkerThread.getLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                return mWorkerCallback.handleWorkerMessage(msg);
            }
        });

        mIsChannelOpened = true;
        return true;
    }

    /**
     * Close channel
     */
    public void close() {
        if(mIsChannelOpened == false)
            return;

        mMainThreadHandler.removeCallbacksAndMessages(null);
        mWorkerThreadHandler.removeCallbacksAndMessages(null);

        mWorkerThread.quit();
        mWorkerThread = null;

        mWorkerThreadHandler = null;
        mMainThreadHandler = null;

        mIsChannelOpened = false;
    }
}
