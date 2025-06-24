import 'dart:io';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'preview_page.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Camera Demo',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
        useMaterial3: true,
      ),
      home: TakePhotoScreen(),
    );
  }
}

class TakePhotoScreen extends StatefulWidget {
  @override
  _TakePhotoScreenState createState() => _TakePhotoScreenState();
}

class _TakePhotoScreenState extends State<TakePhotoScreen> {
  static const platform = MethodChannel('com.camera/native');
  List<String> imagePaths = [];

  Future<void> _takePhotos() async {
    try {
      final List<dynamic> result = await platform.invokeMethod('takeFivePhotos');
      List<String> paths = result.cast<String>();
      print(paths);
      setState(() {
        imagePaths = paths;
      });

      if (paths.isNotEmpty) {
        Navigator.push(
          context,
          MaterialPageRoute(
            builder: (context) => PreviewScreen(imagePaths: paths),
          ),
        );
      }
    } on PlatformException catch (e) {
      print("Error: ${e.message}");
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text("Bhagavathi Images")),
      body: Center(
        child: ElevatedButton(
          onPressed: _takePhotos,
          child: Text("Capture Photos"),
        ),
      ),
    );
  }
}
