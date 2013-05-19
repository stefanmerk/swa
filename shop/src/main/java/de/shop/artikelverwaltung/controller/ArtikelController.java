package de.shop.artikelverwaltung.controller;

import static javax.ejb.TransactionAttributeType.REQUIRED;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.TransactionAttribute;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.Flash;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

import org.jboss.logging.Logger;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.artikelverwaltung.service.ArtikelService;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.util.Log;
import de.shop.util.Transactional;


/**
 * Dialogsteuerung fuer die ArtikelService
 */
@Named("ac")
@RequestScoped
@Log
public class ArtikelController implements Serializable {
	
	private static final long serialVersionUID = -7034886139702569282L;

	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	
	private static final String JSF_LIST_ARTIKEL = "/artikelverwaltung/listArtikel";
	private static final String FLASH_ARTIKEL = "artikel";
	//private static final int ANZAHL_LADENHUETER = 5;
	
	private static final String JSF_SELECT_ARTIKEL = "/artikelverwaltung/selectArtikel";
	private static final String SESSION_VERFUEGBARE_ARTIKEL = "verfuegbareArtikel";
	private static final int MAX_AUTOCOMPLETE = 5;
	private String bezeichnung;
	
	//private List<Artikel> ladenhueter;

	@Inject
	private ArtikelService as;
	
	@Inject
	private Flash flash;
	
	@Inject
	private transient HttpSession session;

	
	@PostConstruct
	private void postConstruct() {
		LOGGER.debugf("CDI-faehiges Bean %s wurde erzeugt", this);
	}

	@PreDestroy
	private void preDestroy() {
		LOGGER.debugf("CDI-faehiges Bean %s wird geloescht", this);
	}
	
	@Override
	public String toString() {
		return "ArtikelController [bezeichnung=" + bezeichnung + "]";
	}

	public void setBezeichnung(String bezeichnung) {
		this.bezeichnung = bezeichnung;
	}
	public String getBezeichnung() {
		return bezeichnung;
	}




//	public List<Artikel> getLadenhueter() {
//		return ladenhueter;
//	}
	
	
	@TransactionAttribute(REQUIRED)
	public String findArtikelByBezeichnung() {
		final Artikel artikel = as.findArtikelByBezeichnung(bezeichnung);
		flash.put(FLASH_ARTIKEL, artikel);

		return JSF_LIST_ARTIKEL;
	}
	
	@TransactionAttribute(REQUIRED)
	public List<Artikel> findArtikelByBezPrefix(String BezPrefix) {
		List<Artikel> artikelPrefix = null;
		
		
		artikelPrefix = as.findArtikelbyBezPrefix(BezPrefix);
		if (artikelPrefix == null || artikelPrefix.isEmpty()) {
			return Collections.emptyList();
		}
		
		if (artikelPrefix.size() > MAX_AUTOCOMPLETE) {
			return artikelPrefix.subList(0, MAX_AUTOCOMPLETE);
		}
		return artikelPrefix;
	}
//	@Transactional
//	public void loadLadenhueter() {
//		ladenhueter = as.ladenhueter(ANZAHL_LADENHUETER);
//	}
//	
	@Transactional
	public String selectArtikel() {
		if (session.getAttribute(SESSION_VERFUEGBARE_ARTIKEL) != null) {
			return JSF_SELECT_ARTIKEL;
		}
		
		final List<Artikel> alleArtikel = as.findVerfuegbareArtikel();
		session.setAttribute(SESSION_VERFUEGBARE_ARTIKEL, alleArtikel);
		return JSF_SELECT_ARTIKEL;
	}
}
