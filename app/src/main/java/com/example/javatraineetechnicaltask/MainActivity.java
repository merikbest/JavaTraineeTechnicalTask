package com.example.javatraineetechnicaltask;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {
    private static final int BUMP_MSG = 1;

    IAidlService aidlService = null;

    TextView callbackText;

    private InternalHandler handler;
    private boolean isBound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.bind);
        button.setOnClickListener(mBindListener);

        button = findViewById(R.id.unbind);
        button.setOnClickListener(unbindListener);

        callbackText = findViewById(R.id.callback);
        callbackText.setText("Not attached.");
        handler = new InternalHandler(callbackText);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            aidlService = IAidlService.Stub.asInterface(service);
            callbackText.setText("Attached.");

            try {
                aidlService.registerCallback(mCallback);
            } catch (RemoteException e) {

            }
            Toast.makeText(MainActivity.this, R.string.remote_service_connected, Toast.LENGTH_SHORT).show();
        }

        public void onServiceDisconnected(ComponentName className) {
            aidlService = null;
            callbackText.setText("Disconnected.");

            Toast.makeText(MainActivity.this, R.string.remote_service_disconnected, Toast.LENGTH_SHORT).show();
        }
    };

    private View.OnClickListener mBindListener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, AidlService.class);
            intent.setAction(IAidlService.class.getName());
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            isBound = true;
            callbackText.setText("Binding.");
        }
    };

    private View.OnClickListener unbindListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (isBound) {
                if (aidlService != null) {
                    try {
                        aidlService.unregisterCallback(mCallback);
                    } catch (RemoteException e) {

                    }
                }

                unbindService(mConnection);
                isBound = false;
                callbackText.setText("Unbinding.");
            }
        }
    };

    private IAidlServiceCallback.Stub mCallback = new IAidlServiceCallback.Stub() {
        @Override
        public void onValueChanged(int value) throws RemoteException {
            handler.sendMessage(handler.obtainMessage(BUMP_MSG, value, 0));
        }
    };

    private static class InternalHandler extends Handler {
        private final WeakReference<TextView> weakTextView;

        InternalHandler(TextView textView) {
            weakTextView = new WeakReference<>(textView);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == BUMP_MSG) {
                TextView textView = weakTextView.get();
                if (textView != null) {
                    textView.setText("Received from service: " + msg.arg1);
                }
            } else {
                super.handleMessage(msg);
            }
        }
    }
}