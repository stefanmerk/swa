package de.shop.artikelverwaltung.controller;

import static de.shop.util.Constants.JSF_INDEX;
import static de.shop.util.Constants.JSF_REDIRECT_SUFFIX;
import static de.shop.util.Messages.MessagesType.KUNDENVERWALTUNG;
import static javax.ejb.TransactionAttributeType.REQUIRED;
import static javax.ejb.TransactionAttributeType.SUPPORTS;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Event;
import javax.faces.context.Flash;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.OptimisticLockException;
import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolation;

import org.jboss.logging.Logger;
import org.richfaces.cdi.push.Push;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.artikelverwaltung.service.ArtikelService;
import de.shop.artikelverwaltung.service.BezeichnungExistsException;
import de.shop.artikelverwaltung.service.InvalidArtikelException;
import de.shop.auth.controller.AuthController;
import de.shop.util.ConcurrentDeletedException;
import de.shop.util.Log;
import de.shop.util.Messages;
import de.shop.util.Transactional;




/**
 * Dialogsteuerung fuer die ArtikelService
 */
@Named("ac")
@SessionScoped
@Stateful
@TransactionAttribute(SUPPORTS)//
@Log
public class ArtikelController implements Serializable {
	
	private static final long serialVersionUID = -7034886139702569282L;

	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	
	private static final String JSF_LIST_ARTIKEL = "/artikelverwaltung/listArtikel";
	private static final String FLASH_ARTIKEL = "artikel";
	//private static final int ANZAHL_LADENHUETER = 5;
	
	private static final String CLIENT_ID_UPDATE_BEZEICHNUNG = "updateArtikelForm:bezeichnung";
	private static final String MSG_KEY_UPDATE_ARTIKEL_DUPLIKAT = "updateArtikel.duplikat";
	private static final String MSG_KEY_UPDATE_ARTIKEL_CONCURRENT_UPDATE = "updateArtikel.concurrentUpdate";
	
	private static final String JSF_SELECT_ARTIKEL = "/artikelverwaltung/selectArtikel";
	private static final String SESSION_VERFUEGBARE_ARTIKEL = "verfuegbareArtikel";
	private static final String SESSION_ALLE_ARTIKEL = "findAllArtikel";
	private static final int MAX_AUTOCOMPLETE = 5;
	private String bezeichnung;
	private Artikel neuerArtikel;
	private Long artikelId;
	private Artikel artikel;
	private boolean geaendertArtikel;
	private double preis;
	

	@Inject
	private ArtikelService as;
	
	@Inject
	private Flash flash;
	
	@Inject
	private transient HttpSession session;

	@Inject
	private Messages messages;
	
	@Inject
	@Push(topic = "marketing")
	private transient Event<String> neuerArtikelEvent;
	
	@Inject
	@Push(topic = "updateArtikel")
	private transient Event<String> updateArtikelEvent;
	
	@PostConstruct
	private void postConstruct() {
		LOGGER.debugf("CDI-faehiges Bean %s wurde erzeugt", this);
	}
	

	@Inject
	private AuthController auth;

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
	
	public void setPreis(double preis) {
		this.preis = preis;
	}
	public double getPreis() {
		return preis;
	}

	public void setartikelId(Long artikelId) {
		
		this.artikelId = artikelId;
	}

	public Long getartikelId() {
		return artikelId;
	}
	
	public Artikel getNeuerArtikel() {
		return neuerArtikel;
	}
	
	public void setArtikel(Artikel artikel) {
		this.artikel = artikel;
	}
	
