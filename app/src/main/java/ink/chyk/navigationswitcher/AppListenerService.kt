package ink.chyk.navigationswitcher

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import moe.shizuku.server.IShizukuService
import rikka.shizuku.Shizuku

class AppListenerService : AccessibilityService() {
    private var rhythmGames: List<String> = emptyList()

    private var lastPackageName = ""

    override fun onInterrupt() {}

    private fun setNavigationState(state: String) {
        Log.i("AppListenerService", "setNavigationState: $state")
        Shizuku.getBinder()!!.let {
            IShizukuService.Stub.asInterface(it).newProcess(
                arrayOf("settings", "put", "system", "launcher_state", state),
                null, null
            ).apply {
                waitFor()
            }
            IShizukuService.Stub.asInterface(it).newProcess(
                arrayOf("settings", "put", "global", "force_fsg_nav_bar", state),
                null, null
            ).apply {
                waitFor()
            }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            if (this.rhythmGames.isEmpty()) {
                this.rhythmGames = StorageHelper().getRhythmGamePackageNames(this)
                Log.i("AppListenerService", this.rhythmGames.joinToString())
            }

            val packageName = event.packageName.toString()
            if (packageName != this.lastPackageName) {
                // 应用改变
                Log.i("AppListenerService", packageName)
                if (packageName in this.rhythmGames) {
                    this.setNavigationState("0")
                } else if ((
                            this.lastPackageName in this.rhythmGames || this.lastPackageName == "com.miui.securitycenter"
                        ) && packageName == "com.miui.home") {
                    this.setNavigationState("1")
                }
                this.lastPackageName = packageName
            }
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.i("AppListenerService", "onServiceConnected")
    }
}