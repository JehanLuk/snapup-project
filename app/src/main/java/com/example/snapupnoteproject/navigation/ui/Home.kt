package com.example.snapupnoteproject.navigation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen () {
    Scaffold (
        content = { pad ->

            Column (
                modifier = Modifier
                    .padding(pad)
                    .fillMaxSize()
            ){
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = "Meus Cards",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .border(
                            width = 2.dp,
                            Color(140, 180, 20),
                            shape = RoundedCornerShape(32.dp)
                        )
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Adicionar um novo card"
                    )
                }
            }
        }
    )
}