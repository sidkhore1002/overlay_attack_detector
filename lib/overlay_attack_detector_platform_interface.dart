import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'overlay_attack_detector_method_channel.dart';

abstract class OverlayAttackDetectorPlatform extends PlatformInterface {
  /// Constructs a OverlayAttackDetectorPlatform.
  OverlayAttackDetectorPlatform() : super(token: _token);

  static final Object _token = Object();

  static OverlayAttackDetectorPlatform _instance =
      MethodChannelOverlayAttackDetector();

  /// The default instance of [OverlayAttackDetectorPlatform] to use.
  ///
  /// Defaults to [MethodChannelOverlayAttackDetector].
  static OverlayAttackDetectorPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [OverlayAttackDetectorPlatform] when
  /// they register themselves.
  static set instance(OverlayAttackDetectorPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
