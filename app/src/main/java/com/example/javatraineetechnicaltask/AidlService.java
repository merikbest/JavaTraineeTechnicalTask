package com.example.javatraineetechnicaltask;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

public class AidlService extends Service {
    final RemoteCallbackList<IAidlServiceCallback> callbackList = new RemoteCallbackList<>();
    private static final int MESSAGE = 1;
    private int mValue = 0;

    public AidlService() {
    }

    @Override
    public void onCreate() {
        mHandler.sendEmptyMessage(MESSAGE);
    }

    @Override
    public void onDestroy() {
        callbackList.kill();
        mHandler.removeMessages(MESSAGE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return serviceBinder;
    }

    private final IAidlService.Stub serviceBinder = new IAidlService.Stub() {
        @Override
        public void registerCallback(IAidlServiceCallback callback) throws RemoteException {
            if (callback != null) callbackList.register(callback);
        }

        @Override
        public void unregisterCallback(IAidlServiceCallback callback) throws RemoteException {
            if (callback != null) callbackList.unregister(callback);
        }
    };

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MESSAGE) {
                int value = ++mValue;
                final int number = callbackList.beginBroadcast();

                for (int i = 0; i < number; i++) {
                    try {
                        callbackList.getBroadcastItem(i).onValueChanged(value);
                    } catch (RemoteException e) {

                    }
                }
                callbackList.finishBroadcast();

                sendMessageDelayed(obtainMessage(MESSAGE), 5000);
            } else {
                super.handleMessage(msg);
            }
        }
    };
}
