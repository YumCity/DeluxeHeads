package nl.marido.deluxeheads.command.admin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import nl.marido.deluxeheads.DeluxeHeads;
import nl.marido.deluxeheads.command.AbstractCommand;
import nl.marido.deluxeheads.config.MainConfig;
import nl.marido.deluxeheads.config.lang.Lang;
import nl.marido.deluxeheads.menu.ui.item.Item;

public class ItemEcoCommand extends AbstractCommand {

	@Override
	public String getCommandLabel(MainConfig config) {
		return config.getItemEcoCommand();
	}

	@Override
	public String getPermission() {
		return "deluxeheads.item-eco";
	}

	@Override
	public Lang.HelpSection getHelp() {
		return Lang.Command.ItemEco.help();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length < 2) {
			sendInvalidArgs(sender);
			return true;
		}

		if (args[1].equalsIgnoreCase("give")) {
			onGiveCommand(sender, args);
			return true;
		}

		if (!(sender instanceof Player)) {
			Lang.Command.Errors.mustBePlayer().send(sender);
			return true;
		}

		Player player = (Player) sender;

		if (args[1].equalsIgnoreCase("set")) {
			onSetCommand(player, args);
			return true;
		}

		if (args[1].equalsIgnoreCase("get")) {
			onGetCommand(player, args);
			return true;
		}

		sendInvalidArgs(sender);
		return true;
	}

	private void onSetCommand(Player player, String[] args) {
		if (args.length != 2) {
			Lang.Command.ItemEco.Set.help().sendInvalidArgs(player);
			return;
		}

		@SuppressWarnings("deprecation")
		// Had to do this to resolve the compatibility issue with 1.13.
		ItemStack itemStack = player.getInventory().getItemInHand();

		if (itemStack == null) {
			Lang.Command.ItemEco.Set.noItem().send(player);
			return;
		}

		Item item = Item.create(itemStack).amount(1);

		DeluxeHeads.getMainConfig().setItemEcoItem(item);

		Lang.Command.ItemEco.Set.set().send(player);
	}

	private void onGetCommand(Player player, String[] args) {
		if (args.length != 2 && args.length != 3) {
			Lang.Command.ItemEco.Get.help().sendInvalidArgs(player);
			return;
		}

		int amount = 1;

		if (args.length == 3) {
			try {
				amount = Integer.valueOf(args[2]);
			} catch (NumberFormatException e) {
				Lang.Command.Errors.integer(args[2]);
				return;
			}

			if (amount < 1) {
				Lang.Command.Errors.negative(args[2]);
				return;
			}
		}

		giveTokens(player, amount);

		Lang.Command.ItemEco.Get.got(amount).send(player);
	}

	private void onGiveCommand(CommandSender sender, String[] args) {
		if (args.length != 3 && args.length != 4) {
			Lang.Command.ItemEco.Give.help().sendInvalidArgs(sender);
			return;
		}

		int amount = 1;

		if (args.length == 4) {
			try {
				amount = Integer.valueOf(args[3]);
			} catch (NumberFormatException e) {
				Lang.Command.Errors.integer(args[3]);
				return;
			}

			if (amount < 1) {
				Lang.Command.Errors.negative(args[3]);
				return;
			}
		}

		Player player = Bukkit.getPlayer(args[2]);

		if (player == null) {
			Lang.Command.ItemEco.Give.unknownPlayer(args[2]).send(sender);
			return;
		}

		giveTokens(player, amount);

		Lang.Command.ItemEco.Give.got(amount).send(player);
		Lang.Command.ItemEco.Give.given(player.getName(), amount).send(sender);
	}

	private void giveTokens(Player player, int amount) {
		while (amount > 0) {
			int giveAmount = Math.min(64, amount);
			amount -= giveAmount;

			ItemStack itemStack = DeluxeHeads.getMainConfig().getItemEconomyItem().amount(giveAmount).build();

			if (player.getInventory().firstEmpty() != -1) {
				player.getInventory().addItem(itemStack);
			} else {
				org.bukkit.entity.Item item = player.getWorld().dropItemNaturally(player.getEyeLocation(), itemStack);

				item.setPickupDelay(0);
			}
		}
	}

}
