package dark.detraismc.detraisequipment;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.groups.NestedItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.SubItemGroup;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public final class DEItems {

    public static final NamespacedKey DURABILITY_KEY = new NamespacedKey(DetraisEquipment.getInstance(), "custom_durability");
    public static final NamespacedKey MAX_DURABILITY_KEY = new NamespacedKey(DetraisEquipment.getInstance(), "max_durability");

    public static NestedItemGroup de_category;
    public static ItemGroup category_multiblock, category_materials, category_weapons, category_tools, category_armor;

    public static final SlimefunItemStack COPPER_SWORD;
    public static final SlimefunItemStack COPPER_HELMET, COPPER_CHESTPLATE, COPPER_LEGGINGS, COPPER_BOOTS;

    static {
        de_category = new NestedItemGroup(new NamespacedKey(DetraisEquipment.getInstance(), "de_category"), new CustomItemStack(Material.GOLDEN_CHESTPLATE, "&cDetrais Equipments", new String[0]));
        category_multiblock = new SubItemGroup(new NamespacedKey(DetraisEquipment.getInstance(), "de_category_multiblock"), de_category, new CustomItemStack(Material.BRICKS, "&cDetrais Equipments &8- &dMultiblock", new String[0]));
        category_materials = new SubItemGroup(new NamespacedKey(DetraisEquipment.getInstance(), "de_category_materials"), de_category, new CustomItemStack(Material.IRON_INGOT, "&cDetrais Equipments &8- &bMaterials", new String[0]));
        category_weapons = new SubItemGroup(new NamespacedKey(DetraisEquipment.getInstance(), "de_category_weapons"), de_category, new CustomItemStack(Material.IRON_SWORD, "&cDetrais Equipments &8- &4Weapons", new String[0]));
        category_tools = new SubItemGroup(new NamespacedKey(DetraisEquipment.getInstance(), "de_category_tools"), de_category, new CustomItemStack(Material.IRON_PICKAXE, "&cDetrais Equipments &8- &9Tools", new String[0]));
        category_armor = new SubItemGroup(new NamespacedKey(DetraisEquipment.getInstance(), "de_category_armor"), de_category, new CustomItemStack(Material.IRON_CHESTPLATE, "&cDetrais Equipments &8- &aArmor", new String[0]));

        // --- WEAPONS ---
        COPPER_SWORD = new SlimefunItemStack("COPPER_SWORD", Material.WOODEN_SWORD, "&6Copper Sword", "&8Weapon", "", "&c6 Damage", "", "&7Weapon Speed: &bNormal", "&7Rarity: &fCommon");
        setupTool(COPPER_SWORD, "copper_sword", 5.0D, -2.4D, 191);

        // --- ARMOR ---
        // Using Leather as base to apply Copper Color, but you can change to IRON_HELMET etc. if preferred
        COPPER_HELMET = new SlimefunItemStack("COPPER_HELMET", Material.LEATHER_HELMET, "&6Copper Helmet", "&8Armor", "", "&9+2 Armor", "", "&7Rarity: &fCommon");
        COPPER_CHESTPLATE = new SlimefunItemStack("COPPER_CHESTPLATE", Material.LEATHER_CHESTPLATE, "&6Copper Chestplate", "&8Armor", "", "&9+5 Armor", "", "&7Rarity: &fCommon");
        COPPER_LEGGINGS = new SlimefunItemStack("COPPER_LEGGINGS", Material.LEATHER_LEGGINGS, "&6Copper Leggings", "&8Armor", "", "&9+4 Armor", "", "&7Rarity: &fCommon");
        COPPER_BOOTS = new SlimefunItemStack("COPPER_BOOTS", Material.LEATHER_BOOTS, "&6Copper Boots", "&8Armor", "", "&9+1 Armor", "", "&7Rarity: &fCommon");

        // Stats: (Item, Key, ArmorPoints, Toughness, KB_Resist, HP_Bonus, Slot, MaxDurability)
        // Values are slightly lower than Iron
        setupArmor(COPPER_HELMET, "copper_helmet", 2.0D, 0.0D, 0.0D, 0.0D, EquipmentSlotGroup.HEAD, 120);
        setupArmor(COPPER_CHESTPLATE, "copper_chestplate", 5.0D, 0.0D, 0.0D, 0.0D, EquipmentSlotGroup.CHEST, 170);
        setupArmor(COPPER_LEGGINGS, "copper_leggings", 4.0D, 0.0D, 0.0D, 0.0D, EquipmentSlotGroup.LEGS, 155);
        setupArmor(COPPER_BOOTS, "copper_boots", 1.0D, 0.0D, 0.0D, 0.0D, EquipmentSlotGroup.FEET, 140);
    }

    private static AttributeModifier createMod(String key, double amount, AttributeModifier.Operation op, EquipmentSlotGroup slot) {
        return new AttributeModifier(new NamespacedKey(DetraisEquipment.getInstance(), key), amount, op, slot);
    }

    private static void setupTool(SlimefunItemStack item, String keyBase, double dmg, double speed, int maxDurability) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        meta.addAttributeModifier(Attribute.ATTACK_DAMAGE, createMod(keyBase + "_dmg", dmg, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
        meta.addAttributeModifier(Attribute.ATTACK_SPEED, createMod(keyBase + "_spd", speed, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        applyDurabilityMeta(meta, maxDurability);
        item.setItemMeta(meta);
    }

    private static void setupArmor(SlimefunItemStack item, String keyBase, double arm, double tough, double kb, double hp, EquipmentSlotGroup slot, int maxDurability) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        // Attributes
        meta.addAttributeModifier(Attribute.ARMOR, createMod(keyBase + "_arm", arm, AttributeModifier.Operation.ADD_NUMBER, slot));
        meta.addAttributeModifier(Attribute.ARMOR_TOUGHNESS, createMod(keyBase + "_tough", tough, AttributeModifier.Operation.ADD_NUMBER, slot));
        meta.addAttributeModifier(Attribute.KNOCKBACK_RESISTANCE, createMod(keyBase + "_kb", kb, AttributeModifier.Operation.ADD_NUMBER, slot));
        meta.addAttributeModifier(Attribute.MAX_HEALTH, createMod(keyBase + "_hp", hp, AttributeModifier.Operation.ADD_NUMBER, slot));

        // Appearance
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DYE);
        if (meta instanceof LeatherArmorMeta) {
            ((LeatherArmorMeta) meta).setColor(Color.fromRGB(184, 115, 51)); // Copper Color
        }

        applyDurabilityMeta(meta, maxDurability);
        item.setItemMeta(meta);
    }

    private static void applyDurabilityMeta(ItemMeta meta, int maxDurability) {
        meta.getPersistentDataContainer().set(DURABILITY_KEY, PersistentDataType.INTEGER, maxDurability);
        meta.getPersistentDataContainer().set(MAX_DURABILITY_KEY, PersistentDataType.INTEGER, maxDurability);
        List<String> lore = meta.getLore();
        if (lore == null) lore = new ArrayList<>();
        lore.add("");
        lore.add("§8Durability: " + maxDurability + " / " + maxDurability);
        meta.setLore(lore);
    }

    private DEItems() {}
}