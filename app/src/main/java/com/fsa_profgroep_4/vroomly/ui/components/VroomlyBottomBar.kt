package com.fsa_profgroep_4.vroomly.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.fsa_profgroep_4.vroomly.R

data class BottomNavItem(
    val route: String,
    val label: String,
    @param:DrawableRes val selectedIconRes: Int? = null,
    @param:DrawableRes val unselectedIconRes: Int? = null,
    val isLogo: Boolean = false
)

@Composable
fun VroomlyBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val navItems = listOf(
        BottomNavItem(
            route = "home",
            label = "Home",
            selectedIconRes = R.drawable.ic_home,
            unselectedIconRes = R.drawable.ic_home_outlined
        ),
        BottomNavItem(
            route = "search",
            label = "Search",
            selectedIconRes = R.drawable.ic_search,
            unselectedIconRes = R.drawable.ic_search
        ),
        BottomNavItem(
            route = "",
            label = "",
            isLogo = true
        ),
        BottomNavItem(
            route = "reservations",
            label = "Reservations",
            selectedIconRes = R.drawable.ic_directions_car,
            unselectedIconRes = R.drawable.ic_directions_car_outlined
        ),
        BottomNavItem(
            route = "account",
            label = "Account",
            selectedIconRes = R.drawable.ic_account_circle,
            unselectedIconRes = R.drawable.ic_account_circle_outlined
        )
    )

    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        navItems.forEach { item ->
            if (item.isLogo) {
                NavigationBarItem(
                    selected = false,
                    onClick = { /* Logo doesn't navigate */ },
                    modifier = Modifier.testTag("bottom_nav_logo"),
                    icon = {
                        Image(
                            painter = painterResource(id = R.drawable.logo_small),
                            contentDescription = "Vroomly Logo",
                            modifier = Modifier.size(32.dp)
                        )
                    },
                    label = null,
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = MaterialTheme.colorScheme.surface
                    ),
                    enabled = false
                )
            } else {
                val isSelected = currentRoute == item.route
                val iconRes = if (isSelected) item.selectedIconRes!! else item.unselectedIconRes!!

                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onNavigate(item.route) },
                    modifier = Modifier.testTag("bottom_nav_${item.route}"),
                    icon = {
                        Icon(
                            painter = painterResource(id = iconRes),
                            contentDescription = item.label,
                            modifier = Modifier.size(28.dp)
                        )
                    },
                    label = null,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        indicatorColor = MaterialTheme.colorScheme.surface
                    )
                )
            }

        }
    }
}
