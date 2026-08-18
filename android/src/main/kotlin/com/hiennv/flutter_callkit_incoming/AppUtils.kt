package com.hiennv.flutter_callkit_incoming

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.graphics.drawable.GradientDrawable

object AppUtils {

    private const val CALL_NOTIFICATION_SERVICE =
        "com.wowphonedev.softphone.CallNotificationService"

    fun getAppIntent(
        context: Context,
        action: String? = null,
        data: Bundle? = null
    ): Intent {

        val intent = Intent()

        intent.setClassName(
            context,
            CALL_NOTIFICATION_SERVICE
        )

        intent.action = action

        intent.putExtra(
            FlutterCallkitIncomingPlugin.EXTRA_CALLKIT_CALL_DATA,
            data
        )

        return intent
    }

    fun createCircleDrawable(
        fillColor: Int
    ): GradientDrawable {
        val shape = GradientDrawable()

        shape.setShape(
            GradientDrawable.OVAL
        )

        shape.setColor(fillColor)

        return shape
    }
}