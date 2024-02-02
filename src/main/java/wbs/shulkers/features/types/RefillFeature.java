package wbs.shulkers.features.types;

import wbs.shulkers.features.ShulkerFeature;

public class RefillFeature extends ShulkerFeature {
    public static final String TYPE_FOOD = "Food";
    public static final String TYPE_BLOCKS = "Blocks";
    public static final String TYPE_TOOLS = "Tools";
    public static final String TYPE_DROP = "Drop";

    public final String type;

    @Override
    public String getDescription() {
        return switch (type) {
            case TYPE_BLOCKS -> "When you place a block, if this box is in your inventory and contains the same type of block, " +
                    "it will be immediately moved from this box into your inventory.";
            case TYPE_DROP -> "When you drop an item, if this box is in your inventory and contains the same type of item, " +
                    "it will be immediately moved from this box into your inventory.";
            case TYPE_FOOD -> "When you finish eating a stack of food, if this box is in your inventory and contains the same " +
                    "type of food, it will be immediately moved from this box into your inventory.";
            case TYPE_TOOLS -> "If you break a tool/weapon while this box is in your inventory and contains the same type of " +
                    "tool/weapon, it will be immediately moved from this box into your inventory!";
            default -> null;
        };
    }

    public RefillFeature(String type) {
        super("Refill: " + type);

        this.type = type;
    }
}
