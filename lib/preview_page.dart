import 'dart:io';
import 'package:flutter/material.dart';

class PreviewScreen extends StatelessWidget {
  final List<String> imagePaths;

  const PreviewScreen({super.key, required this.imagePaths});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Preview Photos')),
      body: GridView.builder(
        padding: const EdgeInsets.all(8),
        gridDelegate:
        const SliverGridDelegateWithFixedCrossAxisCount(crossAxisCount: 2, crossAxisSpacing: 8, mainAxisSpacing: 8),
        itemCount: imagePaths.length,
        itemBuilder: (context, index) {
          return Image.file(File(imagePaths[index]), fit: BoxFit.cover);
        },
      ),
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
