package wbs.shulkers.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wbs.shulkers.WbsShulkers;
import wbs.shulkers.features.ShulkerFeature;
import wbs.shulkers.features.ShulkerFeatureManager;
import wbs.shulkers.features.types.SizeFeature;
import wbs.utils.util.string.WbsStrings;

import java.util.*;
import java.util.logging.Logger;

import static wbs.shulkers.util.ShulkerBoxUtils.CUSTOM_INV_KEY;

public class CustomShulkerBox {
    private final ShulkerBox box;
    @Nullable
    private Inventory cachedInventory;
    @NotNull
    private final UUID uuid;
    @Nullable
    private ItemStack asItem = null;
    @Nullable
    private Block asBlock = null;

    public CustomShulkerBox(@NotNull ItemStack item) {
        this(ShulkerBoxUtils.getVanillaBox(item));

        asItem = item;
    }

    public CustomShulkerBox(@NotNull Block block) {
        this(ShulkerBoxUtils.getVanillaBox(block));

        asBlock = block;
    }

    public CustomShulkerBox(ShulkerBox box) {
        if (box == null) {
            throw new IllegalArgumentException("Shulker box cannot be null.");
        }
        this.box = box;

        UUID existingUUID = ShulkerBoxUtils.getUUID(box);
        if (existingUUID == null) {
            uuid = UUID.randomUUID();
        } else {
            uuid = existingUUID;
        }

        ShulkerBoxUtils.register(this);
    }

    public boolean isCustom() {
        return !ShulkerFeatureManager.getFeatures(box).isEmpty();
    }

    @NotNull
    public Inventory readInventory() {
        PersistentDataContainer container = box.getPersistentDataContainer();
        Inventory derivedInv = container.get(CUSTOM_INV_KEY, PersistentInventoryDataType.INSTANCE);
        if (derivedInv == null) {
            container.set(CUSTOM_INV_KEY, PersistentInventoryDataType.INSTANCE, box.getInventory());
            derivedInv = box.getInventory();
        }

        InventoryType finalType = derivedInv.getType();
        int finalSize = derivedInv.getSize();

        List<ShulkerFeature> features = ShulkerFeatureManager.getFeatures(box);

        SizeFeature highestSizeFeature = features.stream()
                .filter(feature -> feature instanceof SizeFeature)
                .map(SizeFeature.class::cast)
                .max(Comparator.comparingInt(a -> a.customSize))
                .orElse(null);

        if (highestSizeFeature != null) {
            finalType = highestSizeFeature.type;
            finalSize = highestSizeFeature.customSize;
        }

        // Rename via inv wrapping
        if (finalType == InventoryType.CHEST) {
            cachedInventory = Bukkit.getServer().createInventory(null, finalSize, getInvName());
        } else if (finalType == InventoryType.SHULKER_BOX) {
            // Don't use actual shulker box displays -- it's nice that it prevents the user from adding shulkers to it,
            // but we check that manually and want to allow it when we have the Inception upgrade -- just use chests
            cachedInventory = Bukkit.getServer().createInventory(null, InventoryType.CHEST, getInvName());
        } else {
            cachedInventory = Bukkit.getServer().createInventory(null, finalType, getInvName());
        }

        for (int i = 0; i < cachedInventory.getSize() && i < derivedInv.getSize(); i++) {
            ItemStack stack = derivedInv.getItem(i);
            if (stack == null) {
                stack = new ItemStack(Material.AIR);
            }
            cachedInventory.setItem(i, stack);
        }

        return cachedInventory;
    }

    @NotNull
    public Inventory getInventory() {
        if (cachedInventory == null) {
            return readInventory();
        }

        return cachedInventory;
    }

    public void setInventory(Inventory updatedInventory) {
        ShulkerBoxUtils.saveInventoryTo(box, updatedInventory);
        readInventory();
    }

    public HashMap<Integer, ItemStack> addItem(ItemStack toAdd) {
        Inventory customInv = getInventory();
        return customInv.addItem(toAdd);
    }

    public HashMap<Integer, ItemStack> removeItem(ItemStack toRemove) {
        Inventory customInv = getInventory();
        return customInv.removeItem(toRemove);
    }

    public ShulkerBox getVanillaBox() {
        return box;
    }

    public void saveToNBT() {
        saveToBlock();
        saveToItem();
    }

    public void saveToItem() {
        if (asItem != null && asItem.getType() != Material.AIR) {
            saveToItem(asItem);
        }
    }

    public void saveToItem(@NotNull ItemStack other) {
        ShulkerBox otherBox = ShulkerBoxUtils.getVanillaBox(other);

        if (otherBox == null) {
            // Serialize item to string for debugging purposes:
            YamlConfiguration config = new YamlConfiguration();
            config.set("item", other);
            throw new IllegalArgumentException("Attempted to save to a non-shulker item!\nItem: " + config.saveToString());
        }

        saveTo(otherBox);
        ShulkerBoxUtils.saveToItem(other, otherBox);

        for (ShulkerFeature feature : this.getFeatures()) {
            feature.applyLore(other);
        }
        ShulkerBoxUtils.saveToItem(other, otherBox);
    }

