import Cocoa
import FlutterMacOS

class MainFlutterWindow: NSWindow {
  override func awakeFromNib() {
    let flutterViewController = FlutterViewController()
    let windowFrame = self.frame
    self.contentViewController = flutterViewController
    self.setFrame(windowFrame, display: true)

    RegisterGeneratedPlugins(registry: flutterViewController)
    
    // Enable window transparency
    self.isOpaque = false
    self.backgroundColor = .clear
    
    // Add visual effect view for blur
    let visualEffectView = NSVisualEffectView()
    visualEffectView.material = .headerView // or .underWindowBackground for darker
    visualEffectView.blendingMode = .behindWindow
    visualEffectView.state = .active
    visualEffectView.frame = self.contentView!.bounds
    visualEffectView.autoresizingMask = [.width, .height]
    
    // Add visual effect view BEHIND the flutter view
    self.contentView?.addSubview(visualEffectView, positioned: .below, relativeTo: flutterViewController.view)

    super.awakeFromNib()
  }
}
