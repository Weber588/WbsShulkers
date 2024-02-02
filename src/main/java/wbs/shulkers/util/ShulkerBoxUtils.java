package wbs.shulkers.util;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import wbs.shulkers.WbsShulkers;
import wbs.shulkers.features.ShulkerFeature;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.UUID;

public class ShulkerBoxUtils {
    private ShulkerBoxUtils() {}

    public static final NamespacedKey UUID_KEY = new NamespacedKey(WbsShulkers.getInstance(), "custom_shulker_uuid");
    public static final NamespacedKey CUSTOM_INV_KEY = new NamespacedKey(WbsShulkers.getInstance(), "custom_inventory");

    private static final Map<UUID, CustomShulkerBox> knownBoxes = new HashMap<>();

    static void register(CustomShulkerBox newBox) {
        UUID uuid = newBox.getUUID();
        knownBoxes.put(uuid, newBox);
    }

    @Nullable
    @Contract("null -> null")
    public static ShulkerBox getVanillaBox(ItemStack item) {
        if (item == null) {
            return null;
        }

        if (!Tag.SHULKER_BOXES.isTagged(item.getType())) {
            return null;
        }

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return null;
        }

        if (!(meta instanceof BlockStateMeta blockStateMeta)) {
            return null;
        }

        BlockState blockState = blockStateMeta.getBlockState();
        if (!(blockState instanceof ShulkerBox box)) {
            return null;
        }

        return box;
    }

    @Nullable
    @Contract("null -> null")
    public static ShulkerBox getVanillaBox(@Nullable Block block) {
        if (block == null) {
            return null;
        }

        if (!Tag.SHULKER_BOXES.isTagged(block.getType())) {
            return null;
        }

        BlockState blockState = block.getState();

        return getVanillaBox(blockState);
    }

    @Nullable
    @Contract("null -> null")
    public static ShulkerBox getVanillaBox(@Nullable BlockState blockState) {
        if (!(blockState instanceof ShulkerBox box)) {
            return null;
        }

        return box;
    }

    @Nullable
    @Contract("null -> null")
    public static CustomShulkerBox from(@Nullable ItemStack item) {
        ShulkerBox box = getVanillaBox(item);
        if (box == null) {
            return null;
        }

        CustomShulkerBox existing = getIfExisting(box);

        if (existing != null) {
            return existing.setItem(item);
        }

        return new CustomShulkerBox(item);
    }

    @Nullable
    @Contract("null -> null")
    public static CustomShulkerBox from(@Nullable Block block) {
        ShulkerBox box = getVanillaBox(block);
        if (box == null) {
            return null;
        }

        CustomShulkerBox existing = getIfExisting(box);

        if (existing != null) {
            return existing.setBlock(block);
        }


        return new CustomShulkerBox(block);
    }

    @Nullable
    @Contract("null -> null")
    public static CustomShulkerBox from(@Nullable BlockState blockState) {
        if (!(blockState instanceof ShulkerBox box)) {
            return null;
        }

        CustomShulkerBox existing = getIfExisting(box);

        if (existing != null) {
            return existing;
        }

        return new CustomShulkerBox(box);
    }

    public static Map<CustomShulkerBox, ItemStack> getInvShulkersWithOption(Inventory inventory, ShulkerFeature feature) {
        // Use reference equality map rather than object equality map since apparently shulker boxes
        // don't generate unique hashCodes
        IdentityHashMap<CustomShulkerBox, ItemStack> boxesWithFeature = new IdentityHashMap<>();

        for (ItemStack check : inventory) {
            if (feature.isAppliedTo(check)) {
                boxesWithFeature.put(from(check), check);
            }
        }

        return boxesWithFeature;
    }


    public static void saveInventoryTo(ShulkerBox toUpdate, Inventory updatedInventory) {
        toUpdate.getPersistentDataContainer().set(CUSTOM_INV_KEY, PersistentInventoryDataType.INSTANCE, updatedInventory);

        // For some reason this is working when updatedInventory.size >= vanillaInv.size, but not other way
        // around. It writes "HOPPER" as type (for example) but none of the items -- are slots done differently
        // with hoppers/other inv types???
        // TODO: print item stacks 0-5 of hopper -- it should be safe since that's the smallest (normal) inv and reveal
        //  details about slot numbers hopefully
        Inventory vanillaInventory = toUpdate.getInventory();
        vanillaInventory.clear();
        for (int i = 0; i < vanillaInventory.getSize() && i < updatedInventory.getSize(); i++) {
            ItemStack stack = updatedInventory.getItem(i);
            if (stack == null) {
                stack = new ItemStack(Material.AIR);
            }
            vanillaInventory.setItem(i, stack);
        }
    }

    public static void saveToItem(ItemStack item, ShulkerBox box) {
        if (item != null) {
            ItemMeta meta = item.getItemMeta();

            if (meta instanceof BlockStateMeta blockStateMeta) {
                blockStateMeta.setBlockState(box);
                item.setItemMeta(meta);
            }
        }
    }

    @Nullable
    public static UUID getUUID(ShulkerBox box) {
        String existingUUIDString = box.getPersistentDataContainer().get(UUID_KEY, PersistentDataType.STRING);
        if (existingUUIDString == null) {
            return null;
        }

        try {
            return UUID.fromString(existingUUIDString);
        } catch (IllegalArgumentException e) {
            WbsShulkers.getInstance().getLogger().warning("Invalid UUID on shulker box: " + existingUUIDString);
        }

        return null;
    }

    @Nullable
    private static CustomShulkerBox getIfExisting(ShulkerBox box) {
        UUID uuid = getUUID(box);

        if (uuid != null) {
            return knownBoxes.get(uuid);
        }

        return null;
    }
}
