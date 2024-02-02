package wbs.shulkers.listeners.feature;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import wbs.shulkers.WbsShulkers;
import wbs.shulkers.util.CustomShulkerBox;
import wbs.shulkers.features.ShulkerFeatureManager;
import wbs.shulkers.util.ShulkerBoxUtils;

import java.util.Map;

public class RefillListener implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlaceBlock(BlockPlaceEvent event) {
        ItemStack placed = event.getItemInHand();

        if (placed.getAmount() == 1) {
            Player player = event.getPlayer();
            Map<CustomShulkerBox, ItemStack> refillBoxes = ShulkerBoxUtils.getInvShulkersWithOption(
                    player.getInventory(),
                    ShulkerFeatureManager.REFILL_BLOCKS
            );

            if (refillBoxes.isEmpty()) {
                return;
            }

            for (CustomShulkerBox box : refillBoxes.keySet()) {
                Inventory checkForRefill = box.getInventory();
                for (ItemStack stack : checkForRefill) {
                    if (placed.isSimilar(stack)) {
                        placed.setAmount(stack.getAmount());

                        box.removeItem(stack);
                        box.saveToItem();

                        WbsShulkers.getInstance().sendActionBar("Item replaced from " + box.getInvName() + "&r!", player);
                        return;
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onItemDurabilityDamage(PlayerItemBreakEvent event) {
        ItemStack broken = event.getBrokenItem();

        Player player = event.getPlayer();
        PlayerInventory playerInv = player.getInventory();
        Map<CustomShulkerBox, ItemStack> refillBoxes = ShulkerBoxUtils.getInvShulkersWithOption(
                playerInv,
                ShulkerFeatureManager.REFILL_TOOLS
        );

        if (refillBoxes.isEmpty()) {
            return;
        }

        for (CustomShulkerBox box : refillBoxes.keySet()) {
            Inventory checkForRefill = box.getInventory();
            for (ItemStack stack : checkForRefill) {
                if (stack != null && stack.getType() == broken.getType()) {
                    box.removeItem(stack);

                    playerInv.setItemInMainHand(stack);

                    box.saveToItem();

                    WbsShulkers.getInstance().sendActionBar("Item replaced from " + box.getInvName() + "&r!", player);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onEatFood(PlayerItemConsumeEvent event) {
        ItemStack consumed = event.getItem();

        Player player = event.getPlayer();
        PlayerInventory playerInv = player.getInventory();
        Map<CustomShulkerBox, ItemStack> refillBoxes = ShulkerBoxUtils.getInvShulkersWithOption(
                playerInv,
                ShulkerFeatureManager.REFILL_FOOD
        );

        if (refillBoxes.isEmpty()) {
            return;
        }

        for (CustomShulkerBox box : refillBoxes.keySet()) {
            Inventory checkForRefill = box.getInventory();
            for (ItemStack stack : checkForRefill) {
                if (stack != null && stack.isSimilar(consumed)) {
                    box.removeItem(stack);

                    consumed.setAmount(stack.getAmount());

                    box.saveToItem();

                    WbsShulkers.getInstance().sendActionBar("Item replaced from " + box.getInvName() + "&r!", player);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        ItemStack dropped = event.getItemDrop().getItemStack();

        Player player = event.getPlayer();
        PlayerInventory playerInv = player.getInventory();
        Map<CustomShulkerBox, ItemStack> refillBoxes = ShulkerBoxUtils.getInvShulkersWithOption(
                playerInv,
                ShulkerFeatureManager.REFILL_DROPS
        );

        if (refillBoxes.isEmpty()) {
            return;
        }

        for (CustomShulkerBox box : refillBoxes.keySet()) {
            Inventory checkForRefill = box.getInventory();
            for (ItemStack stack : checkForRefill) {
                if (stack != null && stack.isSimilar(dropped)) {
                    box.removeItem(stack);

                    playerInv.addItem(stack);

                    box.saveToItem();

                    WbsShulkers.getInstance().sendActionBar("Item replaced from " + box.getInvName() + "&r!", player);
                    return;
                }
            }
        }
    }
}
