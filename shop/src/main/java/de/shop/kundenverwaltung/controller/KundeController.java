package de.shop.kundenverwaltung.controller;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.Flash;
import javax.inject.Inject;
import javax.inject.Named;

import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.kundenverwaltung.service.KundeService;
import de.shop.kundenverwaltung.service.KundeService.FetchType;
import de.shop.util.Log;
import de.shop.util.Transactional;


/**
 * Dialogsteuerung fuer die Kundenverwaltung
 */
@Named("kc")
@RequestScoped
@Log
public class KundeController implements Serializable {
	private static final long serialVersionUID = -8817180909526894740L;
	
	private static final String FLASH_KUNDE = "kunde";
	private static final String JSF_VIEW_KUNDE = "/kundenverwaltung/viewKunde";
	
	@Inject
	private KundeService ks;
	
	@Inject
	private Flash flash;
	
	private Long kundeId;

	@Override
	public String toString() {
		return "KundeController [kundeId=" + kundeId + "]";
	}

	public void setkundeId(Long kundeId) {
		this.kundeId = kundeId;
	}

	public Long getkundeId() {
		return kundeId;
	}

	/**
	 * Action Methode, um einen Kunden zu gegebener ID zu suchen
	 * @return URL fuer Anzeige des gefundenen Kunden; sonst null
	 */
	@Transactional
	public String findKundeById() {
		final Kunde kunde = ks.findKundebyID(kundeId, FetchType.NUR_KUNDE, null);
		if (kunde == null) {
			flash.remove(FLASH_KUNDE);
			return null;
		}
		
		flash.put(FLASH_KUNDE, kunde);
		return JSF_VIEW_KUNDE;
	}
}
