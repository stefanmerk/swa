package de.shop.kundenverwaltung.rest;

import java.lang.invoke.MethodHandles;
import java.net.URI;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.jboss.logging.Logger;

import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.util.Log;


@ApplicationScoped
@Log
public class UriHelperKunde {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	
	
	public URI getUriKunde(Kunde kunde, UriInfo uriInfo) {
		final UriBuilder ub = uriInfo.getBaseUriBuilder()
		                             .path(KundeResource.class)
		                             .path(KundeResource.class, "findKundeById");
		final URI kundeUri = ub.build(kunde.getKId());
		return kundeUri;
	}
	
	
	public void updateUriKunde(Kunde kunde, UriInfo uriInfo) {
		// URI fuer Bestellungen setzen
		final UriBuilder uri = uriInfo.getBaseUriBuilder()
                         .path(KundeResource.class)
                         .path(KundeResource.class, "findBestellungenByKundeId");
		final URI kundeUri = uri.build(kunde.getKId());
                         
		kunde.setBestellungenUri(kundeUri);

		final URI  fileUri = getUriDownload(kunde.getKId(), uriInfo);
		kunde.setFileUri(fileUri);
		
		LOGGER.trace(kunde);
	}
	
	public URI getUriDownload(Long kundeId, UriInfo uriInfo) {
		final URI uri = uriInfo.getBaseUriBuilder()
		                       .path(KundeResource.class)
		                       .path(KundeResource.class, "download")
		                       .build(kundeId);
		return uri;
	}
}
