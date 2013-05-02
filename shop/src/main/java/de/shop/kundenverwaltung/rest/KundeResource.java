package de.shop.kundenverwaltung.rest;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.jboss.logging.Logger;

import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.bestellverwaltung.rest.UriHelperBestellung;
import de.shop.bestellverwaltung.service.BestellService;
import de.shop.kundenverwaltung.domain.Adresse;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.kundenverwaltung.service.KundeService;
import de.shop.kundenverwaltung.service.KundeService.FetchType;
import de.shop.util.Log;
import de.shop.util.NotFoundException;
import de.shop.util.RestLongWrapper;
import de.shop.util.RestStringWrapper;
import de.shop.util.Transactional;


@Path("/kunden")
@Produces(APPLICATION_JSON)
@Consumes
@RequestScoped
@Log
@Transactional
public class KundeResource {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	private static final String VERSION = "1.0";
	
	@Inject
	private KundeService ks;
	
	@Inject
	private BestellService bs;
	
	@Inject
	private UriHelperKunde uriHelperKunde;
	
	@Inject
	private UriHelperBestellung uriHelperBestellung;
	
	@PostConstruct
	private void postConstruct() {
		LOGGER.debugf("CDI-faehiges Bean %s wurde erzeugt", this);
	}
	
	@PreDestroy
	private void preDestroy() {
		LOGGER.debugf("CDI-faehiges Bean %s wird geloescht", this);
	}

	
	
	@GET
	@Produces(TEXT_PLAIN)
	@Path("version")
	
	public String getVersion() {
		return VERSION;
	}
	
	/**
	 * Mit der URL /kunden/{id} einen Kunden ermitteln
	 * @param id ID des Kunden
	 * @return Objekt mit Kundendaten, falls die ID vorhanden ist
	 */
	@GET
	@Path("{id:[1-9][0-9]*}")
	public Kunde findKundeById(@PathParam("id") Long id,
			                           @Context UriInfo uriInfo,
			                           @Context HttpHeaders headers) {
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		final Kunde kunde = ks.findKundebyID(id, FetchType.NUR_KUNDE, locale);
		if (kunde == null) {
			final String msg = "Kein Kunde gefunden mit der ID " + id;
			throw new NotFoundException(msg);
		}
	
		
		uriHelperKunde.updateUriKunde(kunde, uriInfo);
		
		return kunde;
	}
	

	/**
	 * Mit der URL /kunden werden alle Kunden ermittelt oder
	 * mit kundenverwaltung/kunden?nachname=... diejenigen mit einem bestimmten Nachnamen.
	 * @return Collection mit den gefundenen Kundendaten
	 */
	@GET
	public Collection<Kunde> findKundenByNachname(@QueryParam("nachname") @DefaultValue("") String nachname,
			                                              @Context UriInfo uriInfo,
			                                              @Context HttpHeaders headers) {
		Collection<Kunde> kunden = null;
		if ("".equals(nachname)) {
			kunden = ks.findAllKunde(FetchType.NUR_KUNDE, null);
			if (kunden.isEmpty()) {
				final String msg = "Keine Kunden vorhanden";
				throw new NotFoundException(msg);
			}
		}
		else {
			final List<Locale> locales = headers.getAcceptableLanguages();
			final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
			kunden = ks.findKundenByNachname(nachname, FetchType.NUR_KUNDE, locale);
			if (kunden.isEmpty()) {
				final String msg = "Kein Kunde gefunden mit Nachname " + nachname;
				throw new NotFoundException(msg);
			}
		}
	
		for (Kunde kunde : kunden) {
			uriHelperKunde.updateUriKunde(kunde, uriInfo);
		}
		
	
		return kunden;
	}
	
	
	@GET
	@Path("/prefix/id/{id:[1-9][0-9]*}")
	public Collection<RestLongWrapper> findIdsByPrefix(@PathParam("id") String idPrefix, @Context UriInfo uriInfo) {
		final Collection<Long> ids = ks.findIdsByPrefix(idPrefix);
		final Collection<RestLongWrapper> result = new ArrayList<>(ids.size());
		for (Long id : ids) {
			result.add(new RestLongWrapper(id));
		}
		return result;
	}
	
	@GET
	@Path("/prefix/nachname/{nachname}")
	public Collection<RestStringWrapper> findNachnamenByPrefix(@PathParam("nachname") String nachnamePrefix,
			                                                   @Context UriInfo uriInfo) {
		final Collection<String> nachnamen = ks.findNachnamenByPrefix(nachnamePrefix);
		final Collection<RestStringWrapper> wrapperColl = new ArrayList<>(nachnamen.size());
		for (String n : nachnamen) {
			wrapperColl.add(new RestStringWrapper(n));
		}
		return wrapperColl;
	}

	
	/**
	 * Mit der URL /kunden/{id}/bestellungen die Bestellungen zu eine Kunden ermitteln
	 * @param kundeId ID des Kunden
	 * @return Objekt mit Bestellungsdaten, falls die ID vorhanden ist
	 */
	@GET
	@Path("{id:[1-9][0-9]*}/bestellungen")
	public Collection<Bestellung> findBestellungenByKundeId(@PathParam("id") Long kundeId,  @Context UriInfo uriInfo) {
		final Collection<Bestellung> bestellungen = bs.findBestellungenByKunde(kundeId);
		if (bestellungen.isEmpty()) {
			final String msg = "Kein Kunde gefunden mit der ID " + kundeId;
			throw new NotFoundException(msg);
		}
		
		
		for (Bestellung bestellung : bestellungen) {
			uriHelperBestellung.updateUriBestellung(bestellung, uriInfo);
		}
		
		return bestellungen;
	}


	/**
	 * Mit der URL /kunden einen Privatkunden per POST anlegen.
	 * @param kunde neuer Kunde
	 * @return Response-Objekt mit URL des neuen Privatkunden
	 */
	@POST
	@Consumes({APPLICATION_JSON})
	@Produces
	public Response createKunde(Kunde kunde, @Context UriInfo uriInfo, @Context HttpHeaders headers) {
		final Adresse adresse = kunde.getAdresse();
		if (adresse != null) {
			adresse.setKunde(kunde);
		}

		kunde = ks.createKunde(kunde, adresse);
		LOGGER.debugf("Kunde: %s", kunde);
		
		final URI kundeUri = uriHelperKunde.getUriKunde(kunde, uriInfo);
		return Response.created(kundeUri).build();
	}
	
	
	/**
	 * Mit der URL /kunden einen Kunden per PUT aktualisieren
	 * @param kunde zu aktualisierende Daten des Kunden
	 */
	@PUT
	@Consumes(APPLICATION_JSON)
	@Produces
	@RolesAllowed({"Admin","Mitarbeiter"}) 
	public void updateKunde(Kunde kunde, @Context UriInfo uriInfo, @Context HttpHeaders headers) {
		// Vorhandenen Kunden ermitteln
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		final Kunde origKunde = ks.findKundebyID(kunde.getKId(), FetchType.NUR_KUNDE, locale);
		if (origKunde == null) {

			final String msg = "Kein Kunde gefunden mit der ID " + kunde.getKId();
			throw new NotFoundException(msg);
		}
		LOGGER.debugf("Kunde vorher: %s", origKunde);
	
		origKunde.setValues(kunde);
		LOGGER.debugf("Kunde nachher: %s", origKunde);
		
		kunde = ks.updateKunde(origKunde);
		if (kunde == null) {

			final String msg = "Kein Kunde gefunden mit der ID " + origKunde.getKId();
			throw new NotFoundException(msg);
		}
	}	
}
