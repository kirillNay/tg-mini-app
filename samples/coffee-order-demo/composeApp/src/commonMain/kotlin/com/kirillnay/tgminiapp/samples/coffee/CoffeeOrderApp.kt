package com.kirillnay.tgminiapp.samples.coffee

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

private val CoffeeMenu = listOf(
    Drink(
        id = "cappuccino",
        name = "Cappuccino",
        roast = "Balanced roast",
        shortDescription = "Classic milk foam and espresso for the everyday Telegram checkout flow.",
        longDescription = "A compact example item that shows a detailed card, a platform main button action, and cart state shared across Android, iOS, and Web.",
        priceCents = 470,
    ),
    Drink(
        id = "flat-white",
        name = "Flat White",
        roast = "Bright roast",
        shortDescription = "Smaller milk ratio, stronger coffee profile, fast add-to-cart path.",
        longDescription = "Useful for the sample because it makes quantity changes visible and demonstrates cart totals without adding extra feature surface.",
        priceCents = 520,
    ),
    Drink(
        id = "cold-brew",
        name = "Cold Brew",
        roast = "Slow steep",
        shortDescription = "A chilled option that makes the product list feel like a real catalog.",
        longDescription = "The third product gives the sample enough variation to exercise navigation, settings, and order confirmation while keeping the app small.",
        priceCents = 560,
    ),
)

private enum class RootDestination(val title: String) {
    Menu("Menu"),
    Cart("Cart"),
    Settings("Settings"),
}

data class Drink(
    val id: String,
    val name: String,
    val roast: String,
    val shortDescription: String,
    val longDescription: String,
    val priceCents: Int,
)

data class CartLine(
    val drink: Drink,
    val quantity: Int,
)

data class BridgeAction(
    val label: String,
    val onClick: () -> Unit,
)

data class AppPalette(
    val isDark: Boolean,
    val background: Color,
    val surface: Color,
    val surfaceAccent: Color,
    val primary: Color,
    val onPrimary: Color,
    val text: Color,
    val mutedText: Color,
    val border: Color,
)

data class AppEnvironment(
    val palette: AppPalette,
    val platformLabel: String,
    val runtimeLabel: String,
    val userLabel: String,
    val usernameLabel: String?,
    val storageLabel: String,
    val viewportLabel: String,
    val themeLabel: String,
    val isTelegramRuntime: Boolean,
)

interface PlatformAppBridge {
    val environment: AppEnvironment

    suspend fun loadNote(): Result<String>

    suspend fun saveNote(note: String): Result<Unit>

    suspend fun confirmOrder(summary: String): Boolean

    fun updateChrome(backAction: (() -> Unit)?, mainAction: BridgeAction?)

    fun clearChrome()

    fun onItemAdded()

    fun onOrderCompleted()
}

class DemoPlatformBridge(
    override val environment: AppEnvironment,
) : PlatformAppBridge {

    private var note: String = ""

    override suspend fun loadNote(): Result<String> = Result.success(note)

    override suspend fun saveNote(note: String): Result<Unit> {
        this.note = note
        return Result.success(Unit)
    }

    override suspend fun confirmOrder(summary: String): Boolean = true

    override fun updateChrome(backAction: (() -> Unit)?, mainAction: BridgeAction?) = Unit

    override fun clearChrome() = Unit

    override fun onItemAdded() = Unit

    override fun onOrderCompleted() = Unit
}

fun demoEnvironment(platformName: String, isDark: Boolean): AppEnvironment {
    val palette = if (isDark) {
        AppPalette(
            isDark = true,
            background = Color(0xFF101315),
            surface = Color(0xFF171C1F),
            surfaceAccent = Color(0xFF1F2529),
            primary = Color(0xFFF5C86B),
            onPrimary = Color(0xFF2A1C00),
            text = Color(0xFFF2F4F5),
            mutedText = Color(0xFF9AA8B2),
            border = Color(0xFF2A3339),
        )
    } else {
        AppPalette(
            isDark = false,
            background = Color(0xFFF6F1E8),
            surface = Color(0xFFFFFBF4),
            surfaceAccent = Color(0xFFF0E6D5),
            primary = Color(0xFF6B3F1D),
            onPrimary = Color(0xFFFFF8F1),
            text = Color(0xFF24170F),
            mutedText = Color(0xFF7B6A5A),
            border = Color(0xFFE3D4BE),
        )
    }

    return AppEnvironment(
        palette = palette,
        platformLabel = platformName,
        runtimeLabel = "$platformName demo host",
        userLabel = "Demo guest",
        usernameLabel = null,
        storageLabel = "In-memory demo storage",
        viewportLabel = "Native full-screen host",
        themeLabel = if (isDark) "Dark demo palette" else "Light demo palette",
        isTelegramRuntime = false,
    )
}

