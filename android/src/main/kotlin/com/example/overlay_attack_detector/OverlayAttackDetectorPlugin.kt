package com.example.overlay_attack_detector

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.MotionEvent
import android.view.Window
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

class OverlayAttackDetectorPlugin : FlutterPlugin,
    MethodChannel.MethodCallHandler,
    EventChannel.StreamHandler,
    ActivityAware {

    private var activity: Activity? = null
    private var eventSink: EventChannel.EventSink? = null
    private var originalCallback: Window.Callback? = null

    private lateinit var methodChannel: MethodChannel
    private lateinit var eventChannel: EventChannel

    override fun onAttachedToEngine(
        @NonNull binding: FlutterPlugin.FlutterPluginBinding
    ) {

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

                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + activity!!.packageName)
                    )

                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    activity!!.startActivity(intent)
                }

                result.success(null)
            }

            else -> result.notImplemented()
        }
    }

    override fun onDetachedFromEngine(
        @NonNull binding: FlutterPlugin.FlutterPluginBinding
    ) {

        methodChannel.setMethodCallHandler(null)
        eventChannel.setStreamHandler(null)
    }

    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {

        eventSink = events
        Log.d("OverlayDetector", "Overlay detection started")
    }

    override fun onCancel(arguments: Any?) {

        eventSink = null
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {

        activity = binding.activity

        val window = activity!!.window
        originalCallback = window.callback

        window.callback = object : Window.Callback by originalCallback!! {

            override fun dispatchTouchEvent(event: MotionEvent): Boolean {

                if (event.action == MotionEvent.ACTION_DOWN) {

                    val obscured =
                        (event.flags and MotionEvent.FLAG_WINDOW_IS_OBSCURED) != 0 ||
                        (event.flags and MotionEvent.FLAG_WINDOW_IS_PARTIALLY_OBSCURED) != 0

                    Log.d(
                        "OverlayDetector",
                        "Touch flags=${event.flags} overlayDetected=$obscured"
                    )

                    eventSink?.success(obscured)
                }

                return originalCallback!!.dispatchTouchEvent(event)
            }
        }
    }

    override fun onDetachedFromActivityForConfigChanges() {

        activity = null
    }

    override fun onReattachedToActivityForConfigChanges(
        binding: ActivityPluginBinding
    ) {

        onAttachedToActivity(binding)
    }

    override fun onDetachedFromActivity() {

        activity = null
    }
}