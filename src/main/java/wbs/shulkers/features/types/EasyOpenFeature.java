package wbs.shulkers.features.types;

import wbs.shulkers.features.ShulkerFeature;

public class EasyOpenFeature extends ShulkerFeature {
    public EasyOpenFeature() {
        super("Easy Open");
    }

    @Override
    public String getDescription() {
        return "Allows you to open this shulker box without placing it, by holding it and right clicking. " +
                "Hold shift to place normally";
    }
}
