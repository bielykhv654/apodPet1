import Shared
import SwiftUI


enum AppTab: Int, CaseIterable {
    case random = 1
    case byDate = 2
    case saved = 3
}

// MARK: - AppRoute Extensions

extension AppRoute {
    var associatedTab: AppTab {
        switch self {
        case is RandomPhoto:
            return .random
        case is ByDates:
            return .byDate
        case is Saved:
            return .saved
        default:
            return .random
        }
    }

    var isTabBarHidden: Bool {
        switch self {
        case is PhotoDetails:
            return true
        default:
            return false
        }
    }


    var titleKey: String {
        switch self {
        case is RandomPhoto:
            return "random"
        case is ByDates:
            return "byDate"
        case is Saved:
            return "saved"
        default:
            return ""
        }
    }
}


@available(iOS 26.0, *)
@Observable
class TabNavigationCoordinator {
    var path: [RouteWrapper] = []

    func push(_ route: AppRoute) {
        path.append(RouteWrapper(route: route))
    }

    func pop() {
        if !path.isEmpty {
            path.removeLast()
        }
    }

    func popToRoot() {
        path.removeAll()
    }
}


@available(iOS 26.0, *)
@Observable
class AppNavigationCoordinator {
    var selectedTab: AppTab = .random
    let tabCoordinators: [AppTab: TabNavigationCoordinator]
    init() {
        self.tabCoordinators = Dictionary(
            uniqueKeysWithValues: AppTab.allCases.map { ($0, TabNavigationCoordinator()) }
        )
    }
    @MainActor
    private func handleAuthChange(_ isAuthorised: Bool) {
        open(route: RandomPhoto())
    }

    func coordinator(for tab: AppTab) -> TabNavigationCoordinator {
        guard let coordinator = tabCoordinators[tab] else {
            fatalError("No coordinator for tab \(tab)")
        }
        return coordinator
    }

    func tab(for route: AppRoute) -> AppTab {
        route.associatedTab
    }

    func open(route: AppRoute) {
        let targetTab = tab(for: route)
        let coordinator = coordinator(for: targetTab)
        if targetTab == selectedTab {
            if !(route is TopLevelRoute) {
                coordinator.push(route)
            }
            return
        }

        switchTab(to: targetTab, coordinator: coordinator, pushing: route)
    }
    private func switchTab(
        to targetTab: AppTab,
        coordinator: TabNavigationCoordinator,
        pushing route: AppRoute
    ) {
        let previousTab = selectedTab
        selectedTab = targetTab
        coordinator.popToRoot()
        self.tabCoordinators[previousTab]?.popToRoot()
    }
}

@available(iOS 26.0, *)
extension AppNavigationCoordinator {

    var currentTabCoordinator: TabNavigationCoordinator {
        coordinator(for: selectedTab)
    }

    var currentRoute: AppRoute? {
        return currentTabCoordinator.path.last?.route
    }


    private func routeFlag(_ predicate: (AppRoute) -> Bool) -> Bool {
        currentRoute.map(predicate) ?? false
    }
    var isTabBarHidden: Bool { routeFlag { $0.isTabBarHidden } }
}

func topLevelRoute(for tab: AppTab) -> AppRoute {
    switch tab {
    case .random:
        RandomPhoto()
    case .byDate:
        ByDates()
    case .saved:
        Saved()
    }
}

func shouldHideTabBar(for route: AppRoute) -> Bool {
    route.isTabBarHidden
}

func titleKey(for route: AppRoute) -> String {
    route.titleKey
}