	public Artikel getArtikel() {
		return this.artikel;
	}

	
	@TransactionAttribute(REQUIRED)
	public String findArtikelByBezeichnung() {
		
		 artikel = as.findArtikelByBezeichnung(bezeichnung);
//		if(alleArtikel.size()!= 1)
//			LOGGER.info("MEHRERE OBJEKTE GESPEICHERT! bei findArtikelByBezeichnung");
//		artikel = alleArtikel.get(0);
//		alleArtikel.clear();
//		LOGGER.info("Der Artikel wird bearbeitet :" +artikel.toString());
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

	@Transactional
	public String selectArtikel() {
		if (session.getAttribute(SESSION_VERFUEGBARE_ARTIKEL) != null) {
			return JSF_SELECT_ARTIKEL;
		}
		
		final List<Artikel> alleArtikel = as.findVerfuegbareArtikel();
		session.setAttribute(SESSION_VERFUEGBARE_ARTIKEL, alleArtikel);
		return JSF_SELECT_ARTIKEL;
	}
	
	public void createEmptyArtikel() {
		if (neuerArtikel != null) {
			return;
		}

		neuerArtikel = new Artikel();
		
	}
	
	@TransactionAttribute(REQUIRED)
	public String createArtikel() {
		try {
			neuerArtikel = as.createArtikel(neuerArtikel);
		}
		catch (BezeichnungExistsException | InvalidArtikelException
			  | OptimisticLockException | ConcurrentDeletedException e) {
			final String outcome = updateErrorMsg(e, artikel.getClass());
			return outcome;
		}
		
		

		// Push-Event fuer Webbrowser
		neuerArtikelEvent.fire(String.valueOf(neuerArtikel.getAId()));
		
		// Aufbereitung fuer viewKunde.xhtml
		artikelId = neuerArtikel.getAId();
		artikel = neuerArtikel;
		neuerArtikel = null;  // zuruecksetzen
		
		
		return JSF_LIST_ARTIKEL + JSF_REDIRECT_SUFFIX;
	}
	
	
	public void geaendert(ValueChangeEvent e) {
		if (geaendertArtikel) {
			return;
		}
		
		if (e.getOldValue() == null) {
			if (e.getNewValue() != null) {
				geaendertArtikel = true;
			}
			return;
		}

		if (!e.getOldValue().equals(e.getNewValue())) {
			geaendertArtikel = true;				
		}
	}
	
	@TransactionAttribute(REQUIRED)
	public String update() {
		auth.preserveLogin();
		
		if (!geaendertArtikel || artikel == null) {
			return JSF_INDEX;
		}
		
	LOGGER.tracef("Aktualisierter artikel: %s", artikel);
	
	try {
		artikel = as.updateArtikel(artikel);
	}
	catch (BezeichnungExistsException | InvalidArtikelException
		  | OptimisticLockException e) {
		final String outcome = updateErrorMsg(e, artikel.getClass());
		return outcome;
	}
	
		// Push-Event fuer Webbrowser
		updateArtikelEvent.fire(String.valueOf(artikel.getAId()));
		
		// ValueChangeListener zuruecksetzen
		geaendertArtikel = false;
		
		// Aufbereitung fuer viewKunde.xhtml
		artikelId = artikel.getAId();
		artikel = null;//
		return JSF_LIST_ARTIKEL + JSF_REDIRECT_SUFFIX;
	}
	
	private String updateErrorMsg(RuntimeException e, Class<? extends Artikel> artikelClass) {
		final Class<? extends RuntimeException> exceptionClass = e.getClass();
		if (exceptionClass.equals(InvalidArtikelException.class)) {
		
			final InvalidArtikelException orig = (InvalidArtikelException) e;
			final Collection<ConstraintViolation<Artikel>> violations = orig.getViolations();
			messages.error(violations, CLIENT_ID_UPDATE_BEZEICHNUNG);
		}
		else if (exceptionClass.equals(BezeichnungExistsException.class)) {
			if (artikelClass.equals(Artikel.class)) {
				messages.error(KUNDENVERWALTUNG, MSG_KEY_UPDATE_ARTIKEL_DUPLIKAT, CLIENT_ID_UPDATE_BEZEICHNUNG);
			}
			
		}
		else if (exceptionClass.equals(OptimisticLockException.class)) {
			if (artikelClass.equals(Artikel.class)) {
				messages.error(KUNDENVERWALTUNG, MSG_KEY_UPDATE_ARTIKEL_CONCURRENT_UPDATE, null);
			}
			
		
		}
		return null;
	}
}
