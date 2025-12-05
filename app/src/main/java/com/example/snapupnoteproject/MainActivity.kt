package com.example.snapupnoteproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.snapupnoteproject.navigation.ui.AppNavigation
import com.example.snapupnoteproject.navigation.ui.HomeScreen
import com.example.snapupnoteproject.navigation.ui.theme.SnapUpNoteProjectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppNavigation()
        }
    }
}
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SnapUpNoteProjectTheme {
        AppNavigation()
    }
}