# Showcase bot branding

Identity of the demo bot `@tgminiapp_demo_bot` and its Mini App.

| File | Size | Use |
|---|---|---|
| `avatar-640.png` | 640×640 | Bot profile photo. Telegram crops it to a circle; the mark stays inside the safe area. |
| `banner-640x360.png` | 640×360 | Mini App photo (`/newapp`, `/editapp`) and bot description picture (`/setdescriptionpic`). |
| `banner-1280x720.png` | 1280×720 | The same banner for places that accept larger images (README, social previews). |

The mark is a Mini App window with a grid of capability tiles, in a gradient from Telegram-like blue (`#2AABEE`)
to violet (`#6A4DF4`); the accent tile uses `#8F5CFF → #E0457B → #FF9A3C`. It does not reuse the Telegram or Kotlin logos.

## Apply in BotFather

- `/setuserpic` → choose the bot → send `avatar-640.png` as a photo.
- `/setdescriptionpic` → choose the bot → send `banner-640x360.png`.
- `/myapps` → choose the Mini App → Edit photo → send `banner-640x360.png`.

## Edit

`avatar.svg` and `banner.svg` are the sources; the banner uses the Inter font from Google Fonts.
Regenerate the PNGs with the Playwright Chromium from the E2E tests:

```bash
cd samples/showcase/branding && node render.mjs
```
