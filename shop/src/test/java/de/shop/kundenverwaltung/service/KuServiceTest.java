package de.shop.kundenverwaltung.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import de.shop.artikelverwaltung.domain.Artikel;
//import de.shop.bestellverwaltung.domain.Bestellposition;
import de.shop.bestellverwaltung.domain.Bestellung;
//import de.shop.kundenverwaltung.service.KundeService;
import de.shop.kundenverwaltung.service.KundeService.FetchType;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.shop.kundenverwaltung.domain.Adresse;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.util.AbstractTest;
@RunWith(Arquillian.class)
public class KuServiceTest extends AbstractTest {

	@Inject
	private KundeService ks;
	
	private static final Long ID_VORHANDEN = Long.valueOf(101);
	private static final String EMAIL_VORHANDEN = "max@hska.de";
	
	private static final String NACHNAME_NEU = "Service";
	private static final String VORNAME_NEU = "Test";
	private static final String EMAIL_NEU = "Service@test.de";
	private static final String GESCHLECHT_NEU = "w";


	
	private static final String PLZ_NEU = "12345";
	private static final String ORT_NEU = "Serviceort";
	private static final String STRASSE_NEU = "Testweg";
	private static final String HAUSNR_NEU = "5";
	
	private final Locale local = Locale.getDefault();

	
	@Test
	public void findKundeByEmail() {
		// Given
		final String email = EMAIL_VORHANDEN;
		

		// When
		final Kunde kunde = ks.findKundebyEmail(email, FetchType.NUR_KUNDE, local);
		// Then
		assertThat(kunde.getEmail(), is(email));
	}
	
	@Test
	public void Juliawillsnichtglauben()
	{
		final Kunde kunde = ks.testtest(ID_VORHANDEN);
		List<Bestellung> b = kunde.getBestellungen();
		Artikel  a = b.get(0).getBestellpositionen().get(0).getArtikel();
		
		assertThat((a!=null), is(true));
		
	}
	@Test
	public void findKundeById() {
		// Given
		final Long id = ID_VORHANDEN;
		
		
		// When
		final Kunde kunde = ks.findKundebyID(id, FetchType.NUR_KUNDE, local);
		// Then
		assertThat(kunde.getKId(), is(id));
	}
	

	@Test
	public void createKunde()throws RollbackException, HeuristicMixedException, HeuristicRollbackException,
    SystemException, NotSupportedException  {

		final Collection<Kunde> kundeVorher = ks.findAllKunde(FetchType.MIT_Adresse, local);
		final UserTransaction trans = getUserTransaction();
		trans.commit();
		
		Kunde kunde = new Kunde();
		kunde.setNachname(NACHNAME_NEU);
		kunde.setVorname(VORNAME_NEU);
		kunde.setEmail(EMAIL_NEU);
	    kunde.setGeschlecht(GESCHLECHT_NEU);
		
	    
		Adresse adresse = new Adresse();
		adresse.setPlz(PLZ_NEU);
		adresse.setOrt(ORT_NEU);
		adresse.setStrasse(STRASSE_NEU);
		adresse.setHausnr(HAUSNR_NEU);
		adresse.addKunde(kunde);
		
		trans.begin();
		Kunde neuerKunde = ks.createKunde(kunde, adresse);
		trans.commit();
		
		trans.begin();
		final Collection<Kunde> kundeNachher = ks.findAllKunde(FetchType.NUR_KUNDE, local);
		trans.commit();
		
		assertThat(kundeVorher.size() + 1, is(kundeNachher.size()));
		
		trans.begin();
		
		neuerKunde = ks.findKundebyEmail(neuerKunde.getEmail(), FetchType.NUR_KUNDE, null);
		trans.commit();
		
		assertThat(neuerKunde.getEmail(), is(EMAIL_NEU));
		
		
	}
	
	@Test
	public void neuerKundename() throws RollbackException, HeuristicMixedException, HeuristicRollbackException,
	                                        SystemException, NotSupportedException {
		// Given
	

		// When
		Kunde kunde = ks.findKundebyID(ID_VORHANDEN, FetchType.NUR_KUNDE, local);
		
		final UserTransaction trans = getUserTransaction();
		trans.commit();
		
		final String neuerNachname = "Mueller";
		kunde.setNachname(neuerNachname);
	
		trans.begin();
		kunde = ks.updateKunde(kunde);
		trans.commit();
		
		// Then
		assertThat(kunde.getNachname(), is(neuerNachname));
		trans.begin();
		
		kunde = ks.findKundebyID(ID_VORHANDEN, FetchType.NUR_KUNDE, local);
		
		trans.commit();
		assertThat(kunde.getKId(), is(ID_VORHANDEN));
	}
}
