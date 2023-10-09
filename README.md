# Telegram mini app
Library for creating telegram [mini apps](https://core.telegram.org/bots/webapps) with Kotlin Multiplatform and Compose Multiplatform.

## Setup
1. Create Kotlin Multiplatform app with js target. Check [this tutorial](https://www.jetbrains.com/lp/compose-multiplatform/) for more information.

2. Place script `telegram-web-app.js` in the `<head>` tag:
```
<script src="https://telegram.org/js/telegram-web-app.js"></script>
```
3. Add implementation for `jsMain` `tg-mini-app` library:
```
implementation("io.github.kirill:tg-mini-app:1.0.0")
```
4. In `main` funtion of `jsMain` call `telegramWebApp` with providing Composable content:
```
fun main() {
   telegramWebApp {
      // Compose content ...
   }
}
```
## WebApp

To get access of Telegram web app instance call `webApp`. All properties and functions of [Telegram WebApp](https://core.telegram.org/bots/webapps#initializing-mini-apps) are available in kotlin style.

### Example
Showing confirmation dialog before closing app.
```
webApp.showConfirm("Want to exit?") { result ->
  if (result) {
      webApp.close()
  }
}
```

## Designing

Mini apps should be designed to provide smooth user experience so pay attention of themeing of app. Check [Telegram's Design Guidelines](https://core.telegram.org/bots/webapps#design-guidelines) for more information.

By default **tg-mini-app** use default Material Theme colors, but you can set your own color pallete. To do it just pass your own implementation of `ColorsConverter` to `telegramWebApp`. There you can define your own colors based on [Telegram App colors](https://telegram.org/blog/protected-content-delete-by-date-and-more/ru?setln=en#global-chat-themes-on-android) and current colorScheme (_light or dark_).
All colors are available through `MaterialTheme.colors`.

### Example
Set colors adaptive to current Telegram chat themes and Color Scheme.
```
class CustomConverter : ColorsConverter {

    override fun convert(themeParams: TelegramColors, colorScheme: ColorScheme): Colors = when(colorScheme) {
        ColorScheme.DARK -> darkColors(
            primary = themeParams.buttonColor,
            onPrimary = themeParams.buttonTextColor,
            background = themeParams.backgroundColor,
        )
        ColorScheme.LIGHT -> lightColors(
            primary = themeParams.buttonColor,
            onPrimary = themeParams.buttonTextColor,
            background = themeParams.backgroundColor,
        )
    }
    
}
```

In case your app contains more complicated scheme of theme setting like dynamic theme switching, you should provide implementation of `ThemeHandler` that has only one filed - `Flow` of accepted colors. By default new colors accepted every time color scheme is changed.

Also in case your app suppports dynamic theme switching you can pass `animationDuration` to `telegramWebApp` to manage animation of theme switching.
