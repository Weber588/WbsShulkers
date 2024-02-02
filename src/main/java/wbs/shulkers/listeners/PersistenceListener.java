package wbs.shulkers.listeners;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import wbs.shulkers.WbsShulkers;
import wbs.shulkers.util.CustomShulkerBox;
import wbs.shulkers.util.ShulkerBoxUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
public class PersistenceListener implements Listener {
    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();

        ItemStack placed = event.getItemInHand();

        CustomShulkerBox itemBox = ShulkerBoxUtils.from(placed);

        if (itemBox != null && itemBox.isCustom()) {
            itemBox.saveToBlock(block);

            // Should never be null since we just added options
            CustomShulkerBox blockBox = Objects.requireNonNull(ShulkerBoxUtils.from(block));
            blockBox.setInventory(itemBox.getInventory());

            blockBox.saveToBlock();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onOpenShulker(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block block = event.getClickedBlock();

        if (block == null) {
            return;
        }

        CustomShulkerBox box = ShulkerBoxUtils.from(block);

        if (box != null && box.isCustom()) {
            WbsShulkers.getInstance().getLogger().info("Opening custom inventory.");
            event.setCancelled(true);
            box.openAndMonitor(event.getPlayer());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockDropItem(BlockDropItemEvent event) {
        BlockState blockState = event.getBlockState();
        CustomShulkerBox blockBox = ShulkerBoxUtils.from(blockState);

        if (blockBox == null || !blockBox.isCustom()) {
            return;
        }

        List<Item> entities = event.getItems();
        List<ItemStack> boxes = new LinkedList<>();

        for (Item itemEntity : entities) {
            ItemStack itemStack = itemEntity.getItemStack();
            if (itemStack.getItemMeta() instanceof BlockStateMeta stateMeta) {
                BlockState dropState = stateMeta.getBlockState();

                if (dropState instanceof ShulkerBox) {
                    boxes.add(itemStack);
                }
            }
        }

        if (boxes.isEmpty()) {
            return;
        }

        for (ItemStack droppedShulker : boxes) {
            blockBox.saveToItem(droppedShulker);

            // Should never be null since we just added options
            CustomShulkerBox itemBox = Objects.requireNonNull(ShulkerBoxUtils.from(droppedShulker));
            // Ensure custom inventory metadata copies across
            itemBox.setInventory(blockBox.getInventory());

            itemBox.saveToItem();
        }
    }

    @EventHandler
    public void onShulkerItemBreak(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Item itemEntity)) {
            return;
        }

        CustomShulkerBox box = ShulkerBoxUtils.from(itemEntity.getItemStack());

        if (box != null && box.isCustom()) {
            List<ItemStack> drops = event.getDrops();

            drops.clear(); // We'll populate the drops ourselves.

            Inventory inventory = box.getInventory();
            Collections.addAll(drops, inventory.getContents());
        }
    }
}
