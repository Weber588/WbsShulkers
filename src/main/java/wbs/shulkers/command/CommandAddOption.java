package wbs.shulkers.command;

import org.bukkit.block.ShulkerBox;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import wbs.shulkers.features.ShulkerFeature;
import wbs.shulkers.features.ShulkerFeatureManager;
import wbs.shulkers.util.CustomShulkerBox;
import wbs.shulkers.util.ShulkerBoxUtils;
import wbs.utils.util.commands.WbsSubcommand;
import wbs.utils.util.plugin.WbsPlugin;

import java.util.LinkedList;
import java.util.List;

public class CommandAddOption extends WbsSubcommand {
    public CommandAddOption(@NotNull WbsPlugin plugin) {
        super(plugin, "add");
    }

    @Override
    protected boolean onCommand(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args, int start) {
        if (!(sender instanceof Player player)) {
            sendMessage("This command is only usable by players.", sender);
            return true;
        }

        if (args.length <= start) {
            sendMessage("Usage: &h/" + label + " add <option>", sender);
            return true;
        }

        String optionName = args[start];
        ShulkerFeature option = ShulkerFeatureManager.getRegisteredFeatures().get(optionName);
        if (option == null) {
            sendMessage("&wUnknown shulker option: &x" + optionName + ". &wPlease choose from the following: &x"
                    + String.join(", ", ShulkerFeatureManager.getRegisteredFeatures().keySet()), sender);
            return true;
        }

        ItemStack held = player.getInventory().getItemInMainHand();

        CustomShulkerBox customBox = ShulkerBoxUtils.from(held);
        if (customBox == null) {
            sendMessage("&wHold a shulker box!", sender);
            return true;
        }

        ShulkerBox box = customBox.getVanillaBox();
        option.applyTo(box);

        customBox.saveToItem();

        sendMessage("Added " + option.getName() + " to your held shulker box!", sender);

        return true;
    }

    @Override
    protected List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args, int start) {
        if (args.length == start) {
            return new LinkedList<>(ShulkerFeatureManager.getRegisteredFeatures().keySet());
        }

        return new LinkedList<>();
    }
}
