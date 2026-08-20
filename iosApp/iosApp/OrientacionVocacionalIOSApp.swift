import SwiftUI
import Shared

@main
struct OrientacionVocacionalIOSApp: App {
    var body: some Scene {
        WindowGroup {
            ContentView()
                .onOpenURL { url in
                    AppEnvironmentKt.handleDeepLink(url: url.absoluteString)
                }
        }
    }
}
