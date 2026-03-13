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
  bool _overlayEnabled = false;
  bool _overlayDetected = false;

  @override
  void initState() {
    super.initState();
    checkOverlay();
    listenOverlay();
  }

  Future<void> checkOverlay() async {
    final result = await OverlayAttackDetector.isOverlayEnabled();
    if (!mounted) return;
    setState(() => _overlayEnabled = result);
  }

  void listenOverlay() {
    OverlayAttackDetector.overlayAttackStream.listen((detected) {
      if (!mounted) return;
      setState(() => _overlayDetected = detected);
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(title: const Text('Overlay Attack Detector Example')),
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Text(
                _overlayEnabled
                    ? "Overlay permission enabled"
                    : "Overlay permission disabled",
                style: const TextStyle(fontSize: 16),
              ),
              const SizedBox(height: 20),
              ElevatedButton(
                onPressed: () => OverlayAttackDetector.openOverlaySettings(),
                child: const Text("Open Overlay Settings"),
              ),
              const SizedBox(height: 40),
              Text(
                _overlayDetected
                    ? "⚠️ Overlay detected! Possible attack!"
                    : "No overlay detected",
                style: TextStyle(
                  fontSize: 18,
                  fontWeight: FontWeight.bold,
                  color: _overlayDetected ? Colors.red : Colors.green,
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
