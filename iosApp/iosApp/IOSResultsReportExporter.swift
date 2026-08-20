import Foundation
import UIKit
import Shared

final class IOSResultsReportExporter: NSObject, ResultsReportExporter {
    func createAndShare(
        userName: String,
        result: ResultsUiState
    ) -> ReportExportResult {
        let fileName = safeFileName(result.reportName)
        let destination = FileManager.default.temporaryDirectory
            .appendingPathComponent(fileName)
        let contents = "Estudiante: \(userName.isEmpty ? "Estudiante USB" : userName)\n\n" +
            result.shareReportText

        let data = makePdf(contents: contents)
        try? data.write(to: destination, options: .atomic)

        DispatchQueue.main.async {
            Self.presentShareSheet(for: destination)
        }

        return ReportExportResult(fileName: fileName)
    }

    private func safeFileName(_ reportName: String) -> String {
        let allowed = CharacterSet.alphanumerics.union(CharacterSet(charactersIn: "_-"))
        let base = reportName.unicodeScalars
            .filter { allowed.contains($0) }
            .map(String.init)
            .joined()
        return "\(base.isEmpty ? "resultado-vocacional" : base).pdf"
    }

    private func makePdf(contents: String) -> Data {
        let page = CGRect(x: 0, y: 0, width: 595, height: 842)
        let renderer = UIGraphicsPDFRenderer(bounds: page)
        let bodyFont = UIFont.systemFont(ofSize: 10)
        let titleFont = UIFont.boldSystemFont(ofSize: 18)
        let orange = UIColor(red: 239.0 / 255.0, green: 125.0 / 255.0, blue: 0, alpha: 1)
        let paragraph = NSMutableParagraphStyle()
        paragraph.lineSpacing = 3

        return renderer.pdfData { context in
            var y: CGFloat = 122

            func beginPage() {
                context.beginPage()
                orange.setFill()
                context.cgContext.fill(CGRect(x: 0, y: 0, width: page.width, height: 92))
                ("Orientación Vocacional USB" as NSString).draw(
                    at: CGPoint(x: 48, y: 32),
                    withAttributes: [
                        .font: titleFont,
                        .foregroundColor: UIColor.white,
                    ]
                )
                y = 122
            }

            beginPage()
            for line in contents.components(separatedBy: .newlines) {
                let text = line.isEmpty ? " " : line
                let attributes: [NSAttributedString.Key: Any] = [
                    .font: bodyFont,
                    .foregroundColor: UIColor.darkGray,
                    .paragraphStyle: paragraph,
                ]
                let width = page.width - 96
                let height = (text as NSString).boundingRect(
                    with: CGSize(width: width, height: .greatestFiniteMagnitude),
                    options: [.usesLineFragmentOrigin, .usesFontLeading],
                    attributes: attributes,
                    context: nil
                ).height.rounded(.up) + 5

                if y + height > page.height - 52 {
                    beginPage()
                }
                (text as NSString).draw(
                    in: CGRect(x: 48, y: y, width: width, height: height),
                    withAttributes: attributes
                )
                y += height
            }
        }
    }

    private static func presentShareSheet(for url: URL) {
        guard
            let scene = UIApplication.shared.connectedScenes
                .compactMap({ $0 as? UIWindowScene })
                .first(where: { $0.activationState == .foregroundActive }),
            let root = scene.windows.first(where: { $0.isKeyWindow })?.rootViewController
        else {
            return
        }

        var presenter = root
        while let presented = presenter.presentedViewController {
            presenter = presented
        }

        let controller = UIActivityViewController(
            activityItems: [url],
            applicationActivities: nil
        )
        if let popover = controller.popoverPresentationController {
            popover.sourceView = presenter.view
            popover.sourceRect = CGRect(
                x: presenter.view.bounds.midX,
                y: presenter.view.bounds.midY,
                width: 1,
                height: 1
            )
        }
        presenter.present(controller, animated: true)
    }
}
