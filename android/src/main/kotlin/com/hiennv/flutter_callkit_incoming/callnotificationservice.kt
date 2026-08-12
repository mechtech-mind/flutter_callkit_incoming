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
    }

    override fun getDartEntrypointFunctionName(): String {
        Log.d(TAG, "getDartEntrypointFunctionName() -> callNotificationMain")
        return "callNotificationMain"
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        Log.d(TAG, "========== CallNotificationService.onCreate ==========")
        Log.d(TAG, "Intent action = ${intent?.action}")

        val data = intent?.getBundleExtra(
            FlutterCallkitIncomingPlugin.EXTRA_CALLKIT_CALL_DATA
        )

        if (data == null) {
            Log.w(TAG, "NO CALL DATA FOUND")
        } else {
            Log.d(TAG, "CALL DATA FOUND")

            for (key in data.keySet()) {
                Log.d(
                    TAG,
                    "Intent data [$key] = ${data.get(key)}"
                )
            }
        }

        super.onCreate(savedInstanceState)
    }

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        Log.d(TAG, "========== configureFlutterEngine ==========")

        MethodChannel(
            flutterEngine.dartExecutor.binaryMessenger,
            CHANNEL
        ).setMethodCallHandler { call, result ->

            Log.d(
                TAG,
                "Flutter -> Native method: ${call.method}"
            )

            if (call.method == GET_CALL_DATA) {

                val data = intent?.getBundleExtra(
                    FlutterCallkitIncomingPlugin.EXTRA_CALLKIT_CALL_DATA
                )

                if (data == null) {
                    Log.w(TAG, "Flutter requested data but Bundle is NULL")
                    result.success(null)
                    return@setMethodCallHandler
                }

                val map = bundleToMap(data)

                Log.d(TAG, "Sending CALL DATA to Flutter")
                Log.d(TAG, "Flutter data = $map")

                result.success(map)

            } else {
                result.notImplemented()
            }
        }

        Log.d(TAG, "MethodChannel ready")
    }

    private fun bundleToMap(bundle: Bundle): Map<String, Any?> {

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
                "Converted [$key] = $convertedValue"
            )
        }

        return map
    }
}