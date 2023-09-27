package com.kirillyemets.catapp.ui.screens.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kirillyemets.catapp.domain.model.ThemeProfile
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsScreenViewModel = getViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val currentTheme = state.currentTheme

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "Settings")
                },
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            ThemeChooser(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp) ,
                selectedOption = currentTheme,
                onThemeProfileChange = { profile ->
                    viewModel.setThemeProfile(profile)
                }
            )
        }
    }
}

@Composable
fun ThemeChooser(
    selectedOption: ThemeProfile,
    modifier: Modifier = Modifier,
    onThemeProfileChange: (ThemeProfile) -> Unit
) {
    Column(modifier) {
        Text(
            modifier = Modifier.padding(bottom = 8.dp),
            text = "Theme"
        )

        ThemeProfile.values().forEach { option ->
            RadioButtonCard(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                text = { Text(option.label) },
                onClick = { onThemeProfileChange(option) },
                selected = selectedOption == option
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RadioButtonCard(
    modifier: Modifier = Modifier,
    text: @Composable () -> Unit,
    onClick: () -> Unit,
    selected: Boolean
) {
    Card(
        modifier = modifier,
        onClick = onClick
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                modifier = Modifier.padding(8.dp),
                selected = selected,
                onClick = onClick
            )
            text()
        }
    }
}