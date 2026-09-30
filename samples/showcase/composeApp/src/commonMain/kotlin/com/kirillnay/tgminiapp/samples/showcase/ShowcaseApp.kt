package com.kirillnay.tgminiapp.samples.showcase

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

private sealed interface Screen {
    data object Home : Screen
    data class Section(val section: FeatureSection) : Screen
    data object LaunchData : Screen
    data object EventLog : Screen
}

/** Shared UI of the showcase for every host. */
@Composable
fun ShowcaseApp(platform: ShowcasePlatform) {
    val backStack = remember { mutableStateListOf<Screen>(Screen.Home) }
    val results = remember { mutableStateMapOf<String, FeatureResult>() }
    val running = remember { mutableStateMapOf<String, Boolean>() }
    val goBack: () -> Unit = { if (backStack.size > 1) backStack.removeAt(backStack.lastIndex) }

    DisposableEffect(backStack.size) {
        platform.onBackAvailable(if (backStack.size > 1) goBack else null)
        onDispose { }
    }

    MaterialTheme(colorScheme = platform.palette.toColorScheme()) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Column(modifier = Modifier.padding(platform.contentPadding)) {
                if (backStack.size > 1 && !platform.hasNativeBackButton) {
                    TextButton(onClick = goBack, modifier = Modifier.padding(start = 8.dp, top = 8.dp)) { Text("← Back") }
                }
                when (val screen = backStack.last()) {
                    Screen.Home -> HomeScreen(platform, open = { backStack.add(it) })
                    is Screen.Section -> SectionScreen(platform, screen.section, results, running)
                    Screen.LaunchData -> LaunchDataScreen(platform)
                    Screen.EventLog -> EventLogScreen(platform)
                }
            }
        }
    }
}

