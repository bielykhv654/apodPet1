import Shared
import SwiftUI

// MARK: - UIViewControllerRepresentable Bridges

@available(iOS 26.0, *)
struct NativeNavComposeView: UIViewControllerRepresentable {
    let topLevelRoute: TopLevelRoute
    let coordinator: TabNavigationCoordinator
    let appCoordinator: AppNavigationCoordinator

    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController(
            topLevelRoute: topLevelRoute,
            onNavigate: { route in coordinator.push(route) },
            onActivate: { route in appCoordinator.open(route: route) }
        )
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

@available(iOS 26.0, *)
struct DetailComposeView: UIViewControllerRepresentable {
    let route: AppRoute
    let coordinator: TabNavigationCoordinator
    let appCoordinator: AppNavigationCoordinator

    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.ScreenViewController(
            route: route,
            onNavigate: { route in coordinator.push(route) },
            onGoBack: { coordinator.pop() },
            crossTab: { route in appCoordinator.open(route: route) },
        )
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

// MARK: - Tab Content

@available(iOS 26.0, *)
struct TabContentView: View {
    let topLevelRoute: TopLevelRoute
    let coordinator: TabNavigationCoordinator
    let appCoordinator: AppNavigationCoordinator


    var body: some View {
        NavigationStack(
            path: Binding(
                get: { coordinator.path },
                set: { coordinator.path = $0 }
            )
        ) {
            NativeNavComposeView(
                topLevelRoute: topLevelRoute,
                coordinator: coordinator,
                appCoordinator: appCoordinator
            )
            .ignoresSafeArea(.all)
            .navigationBarHidden(true)
            .navigationDestination(for: RouteWrapper.self) { wrapper in

                DetailComposeView(
                    route: wrapper.route,
                    coordinator: coordinator,
                    appCoordinator: appCoordinator
                )
                .ignoresSafeArea(.all)
                .navigationTitle(LocalizedStringKey(titleKey(for: wrapper.route)))
                .toolbarTitleDisplayMode(.inline)
            }
        }
        .toolbar(appCoordinator.isTabBarHidden ? .hidden : .visible, for: .tabBar)

    }
}
// MARK: - Root

@available(iOS 26.0, *)
struct NativeNavContentView: View {
    @State private var appCoordinator = AppNavigationCoordinator()
    var body: some View {
        Group {
                TabView(selection: $appCoordinator.selectedTab) {
                    Tab(String(localized: "random"), systemImage: "die.face.5", value: AppTab.home) {
                        TabContentView(
                            topLevelRoute: RandomPhoto(),
                            coordinator: appCoordinator.coordinator(for: .home),
                            appCoordinator: appCoordinator,
                        )
                    }
                    Tab(String(localized: "byDate"), systemImage: "calendar.badge.clock", value: AppTab.search) {
                        TabContentView(
                            topLevelRoute: ByDates(),
                            coordinator: appCoordinator.coordinator(for: .search),
                            appCoordinator: appCoordinator,
                        )
                    }
                    Tab(String(localized: "saved"), systemImage: "heart", value: AppTab.profile) {
                        TabContentView(
                            topLevelRoute: Saved(),
                            coordinator: appCoordinator.coordinator(for: .profile),
                            appCoordinator: appCoordinator,
                        )
                    }
                }
                .tabBarMinimizeBehavior(.automatic)
        }
    }
}
