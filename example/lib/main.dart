import 'package:flutter/material.dart';
import 'package:overlay_attack_detector/overlay_attack_detector.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  bool overlayEnabled = false;
  bool overlayDetected = false;

  @override
  void initState() {
    super.initState();
    checkOverlayPermission();
    listenOverlay();
  }

  Future<void> checkOverlayPermission() async {
    final result = await OverlayAttackDetector.isOverlayEnabled();

    setState(() {
      overlayEnabled = result;
    });
  }

  void listenOverlay() {
    OverlayAttackDetector.overlayAttackStream.listen((detected) {
      setState(() {
        overlayDetected = detected;
      });

      if (detected) {
        showDialog(
          context: context,
          builder: (_) => const AlertDialog(
            title: Text("Security Warning"),
            content: Text("Overlay detected. Please disable overlay apps."),
          ),
        );
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text("Overlay Attack Detector"),
        ),
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Text(
                overlayEnabled
                    ? "Overlay Permission Enabled"
                    : "Overlay Permission Disabled",
                style: const TextStyle(fontSize: 18),
              ),
              const SizedBox(height: 20),
              ElevatedButton(
                onPressed: () {
                  OverlayAttackDetector.openOverlaySettings();
                },
                child: const Text("Open Overlay Settings"),
              ),
              const SizedBox(height: 40),
              Text(
                overlayDetected
                    ? "⚠ Overlay Attack Detected"
                    : "No Overlay Detected",
                style: TextStyle(
                  fontSize: 22,
                  color: overlayDetected ? Colors.red : Colors.green,
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
