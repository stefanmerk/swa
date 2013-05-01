package de.shop.artikelverwaltung.service;

import static de.shop.util.Constants.KEINE_ID;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Predicate;

import org.jboss.logging.Logger;

import com.google.common.base.Strings;



import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.util.Log;
import de.shop.util.ValidatorProvider;

@Log
public class ArtikelService implements Serializable {
	private static final long serialVersionUID = 2929027248430844352L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	@Inject
	private ValidatorProvider validationService;
	
	@PersistenceContext
	private transient EntityManager em;
	
	@PostConstruct
	private void postConstruct() {
		LOGGER.debugf("CDI-faehiges Bean %s wurde erzeugt", this);
		
	
		
	}
	
	public Artikel findArtikelbyID(Long id) {
		final Artikel artikel = em.createNamedQuery(Artikel.FIND_ARTIKEL_ID, Artikel.class)
                								.setParameter(Artikel.PARAM_A_ID, id)
                								.getSingleResult();
		
		return artikel;

	}
	
	public List<Artikel> findVerfuegbareArtikel() {
		final List<Artikel> result = em.createNamedQuery(Artikel.FIND_VERFUEGBARE_ARTIKEL, Artikel.class)
				                       .getResultList();
		return result;
	}
	
	
	
	public Artikel findArtikelByBezeichnung(String bezeichnung) {
		if (Strings.isNullOrEmpty(bezeichnung)) {
			System.err.println("artikel nicht vorhanden");
		}
		final Artikel artikel = em.createNamedQuery(Artikel.FIND_ARTIKEL_BY_BEZ, Artikel.class)
				                        .setParameter(Artikel.PARAM_BEZEICHNUNG, bezeichnung)
				                        .getSingleResult();
		return artikel;
	}
	
	
	public Artikel updateArtikel(Artikel neuerArtikel) {
		if (neuerArtikel == null)
			return null;
		
		em.detach(neuerArtikel);
		em.merge(neuerArtikel);
		return neuerArtikel;
	}

	
	public List<Artikel> findArtikelByIds(List<Long> ids) {
		if (ids == null || ids.isEmpty()) {
			return Collections.emptyList();
		}
		
		/**
		 * SELECT a
		 * FROM   Artikel a
		 * WHERE  a.id = ? OR a.id = ? OR ...
		 */
		final CriteriaBuilder builder = em.getCriteriaBuilder();
		final CriteriaQuery<Artikel> criteriaQuery = builder.createQuery(Artikel.class);
		final Root<Artikel> a = criteriaQuery.from(Artikel.class);
		
		final Path<Long> idPath = a.get("aid");
		//final Path<String> idPath = a.get(Artikel_.id);   // Metamodel-Klassen funktionieren nicht mit Eclipse
		
		Predicate pred = null;
		if (ids.size() == 1) {
			// Genau 1 id: kein OR notwendig
			pred = builder.equal(idPath, ids.get(0));
		}
		else {
			// Mind. 2x id, durch OR verknuepft
			final Predicate[] equals = new Predicate[ids.size()];
			int i = 0;
			for (Long id : ids) {
				equals[i++] = builder.equal(idPath, id);
			}
			
			pred = builder.or(equals);
		}
		
		criteriaQuery.where(pred);
		
		final TypedQuery<Artikel> query = em.createQuery(criteriaQuery);

		final List<Artikel> artikel = query.getResultList();
		return artikel;
	}
	public List<Artikel> findAllArtikel() {
		final List<Artikel> result = em.createNamedQuery(Artikel.FIND_ARTIKEL, Artikel.class)
                .getResultList();
		return result;

	}

	public Artikel createArtikel(Artikel artikel) {
		artikel.setAId(KEINE_ID);
		em.persist(artikel);
		return artikel;
	}
	
	public enum OrderType {
		KEINE,
		ID
	}
	
	public enum FetchType {
		NUR_Artikel
	}
	
}
