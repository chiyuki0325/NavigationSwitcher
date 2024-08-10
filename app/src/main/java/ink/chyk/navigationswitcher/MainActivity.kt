package ink.chyk.navigationswitcher

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import ink.chyk.navigationswitcher.ui.theme.NavigationSwitcherTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        AccessibilityHelper().tryRunAccessibilityService(this)

        var text = ""
        enableEdgeToEdge()
        setContent {
            NavigationSwitcherTheme {
                Scaffold { innerPadding ->
                    MainUI(innerPadding)
                }
            }
        }
    }

    @Composable
    fun MainUI(innerPadding: PaddingValues) {
        val context = this
        var text by rememberSaveable { mutableStateOf(
            StorageHelper().getRhythmGamePackageNames(context).joinToString("\n")
        ) }

        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding)
        ) {
            Text(
                text = "设置要切换为三键导航的应用包名，一行一个:",
            )
            TextField(value = text, onValueChange = { text = it })
            Button(onClick = {
                StorageHelper().setRhythmGamePackageNames(context, text.split("\n"))
                Toast.makeText(context, "保存成功，重启应用生效。", Toast.LENGTH_SHORT).show()
            }) {
                Text("保存")
            }
        }
    }
}