@Composable
fun TelegramRuntimePlaceholder(
    modifier: Modifier = Modifier,
) {
    CoffeeOrderTheme(palette = demoEnvironment(platformName = "Web", isDark = false).palette) {
        Surface(
            modifier = modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center,
            ) {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(
                            text = "Telegram runtime not found",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = "This web sample is initialized only inside Telegram Mini App runtime. rawInitData is empty, so tg-mini-app is not started.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = "Open the demo from Telegram, or use the Android and iOS demo hosts from samples/coffee-order-demo.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CoffeeOrderDemoApp(
    bridge: PlatformAppBridge,
    modifier: Modifier = Modifier,
) {
    val environment = bridge.environment
    val cartLines = remember { mutableStateListOf<CartLine>() }
    val scope = rememberCoroutineScope()

    var rootDestination by remember { mutableStateOf(RootDestination.Menu) }
    var selectedDrinkId by remember { mutableStateOf<String?>(null) }
    var isCheckoutOpen by remember { mutableStateOf(false) }
    var note by remember { mutableStateOf("") }
    var noteStatus by remember { mutableStateOf("Loading saved note...") }
    var checkoutStatus by remember { mutableStateOf("No orders yet.") }

    val selectedDrink = remember(selectedDrinkId) { CoffeeMenu.firstOrNull { it.id == selectedDrinkId } }
    val totalItems = cartLines.sumOf { it.quantity }
    val totalPrice = cartLines.sumOf { it.quantity * it.drink.priceCents }

    fun addDrink(drink: Drink) {
        val index = cartLines.indexOfFirst { it.drink.id == drink.id }
        if (index >= 0) {
            val line = cartLines[index]
            cartLines[index] = line.copy(quantity = line.quantity + 1)
        } else {
            cartLines += CartLine(drink = drink, quantity = 1)
        }
        bridge.onItemAdded()
        checkoutStatus = "${drink.name} added to cart."
    }

    fun changeQuantity(drinkId: String, delta: Int) {
        val index = cartLines.indexOfFirst { it.drink.id == drinkId }
        if (index < 0) return
        val current = cartLines[index]
        val updatedQuantity = current.quantity + delta
        if (updatedQuantity <= 0) {
            cartLines.removeAt(index)
        } else {
            cartLines[index] = current.copy(quantity = updatedQuantity)
        }
    }

    fun checkoutSummary(): String = buildString {
        append("Order total: ")
        append(formatPrice(totalPrice))
        append(". ")
        append("Items: ")
        append(totalItems)
        append(".")
    }

    fun completeOrder() {
        if (cartLines.isEmpty()) return
        scope.launch {
            val confirmed = bridge.confirmOrder(checkoutSummary())
            if (confirmed) {
                bridge.onOrderCompleted()
                checkoutStatus = "Order placed for ${formatPrice(totalPrice)}."
                cartLines.clear()
                isCheckoutOpen = false
                rootDestination = RootDestination.Menu
            } else {
                checkoutStatus = "Order confirmation was cancelled."
            }
        }
    }

    LaunchedEffect(bridge) {
        bridge.loadNote().fold(
            onSuccess = { savedNote ->
                note = savedNote
                noteStatus = if (savedNote.isBlank()) "No saved note yet." else "Saved note loaded."
            },
            onFailure = { error ->
                noteStatus = error.message ?: "Unable to read saved note."
            },
        )
    }

    val backAction = when {
        selectedDrink != null -> ({
            selectedDrinkId = null
        })
        isCheckoutOpen -> ({
            isCheckoutOpen = false
        })
        else -> null
    }

    val mainAction = when {
        selectedDrink != null -> BridgeAction("Add ${formatPrice(selectedDrink.priceCents)}") {
            addDrink(selectedDrink)
        }
        isCheckoutOpen && cartLines.isNotEmpty() -> BridgeAction("Confirm ${formatPrice(totalPrice)}") {
            completeOrder()
        }
        rootDestination == RootDestination.Cart && cartLines.isNotEmpty() -> BridgeAction("Checkout $totalItems") {
            isCheckoutOpen = true
        }
        else -> null
    }

    SideEffect {
        bridge.updateChrome(backAction = backAction, mainAction = mainAction)
    }

    DisposableEffect(bridge) {
        onDispose {
            bridge.clearChrome()
        }
    }

    CoffeeOrderTheme(palette = environment.palette) {
        Surface(
            modifier = modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            Scaffold(
                contentWindowInsets = WindowInsets.safeDrawing,
                bottomBar = {
                    if (selectedDrink == null && !isCheckoutOpen) {
                        BottomNavigationBar(
                            selected = rootDestination,
                            onSelected = { rootDestination = it },
                            cartCount = totalItems,
                        )
                    }
                },
                containerColor = MaterialTheme.colorScheme.background,
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 18.dp),
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    HeaderCard(
                        environment = environment,
                        totalItems = totalItems,
                        totalPrice = totalPrice,
                        checkoutStatus = checkoutStatus,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    when {
                        selectedDrink != null -> DrinkDetailsScreen(
                            drink = selectedDrink,
                            quantity = cartLines.firstOrNull { it.drink.id == selectedDrink.id }?.quantity ?: 0,
                            onAdd = { addDrink(selectedDrink) },
                            onBack = { selectedDrinkId = null },
                        )
                        isCheckoutOpen -> CheckoutScreen(
                            lines = cartLines,
                            totalPrice = totalPrice,
                            note = note,
                            noteStatus = noteStatus,
                            onNoteChange = { note = it },
                            onSaveNote = {
                                scope.launch {
                                    bridge.saveNote(note).fold(
                                        onSuccess = { noteStatus = "Pickup note saved." },
                                        onFailure = { error ->
                                            noteStatus = error.message ?: "Unable to save note."
                                        },
                                    )
                                }
                            },
                            onConfirmOrder = { completeOrder() },
                            onBack = { isCheckoutOpen = false },
                        )
                        rootDestination == RootDestination.Menu -> MenuScreen(
                            drinks = CoffeeMenu,
                            onOpenDrink = { selectedDrinkId = it.id },
                            onQuickAdd = { addDrink(it) },
                        )
                        rootDestination == RootDestination.Cart -> CartScreen(
                            lines = cartLines,
                            totalPrice = totalPrice,
                            onDecrease = { changeQuantity(it, -1) },
                            onIncrease = { changeQuantity(it, 1) },
                            onCheckout = { isCheckoutOpen = true },
                        )
                        else -> SettingsScreen(
                            environment = environment,
                            note = note,
                            noteStatus = noteStatus,
                            onNoteChange = { note = it },
                            onSaveNote = {
                                scope.launch {
                                    bridge.saveNote(note).fold(
                                        onSuccess = { noteStatus = "Saved via ${environment.storageLabel}." },
                                        onFailure = { error ->
                                            noteStatus = error.message ?: "Unable to save note."
                                        },
                                    )
                                }
                            },
                            onReloadNote = {
                                scope.launch {
                                    bridge.loadNote().fold(
                                        onSuccess = { savedNote ->
                                            note = savedNote
                                            noteStatus = if (savedNote.isBlank()) {
                                                "No saved note yet."
                                            } else {
                                                "Saved note reloaded."
                                            }
                                        },
                                        onFailure = { error ->
                                            noteStatus = error.message ?: "Unable to reload note."
                                        },
                                    )
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HeaderCard(
    environment: AppEnvironment,
    totalItems: Int,
    totalPrice: Int,
    checkoutStatus: String,
) {
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "Coffee Order Demo",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = if (environment.isTelegramRuntime) {
                    "Shared Compose UI with real Telegram WebApp integration in jsMain."
                } else {
                    "Shared Compose UI running in a native demo host outside Telegram."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            EnvironmentSummary(
                environment = environment,
                totalItems = totalItems,
                totalPrice = totalPrice,
            )
            Text(
                text = checkoutStatus,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun EnvironmentSummary(
    environment: AppEnvironment,
    totalItems: Int,
    totalPrice: Int,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                shape = RoundedCornerShape(14.dp),
            )
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        EnvironmentLine(label = "Platform", value = environment.platformLabel)
        EnvironmentLine(label = "Theme", value = environment.themeLabel)
        EnvironmentLine(label = "Cart", value = "$totalItems items, ${formatPrice(totalPrice)}")
    }
}

@Composable
private fun EnvironmentLine(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun MenuScreen(
    drinks: List<Drink>,
    onOpenDrink: (Drink) -> Unit,
    onQuickAdd: (Drink) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            SectionTitle(
                title = "Catalog",
                subtitle = "Each item opens a detail screen and also supports a quick add flow.",
            )
        }
        items(drinks, key = { it.id }) { drink ->
            DrinkCard(
                drink = drink,
                onOpenDrink = { onOpenDrink(drink) },
                onQuickAdd = { onQuickAdd(drink) },
            )
        }
    }
}

@Composable
private fun DrinkCard(
    drink: Drink,
    onOpenDrink: () -> Unit,
    onQuickAdd: () -> Unit,
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onOpenDrink)
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = drink.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = drink.roast,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                PriceBadge(price = drink.priceCents)
            }
            Text(
                text = drink.shortDescription,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(onClick = onOpenDrink) {
                    Text("Details")
                }
                OutlinedButton(onClick = onQuickAdd) {
                    Text("Quick add")
                }
            }
        }
    }
}

@Composable
private fun PriceBadge(price: Int) {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primary, CircleShape)
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Text(
            text = formatPrice(price),
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun DrinkDetailsScreen(
    drink: Drink,
    quantity: Int,
    onAdd: () -> Unit,
    onBack: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            SectionTitle(
                title = drink.name,
                subtitle = "This screen is where Telegram WebApp back and main buttons become useful.",
            )
        }
        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = drink.roast,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = drink.longDescription,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text(
                                text = "In cart",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Text(
                                text = quantity.toString(),
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        PriceBadge(price = drink.priceCents)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(onClick = onAdd) {
                            Text("Add to cart")
                        }
                        OutlinedButton(onClick = onBack) {
                            Text("Back")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CartScreen(
    lines: List<CartLine>,
    totalPrice: Int,
    onDecrease: (String) -> Unit,
    onIncrease: (String) -> Unit,
    onCheckout: () -> Unit,
) {
    if (lines.isEmpty()) {
        EmptyState(
            title = "Cart is empty",
            body = "Add a drink from the catalog. In Telegram, this root screen also enables a checkout main button once the cart has items.",
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            SectionTitle(
                title = "Cart",
                subtitle = "Quantities stay in shared state while the host implementation changes by platform.",
            )
        }
        items(lines, key = { it.drink.id }) { line ->
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = line.drink.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = "${line.quantity} x ${formatPrice(line.drink.priceCents)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    QuantityControls(
                        quantity = line.quantity,
                        onDecrease = { onDecrease(line.drink.id) },
                        onIncrease = { onIncrease(line.drink.id) },
                    )
                }
            }
        }
        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest),
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(
                        text = "Total ${formatPrice(totalPrice)}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Button(onClick = onCheckout, modifier = Modifier.fillMaxWidth()) {
                        Text("Proceed to checkout")
                    }
                }
            }
        }
    }
}

@Composable
private fun QuantityControls(
    quantity: Int,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SmallRoundButton(label = "-", onClick = onDecrease)
        Text(
            text = quantity.toString(),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(24.dp),
        )
        SmallRoundButton(label = "+", onClick = onIncrease)
    }
}

@Composable
private fun SmallRoundButton(
    label: String,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        shape = CircleShape,
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier.size(36.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
    ) {
        Text(label)
    }
}

@Composable
private fun CheckoutScreen(
    lines: List<CartLine>,
    totalPrice: Int,
    note: String,
    noteStatus: String,
    onNoteChange: (String) -> Unit,
    onSaveNote: () -> Unit,
    onConfirmOrder: () -> Unit,
    onBack: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            SectionTitle(
                title = "Checkout",
                subtitle = "This screen is the best place to demonstrate `showConfirm`, note persistence, and platform main button wiring.",
            )
        }
        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    lines.forEach { line ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                text = "${line.quantity} x ${line.drink.name}",
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text = formatPrice(line.quantity * line.drink.priceCents),
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text("Total", fontWeight = FontWeight.SemiBold)
                        Text(formatPrice(totalPrice), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(
                        text = "Pickup note",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    OutlinedTextField(
                        value = note,
                        onValueChange = onNoteChange,
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        placeholder = { Text("Add pickup instructions") },
                    )
                    Text(
                        text = noteStatus,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(onClick = onSaveNote) {
                            Text("Save note")
                        }
                        OutlinedButton(onClick = onBack) {
                            Text("Back")
                        }
                    }
                }
            }
        }
        item {
            Button(onClick = onConfirmOrder, modifier = Modifier.fillMaxWidth()) {
                Text("Confirm order")
            }
        }
    }
}

@Composable
private fun SettingsScreen(
    environment: AppEnvironment,
    note: String,
    noteStatus: String,
    onNoteChange: (String) -> Unit,
    onSaveNote: () -> Unit,
    onReloadNote: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            SectionTitle(
                title = "Settings and debug",
                subtitle = "This area shows which pieces come from Telegram on Web and which ones are mocked on Android and iOS.",
            )
        }
        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    LabeledValue(label = "Runtime", value = environment.runtimeLabel)
                    LabeledValue(label = "User", value = environment.userLabel)
                    environment.usernameLabel?.let { username ->
                        LabeledValue(label = "Username", value = username)
                    }
                    LabeledValue(label = "Theme", value = environment.themeLabel)
                    LabeledValue(label = "Viewport", value = environment.viewportLabel)
                    LabeledValue(label = "Storage", value = environment.storageLabel)
                }
            }
        }
        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(
                        text = "Saved pickup note",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    OutlinedTextField(
                        value = note,
                        onValueChange = onNoteChange,
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        placeholder = { Text("Try saving a note here") },
                    )
                    Text(
                        text = noteStatus,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(onClick = onSaveNote) {
                            Text("Save")
                        }
                        OutlinedButton(onClick = onReloadNote) {
                            Text("Reload")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LabeledValue(
    label: String,
    value: String,
) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun SectionTitle(
    title: String,
    subtitle: String,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun EmptyState(
    title: String,
    body: String,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 24.dp),
        contentAlignment = Alignment.Center,
    ) {
        ElevatedCard(
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = body,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun BottomNavigationBar(
    selected: RootDestination,
    onSelected: (RootDestination) -> Unit,
    cartCount: Int,
) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        RootDestination.entries.forEach { destination ->
            val label = if (destination == RootDestination.Cart && cartCount > 0) {
                "Cart ($cartCount)"
            } else {
                destination.title
            }
            NavigationBarItem(
                selected = selected == destination,
                onClick = { onSelected(destination) },
                icon = {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(
                                color = if (selected == destination) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.outlineVariant
                                },
                                shape = CircleShape,
                            ),
                    )
                },
                label = { Text(label) },
            )
        }
    }
}

@Composable
private fun CoffeeOrderTheme(
    palette: AppPalette,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (palette.isDark) {
        darkColorScheme(
            background = palette.background,
            surface = palette.surface,
            surfaceContainerHighest = palette.surfaceAccent,
            primary = palette.primary,
            onPrimary = palette.onPrimary,
            onBackground = palette.text,
            onSurface = palette.text,
            onSurfaceVariant = palette.mutedText,
            outlineVariant = palette.border,
        )
    } else {
        lightColorScheme(
            background = palette.background,
            surface = palette.surface,
            surfaceContainerHighest = palette.surfaceAccent,
            primary = palette.primary,
            onPrimary = palette.onPrimary,
            onBackground = palette.text,
            onSurface = palette.text,
            onSurfaceVariant = palette.mutedText,
            outlineVariant = palette.border,
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}

private fun formatPrice(priceCents: Int): String {
    val dollars = priceCents / 100
    val cents = (priceCents % 100).toString().padStart(2, '0')
    return "$" + dollars + "." + cents
}
