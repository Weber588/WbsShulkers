package wbs.shulkers.util;

import org.bukkit.Material;
import org.bukkit.entity.Player;

public record FoodInfo(int hunger, double saturation, Material material) {

    public void applyTo(Player player) {
        int currentFood = player.getFoodLevel();
        player.setFoodLevel(currentFood + hunger);

        float currentSaturation = player.getSaturation();
        player.setSaturation(currentSaturation + (float) saturation);
    }
}
