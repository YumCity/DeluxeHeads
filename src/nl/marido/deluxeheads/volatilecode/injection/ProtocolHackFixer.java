package nl.marido.deluxeheads.volatilecode.injection;

import java.util.concurrent.Executor;

import javax.annotation.Nonnull;

import nl.marido.deluxeheads.volatilecode.reflection.Version;
import nl.marido.deluxeheads.volatilecode.reflection.craftbukkit.CraftMetaSkullSub1;
import nl.marido.deluxeheads.volatilecode.reflection.nms.TileEntitySkull;

public class ProtocolHackFixer {

	public static String banana = "%%__USER__%%";

	public static void fix() {
		if (Version.v1_8.higherThan(Version.getVersion())) {
			injectTileEntitySkullExecutor();
		}
	}

	private static void injectTileEntitySkullExecutor() {
		TileEntitySkull.setExecutor(new InterceptExecutor(TileEntitySkull.getExecutor()));
	}

	private static class InterceptExecutor implements Executor {

		private final Executor handle;

		private InterceptExecutor(Executor handle) {
			this.handle = handle;
		}

		@Override
		public void execute(@Nonnull Runnable command) {
			if (command.getClass().equals(CraftMetaSkullSub1.CraftMetaSkullSub1Class)) {
				CraftMetaSkullSub1 skull = new CraftMetaSkullSub1(command);

				if (skull.getMeta().getProfile().getName() == null)
					return;
			}

			handle.execute(command);
		}

	}

}
