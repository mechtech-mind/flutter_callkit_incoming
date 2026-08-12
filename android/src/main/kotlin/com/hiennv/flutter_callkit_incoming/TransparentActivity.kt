package com.hiennv.flutter_callkit_incoming

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.wowphonedev.softphone.CallNotificationService

class TransparentActivity : Activity() {

    companion object {
        private const val TAG = "TransparentActivity"

        var isVisible: Boolean = false

        fun getIntent(
            context: Context,
            action: String,
            data: Bundle?
        ): Intent {
            val intent = Intent(
                context,
                TransparentActivity::class.java
            )

            intent.action = action
            intent.putExtra("data", data)

            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)

            return intent
        }
    }

    override fun onStart() {
        super.onStart()

        Log.d(
            TAG,
            "onStart() action=${intent?.action}"
        )

        setVisible(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "========================================")
        Log.d(TAG, "TransparentActivity.onCreate()")
        Log.d(TAG, "intent.action=${intent?.action}")

        val action = intent?.action

        if (action == null) {
            Log.w(
                TAG,
                "Intent action is NULL. Finishing."
            )

            finish()
            return
        }

        val data = intent.getBundleExtra("data")

        Log.d(
            TAG,
            "Received action=$action"
        )

        Log.d(
            TAG,
            "Received data=$data"
        )

        if (data != null) {
            for (key in data.keySet()) {
                Log.d(
                    TAG,
                    "data[$key]=${data.get(key)}"
                )
            }
        }

        // ============================================================
        // 1. PRESERVE THE ORIGINAL CALLKIT BROADCAST PATH
        // ============================================================

        Log.d(
            TAG,
            "Sending broadcast to CallkitIncomingBroadcastReceiver"
        )

        val broadcastIntent =
            CallkitIncomingBroadcastReceiver.getIntent(
                this,
                action,
                data
            )

        broadcastIntent.addFlags(
            Intent.FLAG_RECEIVER_FOREGROUND
        )

        sendBroadcast(broadcastIntent)

        Log.d(
            TAG,
            "CallkitIncomingBroadcastReceiver broadcast sent"
        )

        // ============================================================
        // 2. START OUR SECONDARY FLUTTER ENGINE
        // ============================================================

        Log.d(
            TAG,
            "Starting CallNotificationService"
        )

        val activityIntent = Intent(
            this,
            CallNotificationService::class.java
        ).apply {

            this.action = action

            putExtra(
                FlutterCallkitIncomingPlugin.EXTRA_CALLKIT_CALL_DATA,
                data
            )

            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
            )
        }

        startActivity(activityIntent)

        Log.d(
            TAG,
            "CallNotificationService started"
        )

        finish()
        overridePendingTransition(0, 0)

        Log.d(TAG, "TransparentActivity finished")
        Log.d(TAG, "========================================")
    }
}