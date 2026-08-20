import SwiftUI
import UIKit
import Shared

struct ComposeView: UIViewControllerRepresentable {
    private let reportExporter = IOSResultsReportExporter()

    init() {
    }

    func makeUIViewController(context: Context) -> UIViewController {
        let configuredUrl = Bundle.main.object(forInfoDictionaryKey: "API_BASE_URL") as? String
        let apiBaseUrl = configuredUrl.flatMap { value in
            value.isEmpty ? nil : value
        } ?? "http://localhost:8088/"

        return MainViewControllerKt.MainViewController(
            apiBaseUrl: apiBaseUrl,
            reportExporter: reportExporter
        )
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
    }
}

struct ContentView: View {
    var body: some View {
        ComposeView()
            .ignoresSafeArea(.keyboard)
    }
}
