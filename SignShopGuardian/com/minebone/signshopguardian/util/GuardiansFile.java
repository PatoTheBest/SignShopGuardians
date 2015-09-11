package com.minebone.signshopguardian.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import com.minebone.signshopguardian.SignShopGuardian;

public class GuardiansFile extends SignShopGuardiansFile {
		
	public GuardiansFile(SignShopGuardian plugin) {
		super(plugin, "guardians");
	}
	
	public void load() {
		try {
			guardiansFile.createNewFile();
			BufferedWriter writer = new BufferedWriter(new FileWriter(guardiansFile));
			writer.write(getHeader("Guardianes de Jugadores"));
			writer.write("\n# Aqui se guardan los guardianes de los jugadores\n");
			writer.close();
			reload();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void eat() {
		this.save();
		this.fileName = null;
		this.guardiansConfig = null;
		this.guardiansFile = null;
	}
	
}
