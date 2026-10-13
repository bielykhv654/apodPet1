import Shared
import SwiftUI

// MARK: - KMP Bridge (fallback for iOS < 26)

struct ComposeView: UIViewControllerRepresentable {
    let topLevelRoute: TopLevelRoute
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController(topLevelRoute: topLevelRoute)
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

// MARK: - Entry Point

struct ContentView: View {
    var body: some View {
        if #available(iOS 26.0, *) {
            NativeNavContentView()
        } else {
            ComposeView(topLevelRoute: RandomPhoto())
                .ignoresSafeArea(.all)
        }
    }
}
