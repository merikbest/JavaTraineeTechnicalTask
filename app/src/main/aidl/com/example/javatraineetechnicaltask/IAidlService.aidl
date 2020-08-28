package com.example.javatraineetechnicaltask;

import com.example.javatraineetechnicaltask.IAidlServiceCallback;

interface IAidlService {
    void registerCallback(IAidlServiceCallback callback);
    void unregisterCallback(IAidlServiceCallback callback);
}
