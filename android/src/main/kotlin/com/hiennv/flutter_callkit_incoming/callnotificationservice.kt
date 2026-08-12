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

        private const val CHANNEL =
            "call_notification_service"

        private const val GET_CALL_DATA =
            "getCallData"

        private const val CALL_ACCEPTED =
            "CALL_ACCEPTED_INTENT"
    }

    private var flutterChannel: MethodChannel? = null

    override fun getDartEntrypointFunctionName(): String {
        Log.d(
            TAG,
            "getDartEntrypointFunctionName() -> callNotificationMain"
        )

        return "callNotificationMain"
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        Log.d(TAG, "========================================")
        Log.d(TAG, "CallNotificationService.onCreate()")
        Log.d(TAG, "Intent action=${intent?.action}")

        val data = intent?.getBundleExtra(
            FlutterCallkitIncomingPlugin.EXTRA_CALLKIT_CALL_DATA
        )

        Log.d(
            TAG,
            "CALL DATA=$data"
        )

        if (data != null) {
            for (key in data.keySet()) {
                Log.d(
                    TAG,
                    "CALL DATA [$key] = ${data.get(key)}"
                )
            }
        }

        super.onCreate(savedInstanceState)

        Log.d(TAG, "super.onCreate() completed")
        Log.d(TAG, "========================================")
    }

    override fun configureFlutterEngine(
        flutterEngine: FlutterEngine
    ) {
        super.configureFlutterEngine(flutterEngine)

        Log.d(
            TAG,
            "configureFlutterEngine()"
        )

        flutterChannel = MethodChannel(
            flutterEngine.dartExecutor.binaryMessenger,
            CHANNEL
        )

        flutterChannel?.setMethodCallHandler { call, result ->

            Log.d(
                TAG,
                "Flutter -> Native: ${call.method}"
            )

            when (call.method) {

                GET_CALL_DATA -> {

                    Log.d(
                        TAG,
                        "Flutter requested call data"
                    )

                    val data = intent?.getBundleExtra(
                        FlutterCallkitIncomingPlugin
                            .EXTRA_CALLKIT_CALL_DATA
                    )

                    if (data == null) {

                        Log.w(
                            TAG,
                            "No call data available"
                        )

                        result.success(null)
                        return@setMethodCallHandler
                    }

                    val map = bundleToMap(data)

                    Log.d(
                        TAG,
                        "Returning call data to Flutter: $map"
                    )

                    result.success(map)
                }

                else -> {
                    result.notImplemented()
                }
            }
        }

        Log.d(
            TAG,
            "MethodChannel registered"
        )

        // ============================================================
        // IMPORTANT:
        // Forward the Android ACTION into the NEW Flutter engine.
        // ============================================================

        forwardIntentToFlutter(intent)
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)

        Log.d(
            TAG,
            "onNewIntent() action=${intent.action}"
        )

        setIntent(intent)

        forwardIntentToFlutter(intent)
    }

    private fun forwardIntentToFlutter(
        incomingIntent: android.content.Intent?
    ) {

        if (incomingIntent == null) {
            Log.w(
                TAG,
                "forwardIntentToFlutter(): intent is NULL"
            )
            return
        }

        val action = incomingIntent.action

        Log.d(
            TAG,
            "forwardIntentToFlutter() action=$action"
        )

        if (action == null) {
            Log.w(
                TAG,
                "No action to forward"
            )
            return
        }

        val data = incomingIntent.getBundleExtra(
            FlutterCallkitIncomingPlugin.EXTRA_CALLKIT_CALL_DATA
        )

        val map = mutableMapOf<String, Any?>()

        if (data != null) {
            map.putAll(bundleToMap(data))
        }

        Log.d(
            TAG,
            "Forwarding ACTION to Flutter"
        )

        Log.d(
            TAG,
            "action=$action"
        )

        Log.d(
            TAG,
            "arguments=$map"
        )

        flutterChannel?.invokeMethod(
            CALL_ACCEPTED,
            map
        )

        Log.d(
            TAG,
            "CALL_ACCEPTED_INTENT sent to Flutter"
        )
    }

    private fun bundleToMap(
        bundle: Bundle
    ): Map<String, Any?> {

        val map = mutableMapOf<String, Any?>()

        for (key in bundle.keySet()) {

            val value = bundle.get(key)

            val convertedValue = when (value) {
                is Bundle -> bundleToMap(value)
                else -> value
            }

            map[key] = convertedValue

            Log.d(
                TAG,
                "bundleToMap: $key = $convertedValue"
            )
        }

        return map
    }
}