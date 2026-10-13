package com.example.apodpet

import platform.UIKit.UIDevice

actual val isNative: Boolean = (UIDevice.currentDevice.systemVersion
        .substringBefore(".").toIntOrNull() ?: 0) >= 26

actual val isDesktop: Boolean = false
