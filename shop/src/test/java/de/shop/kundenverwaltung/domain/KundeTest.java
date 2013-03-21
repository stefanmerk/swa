package de.shop.kundenverwaltung.domain;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.shop.util.AbstractDomainTest;

@RunWith(Arquillian.class)
public class KundeTest extends AbstractDomainTest {

	private static final Long ID_VORHANDEN = Long.valueOf(101);
	private static final String EMAIL_VORHANDEN = "max@hska.de";
	
	private static final String NACHNAME_NEU = "Test";
	private static final String VORNAME_NEU = "Theo";
	private static final String EMAIL_NEU = "theo@test.de";
	private static final String GESCHLECHT_NEU = "m";


	
	private static final String PLZ_NEU = "11111";
	private static final String ORT_NEU = "Testort";
	private static final String STRASSE_NEU = "Testweg";
	private static final String HAUSNR_NEU = "1";
	

	
	
	
	@Test
	public void findKundeByIdVorhanden() {
		
		final Long id = ID_VORHANDEN;
				
		
	
		final Kunde kunde = getEntityManager().find(Kunde.class, id);
		
		
		assertThat(kunde.getKId(), is(id));

	}
	
	@Test
	public void validate() {
		assertThat(true, is(true));
	}

	@Test
	public void findKundeByEmail() {
		
		final String email = EMAIL_VORHANDEN;
		
		
		final Kunde kunde = getEntityManager().createNamedQuery(Kunde.FIND_KUNDE_BY_EMAIL,
                                                                        Kunde.class)
                                                      .setParameter(Kunde.PARAM_KUNDE_EMAIL, email)
				                                      .getSingleResult();
		
		assertThat(kunde.getEmail(), is(email));
	}
	
	@Test
	public void  createKunde() {
		
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
		
		kunde.setAdresse(adresse);
		
		

		
		try {
			getEntityManager().persist(kunde);         
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
	
		
		final List<Kunde> kunden = getEntityManager().createNamedQuery(Kunde.FIND_KUNDE_BY_EMAIL,
                                                                               Kunde.class)
                                                             .setParameter(Kunde.PARAM_KUNDE_EMAIL,
                                                            		       EMAIL_NEU)
				                                             .getResultList();
		
	
		
		assertThat(kunden.size(), is(1));
		assertThat(kunde.getKId() > 0, is(true));
		assertThat(kunde.getEmail(), is(EMAIL_NEU));
	}
	
	
}