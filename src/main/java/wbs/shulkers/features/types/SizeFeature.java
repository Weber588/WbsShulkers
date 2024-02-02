package wbs.shulkers.features.types;

import org.bukkit.event.inventory.InventoryType;
import wbs.shulkers.features.ShulkerFeature;

import java.util.Arrays;

public class SizeFeature extends ShulkerFeature {
    public final int customSize;
    public final InventoryType type;

    public SizeFeature(String customName, int customSize) {
        this(customName,
                Arrays.stream(InventoryType.values())
                .filter(type -> type.getDefaultSize() == customSize)
                .findAny()
                .orElse(InventoryType.CHEST),
            customSize
        );
    }

    public SizeFeature(String customName, InventoryType type) {
        this(customName, type, type.getDefaultSize());
    }
    public SizeFeature(String customName, InventoryType type, int customSize) {
        super(customName != null ? customName
                : "Size: " + customSize);
        if (type == InventoryType.CHEST && (customSize % 9 != 0 || customSize <= 0 || customSize > 6 * 9)) {
            throw new IllegalArgumentException("Custom size must be multiple of 9 for non-chest menus.");
        }

        this.type = type;
        this.customSize = customSize;
    }

    @Override
    public String getDescription() {
        return "This inventory has a total of " + customSize + " slots!";
    }
}
