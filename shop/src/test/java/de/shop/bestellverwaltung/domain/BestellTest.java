package de.shop.bestellverwaltung.domain;



import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.util.AbstractDomainTest;

@RunWith(Arquillian.class)

public class BestellTest extends AbstractDomainTest {
	
	private static final Long ID_VORHANDEN = Long.valueOf(101);
	private static final String EMAIL_VORHANDEN = "max@hska.de"; 
	private static final String ARTIKEL_BEZEICHNUNG = "Sneakers-G-UNIT";
	
	

	@Test
	public void validate() {
		assertThat(true, is(true));
	}
	
	@Test
	public void findKundeByEmail() {
	
		final String email = EMAIL_VORHANDEN;
		Kunde kunde = getEntityManager().createNamedQuery(Kunde.FIND_KUNDE_BY_EMAIL, Kunde.class)
			.setParameter(Kunde.PARAM_KUNDE_EMAIL, email)
			.getSingleResult();
	assertThat(kunde.getEmail(), is(email));
	
	}

	
	@Test
	public void findBestellungenByKunde() {
		// Given
		final Long kundeid = ID_VORHANDEN;
		Kunde kunde = getEntityManager().find(Kunde.class, kundeid);
		assertThat(kunde.getNachname(), is("Schmidt"));
		// When
		final List<Bestellung> bestList  = getEntityManager().createNamedQuery(Bestellung.FIND_BESTELLUNGEN_BY_KUNDE,
                                                                        Bestellung.class)
                                                      .setParameter(Bestellung.PARAM_KUNDEID, kundeid)
				                                      .getResultList();
		
		// Then
		assertThat(bestList.isEmpty(), is(false));
		for (Bestellung a : bestList) {
			assertThat(a.getKunde().getKId(), is(kundeid));
		}
	}

	
		@Test
		public void createBestellung() {
			
			final Long kundeid = ID_VORHANDEN;
			Kunde kunde = getEntityManager().find(Kunde.class, kundeid);
			assertThat(kunde.getNachname(), is("Schmidt"));
			System.out.println("Kunde gefunden mit Name" + kunde.getNachname());
			// Given
		final List<Bestellung> bestList = getEntityManager().createNamedQuery(Bestellung.FIND_BESTELLUNGEN_BY_KUNDE,
                    Bestellung.class)
                   .setParameter(Bestellung.PARAM_KUNDEID, kundeid)
                   	.getResultList();

			assertThat(bestList.isEmpty(), is(false));
	
			
			Bestellung bestellung = new Bestellung();
			Bestellposition b = new Bestellposition();
	
			
			Artikel artikel = new Artikel();
			 artikel = getEntityManager().createNamedQuery(Artikel.FIND_ARTIKEL_BY_BEZ, Artikel.class)
					.setParameter(Artikel.PARAM_BEZEICHNUNG, ARTIKEL_BEZEICHNUNG)
					.getSingleResult();
		
			 assertThat(artikel.getBezeichnung(), is(ARTIKEL_BEZEICHNUNG));
			 
			b.setArtikel(artikel);
			
			bestellung.addBestellposition(b);
			assertThat(b.getArtikel(), is(artikel));

			bestellung.setKunde(kunde);
			
					
			try {
				getEntityManager().persist(bestellung);        
			}
			catch (ConstraintViolationException e) {
				
				final Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
				for (ConstraintViolation<?> v : violations) {
					System.err.println("!!! FEHLERMELDUNG>>> " + v.getMessage());
					System.err.println("!!! ATTRIBUT>>> " + v.getPropertyPath());
					System.err.println("!!! ATTRIBUTWERT>>> " + v.getInvalidValue());
				}
				
				throw new RuntimeException(e);
			}

	  final List<Bestellung> bestellungn = getEntityManager().createNamedQuery(Bestellung.FIND_BESTELLUNGEN_BY_KUNDE,
	        		 																Bestellung.class)
                                                       .setParameter(Bestellung.PARAM_KUNDEID,
                                                    		   ID_VORHANDEN)
			                                             .getResultList();
	      
			assertThat(bestellungn.size(), is(bestList.size() + 1));
			bestellung = (Bestellung) bestellungn.get(0);
			assertThat(bestellung.getBId()  > 0, is(true));
			assertThat(bestellung.getKunde().getKId(), is(ID_VORHANDEN));

		
	}

}
