package wbs.shulkers.features.types;

import wbs.shulkers.features.ShulkerFeature;

public class ImmortalFeature extends ShulkerFeature {
    public ImmortalFeature() {
        super("Immortal");
    }

    @Override
    public String getDescription() {
        return "While placed the box can be broken, but the item form is indestructible - it can't be burnt, blown up, " +
                "or destroyed by cactus or anvils.";
    }
}
