package net.ciebus.kokoa.forgemoddetector;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;

import java.util.List;

public class Commands implements TabExecutor {
    private final ForgeModDetector plugin;

    public Commands(ForgeModDetector plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("forgemoddetector.admin")) {
            sender.sendMessage(ChatColor.RED + "権限ないです");
            return true;
        } else {
            Player target;
            if (args.length > 0) {
                target = Bukkit.getPlayer(args[0]);
                if (target == null) {
                    sender.sendMessage(ChatColor.RED + "ユーザいなさそうです");
                    return true;
                }
            } else {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "コマンドが間違ってそうです");
                    return true;
                }

                target = (Player)sender;
            }

            if (!target.hasMetadata("forge_mods")) {
                sender.sendMessage(ChatColor.RED + "MODいれてなさそう");
                return true;
            } else {
                List mods = (List)((MetadataValue)target.getMetadata("forge_mods").get(0)).value();
                sender.sendMessage( target.getName() + " : " + ChatColor.GREEN + StringUtils.join(mods, ChatColor.DARK_AQUA + ", " + ChatColor.GREEN) + ChatColor.DARK_AQUA);
                return true;
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }
}
