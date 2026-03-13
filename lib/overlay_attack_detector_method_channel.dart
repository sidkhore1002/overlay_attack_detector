import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'overlay_attack_detector_platform_interface.dart';

/// An implementation of [OverlayAttackDetectorPlatform] that uses method channels.
class MethodChannelOverlayAttackDetector extends OverlayAttackDetectorPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('overlay_attack_detector');

  @override
  Future<String?> getPlatformVersion() async {
    final version =
        await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }
}
