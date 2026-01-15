package com.fsa_profgroep_4.vroomly.ui.screens.auth.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.fsa_profgroep_4.vroomly.R
import com.fsa_profgroep_4.vroomly.ui.theme.spacing

@Composable
fun VroomlyAuthHeader(
    title: String,
    subtitles: List<String>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Image(
            painter = painterResource(R.drawable.logo_small),
            contentDescription = "Vroomly",
        )

        Text(
            text = title,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(bottom = MaterialTheme.spacing.small)
        )

        subtitles.forEachIndexed { index, text ->
            Text(
                text = text,
                fontSize = if (index == 0) 18.sp else 14.sp,
                modifier = if (index == subtitles.lastIndex) {
                    Modifier.padding(bottom = MaterialTheme.spacing.medium)
                } else {
                    Modifier
                }
            )
        }
    }
}
