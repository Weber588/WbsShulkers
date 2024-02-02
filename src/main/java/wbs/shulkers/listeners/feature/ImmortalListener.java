package wbs.shulkers.listeners.feature;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import wbs.shulkers.util.CustomShulkerBox;
import wbs.shulkers.features.ShulkerFeatureManager;
import wbs.shulkers.util.ShulkerBoxUtils;

public class ImmortalListener implements Listener {
    @EventHandler
    public void onShulkerBoxDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Item itemEntity)) {
            return;
        }

        CustomShulkerBox box = ShulkerBoxUtils.from(itemEntity.getItemStack());

        if (box != null && ShulkerFeatureManager.IMMORTAL.isAppliedTo(box)) {
            event.setCancelled(true);
        }
    }
}
