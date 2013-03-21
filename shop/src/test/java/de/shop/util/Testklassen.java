package de.shop.util;

import java.util.Arrays;
import java.util.List;

import de.shop.artikelverwaltung.service.ArtikelServiceTest;
import de.shop.bestellverwaltung.service.BestServiceTest;
import de.shop.kundenverwaltung.service.KuServiceTest;



public enum Testklassen {
	INSTANCE;
	
	// Testklassen aus verschiedenen Packages auflisten (durch Komma getrennt):
	// so dass alle darin enthaltenen Klassen ins Web-Archiv mitverpackt werden
	private final List<Class<? extends AbstractTest>> classes = Arrays.asList(ArtikelServiceTest.class, 
			BestServiceTest.class, KuServiceTest.class);
	
	public static Testklassen getInstance() {
		return INSTANCE;
	}
	
	public List<Class<? extends AbstractTest>> getTestklassen() {
		return classes;
	}
}
