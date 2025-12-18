package com.example.mobileassistant.ui.main.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mobileassistant.ui.main.model.WeekActivityUi

@Composable
fun WeekActivity(data: List<WeekActivityUi>) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Активность за неделю")
        data.forEach {
            Text("${it.day}: ${it.completed}")
        }
    }
}
