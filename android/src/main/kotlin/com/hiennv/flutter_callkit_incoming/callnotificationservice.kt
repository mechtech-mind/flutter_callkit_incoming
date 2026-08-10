package com.wowphonedev.softphone

import io.flutter.embedding.android.FlutterActivity

class CallNotificationService : FlutterActivity() {

    override fun getDartEntrypointFunctionName(): String {
        return "callNotificationMain"
    }
}