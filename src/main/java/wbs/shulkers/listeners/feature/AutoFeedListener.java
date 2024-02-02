package wbs.shulkers.listeners.feature;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.inventory.ItemStack;
import wbs.shulkers.features.ShulkerFeatureManager;
import wbs.shulkers.util.CustomShulkerBox;
import wbs.shulkers.util.FoodInfo;
import wbs.shulkers.util.FoodManager;
import wbs.shulkers.util.ShulkerBoxUtils;
import wbs.utils.util.WbsSound;
import wbs.utils.util.WbsSoundGroup;
import wbs.utils.util.entities.WbsEntityUtil;
import wbs.utils.util.particles.NormalParticleEffect;
import wbs.utils.util.particles.WbsParticleGroup;

import java.util.Map;
import java.util.Objects;

public class AutoFeedListener implements Listener {

    // Can't find this anywhere in code? Not in spigot API at least from what I can find
    private static final int MAX_FOOD_LEVEL = 20;
    private static final WbsSoundGroup EAT_SOUND = new WbsSoundGroup();


    static {
        for (int i = 0; i < 7; i++) {
            EAT_SOUND.addSound(new WbsSound(Sound.ENTITY_GENERIC_EAT), 2);
        }
        EAT_SOUND.addSound(new WbsSound(Sound.ENTITY_PLAYER_BURP), 2);
    }

    @EventHandler
    public void onHungerChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        Map<CustomShulkerBox, ItemStack> feedBoxes =
                ShulkerBoxUtils.getInvShulkersWithOption(player.getInventory(), ShulkerFeatureManager.AUTO_FEED);

        AttributeInstance healthInstance = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        Objects.requireNonNull(healthInstance);

        boxLoop: for (CustomShulkerBox box : feedBoxes.keySet()) {
            for (ItemStack itemInBox : box.getInventory()) {
                if (itemInBox == null || itemInBox.getType() == Material.AIR) {
                    continue;
                }

                // Why isn't this in the spigot api :(
                FoodInfo foodInfo = FoodManager.getFoodInfo(itemInBox.getType());
                if (foodInfo != null) {
                    // Eat only when optimal, unless the player is below max health & no longer regenerating
                    if (MAX_FOOD_LEVEL - event.getFoodLevel() >= foodInfo.hunger() ||
                            (player.getHealth() < healthInstance.getValue() && MAX_FOOD_LEVEL - event.getFoodLevel() > 2)) {
                        foodInfo.applyTo(player);

                        // Event will override if we don't set it back into the event obj after updating the player.
                        event.setFoodLevel(player.getFoodLevel());

                        ItemStack foodItem = itemInBox.clone();
                        foodItem.setAmount(1);

                        box.removeItem(foodItem);

                        box.saveToItem();

                        EAT_SOUND.play(player.getEyeLocation());

                        new NormalParticleEffect().setAmount(1)
                                .setOptions(new ItemStack(foodInfo.material()))
                                .play(Particle.ITEM_CRACK, player.getEyeLocation());

                        new NormalParticleEffect().setAmount(15)
                                .play(Particle.SPELL_WITCH, WbsEntityUtil.getMiddleLocation(player));

                        return;
                    }

                    // Only look at the first food item in each box to allow players to prioritise if desired
                    continue boxLoop;
                }
            }
        }
    }
}
