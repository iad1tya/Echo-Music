import 'package:flutter/material.dart';
import 'package:url_launcher/url_launcher.dart';

class AboutPage extends StatelessWidget {
  const AboutPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("About"),
      ),
      body: Center(
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(24),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              // Echo Logo
              ClipRRect(
                borderRadius: BorderRadius.circular(16),
                child: Image.asset(
                  'icons/Echo_nobg.png',
                  width: 120,
                  height: 120,
                  fit: BoxFit.contain,
                ),
              ),
              const SizedBox(height: 24),
              
              // App Name
              const Text(
                'Echo Music',
                style: TextStyle(fontSize: 28, fontWeight: FontWeight.bold),
              ),
              const SizedBox(height: 8),
              
              // Version
              const Text(
                'Version 1.0.0',
                style: TextStyle(fontSize: 16, color: Colors.grey),
              ),
              const SizedBox(height: 32),
              
              // Developer Section
              const Text(
                'Developed by',
                style: TextStyle(fontSize: 14, color: Colors.grey),
              ),
              const SizedBox(height: 8),
              const Text(
                'Aditya Yadav',
                style: TextStyle(fontSize: 20, fontWeight: FontWeight.w600),
              ),
              const SizedBox(height: 24),
              
              // GitHub Link
              InkWell(
                onTap: () async {
                  final uri = Uri.parse('https://github.com/iad1tya/Echo-Music');
                  if (await canLaunchUrl(uri)) {
                    await launchUrl(uri, mode: LaunchMode.externalApplication);
                  }
                },
                child: Container(
                  padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 12),
                  decoration: BoxDecoration(
                    border: Border.all(color: Colors.grey),
                    borderRadius: BorderRadius.circular(8),
                  ),
                  child: Row(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      const Icon(Icons.code, size: 20),
                      const SizedBox(width: 8),
                      const Text(
                        'View on GitHub',
                        style: TextStyle(fontSize: 16),
                      ),
                    ],
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
