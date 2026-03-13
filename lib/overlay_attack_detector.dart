import 'dart:async';
import 'package:flutter/services.dart';

class OverlayAttackDetector {
  static const MethodChannel _channel =
      MethodChannel('overlay_attack_detector');

  static const EventChannel _overlayEvents =
      EventChannel('overlay_attack_detector/events');

  /// Check if overlay permission is enabled
  static Future<bool> isOverlayEnabled() async {
    final bool result = await _channel.invokeMethod('isOverlayEnabled');
    return result;
  }

  /// Open overlay settings
  static Future<void> openOverlaySettings() async {
    await _channel.invokeMethod('openOverlaySettings');
  }

  /// Real-time overlay detection stream
  static Stream<bool> get overlayAttackStream =>
      _overlayEvents.receiveBroadcastStream().map((event) => event as bool);
}
