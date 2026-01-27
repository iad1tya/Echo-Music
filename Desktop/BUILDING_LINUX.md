# Building Linux Packages for Echo Music

This guide explains how to build Linux distribution packages (DEB, AppImage, RPM) for Echo Music Desktop.

## Platform Limitation ⚠️

**Linux packages cannot be built on macOS.** The `flutter_distributor` tool requires a Linux environment to build Linux packages.

## Available Package Formats

The project is configured to build three Linux package formats:

- **DEB** - For Debian, Ubuntu, and derivatives
- **AppImage** - Universal Linux package (works on most distributions)
- **RPM** - For Fedora, RHEL, CentOS, and derivatives

## Option 1: Manual Build on Linux

### Prerequisites
```bash
# Install system dependencies
sudo apt-get update
sudo apt-get install -y clang cmake ninja-build pkg-config libgtk-3-dev liblzma-dev libstdc++-12-dev

# Install Flutter (if not already installed)
# Follow: https://docs.flutter.dev/get-started/install/linux

# Install flutter_distributor
flutter pub global activate flutter_distributor
```

### Build All Packages
```bash
# Navigate to project directory
cd /path/to/Echo-Music-Desktop

# Install dependencies
flutter pub get

# Build all Linux packages
flutter_distributor release --name dev --jobs release-dev-linux-deb,release-dev-linux-appimage,release-dev-linux-rpm
```

### Build Specific Package Type
```bash
# DEB only
flutter_distributor release --name dev --jobs release-dev-linux-deb

# AppImage only
flutter_distributor release --name dev --jobs release-dev-linux-appimage

# RPM only
flutter_distributor release --name dev --jobs release-dev-linux-rpm
```

### Output Location
Packages will be created in: `dist/1.0.0+0/`

## Option 2: GitHub Actions (Recommended)

A GitHub Actions workflow has been created at `.github/workflows/build-linux.yml`.

### Trigger the Workflow

**Method 1: Manual Trigger**
1. Go to your GitHub repository
2. Click "Actions" tab
3. Select "Build Linux Packages" workflow
4. Click "Run workflow"

**Method 2: Tag-based Release**
```bash
# Create and push a version tag
git tag v1.0.0
git push origin v1.0.0
```

The workflow will automatically:
- Build all three package types
- Upload packages as artifacts
- Create a GitHub Release (if triggered by tag)

## Option 3: Docker

Create a temporary Linux container to build packages:

```bash
# Run Ubuntu container with Flutter
docker run -it --rm -v $(pwd):/workspace ubuntu:22.04 bash

# Inside container:
apt-get update
apt-get install -y curl git unzip xz-utils zip libglu1-mesa clang cmake ninja-build pkg-config libgtk-3-dev liblzma-dev

# Install Flutter
git clone https://github.com/flutter/flutter.git -b stable
export PATH="$PATH:`pwd`/flutter/bin"

# Navigate and build
cd /workspace
flutter pub get
flutter pub global activate flutter_distributor
export PATH="$PATH:$HOME/.pub-cache/bin"
flutter_distributor release --name dev
```

## Distribution

Once built, distribute the packages:

- **DEB**: Users install with `sudo dpkg -i Echo-*.deb`
- **AppImage**: Users make executable and run: `chmod +x Echo-*.AppImage && ./Echo-*.AppImage`
- **RPM**: Users install with `sudo rpm -i Echo-*.rpm`

## Current Status

- ✅ Configuration ready for DEB, AppImage, and RPM
- ✅ GitHub Actions workflow created
- ⚠️ Requires Linux environment to build
