package wbs.shulkers.listeners.feature;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import wbs.shulkers.util.CustomShulkerBox;
import wbs.shulkers.features.ShulkerFeatureManager;
import wbs.shulkers.util.ShulkerBoxUtils;

public class EasyOpenListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onRightClickShulker(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        if (player.isSneaking()) {
            return;
        }

        ItemStack heldItem = event.getItem();
        if (ShulkerFeatureManager.EASY_OPEN.isAppliedTo(heldItem)) {
            CustomShulkerBox box = ShulkerBoxUtils.from(heldItem);

            if (box != null) {
                event.setCancelled(true);
                box.openAndMonitor(player);
            }
        }
    }
}
