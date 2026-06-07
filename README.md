# FTB Quests

A Minecraft plugin inspired by the FTB Quests mod, adding a quest system to your server. Players can complete objectives, earn rewards, and track their progress through an intuitive GUI.

## Features

- **Quest Categories** — Organize quests into categories with their own GUI
- **Multiple Objective Types** — Built-in types plus integrations with popular plugins
- **Custom GUI** — Fully customizable quest and category menus with sounds, titles, and custom items
- **Reward System** — Commands executed on quest completion (console or player)
- **Objective Commands** — Guide players with clickable commands in the quest GUI
- **Database** — SQLite (default) or MySQL for persistent player data
- **Integrations** — MMOItems, ItemsAdder, MythicMobs, Slimefun
- **PlaceholderAPI** — Support for placeholders in GUI items
- **Auto-save** — Automatic database saving at configurable intervals

## Installation

1. Place the `FTBQuests.jar` in your server's `plugins/` folder
2. Restart your server
3. Configure categories in `plugins/FTBQuests/category/` and quests in `plugins/FTBQuests/quests/`
4. Give yourself the quest book with `/questbook` or use direct command `/ftbquests open <category>`

## Configuration

The main configuration file: `plugins/FTBQuests/config.yml`

```yaml
config-version: 1
update-checker: true
auto-save-interval: 5              # Minutes between automatic DB saves
gui-click-cooldown: 200            # Milliseconds between GUI clicks

mysql-settings:
  enabled: false                    # true = MySQL, false = SQLite
  hostname: 0.0.0.0
  port: 0
  database-name: ''
  user-name: ''
  user-password: ''
  use-ssl: false
  connection-pool-size: 3
```

Messages are fully customizable under the `messages:` section using Minecraft color codes (`&`) and hex colors (`<#HEX>`). All messages support `{placeholder}` variables.

Quest book item properties are configurable under the `quest-book:` section including material, name, lore, sounds, and auto-give for new players.

## Categories

Category files are located in `plugins/FTBQuests/category/`. Each `.yml` file becomes its own category with a unique ID (the filename without `.yml`).

Example: `category/introduction.yml`

```yaml
gui-name: '&nQuests&8 -> Introduction'    # GUI title
gui-rows: 6                                 # GUI height (1-6 rows)

# Sounds for GUI interactions
sound:
  click:    { id: ITEM_BOOK_PAGE_TURN, pitch: 1.0, volume: 1.0 }
  complete: { id: ENTITY_PLAYER_LEVELUP, pitch: 1.0, volume: 1.0 }
  claim:    { id: BLOCK_NOTE_BLOCK_PLING, pitch: 1.0, volume: 1.0 }
  no:       { id: ENTITY_VILLAGER_NO, pitch: 1.0, volume: 1.0 }

# Completion notification settings
message-complete: { enable: true, message: '&aQuest Completed: &e{quest}' }
title-complete:   { enable: true, title: '%quest%', subtitle: '&fQuest Completed!', fadein: 10, stay: 40 }

# Quest layout (true = auto-arrange in quests-slots, false = manual slot/page per quest)
quests-automatic-layout: true
quests-slots: [2, 3, 4, 5, 6, 7, 8, 11, 12, 13, 14, 15, 16, 17, ...]

# Page navigation buttons
item-page:
  next: { slot: 51, item: ARROW, name: '&aNext Page', lore: ['', '&f➥ Click'] }
  prev: { slot: 49, item: ARROW, name: '&aPrevious Page', lore: ['', '&f➥ Click'] }

# Quest display at each stage (ongoing, complete, claimed, locked)
quests-item:
  ongoing:  { name: '&f<display>', lore: [...] }
  complete: { item: CHEST_MINECART, name: '&f<display>', lore: [...] }
  claimed:  { item: MINECART, name: '&f<display>', lore: [...] }
  locked:   { item: BARRIER, name: '&f<display>', lore: [...] }

# Custom decorative/navigation items at specific slots
item-custom:
  pane1:  { slots: [1, 10, ...], item: BLACK_STAINED_GLASS_PANE, name: ' ' }
  mynav:  { slot: 0, item: BOOK, name: '&aMenu', commands: ['[console] mycommand %player%'] }
```

Supported placeholders in quest item lore: `<display>`, `<desc>`, `<objective-value>`, `<objective-max-value>`, `<reward>`, `<required-quests>`.

## Quests

Quest files are located in `plugins/FTBQuests/quests/`. Each `.yml` file can contain multiple quests.

```yaml
QUEST_ID:                             # Unique identifier (used in commands)
  category: overworld                  # Must match a category filename
  slot: 23                            # GUI slot (only if category uses manual layout)
  page: 1                             # GUI page (only if category uses manual layout)

  icon:                               # The quest's display item
    item: DIAMOND                     # Minecraft material
    amount: 1                         # Stack size
    model: 0                          # Custom model data
    skullvalue: ''                    # Player head texture (base64)
    display: '&bQuest Name'           # Display name
    desc:                             # Lore/description
      - '&7Description here.'

  objective:                          # The objective to complete
    type: BREAK_BLOCK                 # Objective type (see below)
    amount: 10                        # How many times to complete
    required:                         # What to track (varies by type)
      - DIAMOND_BLOCK

  objective-command:                  # Runs when left-clicking the quest
    - '[player] warp mine'            # [player] = run as player
    - '[close]'                       # [close] = close the GUI

  reward-display:                     # Text shown in the reward section
    - '&f5x Diamond'
  reward-command:                     # Runs when claiming the reward
    - '[console] give %player% diamond 5'
```

