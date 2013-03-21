package de.shop.artikelverwaltung.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Collection;

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
import de.shop.util.AbstractTest;

@RunWith(Arquillian.class)
public class ArtikelServiceTest extends AbstractTest {
	
	@Inject 
	private ArtikelService as;
	
	
	
	private static final String BEZEICHNUNG_VORHANDEN = "Hose-Diesel";
	private static final String BEZEICHNUNG_NEU = "Bluse-Esprit";
	private static final double PREIS_NEU = 100;	 
	private static final char VERFUEGBARKEIT_NEU = 'y';
	private static final Long ID_VORHANDEN = Long.valueOf(301);
	private static final Long ID_VOR = Long.valueOf(300);
	 
	 
		@Test
		public void findKundeById() {
			
			final Long id = ID_VORHANDEN;
			
			final Artikel artikle = as.findArtikelbyID(id);
			
			assertThat(artikle.getAId(), is(id));
		}
	
	@Test
	public void findartikelByBez() {
		
		String bezeichnung = BEZEICHNUNG_VORHANDEN;
		
		Artikel art = as.findArtikelByBezeichnung(bezeichnung);
		
		assertThat(art.getBezeichnung(), is(bezeichnung));

	}
	
	@Test
	public void findVerfuegbareArt() {
		
		final Collection<Artikel> a = as.findVerfuegbareArtikel();
		
		assertThat(a.isEmpty(), is(false));
		
	
	}

	@Test
	public void createArtikel()throws RollbackException, HeuristicMixedException, HeuristicRollbackException,
    SystemException, NotSupportedException  {
		
		final Collection<Artikel> artikelVorher = as.findAllArtikel();
		final UserTransaction trans = getUserTransaction();
		trans.commit();
		
		
		Artikel artikel = new Artikel();
		artikel.setBezeichnung(BEZEICHNUNG_NEU);
		artikel.setPreis(PREIS_NEU);
		artikel.setVerfuegbarkeit(VERFUEGBARKEIT_NEU);
		
		trans.begin();
		Artikel neuerArtikel = as.createArtikel(artikel);
		trans.commit();
		
		trans.begin();
		final Collection<Artikel> artikelNachher = as.findAllArtikel();
		trans.commit();
		
		assertThat(artikelVorher.size() + 1, is(artikelNachher.size()));
		
		trans.begin();
		neuerArtikel = as.findArtikelByBezeichnung(neuerArtikel.getBezeichnung());
		trans.commit();
		
		assertThat(neuerArtikel.getBezeichnung(), is(BEZEICHNUNG_NEU));
	}

	@Test
	public void neuerArtikelname() throws RollbackException, HeuristicMixedException, HeuristicRollbackException,
	                                        SystemException, NotSupportedException {
		
		final Long id = ID_VOR;

		
		Artikel artikel = as.findArtikelbyID(id);
		
		final UserTransaction trans = getUserTransaction();
		trans.commit();
		
		final String neueBezeichnung = "BENZIN-HOSE";
		artikel.setBezeichnung(neueBezeichnung);
	
		trans.begin();
		artikel = as.updateArtikel(artikel);
		trans.commit();
		
		assertThat(artikel.getBezeichnung(), is(neueBezeichnung));

	}
	
	
	
	

		

}
