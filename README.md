# Overlay Attack Detector

Flutter plugin to detect overlay apps and protect sensitive UI such as payment screens.

## Features

- Detect overlay permission
- Open overlay permission settings
- Android support

## Usage

import 'package:overlay_attack_detector/overlay_attack_detector.dart';

bool overlay = await OverlayAttackDetector.isOverlayEnabled();