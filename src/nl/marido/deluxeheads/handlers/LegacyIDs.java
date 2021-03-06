package nl.marido.deluxeheads.handlers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;

import nl.marido.deluxeheads.DeluxeHeads;
import nl.marido.deluxeheads.util.Checks;

public class LegacyIDs {

	public static final LegacyIDs EMPTY = new LegacyIDs(Collections.emptyMap());

	private final Map<Integer, String> idToType;

	public LegacyIDs(Map<Integer, String> idToType) {
		Checks.ensureNonNull(idToType, "idToType");

		this.idToType = idToType;
	}

	public String fromId(int id) {
		return idToType.get(id);
	}

	public void write(File file) throws IOException {
		try (FileOutputStream fos = new FileOutputStream(file); OutputStreamWriter osr = new OutputStreamWriter(fos); BufferedWriter writer = new BufferedWriter(osr)) {

			write(writer);
		}
	}

	public void write(BufferedWriter writer) throws IOException {
		for (Map.Entry<Integer, String> entry : idToType.entrySet()) {
			writer.write(entry.getKey() + ":" + entry.getValue() + "\n");
		}
	}

	@SuppressWarnings("deprecation")
	public static LegacyIDs create() {
		Map<Integer, String> idToType = new HashMap<>();

		for (Material type : Material.values()) {
			// This need to be kept for the legacy IDS for 1.13.
			idToType.put(type.getId(), type.name());
		}

		return new LegacyIDs(idToType);
	}

	public static LegacyIDs readResource(String resource) throws IOException {
		try (InputStream is = DeluxeHeads.getInstance().getResource(resource); InputStreamReader isr = new InputStreamReader(is); BufferedReader reader = new BufferedReader(isr)) {

			return read(reader);
		}
	}

	public static LegacyIDs read(BufferedReader reader) throws IOException {
		Map<Integer, String> idToType = new HashMap<>();

		String line;
		while ((line = reader.readLine()) != null) {
			int splitIndex = line.indexOf(':');
			int id = Integer.valueOf(line.substring(0, splitIndex));
			String type = line.substring(splitIndex + 1);
			idToType.put(id, type);
		}

		return new LegacyIDs(idToType);
	}
}
