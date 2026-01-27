import 'package:flutter/material.dart';
import 'package:Echo/services/import_service.dart';
import 'package:Echo/themes/colors.dart';

class ImportDialog extends StatefulWidget {
  final Stream<ImportState> stream;
  const ImportDialog({super.key, required this.stream});

  @override
  State<ImportDialog> createState() => _ImportDialogState();
}

class _ImportDialogState extends State<ImportDialog> {
  String message = "Starting...";
  double? progress;
  bool isDone = false;
  bool isError = false;

  @override
  void initState() {
    super.initState();
    widget.stream.listen((event) {
      if (mounted) {
        setState(() {
          message = event.message;
          if (event.total > 0) {
            progress = event.current / event.total;
          }
          isDone = event.isDone;
          isError = event.isError;
        });
        if (event.isDone || event.isError) {
           Future.delayed(const Duration(seconds: 1), () {
              if (mounted) Navigator.pop(context);
           });
        }
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      backgroundColor: const Color(0xff1e1e1e),
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(15)),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            if (!isDone && !isError)
              const CircularProgressIndicator(),
            if (isDone)
              const Icon(Icons.check_circle, color: Colors.green, size: 50),
            if (isError)
              const Icon(Icons.error, color: Colors.red, size: 50),
            const SizedBox(height: 20),
            Text(
              message,
              textAlign: TextAlign.center,
              style: const TextStyle(color: Colors.white),
            ),
            if (progress != null && !isDone && !isError)
              Padding(
                padding: const EdgeInsets.only(top: 10),
                child: LinearProgressIndicator(value: progress),
              ),
          ],
        ),
    );
  }
}
