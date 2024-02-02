package wbs.shulkers.features.types;

import wbs.shulkers.features.ShulkerFeature;
import wbs.shulkers.features.ShulkerFeatureManager;

import java.util.Collections;
import java.util.List;

public class AutoSmeltFeature extends ShulkerFeature {
    public AutoSmeltFeature() {
        super("Auto Smelt");
    }

    @Override
    public String getDescription() {
        return "If this box contains fuel when you pick up an item, it will be consumed to automatically smelt" +
                "the item!";
    }

    @Override
    public List<ShulkerFeature> requires() {
        return Collections.singletonList(ShulkerFeatureManager.AUTO_PICKUP);
    }

    @Override
    public List<ShulkerFeature> conflictsWith() {
        return Collections.singletonList(ShulkerFeatureManager.COMPRESSION);
    }
}
