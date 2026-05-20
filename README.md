# 🔥 EzFurnace

[![Slimefun4 Addon](https://img.shields.io/badge/Slimefun4-Addon-brightgreen.svg)](https://github.com/Slimefun/Slimefun4)
[![Minecraft Version](https://img.shields.io/badge/Minecraft-1.16+-blue.svg)]()

**EzFurnace** is a lightweight Slimefun4 addon that expands exclusively upon the **Enhanced Furnace**. Designed for an early-game friendly experience, it introduces a tiered progression of easy-to-craft furnaces without the need for complex Slimefun machinery. Skip the complicated setups and get straight to faster smelting!

## ✨ Features

* **Early-Game Compatible**: Designed to be accessible and easy to craft right from the start of your survival journey.
* **8 Tiered Furnaces**: Upgrade your smelting setup through 8 simple progression tiers:
  * 🟠 **Copper Furnace**
  * ⚪ **Iron Furnace**
  * 🟡 **Golden Furnace**
  * 🔵 **Lapis Furnace**
  * 🔴 **Redstone Furnace**
  * 💎 **Diamond Furnace**
  * ❇️ **Emerald Furnace**
  * 🖤 **Netherite Furnace**
* **Increased Efficiency**: Higher tier furnaces provide significantly faster smelting speeds and better fuel rates.
* **Easy Vanilla Crafting**: Craft these custom Slimefun furnaces directly in a standard Vanilla Crafting Table using intuitive recipes. (This feature is completely optional and can be toggled in the config).

## 🛠️ Crafting Recipes

If vanilla crafting is enabled, you can upgrade your furnaces using the previous tier's furnace, surrounded by ingots (or dust/gems) and a block of the respective material at the bottom.

**Example: Iron Furnace**
```text
[ Iron Ingot ] [ Iron Ingot ] [ Iron Ingot ]
[ Iron Ingot ] [Copper Furnace] [ Iron Ingot ]
[ Iron Ingot ] [ Iron Block ] [ Iron Ingot ]
```
*(You can also craft them using the traditional Slimefun Magic Workbench / Enhanced Crafting Table depending on your setup!)*

## 📦 Installation

1. Make sure you have the latest version of [Slimefun4](https://github.com/Slimefun/Slimefun4) installed on your server.
2. Download the latest `EzFurnace.jar` from the [Releases](../../releases) page.
3. Drop the `.jar` file into your server's `plugins/` folder.
4. Restart your server.

## ⚙️ Configuration

A `config.yml` file will be generated on your first startup. 

```yaml
update-checker: true

# Enable or disable the ability to craft Enhanced Furnaces in a Vanilla Crafting Table
allow-vanilla-craft: true
```

## 🔨 Building from Source

To build this project yourself, you will need Git and Maven installed.

```bash
git clone [https://github.com/YourUsername/EzFurnace.git](https://github.com/YourUsername/EzFurnace.git)
cd EzFurnace
mvn clean package
```
The compiled jar will be located in the `target/` directory.

## 📜 License

This project is open-source. Feel free to contribute, fork, or modify it!
