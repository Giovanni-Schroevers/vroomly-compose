package com.fsa_profgroep_4.vroomly.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.fsa_profgroep_4.vroomly.R
import coil.compose.AsyncImage
import java.text.NumberFormat
import java.util.Locale

data class VehicleCardUi(
    val vehicleId: Int,
    val imageUrl: String,
    val title: String,
    val location: String,
    val owner: String,
    val tagText: String,
    val badgeText: String,
    val costPerDay: Double
)

private fun formatEuroNl(value: Double): String {
    val nf = NumberFormat.getCurrencyInstance(Locale("nl", "NL"))
    return nf.format(value)
}

@Composable
fun VehicleListItem(
    data: VehicleCardUi,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val shape = RoundedCornerShape(12.dp)

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = shape,
        color = Color(0xFFF3F3F3),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        onClick = { onClick?.invoke() },
        enabled = onClick != null
    ) {
        Row(
            modifier = Modifier
                .heightIn(min = 74.dp)
                .padding(end = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = if (data.imageUrl == "error") null else data.imageUrl,
                contentDescription = data.title,
                fallback = painterResource(R.drawable.vroomly_logo),
                placeholder = painterResource(R.drawable.vroomly_logo),
                contentScale = ContentScale.Crop,
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
                    color = Color(0xFF1A1A1A),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = data.location,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF666666),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = data.owner,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF666666),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Column(
                modifier = Modifier.padding(end = 10.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                ChipPillSmall(
                    text = data.tagText,
                    bg = Color(0xFFFF9F1A),
                    fg = Color(0xFF1A1206)
                )
                Spacer(Modifier.height(3.dp))
                ChipPillSmall(
                    text = data.badgeText + "★",
                    bg = Color(0xFF3DA5FF),
                    fg = Color(0xFF06131C)
                )
            }

            Text(
                text = formatEuroNl(data.costPerDay),
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF1A1A1A),
                maxLines = 1,
                overflow = TextOverflow.Clip
            )
        }
    }
}

@Composable
private fun ChipPillSmall(
    text: String,
    bg: Color,
    fg: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(bg)
            .padding(horizontal = 4.dp, vertical = 1.dp)
    ) {
        Text(
            text = text,
            color = fg,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1
        )
    }
}
