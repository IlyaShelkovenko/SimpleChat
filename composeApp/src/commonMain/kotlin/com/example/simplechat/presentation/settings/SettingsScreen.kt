package com.example.simplechat.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.simplechat.presentation.app.SimpleChatViewModelFactory
import kotlin.math.roundToInt

@Composable
fun SettingsRoute(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = viewModel(factory = SimpleChatViewModelFactory.settingsViewModelFactory())
) {
    val uiState by viewModel.uiState.collectAsState()

    SettingsScreen(
        state = uiState,
        onBack = onBack,
        onApiKeyChanged = { viewModel.onEvent(SettingsEvent.ApiKeyChanged(it)) },
        onModelChanged = { viewModel.onEvent(SettingsEvent.ModelChanged(it)) },
        onPromptChanged = { viewModel.onEvent(SettingsEvent.CustomSystemPromptChanged(it)) },
        onCustomToggleChanged = { viewModel.onEvent(SettingsEvent.CustomSystemPromptEnabledChanged(it)) },
        onJsonToggleChanged = { viewModel.onEvent(SettingsEvent.JsonFormatEnabledChanged(it)) },
        onTemperatureChanged = { viewModel.onEvent(SettingsEvent.TemperatureChanged(it)) },
        onSave = { viewModel.onEvent(SettingsEvent.Save) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: SettingsUiState,
    onBack: () -> Unit,
    onApiKeyChanged: (String) -> Unit,
    onModelChanged: (String) -> Unit,
    onPromptChanged: (String) -> Unit,
    onCustomToggleChanged: (Boolean) -> Unit,
    onJsonToggleChanged: (Boolean) -> Unit,
    onTemperatureChanged: (Double) -> Unit,
    onSave: () -> Unit
) {
    val temperatureOptions = listOf(0.0, 0.3, 0.7, 1.0)
    var isTemperatureMenuExpanded by remember { mutableStateOf(false) }
    var isModelMenuExpanded by remember { mutableStateOf(false) }
    val selectedTemperatureText = remember(state.temperature) { state.temperature.toTemperatureDisplay() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "API key",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    OutlinedTextField(
                        value = state.apiKey,
                        onValueChange = onApiKeyChanged,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        placeholder = { Text("Enter your Hugging Face token") }
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Model",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    ExposedDropdownMenuBox(
                        expanded = isModelMenuExpanded,
                        onExpandedChange = { isModelMenuExpanded = !isModelMenuExpanded }
                    ) {
                        OutlinedTextField(
                            value = state.selectedModel,
                            onValueChange = {},
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isModelMenuExpanded)
                            }
                        )
                        ExposedDropdownMenu(
                            expanded = isModelMenuExpanded,
                            onDismissRequest = { isModelMenuExpanded = false }
                        ) {
                            state.availableModels.forEach { model ->
                                DropdownMenuItem(
                                    text = { Text(model) },
                                    onClick = {
                                        isModelMenuExpanded = false
                                        onModelChanged(model)
                                    }
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Custom system prompt",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Define how the assistant should format its responses.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = state.isCustomPromptEnabled,
                        onCheckedChange = onCustomToggleChanged,
                        enabled = !state.isJsonFormatEnabled
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Use JSON format",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Request responses structured as JSON using the API parameter.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = state.isJsonFormatEnabled,
                        onCheckedChange = onJsonToggleChanged,
                        enabled = !state.isCustomPromptEnabled
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Temperature",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Adjust response creativity (higher values are more random).",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    ExposedDropdownMenuBox(
                        expanded = isTemperatureMenuExpanded,
                        onExpandedChange = { isTemperatureMenuExpanded = !isTemperatureMenuExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedTemperatureText,
                            onValueChange = {},
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isTemperatureMenuExpanded)
                            }
                        )
                        ExposedDropdownMenu(
                            expanded = isTemperatureMenuExpanded,
                            onDismissRequest = { isTemperatureMenuExpanded = false }
                        ) {
                            temperatureOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.toTemperatureDisplay()) },
                                    onClick = {
                                        isTemperatureMenuExpanded = false
                                        onTemperatureChanged(option)
                                    }
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = state.customSystemPrompt,
                    onValueChange = onPromptChanged,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    placeholder = {
                        Text(
                            "e.g. Reply using bullet points and include a summary at the end.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    enabled = state.isCustomPromptEnabled,
                    singleLine = false,
                )

                state.errorMessage?.let { message ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                        )
                    }
                }

                if (state.isSaved) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Settings saved",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                        )
                    }
                }

                Button(
                    onClick = onSave,
                    enabled = !state.isSaving,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(if (state.isSaving) "Saving..." else "Save")
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

private fun Double.toTemperatureDisplay(): String {
    val normalized = ((this * 10.0).roundToInt() / 10.0)
    return normalized.toString()
}
