import SwiftUI
import Shared

@main
struct iOSApp: App {

    init() {
        InitIosKt.initialize( onKoinStart: { _ in })
    }
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
