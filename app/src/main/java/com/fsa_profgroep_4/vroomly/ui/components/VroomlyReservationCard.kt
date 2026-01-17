package com.fsa_profgroep_4.vroomly.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.fsa_profgroep_4.vroomly.R
import java.text.NumberFormat
import java.util.Locale

data class ReservationCardData(
    val imageUrl: String,
    val title: String,
    val location: String,
    val totalCost: Double,
    val status: String,
    val reservationId: Int,
    val vehicleId: Int
)

private fun formatEuroNl(value: Double): String {
    val nf = NumberFormat.getCurrencyInstance(Locale("nl", "NL"))
    return nf.format(value)
}

@Composable
fun ReservationListItem(
    data: ReservationCardData,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
){
    val shape = RoundedCornerShape(12.dp)

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = shape,
        color = Color(0xFFE9EEF5),
        border = BorderStroke(1.dp, Color(0xFF1C2430)),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        onClick = { onClick?.invoke() },
        enabled = onClick != null
    ){
        Row(
            modifier = Modifier
                .heightIn(min = 74.dp)
                .padding(end = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = if (data.imageUrl == "error") null else data.imageUrl,
                contentDescription = data.title,
                fallback = painterResource(R.drawable.logo),
                modifier = Modifier
                    .size(74.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 12.dp,
                            bottomStart = 12.dp,
                            topEnd = 0.dp,
                            bottomEnd = 0.dp
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp, end = 8.dp, top = 10.dp, bottom = 10.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = data.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF0B0F14),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = data.location,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF0B0F14),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Column(
                modifier = Modifier.padding(end = 10.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = modifier
                        .clip(CircleShape)
                        .background(Color(0xFF82D39F))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = data.status,
                        color = Color(0xFF0B0F14),
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1
                    )
                }
            }

            Text(
                text = formatEuroNl(data.totalCost),
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF030303),
                maxLines = 1,
                overflow = TextOverflow.Clip
            )
        }
    }
}