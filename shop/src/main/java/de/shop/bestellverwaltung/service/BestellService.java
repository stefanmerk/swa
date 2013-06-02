package de.shop.bestellverwaltung.service;

import static de.shop.util.Constants.KEINE_ID;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.logging.Logger;

import de.shop.bestellverwaltung.domain.Bestellposition;
import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.util.Log;
import de.shop.util.ValidatorProvider;

@Log
public class BestellService implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8250818844409307651L;
private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	@Inject
	private ValidatorProvider validationService;
	
	@PersistenceContext
	private transient EntityManager em;
	
	@PostConstruct
	private void postConstruct() {
		LOGGER.debugf("CDI-faehiges Bean {0} wurde erzeugt", this);
	}
	
	public Bestellung findBestellungById(Long id) {
		
		final Bestellung bestellung = em.createNamedQuery(Bestellung.FIND_BESTELLUNG_ID, Bestellung.class)
                								.setParameter(Bestellung.PARAM_B_ID, id)
                								.getSingleResult();
		
		return bestellung;
	}
	
	public Collection<Bestellung> findBestellungenByKunde(Long kid) {
		final Collection<Bestellung> bestList = em.createNamedQuery(Bestellung.FIND_BESTELLUNGEN_BY_KUNDE,
                									Bestellung.class)
                									.setParameter(Bestellung.PARAM_KUNDEID, kid)
                									.getResultList();

		return bestList;
		
	}
	
	public List<Bestellung> findAllBestellung() {
		final List<Bestellung> result = em.createNamedQuery(Bestellung.FIND_BESTELLUNG, Bestellung.class)
                .getResultList();
		return result;

	}
	
	
	
	public Bestellung createBestellung(Bestellung bestellung, Kunde kunde)	{
		bestellung.setBId(KEINE_ID);

		bestellung.setKunde(kunde);
		
		em.persist(bestellung);

		return bestellung;
	}

	public Bestellung updateBestellung(Bestellung best) {
		
		if (best == null)
			return null;
		
		em.detach(best);
		em.merge(best);
		
		return best;
	}	
}