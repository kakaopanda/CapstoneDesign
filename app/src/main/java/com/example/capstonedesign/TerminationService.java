package com.example.capstonedesign;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;

public class TerminationService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        removeNotification();
        stopSelf();
    }

    private void removeNotification() {
        NotificationManagerCompat.from(this).cancel(1);
    }
}
