package wbs.shulkers;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import wbs.shulkers.util.FoodInfo;
import wbs.shulkers.util.FoodManager;
import wbs.utils.util.WbsEnums;
import wbs.utils.util.plugin.WbsPlugin;
import wbs.utils.util.plugin.WbsSettings;

import java.util.Objects;

public class ShulkerSettings extends WbsSettings {
    protected ShulkerSettings(WbsPlugin plugin) {
        super(plugin);
    }

    private YamlConfiguration config = null;
    private YamlConfiguration foodConfig = null;

    @Override
    public void reload() {
        errors.clear();
        config = loadDefaultConfig("config.yml");
        foodConfig = loadConfigSafely(genConfig("food.yml"));

        loadSettings();
        loadFood();
    }

    private void loadSettings() {
        ConfigurationSection section = config.getConfigurationSection("settings");
        String directory = config.getName() + "/settings";
        if (section == null) {
            logError("Settings config missing! Defaults will be used.", directory);
        } else {
            loreFormat = section.getString("lore-format", loreFormat);
        }
    }

    private void loadFood() {
        ConfigurationSection section = foodConfig.getRoot();
        String directory = foodConfig.getName();
        if (section == null) {
            logError("Food config was empty. Disabling food related upgrades.", directory);
        } else {
            for (String foodName : foodConfig.getKeys(false)) {
                Material material = WbsEnums.getEnumFromString(Material.class, foodName);

                if (material == null) {
                    logError("Invalid material: " + foodName, directory + "/" + foodName);
                    continue;
                }

                ConfigurationSection subsection = Objects.requireNonNull(section.getConfigurationSection(foodName));

                int hunger;
                if (subsection.isInt("hunger")) {
                    hunger = subsection.getInt("hunger");
                } else {
                    logError("Hunger value is required. ", directory + "/" + foodName);
                    continue;
                }

                double saturation;
                if (subsection.isDouble("saturation") || subsection.isInt("saturation")) {
                    saturation= subsection.getDouble("saturation");
                } else {
                    logError("Saturation value is required. ", directory + "/" + foodName);
                    continue;
                }

                FoodInfo foodInfo = new FoodInfo(hunger, saturation, material);
                FoodManager.registerFood(foodInfo);
            }
        }
    }

    private String loreFormat = "&7";
    public String getLoreFormat() {
        return loreFormat;
    }
}
