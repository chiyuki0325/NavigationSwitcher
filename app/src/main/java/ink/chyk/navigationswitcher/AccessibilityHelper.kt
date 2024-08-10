package ink.chyk.navigationswitcher

import android.Manifest.permission.WRITE_SETTINGS
import android.Manifest.permission.WRITE_SECURE_SETTINGS
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import moe.shizuku.server.IShizukuService
import rikka.shizuku.Shizuku

class AccessibilityHelper {
    private fun isShizukuAvailable(): Boolean? {
        if (Shizuku.isPreV11() || !Shizuku.pingBinder()) return false
        if (Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED) {
            if (Shizuku.shouldShowRequestPermissionRationale()) return false
            return null
        }
        return true
    }

    private fun enableAccessibilitySettings(context: Context): Boolean {
        try {
            Settings.Secure.putString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,
                "${context.packageName}/${AppListenerService::class.java.name}"
            )
            Settings.Secure.putInt(
                context.contentResolver,
                Settings.Secure.ACCESSIBILITY_ENABLED,
                1
            )
            return true
        } catch (e: Exception) {
            Log.e("NavigatorSwitchShizuku", "Failed to enable accessibility settings")
            return false
        }
    }

    private fun queryShizuku(context: Context, grant: Boolean = true) {
        val action = if (grant) "grant" else "revoke"
        Shizuku.getBinder()!!.let {
            IShizukuService.Stub.asInterface(it).newProcess(
                arrayOf("pm", action, context.packageName, WRITE_SETTINGS),
                null, null
            ).apply {
                waitFor()
                Log.i(
                    "NavigatorSwitchShizuku",
                    "Shizuku WRITE_SETTINGS process exit with value: ${exitValue()}"
                )
            }
            IShizukuService.Stub.asInterface(it).newProcess(
                arrayOf("pm", action, context.packageName, WRITE_SECURE_SETTINGS),
                null, null
            ).apply {
                waitFor()
                Log.i(
                    "NavigatorSwitchShizuku",
                    "Shizuku WRITE_SECURE_SETTINGS process exit with value: ${exitValue()}"
                )
            }
        }
    }

    public fun tryRunAccessibilityService(context: Context) {
        if (!this.enableAccessibilitySettings(context)) {
            when (this.isShizukuAvailable()) { // 调用shizuku写入设置
                true -> { // shizuku可用
                    Log.i("NavigatorSwitchShizuku", "Shizuku is available")
                    this.queryShizuku(context)
                    val result = this.enableAccessibilitySettings(context)
                    when (result) {
                        true -> {
                            Log.i("NavigatorSwitchShizuku", "Accessibility service enabled")
                        }
                        false -> {
                            Log.i("NavigatorSwitchShizuku", "Failed to enable accessibility service")
                            Toast.makeText(context, "请手动开启无障碍服务", Toast.LENGTH_SHORT).show()
                            val i = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            context.startActivity(i)
                        }
                    }
                }

                false -> { // 无 shizuku
                    Log.i("NavigatorSwitchShizuku", "Shizuku is not available")
                    Toast.makeText(context, "请手动开启无障碍服务", Toast.LENGTH_SHORT).show()
                    val i = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                    i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(i)
                }

                null -> { //shizuku未授权
                    Log.i("NavigatorSwitchShizuku", "Shizuku is not authorized")
                    Shizuku.requestPermission(0x0)
                    this.tryRunAccessibilityService(context)
                }
            }
        }
    }
}