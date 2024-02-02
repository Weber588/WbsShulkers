package wbs.shulkers.listeners.feature;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import wbs.shulkers.util.CustomShulkerBox;
import wbs.shulkers.features.ShulkerFeatureManager;
import wbs.shulkers.util.ShulkerBoxUtils;
import wbs.utils.util.WbsSound;
import wbs.utils.util.entities.WbsEntityUtil;
import wbs.utils.util.particles.NormalParticleEffect;
import wbs.utils.util.particles.WbsParticleGroup;

import java.util.*;

public class AutoPickupListener implements Listener {

    private static final WbsParticleGroup PICKUP_EFFECT = new WbsParticleGroup();
    private static final WbsSound PICKUP_SOUND = new WbsSound(Sound.ENTITY_ITEM_PICKUP);

    static {
        PICKUP_EFFECT.addEffect(new NormalParticleEffect().setXYZ(0).setAmount(3), Particle.SPELL_WITCH);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockDropItem(EntityPickupItemEvent event) {
        LivingEntity entity = event.getEntity();

        if (!(entity instanceof Player player)) {
            return;
        }

        Map<CustomShulkerBox, ItemStack> autoPickupBoxes = ShulkerBoxUtils.getInvShulkersWithOption(
                        player.getInventory(),
                        ShulkerFeatureManager.AUTO_PICKUP
                );

        if (autoPickupBoxes.isEmpty()) {
            return;
        }

        Item dropEntity = event.getItem();
        ItemStack drop = dropEntity.getItemStack();

        if (Tag.SHULKER_BOXES.isTagged(drop.getType())) {
            return;
        }

        for (CustomShulkerBox box : autoPickupBoxes.keySet()) {
            Inventory inventory = box.getInventory();
            if (!inventory.containsAtLeast(drop, 1)) {
                continue;
            }

            if (box.canContain(drop)) {
                HashMap<Integer, ItemStack> failed = box.addItem(drop);
                box.saveToItem();

                PICKUP_EFFECT.play(dropEntity.getLocation(), WbsEntityUtil.getMiddleLocation(player));
                PICKUP_SOUND.play(dropEntity.getLocation());

                if (failed.isEmpty()) {
                    dropEntity.remove();
                    event.setCancelled(true);
                    break;
                } else {
                    ItemStack failedToAdd = failed.get(0);

                    drop.setAmount(failedToAdd.getAmount());
                    dropEntity.setItemStack(drop);
                }
            }
        }
    }
}



















