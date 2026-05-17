# SkyfunUtils

A feature-packed, performance-optimized utility addon for **Slimefun 4**. Heavily inspired by classic skyblock mods like **Ex Nihilo**, the primary goal of this addon is to make "finite world" survival genres—such as **Skyblock, AcidIsland, and CaveBlock**—much more playable, engaging, and progression-friendly by filling resource gaps and adding interactive mechanics.

This addon introduces custom tiered tools, automated resource-generating machinery, interactive blocks, and classic quality-of-life mechanics to enhance your survival experience when resources are scarce.

---

## 🚀 Features & Content

### 📂 Slimefun Guide Categories
The addon seamlessly introduces three dedicated subcategories into your Slimefun guide under the core **Skyfun Utils** umbrella:
* 🛠️ **Tools:** Specialized hand tools for block processing, silkworm farming, and extraction.
* ⚡ **Machines:** Custom manual and automated power-consuming machinery.
* 📜 **Server Info:** In-game guide entries explaining unique server mechanics.

---

### ✨ Unique Mechanics
* **Twerk For Trees:** A server-wide behavior allowing players to spam their sneak/crouch key (`Shift`) near any vanilla sapling. Doing so creates green visual particles and grants a percentage-based chance to act like bonemeal, accelerating tree growth natively.
* **Vanilla Crafting Integration:** Select early-game items bypass Slimefun's strict crafting engine, allowing players to craft custom items seamlessly inside standard Vanilla Crafting Tables.
* **Alternative Skyblock Recipes:** Injects cheaper, iron-free crafting recipes directly into Slimefun's native guide for essential early-game blocks (e.g., craft a Crucible from Bricks, or a Composter using wooden slabs and a vanilla composter).
* **Fully Configurable Loot Tables:** Server administrators have total control over sifting rewards for **both the Manual and Auto Sieves**. Drop tables use a dynamic weighted registry in the `config.yml`, allowing you to easily add custom items, change drop rates, or remove defaults without touching the code.

---

### 🛠️ Custom Tools
* **Crooks (Standard & Bone):** Used on leaf blocks to significantly boost sapling, stick, and apple drop rates, with a random chance to extract precious Silkworms.
* **Hammers (Wood, Stone, Iron, Gold, Diamond):** Efficiently processes blocks by smashing them down a tier. Crushes Cobblestone/Stone variants into Gravel, and Gravel into Sand.
* **Flint Shears:** An early-game alternative to iron shears craftable in a vanilla crafting table. Fragile by nature, taking double durability damage when used to break blocks!

---

### ⚡ Automation & Machinery
* **Manual Sieve:** An interactive station allowing players to right-click materials to instantly sift them for resources. 
  * **Fully Configurable:** All drops and percentages are pulled dynamically from your `config.yml`!
  * **Supported Blocks:** Sand, Gravel, Dirt, Soul Sand, and Soul Soil.
* **Auto Sieve (Tier I - III):** Automatically feeds siftable blocks into an electronic filtration system to harvest ore components, seeds, and dust completely hands-free. Features an adaptive dynamic recipe matrix perfectly balanced across energy/speed tiers and linked directly to your custom config rates.
* **Auto Composter (Tier I - III):** Automatically processes compostable organic materials into dirt/fertilizers, operating progressively faster across higher tiers with dedicated energy buffers.

---

### 🏺 Materials & Items
* **Silkworm:** Obtained from leaves via Crooks; can be deployed onto live foliage to slowly turn leaf blocks into Cobwebs over time.
* **Unfired Clay Bucket:** A cheap alternative bucket blueprint made from clay balls that can be fired inside a conventional furnace or Slimefun Smeltery to produce functional water/lava containers.
* **Grass Seeds:** Sifted from Dirt; right-click on bare Dirt blocks to instantly cover them in lush vanilla Grass.

---

## 🛠️ Requirements

To run this addon successfully, your server must have the following dependencies installed:
* **Spigot** or **Paper** (1.20+)
* **Slimefun 4** (Latest Build)

---

## 💾 Installation

1. Make sure your server meets the minimum requirements listed above.
2. Download the latest compiled `SkyfunUtils.jar` file.
3. Drop the `.jar` file directly into your server's `/plugins/` directory.
4. Restart your Minecraft server.
5. Open the in-game Slimefun guide (`/sf guide`) to view your brand new items!

---

## 🛰️ Automatic Update Checking

This plugin features a built-in, asynchronous GitHub Update Checker. Upon server initialization, it safely queries the GitHub Releases API in the background (preventing server thread lag) to inform the console and administrators if a newer version of the addon is available for download.
