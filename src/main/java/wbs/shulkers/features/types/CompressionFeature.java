package wbs.shulkers.features.types;

import wbs.shulkers.features.ShulkerFeature;
import wbs.shulkers.features.ShulkerFeatureManager;

import java.util.Collections;
import java.util.List;

public class CompressionFeature extends ShulkerFeature {
    public CompressionFeature() {
        super("Compression");
    }

    @Override
    public String getDescription() {
        return "Automatically compresses items to storage blocks when picked up (i.e. iron ingot -> iron block etc).";
    }

    @Override
    public List<ShulkerFeature> requires() {
        return Collections.singletonList(ShulkerFeatureManager.AUTO_PICKUP);
    }

    @Override
    public List<ShulkerFeature> conflictsWith() {
        return Collections.singletonList(ShulkerFeatureManager.AUTO_SMELT);
    }
}
