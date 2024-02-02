package wbs.shulkers;

import org.bukkit.event.Listener;
import wbs.shulkers.command.ShulkerOptionCommand;
import wbs.shulkers.listeners.feature.AutoFeedListener;
import wbs.shulkers.listeners.feature.AutoPickupListener;
import wbs.shulkers.listeners.PersistenceListener;
import wbs.shulkers.listeners.feature.EasyOpenListener;
import wbs.shulkers.listeners.feature.RefillListener;
import wbs.shulkers.features.ShulkerFeatureManager;
import wbs.utils.util.plugin.WbsPlugin;

public class WbsShulkers extends WbsPlugin {

    private static WbsShulkers instance;
    public static WbsShulkers getInstance() {
        return instance;
    }

    public ShulkerSettings settings;

    @Override
    public void onEnable() {
        instance = this;

        ShulkerFeatureManager.registerFeatures();

        settings = new ShulkerSettings(this);
        settings.reload();

        registerListener(new PersistenceListener());

        registerListener(new AutoPickupListener());
        registerListener(new EasyOpenListener());
        registerListener(new RefillListener());
        registerListener(new AutoFeedListener());

        new ShulkerOptionCommand(this, getCommand("shulkeroptions"));
    }

    @Override
    public void registerListener(Listener listener) {
        super.registerListener(listener);
    }
}
