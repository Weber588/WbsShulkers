package wbs.shulkers.command;

import org.bukkit.command.PluginCommand;
import wbs.utils.util.commands.WbsCommand;
import wbs.utils.util.plugin.WbsPlugin;

public class ShulkerOptionCommand extends WbsCommand {
    public ShulkerOptionCommand(WbsPlugin plugin, PluginCommand command) {
        super(plugin, command);

        addSubcommand(new CommandAddOption(plugin), "wbsshulkers.command.add");
    }
}
