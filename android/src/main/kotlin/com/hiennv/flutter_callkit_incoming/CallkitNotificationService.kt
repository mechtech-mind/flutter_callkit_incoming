package com.wowphonedev.softphone

import android.os.Bundle
import com.hiennv.flutter_callkit_incoming.FlutterCallkitIncomingPlugin
import io.flutter.embedding.android.FlutterActivity

class CallNotificationService : FlutterActivity() {

    override fun getDartEntrypointFunctionName(): String {
        return "callNotificationMain"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}