### Command prefixes

| Prefix | Description |
|--------|-------------|
| `[console] <cmd>` | Runs as console |
| `[player] <cmd>` | Runs as the player |
| `[close]` | Closes the GUI |
| `%player%` | Replaced with the player's name |

## Objective Types

### Built-in Types

| Type | Description | `required` value |
|------|-------------|------------------|
| `none` | Manual tracking only | — |
| `BREAK_BLOCK` | Break specific blocks | Material name (e.g. `DIAMOND_ORE`) |
| `PLACE_BLOCK` | Place specific blocks | Material name |
| `CRAFT` | Craft vanilla items (skips items with lore/name/enchants) | Material name (e.g. `BREAD`) |
| `CRAFT_CUSTOM` | Craft custom items (matched by stripped display name) | Display name or `CONTAINS:...` |
| `PICKUP_ITEM` | Pick up items | — |
| `CONSUME` | Eat/drink items | Material name |
| `SMELT` | Smelt items in a furnace | Material name |
| `ENCHANT` | Enchant items | — |
| `FISHING` | Catch fish | — |
| `THROW` | Throw projectiles (eggs, ender pearls, etc.) | — |
| `KILL` | Kill mobs | Entity type (e.g. `ZOMBIE`) |
| `BREED` | Breed animals | Entity type |
| `TAME` | Tame animals | Entity type |
| `SHEAR` | Shear sheep | — |

### MMOItems Integration

Requires MMOItems plugin. Objective `required` format: `TYPE|ID`

| Type | Description |
|------|-------------|
| `MMOITEMS_STATIONS_TRADE` | Trade at an MMOItems crafting station |
| `MMOITEMS_CRAFT` | Craft an MMOItems item (deprecated event) |
| `MMOITEMS_CONSUME` | Consume an MMOItems item |
| `MMOITEMS_APPLY_GEMSTONE` | Apply a gem stone to an item |
| `MMOITEMS_APPLY_UPGRADE` | Use an upgrade item |
| `MMOITEMS_APPLY_SOULBOUND` | Apply soulbound to an item |
| `MMOITEMS_APPLY_REPAIR` | Repair an MMOItems item |
| `MMOITEMS_UNSOCKET_GEMSTONE` | Remove a gem stone from an item |

### ItemsAdder Integration

Requires ItemsAdder plugin. `required` value: namespaced ID (e.g. `myitems:ruby_ore`)

| Type | Description |
|------|-------------|
| `ITEMSADDER_BREAK_BLOCK` | Break a custom block |
| `ITEMSADDER_PLACE_BLOCK` | Place a custom block |

### MythicMobs Integration

Requires MythicMobs plugin.

| Type | Description |
|------|-------------|
| `MYTHICMOBS_KILL` | Kill a MythicMobs mob (`required`: mob internal name) |

### Slimefun Integration

Requires Slimefun plugin.

| Type | Description |
|------|-------------|
| `SLIMEFUN_MULTIBLOCK_{ID}` | Use a Slimefun multiblock machine (e.g. `SLIMEFUN_MULTIBLOCK_ENHANCED_CRAFTING_TABLE`) |
| `SLIMEFUN_ANCIENT_ALTAR` | Use the Slimefun Ancient Altar |

### `CONTAINS:` Matching

For `CRAFT_CUSTOM` and integration types, prefix the `required` value with `CONTAINS:` to match partial names instead of exact:

```yaml
required:
  - CONTAINS:Katana    # Matches "Katana of Fire", "Frost Katana", etc.
```

## Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/questbook` | Give yourself the quest book item | `ftbquests.questbook` |
| `/ftbquests` | All subcommands require admin permission | `ftbquests.admin` |
| `/ftbquests help` | Show help menu | `ftbquests.admin` |
| `/ftbquests reload` | Reload config and quests | `ftbquests.admin` |
| `/ftbquests open <category> [player]` | Open a category GUI | `ftbquests.admin` |
| `/ftbquests objective <add\|subtract\|set> <quest> <player> [amount]` | Modify quest progress | `ftbquests.admin` |
| `/ftbquests quest <reset\|completed\|claimed> <quest> <player>` | Manage quest state | `ftbquests.admin` |
| `/ftbquests resetall <player\|all>` | Reset all progress for a player or everyone | `ftbquests.admin` |
| `/ftbquests resetcategory <category> <player\|all>` | Reset progress in a specific category | `ftbquests.admin` |

- `resetall all` and `resetcategory <category> all` require console (players get an error message)

## Permissions

| Permission | Default | Description |
|------------|---------|-------------|
| `ftbquests.admin` | op | Access to admin commands |
| `ftbquests.questbook` | true | Allows use of `/questbook` command |

## Database

- **SQLite** (default): stored in `plugins/FTBQuests/database.db`
- **MySQL**: enable `mysql-settings.enabled` in `config.yml` and configure connection details

Player data is auto-saved at the configured `auto-save-interval` and on player quit.

## Building from Source

Requirements: Java 21+, Maven

```bash
mvn clean package
```

The compiled jar will be in the `target/` directory.

## License

This project is open-source. Feel free to contribute, fork, or modify it.
