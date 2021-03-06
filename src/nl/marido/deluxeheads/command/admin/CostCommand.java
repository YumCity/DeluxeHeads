package nl.marido.deluxeheads.command.admin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import nl.marido.deluxeheads.command.AbstractCommand;
import nl.marido.deluxeheads.config.MainConfig;
import nl.marido.deluxeheads.config.lang.Lang;
import nl.marido.deluxeheads.oldmenu.mode.CostMode;
import nl.marido.deluxeheads.oldmenu.mode.InvModeType;

public class CostCommand extends AbstractCommand {

	@Override
	public String getCommandLabel(MainConfig config) {
		return config.getCostCommand();
	}

	@Override
	public String getPermission() {
		return "deluxeheads.cost";
	}

	@Override
	public Lang.HelpSection getHelp() {
		return Lang.Command.Cost.help();
	}

	@Override
	public boolean onCommand(final CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			Lang.Command.Errors.mustBePlayer().send(sender);
			return true;
		}

		if (args.length != 2) {
			sendInvalidArgs(sender);
			return true;
		}

		double cost;
		try {
			cost = Double.valueOf(args[1]);
		} catch (NumberFormatException e) {
			Lang.Command.Errors.number(args[1]).send(sender);
			return true;
		}

		if (cost < 0) {
			Lang.Command.Errors.negative(args[1]).send(sender);
			return true;
		}

		InvModeType.COST.open((Player) sender).asType(CostMode.class).setCost(cost);
		return true;
	}

}
