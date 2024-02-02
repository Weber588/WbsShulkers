package wbs.shulkers.util;

import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class FoodManager {
    private FoodManager() {}

    private static final Map<Material, FoodInfo> knownFoods = new HashMap<>();

    public static void registerFood(FoodInfo info) {
        knownFoods.put(info.material(), info);
    }

    @Nullable
    public static FoodInfo getFoodInfo(Material material) {
        return knownFoods.get(material);
    }
}
