package nl.marido.deluxeheads.command.admin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import nl.marido.deluxeheads.DeluxeHeads;
import nl.marido.deluxeheads.cache.CacheHead;
import nl.marido.deluxeheads.command.AbstractCommand;
import nl.marido.deluxeheads.config.MainConfig;
import nl.marido.deluxeheads.config.lang.Lang;

public class GiveCommand extends AbstractCommand {

	@Override
	public String getCommandLabel(MainConfig config) {
		return config.getGiveCommand();
	}

	@Override
	public String getPermission() {
		return "deluxeheads.give";
	}

	@Override
	public Lang.HelpSection getHelp() {
		return Lang.Command.Give.help();
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
		if (args.length != 4) {
			sendInvalidArgs(sender);
			return true;
		}

		int id;
		try {
			id = Integer.valueOf(args[1]);
		} catch (NumberFormatException e) {
			Lang.Command.Errors.integer(args[1]).send(sender);
			return true;
		}

		int amount;
		try {
			amount = Integer.valueOf(args[3]);
		} catch (NumberFormatException e) {
			Lang.Command.Give.invalidAmount(args[3]).send(sender);
			return true;
		}

		if (amount <= 0) {
			Lang.Command.Give.invalidAmount(args[3]).send(sender);
		}

		Player player = Bukkit.getPlayer(args[2]);

		if (player == null || !player.isOnline()) {
			Lang.Command.Give.cantFindPlayer(args[2]).send(sender);
			return true;
		}

		CacheHead head = DeluxeHeads.getCache().findHead(id);

		if (head == null) {
			Lang.Command.Give.cantFindHead(id).send(sender);
			return true;
		}

		ItemStack headItem = head.getItemStack();
		for (int i = 0; i < amount; i++) {
			if (player.getInventory().firstEmpty() != -1) {
				player.getInventory().addItem(headItem.clone());
			} else {
				Item item = player.getWorld().dropItemNaturally(player.getEyeLocation(), headItem.clone());
				item.setPickupDelay(0);
			}
		}

		Lang.Command.Give.give(amount, head.getName(), player.getName()).send(sender);
		return true;
	}
}
