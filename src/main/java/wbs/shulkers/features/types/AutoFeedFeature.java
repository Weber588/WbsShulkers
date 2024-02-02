package wbs.shulkers.features.types;

import wbs.shulkers.features.ShulkerFeature;

public class AutoFeedFeature extends ShulkerFeature {
    public AutoFeedFeature() {
        super("Auto Feed");
    }

    @Override
    public String getDescription() {
        return "When you get hungry enough to eat the first food item in this box while it's in your inventory, " +
                "the box will automatically feed it to you, consuming it directly from the box.";
    }
}
