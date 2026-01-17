package com.fsa_profgroep_4.vroomly.ui.screens.drive.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.rocketreserver.type.LocationSnapshotInput
import com.fsa_profgroep_4.vroomly.R
import com.fsa_profgroep_4.vroomly.ui.components.VroomlyButton

@Composable
fun DriveSavedContent(
    routePoints: List<LocationSnapshotInput>,
    onGoHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.drive_saved),
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(16.dp))
        ) {
            DriveRouteMap(routePoints = routePoints)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        VroomlyButton(
            onClick = onGoHome,
            text = stringResource(R.string.go_to_home)
        )
    }
}
