package com.example.mycomposeapp.ui.screen.leaderboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.mycomposeapp.R
import com.example.mycomposeapp.ui.common.extensions.CollectWithLifecycle
import com.example.mycomposeapp.ui.theme.AppColorScheme
import com.example.mycomposeapp.ui.theme.AppTheme
import com.example.mycomposeapp.ui.theme.AppTheme.colors
import com.example.mycomposeapp.ui.theme.AppTheme.radius
import com.example.mycomposeapp.ui.theme.AppTheme.spacing

@Composable
fun LeaderboardScreen(
    navController: NavController,
    snackBarHostState: SnackbarHostState,
    viewModel: LeaderboardViewModel = hiltViewModel()
){
    val state by viewModel.uiState.collectAsState()

    viewModel.sideEffect.CollectWithLifecycle { effect ->
        when (effect) {
            is LeaderboardContract.SideEffect.ShowSnackBar -> {
                snackBarHostState.showSnackbar(effect.message)
            }
        }
    }

    LeaderboardContent(
        state =state,
        onEvent = viewModel::onEvent
    )
}
@Composable
private fun LeaderboardContent(
    state: LeaderboardContract.State,
    onEvent: (LeaderboardContract.Event) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.splash),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column {
            LeaderboardsTopBar(
                onBackClick = { },
                onInfoClick = { },
            )
            Spacer(modifier = Modifier.height(spacing.spacing32))
            LeaderboardCategories(

            )

            Spacer(modifier = Modifier.height(spacing.spacing64))
            TopLeaderboard()
        }



    }

}
@Composable
private fun LeaderboardsTopBar(
    onBackClick: () -> Unit,
    onInfoClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = onInfoClick) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = "Info",
                    tint = Color.White
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = spacing.spacing64),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Leaderboards",
                style = TextStyle(
                    brush = colors.goldTextGradient,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardCategories(
    options: List<String> = listOf("Movies", "Gold League", "Silver League"),
    selected: String = options.first(),
    onSelectedChange: (String) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            modifier = Modifier
                .menuAnchor(
                    type = MenuAnchorType.PrimaryEditable,
                    enabled = true
                )
                .fillMaxWidth()
                .padding(horizontal = spacing.spacing32),
            value = selected,
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            label = { Text("Category") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(
                focusedContainerColor = colors.darkSurfaceMedium,
                unfocusedContainerColor = colors.darkSurfaceLight,
                disabledContainerColor = colors.darkSurfaceDisabled,

                focusedTextColor = colors.textLight,
                unfocusedTextColor = colors.textLight,

                focusedIndicatorColor = colors.transparent,
                unfocusedIndicatorColor = colors.transparent
            )

        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelectedChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
@Composable
private fun TopLeaderboard(){
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier
            .padding(horizontal = spacing.spacing16)
            .fillMaxWidth()
    ) {
        LeaderboardAvatarCard(
            name = "Nika",
            points = 1433,
            rank = 2
        )
        LeaderboardAvatarCard(
            name = "Nika",
            points = 1433,
            rank = 1
        )
        LeaderboardAvatarCard(
            name = "Nika",
            points = 1433,
            rank = 3
        )
    }
}
@Composable
fun LeaderboardAvatarCard(
    modifier: Modifier = Modifier,
    name: String = "nika",
    points: Int = 1000,
    rank: Int = 2
) {
    val scale = if (rank == 1) 1.3f else 1f
    Column(
        modifier = modifier.height(160.dp)
            .scale(scale),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .wrapContentSize()
            ,
            contentAlignment = Alignment.Center
        ) {
            val medalColor = when (rank) {
                1 -> colors.gold
                2 -> colors.silver
                3 -> colors.bronze
                else -> MaterialTheme.colorScheme.outline
            }
            Surface(
                shape = CircleShape,
                tonalElevation = 2.dp,
                modifier = Modifier
                    .size(86.dp)
                    .border(
                        width = 4.dp,
                        color = medalColor,
                        shape = CircleShape
                    )
            ) {
                Image(
                    painter = painterResource(id = R.drawable.app_logo),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            }

            Surface(
                shape = radius.radius12,
                tonalElevation = 2.dp,
                color = medalColor,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = (10).dp)
            ) {
                Text(
                    text = "#".plus(rank.toString()),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),

                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = name,
            color = colors.white
        )
        Text(text = points.toString().plus(" pts"), color = colors.white)
    }
}
@Composable
private fun Leaderboard(
    users: List<String>
){
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ){
        items(users){
            LeaderboardUserCard(
                it
            )
        }
    }
}

@Composable
private fun LeaderboardUserCard(
    user: String
){
    Row(horizontalArrangement = Arrangement.SpaceBetween){
        Text(text=user)

        Row(){

        }
    }
}


@Preview(
    name = "Leaderboard Card",
    showBackground = true,
    backgroundColor = 0xFF0F1023
)
@Composable
fun LeaderboardAvatarCardPreview() {
    MaterialTheme {
        LeaderboardAvatarCard(
            name = "Jordan",
            points = 22100,
            rank = 2,
            modifier = Modifier.padding(24.dp)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LeaderboardContentPreview() {
    LeaderboardContent(
        state = LeaderboardContract.State(),
        onEvent = {}
    )
}