    public void saveToBlock() {
        if (asBlock != null && asBlock.getType() != Material.AIR) {
            saveToBlock(asBlock);
        }
    }

    public void saveToBlock(@NotNull Block other) {
        ShulkerBox otherBox = ShulkerBoxUtils.getVanillaBox(other);

        if (otherBox == null) {
            throw new IllegalArgumentException("Attempted to save to a non-shulker block!");
        }

        saveTo(otherBox);

        if (otherBox.isPlaced()) {
            otherBox.update();
        }
    }

    private void saveTo(@NotNull ShulkerBox other) {
        PersistentDataContainer container = other.getPersistentDataContainer();
        container.set(ShulkerBoxUtils.UUID_KEY, PersistentDataType.STRING, uuid.toString());

        for (ShulkerFeature feature : this.getFeatures()) {
            feature.applyTo(container);
        }

        Inventory inv = getInventory();
        if (inv.contains(asItem)) {
            Logger logger = WbsShulkers.getInstance().getLogger();
            logger.severe("A shulker box was put into itself! Please report this issue.");
            logger.info("Lost inventory: "
                    + PersistentInventoryDataType.INSTANCE.toPrimitive(inv, null)
            );
        }
        ShulkerBoxUtils.saveInventoryTo(other, inv);
    }

    public void openAndMonitor(Player player) {
        cachedInventory = getInventory();

        Listener listener = new Listener() {
            @EventHandler(priority = EventPriority.HIGHEST)
            public void onInventoryClick(InventoryClickEvent event) {
                if (!isThisBox(event.getView())) {
                    return;
                }
                ItemStack currentItem = event.getCurrentItem();
                if (!canContain(currentItem)) {
                    event.setCancelled(true);
                }

                Inventory clicked = event.getClickedInventory();
                if (clicked == null) {
                    return;
                }

                if (clicked.getType() == InventoryType.PLAYER) {
                    if (event.getSlot() != -1) {
                        if (event.isShiftClick()) {
                            ItemStack shiftClicked = clicked.getItem(event.getSlot());
                            if (!canContain(shiftClicked)) {
                                event.setCancelled(true);
                            }
                        }
                    }
                }

                if (event.getClick() == ClickType.NUMBER_KEY) {
                    ItemStack referenced = event.getView().getBottomInventory().getItem(event.getHotbarButton());
                    if (!canContain(referenced)) {
                        event.setCancelled(true);
                    }
                }

                if (event.getClick() == ClickType.SWAP_OFFHAND) {
                    ItemStack referenced = player.getInventory().getItemInOffHand();
                    if (!canContain(referenced)) {
                        event.setCancelled(true);
                    }
                }

                if (!event.isCancelled()) {
                    saveToNBT();
                }
            }

            @EventHandler
            public void onInventoryClose(InventoryCloseEvent event) {
                if (isThisBox(event.getView())) {
                    saveToNBT();

                    InventoryCloseEvent.getHandlerList().unregister(this);
                    InventoryClickEvent.getHandlerList().unregister(this);
                }
            }
        };

        WbsShulkers.getInstance().registerListener(listener);
        player.openInventory(cachedInventory);
    }

    public boolean isThisBox(InventoryView view) {
        return view.getTitle().equals(getInvName());
    }

    public String getInvName() {
        String name = box.getCustomName();
        if (name == null && asItem != null) {
            name = Objects.requireNonNull(asItem.getItemMeta()).getDisplayName();
        }
        if (name == null || name.isBlank()) {
            name = "Shulker Box";
        }
        return WbsShulkers.getInstance().dynamicColourise(name)
                + WbsStrings.getInvisibleString("WbsShulkers:" + uuid);
    }

    public boolean canContain(ItemStack check) {
        if (check == null) {
            return true;
        }

        if (check.equals(asItem)) {
            return false;
        }

        if (!Tag.SHULKER_BOXES.isTagged(check.getType())) {
            return true;
        }

        return ShulkerFeatureManager.INCEPTION.isAppliedTo(box);
    }

    @NotNull
    public List<ShulkerFeature> getFeatures() {
        return ShulkerFeatureManager.getFeatures(box);
    }

    @Contract("_ -> this")
    public CustomShulkerBox setItem(ItemStack item) {
        this.asItem = item;
        return this;
    }

    @Contract("_ -> this")
    public CustomShulkerBox setBlock(Block asBlock) {
        this.asBlock = asBlock;
        return this;
    }

    @NotNull
    public UUID getUUID() {
        return uuid;
    }
}