@Composable
private fun HomeScreen(platform: ShowcasePlatform, open: (Screen) -> Unit) {
    val events by platform.eventLog.collectAsState()
    val available = Catalog.features.count { platform.availability(it) == Availability.Available }

    ScreenList {
        item {
            Column(modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)) {
                Text("tg-mini-app Showcase", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
                Text(
                    "Every Telegram Mini Apps capability wrapped by tg-mini-app. $available of ${Catalog.features.size} " +
                        "features are available on ${platform.hostName}.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        item { NavigationCard("Launch data", "Init data, theme, viewport, safe area and live sensor values.") { open(Screen.LaunchData) } }
        item { NavigationCard("Event log", "${events.size} Telegram events received.") { open(Screen.EventLog) } }
        items(Catalog.sections, key = { it.id }) { section ->
            val count = section.features.count { platform.availability(it) == Availability.Available }
            NavigationCard(section.title, "${section.description} $count/${section.features.size} available.") {
                open(Screen.Section(section))
            }
        }
    }
}

@Composable
private fun NavigationCard(title: String, subtitle: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun SectionScreen(
    platform: ShowcasePlatform,
    section: FeatureSection,
    results: MutableMap<String, FeatureResult>,
    running: MutableMap<String, Boolean>,
) {
    ScreenList {
        item {
            Column(modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)) {
                Text(section.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
                Text(section.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        items(section.features, key = { it.id }) { feature ->
            FeatureCard(platform, feature, results[feature.id], running[feature.id] == true) { inputs ->
                running[feature.id] = true
                val result = runCatching { platform.run(feature, inputs) }
                    .getOrElse { FeatureResult.Failure(it.message ?: it.toString()) }
                results[feature.id] = result
                running[feature.id] = false
            }
        }
    }
}

@Composable
private fun FeatureCard(
    platform: ShowcasePlatform,
    feature: Feature,
    result: FeatureResult?,
    isRunning: Boolean,
    onRun: suspend (Map<String, String>) -> Unit,
) {
    val availability = platform.availability(feature)
    val values = remember(feature.id) { mutableStateMapOf(*feature.inputs.map { it.key to it.default }.toTypedArray()) }
    val scope = rememberCoroutineScope()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(feature.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                Text(
                    feature.minBotApi?.let { "Bot API $it+" } ?: "All versions",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.tertiary,
                )
            }
            Text(feature.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            feature.inputs.forEach { input ->
                if (input.options.isEmpty()) {
                    OutlinedTextField(
                        value = values[input.key].orEmpty(),
                        onValueChange = { values[input.key] = it },
                        label = { Text(input.label) },
                        supportingText = input.hint?.let { { Text(it) } },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                } else {
                    Text(input.label, style = MaterialTheme.typography.labelMedium)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        input.options.forEach { option ->
                            FilterChip(
                                selected = values[input.key] == option,
                                onClick = { values[input.key] = option },
                                label = { Text(option) },
                            )
                        }
                    }
                }
            }

            when (availability) {
                Availability.Available -> Unit
                is Availability.RequiresBotApi -> Note("Requires Bot API ${availability.version}, this Telegram app supports ${availability.current}.")
                is Availability.Unavailable -> Note(availability.reason)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = { scope.launch { onRun(values.toMap()) } },
                    enabled = availability == Availability.Available && !isRunning,
                ) {
                    Text(feature.actionLabel)
                }
                if (isRunning) {
                    Spacer(Modifier.width(12.dp))
                    Text("Waiting for Telegram…", style = MaterialTheme.typography.bodySmall)
                }
            }

            if (result != null) {
                Text(
                    result.message,
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    color = when (result) {
                        is FeatureResult.Success -> MaterialTheme.colorScheme.primary
                        is FeatureResult.Failure -> MaterialTheme.colorScheme.error
                    },
                )
            }
        }
    }
}

@Composable
private fun Note(text: String) {
    Text(text, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
}

@Composable
private fun LaunchDataScreen(platform: ShowcasePlatform) {
    val groups by platform.launchData.collectAsState()

    ScreenList {
        item { Title("Launch data") }
        items(groups, key = { it.title }) { group ->
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(group.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    group.entries.forEachIndexed { index, (key, value) ->
                        if (index > 0) HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        Row {
                            Text(key, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(0.4f))
                            Text(value, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(0.6f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EventLogScreen(platform: ShowcasePlatform) {
    val events by platform.eventLog.collectAsState()

    ScreenList {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Title("Event log", modifier = Modifier.weight(1f))
                TextButton(onClick = platform::clearEventLog) { Text("Clear log") }
            }
        }
        if (events.isEmpty()) {
            item { Text("No events yet. Interact with the Mini App or Telegram UI.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
        }
        items(events.asReversed(), key = { it.index }) { entry ->
            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Text("#${entry.index} ${entry.name}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                if (entry.details.isNotEmpty()) {
                    Text(entry.details, style = MaterialTheme.typography.bodySmall, fontFamily = FontFamily.Monospace)
                }
            }
        }
    }
}

@Composable
private fun Title(text: String, modifier: Modifier = Modifier) {
    Text(text, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold, modifier = modifier.padding(4.dp))
}

@Composable
private fun ScreenList(content: androidx.compose.foundation.lazy.LazyListScope.() -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        content = content,
    )
}

private fun ShowcasePalette.toColorScheme() = if (isDark) {
    darkColorScheme(
        primary = primary, onPrimary = onPrimary, background = background, onBackground = text,
        surface = surface, onSurface = text, onSurfaceVariant = hint, tertiary = accent,
        error = destructive, outlineVariant = separator, secondaryContainer = primary, onSecondaryContainer = onPrimary,
    )
} else {
    lightColorScheme(
        primary = primary, onPrimary = onPrimary, background = background, onBackground = text,
        surface = surface, onSurface = text, onSurfaceVariant = hint, tertiary = accent,
        error = destructive, outlineVariant = separator, secondaryContainer = primary, onSecondaryContainer = onPrimary,
    )
}
