package wbs.shulkers.features.types;

import wbs.shulkers.features.ShulkerFeature;

public class AutoPickupFeature extends ShulkerFeature {
    public AutoPickupFeature() {
        super("Auto Pickup");
    }

    @Override
    public String getDescription() {
        return "While this is in your inventory, if you pick up an item that is also in this shulker box, it will" +
                "go directly into the shulker box instead of your inventory. Requires at least 1 slot to be open in" +
                "your inventory to pick up items.";
    }
}
