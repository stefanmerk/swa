package de.shop.artikelverwaltung.service;
import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class BezeichnungExistsException extends ArtikelServiceException {

	
		private static final long serialVersionUID = 4867667611097919943L;
		private final String bezeichnung;
		
		public BezeichnungExistsException(String bezeichnung) {
			super("Die Bezeichnung " + bezeichnung + " existiert bereits");
			this.bezeichnung = bezeichnung;
		}

		public String getBezeichnung() {
			return bezeichnung;
		}
}
