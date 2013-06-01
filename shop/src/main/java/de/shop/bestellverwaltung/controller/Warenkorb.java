package de.shop.bestellverwaltung.controller;

import static java.util.concurrent.TimeUnit.MINUTES;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.logging.Logger;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.artikelverwaltung.service.ArtikelService;
import de.shop.bestellverwaltung.domain.Bestellposition;
import de.shop.util.Log;

@Named("wk")
@ConversationScoped
@Log
public class Warenkorb implements Serializable {
	private static final long serialVersionUID = -1981070683990640854L;

	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	private static final String JSF_VIEW_WARENKORB = "/bestellverwaltung/viewWarenkorb?init=true";
	private static final int TIMEOUT = 5;
	
	private final List<Bestellposition> positionen = new ArrayList<Bestellposition>();;
	private Long artikelId; 
	private String artikelbezeichnung;
	private double artikelpreis;
	private double gesamtpreis;
	
	// fuer selectArtikel.xhtml
	
	@Inject
	private transient Conversation conversation;
	
	@Inject
	private ArtikelService as;

	@PostConstruct
	private void postConstruct() {
		LOGGER.debugf("CDI-faehiges Bean %s wurde erzeugt", this);
	}
	
	@PreDestroy
	private void preDestroy() {
		LOGGER.debugf("CDI-faehiges Bean %s wird geloescht", this);
	}
	
	public List<Bestellposition> getPositionen() {
		return positionen;
	}
		
	public void setArtikelBezeichnung(String Artikelbez) {
		this.artikelbezeichnung = Artikelbez;
	}

	public String getArtikelbezeichnung() {
		return artikelbezeichnung;
	}
	public void setArtikelId(Long artikelId) {
		this.artikelId = artikelId;
	}

	public Long getArtikelId() {
		return artikelId;
	}

	public double getArtikelpreis() {
		return artikelpreis;
	}

	public void setArtikelpreis(double artikelpreis) {
		this.artikelpreis = artikelpreis;
	}

	public double getGesamtpreis() {
		gesamtpreis = 0;
		if (!(positionen == null) || !positionen.isEmpty()) {
			for (Bestellposition bp : positionen)
				gesamtpreis += bp.getAnzahl()*bp.getArtikel().getPreis();
		}
		return gesamtpreis;
	}

	@Override
	public String toString() {
		return "Warenkorb " + positionen;
	}
	
	/**
	 */
	public String add(Artikel artikel) {
		beginConversation();
		
		for (Bestellposition bp : positionen) {
			if (bp.getArtikel().equals(artikel)) {
				// bereits im Warenkorb
				final short vorhandeneAnzahl = bp.getAnzahl();
				bp.setAnzahl((short) (vorhandeneAnzahl + 1));
				return JSF_VIEW_WARENKORB;
			}
		}
		
		final Bestellposition neu = new Bestellposition(artikel);
		positionen.add(neu);
		return JSF_VIEW_WARENKORB;
	}
	
	/**
	 */
	public String add() {
		final Artikel artikel = as.findArtikelbyID(artikelId);
		if (artikel == null) {
			return null;
		}
		
		final String outcome = add(artikel);
		artikelId = null;
		return outcome;
	}
	
	/**
	 */
	public void beginConversation() {
		if (!conversation.isTransient()) {
			return;
		}
		conversation.begin();
		conversation.setTimeout(MINUTES.toMillis(TIMEOUT));
		LOGGER.trace("Conversation beginnt");
	}
	
	/**
	 */
	public void endConversation() {
		conversation.end();
		LOGGER.trace("Conversation beendet");
	}
	
	/**
	 */
	public void remove(Bestellposition bestellposition) {
		positionen.remove(bestellposition);
		if (positionen.isEmpty()) {
			endConversation();
		}
	}
}
