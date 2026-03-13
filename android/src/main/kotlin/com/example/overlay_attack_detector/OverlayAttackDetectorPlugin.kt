package com.example.overlay_attack_detector

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.MotionEvent
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

    private lateinit var methodChannel: MethodChannel
    private lateinit var eventChannel: EventChannel
    private var activity: Activity? = null
    private var eventSink: EventChannel.EventSink? = null

    override fun onAttachedToEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {

        methodChannel = MethodChannel(binding.binaryMessenger, "overlay_attack_detector")
        methodChannel.setMethodCallHandler(this)

        eventChannel = EventChannel(binding.binaryMessenger, "overlay_attack_detector/events")
        eventChannel.setStreamHandler(this)
    }

    // Attach Activity
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

    // Method Channel
    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {

        when (call.method) {

            "isOverlayEnabled" -> {

                val enabled =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        Settings.canDrawOverlays(activity)
                    } else true

                result.success(enabled)
            }

            "openOverlaySettings" -> {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:${activity?.packageName}")
                    )

                    activity?.startActivity(intent)
                }

                result.success(null)
            }

            else -> result.notImplemented()
        }
    }

    // Event Channel
    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {

        eventSink = events

        activity?.window?.decorView?.setOnTouchListener { _, event ->

            val obscured =
                event.flags and MotionEvent.FLAG_WINDOW_IS_OBSCURED != 0 ||
                event.flags and MotionEvent.FLAG_WINDOW_IS_PARTIALLY_OBSCURED != 0

            eventSink?.success(obscured)

            false
        }
    }

    override fun onCancel(arguments: Any?) {
        eventSink = null
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {

        methodChannel.setMethodCallHandler(null)
        eventChannel.setStreamHandler(null)
    }
}