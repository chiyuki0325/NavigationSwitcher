package ink.chyk.navigationswitcher

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AutoStartReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            AccessibilityHelper().tryRunAccessibilityService(context)
        }
    }
}