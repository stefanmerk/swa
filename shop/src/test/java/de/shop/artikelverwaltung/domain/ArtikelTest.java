package de.shop.artikelverwaltung.domain;


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
public class ArtikelTest extends AbstractDomainTest {
	
	private static final String BEZEICHNUNG_VORHANDEN = "Hose-Diesel";
	private static final double MIN_PREIS = 101;
	 
	private static final String BEZEICHNUNG_NEU = "Bluse-Esprit";
	private static final double PREIS_NEU = 100;	 
	private static final char VERFUEGBARKEIT_NEU = 'y';
	 
	 
	 

	@Test
	public void validate() {
		assertThat(true, is(true));
	}

	@Test
	public void findartikelByBez() {
		
		final String bezeichnung = BEZEICHNUNG_VORHANDEN;
		
		
		
		final Artikel artikel = getEntityManager().createNamedQuery(Artikel.FIND_ARTIKEL_BY_BEZ,
                                                                        Artikel.class)
                                                      .setParameter(Artikel.PARAM_BEZEICHNUNG, bezeichnung)
				                                      .getSingleResult();
		
		assertThat(artikel.getBezeichnung(), is(bezeichnung));
	}
	
	@Test
	public void findArtikelbyMinPreis() {
		
		final double preis = MIN_PREIS;
		
		
		final List<Artikel> artikelList = getEntityManager().createNamedQuery(Artikel.FIND_ARTIKEL_MIN_PREIS,
                                                                        Artikel.class)
                                                      .setParameter(Artikel.PARAM_PREIS, preis)
				                                      .getResultList();
		
		assertThat(artikelList.isEmpty(), is(false));
		for (Artikel a : artikelList) {
			assertThat(a.getPreis() < preis, is(true));
		}
	}
	
		
		@Test
		public void createArtikel() {
			
			Artikel artikel = new Artikel();
			artikel.setBezeichnung(BEZEICHNUNG_NEU);
			artikel.setPreis(PREIS_NEU);
			artikel.setVerfuegbarkeit(VERFUEGBARKEIT_NEU);
			
			
			
			try {
				getEntityManager().persist(artikel);        
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
			
		
	         final List<Artikel> artikeln = getEntityManager().createNamedQuery(Artikel.FIND_ARTIKEL_BY_BEZ,
                                                                         Artikel.class)
                                                       .setParameter(Artikel.PARAM_BEZEICHNUNG,
                                                      		       BEZEICHNUNG_NEU)
			                                             .getResultList();
	
			assertThat(artikeln.size(), is(1));
			artikel = (Artikel) artikeln.get(0);
			assertThat(artikel.getAId()  > 0, is(true));
			assertThat(artikel.getBezeichnung(), is(BEZEICHNUNG_NEU));
		}
	}
	
	
	

	
	


