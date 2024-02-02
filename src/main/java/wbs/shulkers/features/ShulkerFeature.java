package wbs.shulkers.features;

import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;
import wbs.shulkers.WbsShulkers;
import wbs.shulkers.util.CustomShulkerBox;
import wbs.shulkers.util.ShulkerBoxUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public abstract class ShulkerFeature {

    private final WbsShulkers plugin;

    private final String name;
    private final NamespacedKey key;

    public abstract String getDescription();

    public List<ShulkerFeature> requires() {
        return new LinkedList<>();
    }
    public List<ShulkerFeature> conflictsWith() {
        return new LinkedList<>();
    }

    public ShulkerFeature(String name) {
        this.name = name;

        plugin = WbsShulkers.getInstance();

        key = new NamespacedKey(plugin, ShulkerFeatureManager.stripFeatureName(getName()));
    }

    public void applyLore(ItemStack item) {
        ItemMeta itemMeta = Objects.requireNonNull(item.getItemMeta());
        applyLore(itemMeta);
        item.setItemMeta(itemMeta);
    }

    public void applyLore(ItemMeta meta) {
        List<String> lore = meta.getLore();
        if (lore == null) lore = new LinkedList<>();

        String loreLine = plugin.dynamicColourise(plugin.settings.getLoreFormat() + getName());

        if (lore.contains(loreLine)) {
            return;
        }

        lore.add(0, loreLine);

        meta.setLore(lore);
    }

    public void applyTo(ShulkerBox box) {
        applyTo(box.getPersistentDataContainer());
    }

    public void applyTo(PersistentDataContainer container) {
        container.set(getKey(), PersistentDataType.STRING, "true");
    }

    public boolean isAppliedTo(@Nullable ItemStack item) {
        return isAppliedTo(ShulkerBoxUtils.from(item));
    }

    public boolean isAppliedTo(@Nullable Block block) {
        return isAppliedTo(ShulkerBoxUtils.from(block));
    }

    public boolean isAppliedTo(@Nullable CustomShulkerBox box) {
        if (box == null) {
            return false;
        }
        return isAppliedTo(box.getVanillaBox());
    }

    public boolean isAppliedTo(@Nullable PersistentDataHolder holder) {
        if (holder == null) {
            return false;
        }

        PersistentDataContainer container = holder.getPersistentDataContainer();

        return isAppliedTo(container);
    }

    public boolean isAppliedTo(@Nullable PersistentDataContainer container) {
        if (container == null) {
            return false;
        }
        return Objects.equals(container.get(getKey(), PersistentDataType.STRING), "true");
    }

    public String getName() {
        return name;
    }

    public NamespacedKey getKey() {
        return key;
    }
}
