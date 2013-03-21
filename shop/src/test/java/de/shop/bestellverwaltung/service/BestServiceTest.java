package de.shop.bestellverwaltung.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.Locale;

import javax.inject.Inject;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.artikelverwaltung.service.ArtikelService;
import de.shop.bestellverwaltung.domain.Bestellposition;
import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.kundenverwaltung.service.KundeService;
import de.shop.kundenverwaltung.service.KundeService.FetchType;
import de.shop.util.AbstractTest;

@RunWith(Arquillian.class)
public class BestServiceTest extends AbstractTest {
	
	@Inject
	private BestellService bs;
	
	@Inject
	private KundeService ks;
	
	@Inject
	private ArtikelService as;	
	
	private static final Long B_ID_VORHANDEN = Long.valueOf(401);
	private static final Long ID_VORHANDEN = Long.valueOf(101);
	private static final String ARTIKEL_BEZ = "Sneakers-G-UNIT";
	private static final String EMAIL_VORHANDEN = "max@hska.de";
	

	@Test
	public void findBestellungById() {
		
		final Long id = B_ID_VORHANDEN;
		
		final Bestellung bestellung = bs.findBestellungById(id);
		
		assertThat(bestellung.getBId(), is(id));
	}
	
	@Test
	public void findBestellungenByKunde() {
		
		final Long kundeid = ID_VORHANDEN;
		
		
	
		final Collection<Bestellung> bestList  = bs.findBestellungenByKunde(kundeid);
		
	
		assertThat(bestList.isEmpty(), is(false));
		for (Bestellung a : bestList) {
			assertThat(a.getKunde().getKId(), is(kundeid));
		}
	}
		
		@Test
		public void createBestellung()throws RollbackException, HeuristicMixedException, HeuristicRollbackException,
	    SystemException, NotSupportedException {
			
			
		
			final Collection<Bestellung> bestVorher = bs.findAllBestellung();
			final UserTransaction trans = getUserTransaction();
			trans.commit();
			
			Bestellung bestellung = new Bestellung();
			Bestellposition b = new Bestellposition();
			
			Artikel a = as.findArtikelByBezeichnung(ARTIKEL_BEZ);
			
			Kunde k = ks.findKundebyEmail(EMAIL_VORHANDEN, FetchType.NUR_KUNDE, Locale.GERMAN);
			b.setArtikel(a);
			bestellung.addBestellposition(b);
			
			
			trans.begin();
			bs.createBestellung(bestellung, k);
			trans.commit();
			
			trans.begin();
			final Collection<Bestellung> bestellungnachher = bs.findAllBestellung();
			trans.commit();
			
			assertThat(bestVorher.size() + 1, is(bestellungnachher.size()));
		
			
		}
		
	}
