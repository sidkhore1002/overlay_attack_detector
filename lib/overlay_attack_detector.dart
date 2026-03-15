import 'dart:async';
import 'package:flutter/services.dart';

class OverlayAttackDetector {
  static const MethodChannel _channel =
      MethodChannel('overlay_attack_detector');

  static const EventChannel _overlayEvents =
      EventChannel('overlay_attack_detector/events');

  /// Check if overlay permission enabled
  static Future<bool> isOverlayEnabled() async {
    final bool result = await _channel.invokeMethod('isOverlayEnabled');

    return result;
  }

  /// Open overlay permission screen
  static Future<void> openOverlaySettings() async {
    await _channel.invokeMethod('openOverlaySettings');
  }

  /// Real time overlay detection
  static Stream<bool> get overlayAttackStream {
    return _overlayEvents
        .receiveBroadcastStream()
        .map((event) => event as bool);
  }
}
