package wbs.shulkers.features;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import wbs.shulkers.features.types.*;

import java.util.*;

public final class ShulkerFeatureManager {
    private ShulkerFeatureManager() {}

    public static final ShulkerFeature AUTO_PICKUP = new AutoPickupFeature();
    public static final ShulkerFeature EASY_OPEN = new EasyOpenFeature();
    public static final ShulkerFeature INCEPTION = new InceptionFeature();
    public static final ShulkerFeature IMMORTAL = new ImmortalFeature();
    public static final ShulkerFeature AUTO_SMELT = new AutoSmeltFeature();
    public static final ShulkerFeature COMPRESSION = new CompressionFeature();
    public static final ShulkerFeature AUTO_FEED = new AutoFeedFeature();

    // TODO: Make these configurable, and allow code to check based a single constant rather than filtering by option class
    public static final ShulkerFeature TINY = new SizeFeature("Size: Tiny", InventoryType.HOPPER);
    public static final ShulkerFeature VERY_SMALL = new SizeFeature("Size: Very Small", InventoryType.DROPPER);
    public static final ShulkerFeature SMALL = new SizeFeature("Size: Small", 2 * 9);
    // Don't need to make one for normal size lol
    public static final ShulkerFeature EXPANDED1 = new SizeFeature("Expanded I", 4 * 9);
    public static final ShulkerFeature EXPANDED2 = new SizeFeature("Expanded II", 5 * 9);
    public static final ShulkerFeature DOUBLE = new SizeFeature("Double", 6 * 9);

    public static final ShulkerFeature REFILL_BLOCKS = new RefillFeature(RefillFeature.TYPE_BLOCKS);
    public static final ShulkerFeature REFILL_FOOD = new RefillFeature(RefillFeature.TYPE_FOOD);
    public static final ShulkerFeature REFILL_DROPS = new RefillFeature(RefillFeature.TYPE_DROP);
    public static final ShulkerFeature REFILL_TOOLS = new RefillFeature(RefillFeature.TYPE_TOOLS);

    public static void registerFeatures() {
        register(AUTO_PICKUP);
        register(EASY_OPEN);
        register(INCEPTION);
        register(IMMORTAL);
    //    register(AUTO_SMELT);
    //    register(COMPRESSION);
        register(AUTO_FEED);

        register(TINY);
        register(VERY_SMALL);
        register(SMALL);
        register(EXPANDED1);
        register(EXPANDED2);
        register(DOUBLE);

        register(REFILL_BLOCKS);
        register(REFILL_FOOD);
        register(REFILL_DROPS);
        register(REFILL_TOOLS);
    }

    private static final Map<String, ShulkerFeature> registeredFeatures = new HashMap<>();
    public static void register(ShulkerFeature option) {
        registeredFeatures.put(stripFeatureName(option.getName()), option);
    }

    public static List<ShulkerFeature> getFeatures(ItemStack itemStack) {
        List<ShulkerFeature> options = new LinkedList<>();

        for (ShulkerFeature option : registeredFeatures.values()) {
            if (option.isAppliedTo(itemStack)) {
                options.add(option);
            }
        }

        return options;
    }

    public static List<ShulkerFeature> getFeatures(Block block) {
        return getFeatures(block.getState());
    }

    @NotNull
    public static List<ShulkerFeature> getFeatures(BlockState state) {
        if (state instanceof TileState tileState) {
            return getFeatures(tileState.getPersistentDataContainer());
        } else {
            return new LinkedList<>();
        }
    }

    public static List<ShulkerFeature> getFeatures(PersistentDataContainer container) {
        List<ShulkerFeature> options = new LinkedList<>();

        for (ShulkerFeature option : registeredFeatures.values()) {
            if (option.isAppliedTo(container)) {
                options.add(option);
            }
        }

        return options;
    }

    public static String stripFeatureName(String optionName) {
        return optionName.trim().toLowerCase().replaceAll("[^a-z0-9/._-]+", "_");
    }

    public static Map<String, ShulkerFeature> getRegisteredFeatures() {
        return Collections.unmodifiableMap(registeredFeatures);
    }
}
