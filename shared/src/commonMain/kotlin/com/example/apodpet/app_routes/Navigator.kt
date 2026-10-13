package com.example.apodpet.app_routes

class Navigator(
    val state: NavState,
    val topLevelBackEnabled: Boolean,
) {
    fun goBack() {
        val currentBackstack = state.currentBackstack

        if (state.topLevelRoute == null) {
            // We're using the default stack, remove an entry if possible
            if (currentBackstack.size > 1) {
                currentBackstack.removeLastOrNull()
            }
            return
        }
        if (currentBackstack.size == 1 && state.topLevelRoute != state.primaryTopLevelRoute) {
            // Can't go further up on current backstack, but we're not on the primary route
            if (topLevelBackEnabled) {
                // Go back to the primary top-level route if enabled
                state.topLevelRoute = state.primaryTopLevelRoute
            }
        } else if (currentBackstack.size > 1) {
            currentBackstack.removeLastOrNull()
        }
    }

    fun add(route: AppRoute) {
        if (route is TopLevelRoute) {
            activate(route)
        } else {
            state.currentBackstack.add(route)
        }
    }

    fun activate(route: TopLevelRoute, withReselection: Boolean = true) {
        if (withReselection && route == state.topLevelRoute) {
            val currentBackstack = state.currentBackstack

            // Reselected the current top-level route, clear to root
            if (currentBackstack.size > 1) {
                currentBackstack.removeRange(1, currentBackstack.size)
            }
            return
        }
        state.topLevelRoute = route
    }

    /* Transition from one tab's stack into another tab's nested stack, resetting the current stack */
    fun crossTab(target: AppRoute) {
        val targetTab = target.topLevelRoute

        val currentStack = state.currentBackstack
        if (currentStack.isNotEmpty()) {
            currentStack.removeRange(1, currentStack.size)
        }

        state.topLevelRoute = targetTab

        state.currentBackstack.clear()

        state.currentBackstack.add(targetTab)

        val additionalPath = target.backstackPath.filter { it != targetTab }
        state.currentBackstack.addAll(additionalPath)
    }
}
