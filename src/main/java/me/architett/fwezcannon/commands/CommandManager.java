package me.architett.fwezcannon.commands;

import me.architett.fwezcannon.commands.admin.AuthCommand;
import me.architett.fwezcannon.commands.admin.ReloadCommand;
import me.architett.fwezcannon.commands.user.InfoCommand;
import me.architett.fwezcannon.util.MessageUtil;
import me.architett.fwezcannon.util.NameUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandManager implements TabExecutor{

    private final ArrayList<SubCommand> subcommands = new ArrayList<>();

    public CommandManager(){
        subcommands.add(new ReloadCommand());
        subcommands.add(new AuthCommand());
        subcommands.add(new InfoCommand());
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Error: can't run commands from console");
            return true;
        }

        Player p = (Player) sender;

        if (args.length > 0) {
            for (int i = 0; i < getSubcommands().size(); i++) {
                if (args[0].equalsIgnoreCase(getSubcommands().get(i).getName())) {

                    SubCommand subCommand = getSubcommands().get(i);

                    if (!sender.hasPermission(subCommand.getPermission())) {
                        sender.sendMessage(ChatColor.RED + "Error: can't run command");
                        return true;
                    }

                    if (args.length < subCommand.getArgsRequired()) {
                        sender.sendMessage(ChatColor.RED + "Error: syntax error");
                        return true;
                    }

                    subCommand.perform(p, args);

                }
            }
        }else{
            p.sendMessage(MessageUtil.commandsInfoHeader());

            for (int i = 0; i < getSubcommands().size(); i++) {

                SubCommand subCommand = getSubcommands().get(i);

                if (!sender.hasPermission(subCommand.getPermission()))
                    continue;

                p.sendMessage(MessageUtil.formatListMessage(subCommand.getSyntax()
                        + " - " + ChatColor.AQUA + subCommand.getDescription()));
            }

            p.sendMessage(MessageUtil.chatFooter());
        }

        return true;
    }

    public ArrayList<SubCommand> getSubcommands(){
        return subcommands;
    }


    @SuppressWarnings("NullableProblems")
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        String argChar = "";

        if (args.length == 1) {
            argChar = args[0];

            ArrayList<String> subcommandsArguments = new ArrayList<>();

            for (int i = 0; i < getSubcommands().size(); i++) {
                SubCommand subCommand = getSubcommands().get(i);

                if (!sender.hasPermission(subCommand.getPermission()))
                    continue;

                subcommandsArguments.add(subCommand.getName());
            }

            return NameUtil.filterByStart(subcommandsArguments,argChar);

        }else if (args.length >= 2) {
            argChar = args[1];

            for (int i = 0; i < getSubcommands().size(); i++) {
                if (args[0].equalsIgnoreCase(getSubcommands().get(i).getName())) {
                    return NameUtil.filterByStart(getSubcommands().get(i).getSubcommandArguments((Player) sender, args),argChar);
                }
            }
        }

        return null;
    }

}
