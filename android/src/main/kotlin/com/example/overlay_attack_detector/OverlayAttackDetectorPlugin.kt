package com.example.overlay_attack_detector

import android.app.Activity
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

class OverlayAttackDetectorPlugin : FlutterPlugin, MethodChannel.MethodCallHandler,
    EventChannel.StreamHandler, ActivityAware {

    private var activity: Activity? = null
    private var eventSink: EventChannel.EventSink? = null

    private lateinit var methodChannel: MethodChannel
    private lateinit var eventChannel: EventChannel

    private var touchListener: View.OnTouchListener? = null

    override fun onAttachedToEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {

        methodChannel =
            MethodChannel(binding.binaryMessenger, "overlay_attack_detector")
        methodChannel.setMethodCallHandler(this)

        eventChannel =
            EventChannel(binding.binaryMessenger, "overlay_attack_detector/events")
        eventChannel.setStreamHandler(this)
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {

        when (call.method) {

            "isOverlayEnabled" -> {

                val enabled =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity != null) {
                        Settings.canDrawOverlays(activity!!)
                    } else {
                        true
                    }

                result.success(enabled)
            }

            "openOverlaySettings" -> {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity != null) {

                    val intent = android.content.Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        android.net.Uri.parse("package:" + activity!!.packageName)
                    )

                    intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)

                    activity!!.startActivity(intent)
                }

                result.success(null)
            }

            else -> result.notImplemented()
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {

        methodChannel.setMethodCallHandler(null)
        eventChannel.setStreamHandler(null)
    }

    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {

        eventSink = events

        val rootView = activity?.window?.decorView ?: return

        Log.d("OverlayDetector", "Touch detection enabled")

        touchListener = View.OnTouchListener { _, event ->

            val obscured =
                (event.flags and MotionEvent.FLAG_WINDOW_IS_OBSCURED != 0) ||
                        (event.flags and MotionEvent.FLAG_WINDOW_IS_PARTIALLY_OBSCURED != 0)

            Log.d("OverlayDetector", "Touch flags=${event.flags} obscured=$obscured")

            eventSink?.success(obscured)

            false
        }

        rootView.setOnTouchListener(touchListener)
    }

    override fun onCancel(arguments: Any?) {

        eventSink = null

        activity?.window?.decorView?.setOnTouchListener(null)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {

        activity = binding.activity
    }

    override fun onDetachedFromActivityForConfigChanges() {

        activity = null
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {

        activity = binding.activity
    }

    override fun onDetachedFromActivity() {

        activity = null
    }
}