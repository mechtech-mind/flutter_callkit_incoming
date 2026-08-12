package com.wowphonedev.softphone

import android.os.Bundle
import android.util.Log
import com.hiennv.flutter_callkit_incoming.FlutterCallkitIncomingPlugin
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class CallNotificationService : FlutterActivity() {

    companion object {
        private const val TAG = "CallNotificationService"

        private const val CHANNEL = "call_notification_service"
        private const val GET_CALL_DATA = "getCallData"
        private const val CALL_ACCEPTED = "CALL_ACCEPTED_INTENT"
    }

    private var flutterChannel: MethodChannel? = null

    override fun getDartEntrypointFunctionName(): String {
        return "callNotificationMain"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "CallNotificationService started")

        val action = intent?.action
        val data = intent?.getBundleExtra(
            FlutterCallkitIncomingPlugin.EXTRA_CALLKIT_CALL_DATA
        )

        Log.d(TAG, "Received action=$action")
        Log.d(TAG, "Call data available=${data != null}")
    }

    override fun configureFlutterEngine(
        flutterEngine: FlutterEngine
    ) {
        super.configureFlutterEngine(flutterEngine)

        flutterChannel = MethodChannel(
            flutterEngine.dartExecutor.binaryMessenger,
            CHANNEL
        )

        flutterChannel?.setMethodCallHandler { call, result ->

            when (call.method) {
                GET_CALL_DATA -> {
                    val data = intent?.getBundleExtra(
                        FlutterCallkitIncomingPlugin
                            .EXTRA_CALLKIT_CALL_DATA
                    )

                    if (data == null) {
                        Log.w(TAG, "Call data requested but not available")
                        result.success(null)
                        return@setMethodCallHandler
                    }

                    result.success(bundleToMap(data))
                }

                else -> {
                    result.notImplemented()
                }
            }
        }

        // Forward the CallKit action to the secondary Flutter engine.
        forwardIntentToFlutter(intent)
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)

        setIntent(intent)

        forwardIntentToFlutter(intent)
    }

    private fun forwardIntentToFlutter(
        incomingIntent: android.content.Intent?
    ) {
        if (incomingIntent == null) {
            Log.w(TAG, "Cannot forward CallKit action: intent is null")
            return
        }

        val action = incomingIntent.action ?: return

        val data = incomingIntent.getBundleExtra(
            FlutterCallkitIncomingPlugin.EXTRA_CALLKIT_CALL_DATA
        )

        val map = if (data != null) {
            bundleToMap(data)
        } else {
            emptyMap()
        }

        Log.d(
            TAG,
            "Forwarding CallKit action to Flutter: $action"
        )

        flutterChannel?.invokeMethod(
            CALL_ACCEPTED,
            map
        )
    }

    private fun bundleToMap(
        bundle: Bundle
    ): Map<String, Any?> {
        val map = mutableMapOf<String, Any?>()

        for (key in bundle.keySet()) {
            val value = bundle.get(key)

            map[key] = when (value) {
                is Bundle -> bundleToMap(value)
                else -> value
            }
        }

        return map
    }
}