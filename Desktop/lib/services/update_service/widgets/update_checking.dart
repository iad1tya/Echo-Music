import 'package:flutter/material.dart';

class UpdateCheckingDialog extends StatelessWidget {
  const UpdateCheckingDialog({super.key});

  @override
  Widget build(BuildContext context) {
    return const Dialog(
      insetPadding: EdgeInsets.all(48),
      child: Padding(
        padding: EdgeInsets.all(24),
        child: Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            CircularProgressIndicator(),
            SizedBox(width: 16),
            Text('Checking for updates…'),
          ],
        ),
      ),
    );
  }
}
