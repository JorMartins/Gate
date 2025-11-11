package br.com.fiap.gate

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class NotificationService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        Log.d("NotificationService", "✅ Serviço de notificação criado")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("NotificationService", "✅ Serviço de notificação ativo")
        return START_STICKY
    }
}