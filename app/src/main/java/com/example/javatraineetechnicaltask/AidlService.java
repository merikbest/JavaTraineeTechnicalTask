package com.example.javatraineetechnicaltask;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

public class AidlService extends Service {
    final RemoteCallbackList<IAidlServiceCallback> callbackList = new RemoteCallbackList<>();

    public AidlService() {
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
}
