package de.shop.kundenverwaltung.service;

import static de.shop.util.Constants.KEINE_ID;
import static javax.persistence.PersistenceContextType.EXTENDED;
import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.groups.Default;

import org.jboss.logging.Logger;

import de.shop.kundenverwaltung.domain.Adresse;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.util.File;
import de.shop.util.FileHelper;
import de.shop.util.FileHelper.MimeType;
import de.shop.util.Log;
import de.shop.util.NoMimeTypeException;
import de.shop.util.Transactional;
import de.shop.util.ValidatorProvider;

@Log
@Transactional

//@RolesAllowed({"admin","mitarbeiter","kunde"})
public class KundeService implements Serializable {

	private static final long serialVersionUID = 4360325837484294309L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	@Inject
	private ValidatorProvider validationService;
	
	@Inject
	private FileHelper fileHelper;
	
	@PersistenceContext(type = EXTENDED)
	private transient EntityManager em;
	
	@PostConstruct
	private void postConstruct() {
		LOGGER.debugf("CDI-faehiges Bean %s wurde erzeugt", this);
		}

	
	public enum FetchType {
		NUR_KUNDE,
		MIT_BESTELLUNGEN,
		MIT_Adresse
	}
	
	public Kunde findKundebyEmail(String email, FetchType fetch, Locale local) {
	final Kunde kunde = em.createNamedQuery(Kunde.FIND_KUNDE_BY_EMAIL, Kunde.class)
                								.setParameter(Kunde.PARAM_KUNDE_EMAIL, email)
                								.getSingleResult();
		
		return kunde;

	}
	public Kunde testtest(Long id) {
		final Kunde k = em.createNamedQuery(Kunde.FIND_KUNDE_BY_ID_FETCH_BESTELLUNGEN, Kunde.class)
						.setParameter(Kunde.PARAM_KUNDE_ID, id).getSingleResult();
		
		return k;
		
	}
	
	public Kunde findKundebyID(Long id, FetchType fetch , Locale local) {
		//final Kunde kunde = em.createNamedQuery(Kunde.FIND_KUNDE_ID, Kunde.class)
                								//.setParameter(Kunde.PARAM_K_ID, id)
                								//.getSingleResult();
		
		//return kunde;
		Kunde kunde;
		switch (fetch) {
			case NUR_KUNDE:
				kunde = em.createNamedQuery(Kunde.FIND_KUNDE_ID, Kunde.class)
						   .setParameter(Kunde.PARAM_K_ID, id)
						   .getSingleResult();
				break;
			
			case MIT_BESTELLUNGEN:
				kunde = em.createNamedQuery(Kunde.FIND_KUNDE_BY_ID_FETCH_BESTELLUNGEN, Kunde.class)
						   .setParameter(Kunde.PARAM_K_ID, id)
						   .getSingleResult();
				break;

			default:
				kunde = em.createNamedQuery(Kunde.FIND_KUNDE_ID, Kunde.class)
						   .setParameter(Kunde.PARAM_K_ID, id)
						   .getSingleResult();
				break;
		}

		return kunde;
	}        								
		
		
		
		
		

	
	public List<Long> findIdsByPrefix(String idPrefix) {
		final List<Long> ids = em.createNamedQuery(Kunde.FIND_IDS_BY_PREFIX, Long.class)
				                 .setParameter(Kunde.PARAM_KUNDE_ID_PREFIX, idPrefix + '%')
				                 .getResultList();
		return ids;
	}
	
	public List<Kunde> findKundenByIdPrefix(String id) {
		final List<Kunde> kunden = em.createNamedQuery(Kunde.FIND_KUNDEN_BY_ID_PREFIX, Kunde.class)
				                 .setParameter(Kunde.PARAM_KUNDE_ID_PREFIX, id + '%')
				                 .getResultList();
		return kunden;
	}

	public List<String> findNachnamenByPrefix(String nachnamePrefix) {
		final List<String> nachnamen = em.createNamedQuery(Kunde.FIND_NACHNAMEN_BY_PREFIX,
				                                           String.class)
				                         .setParameter(Kunde.PARAM_KUNDE_NACHNAME_PREFIX, nachnamePrefix + '%')
				                         .getResultList();
		return nachnamen;
	}
		public Kunde findKundeByUserName(String id) {
			Long kid = Long.valueOf(id);
		Kunde kunde = em.createNamedQuery(Kunde.FIND_KUNDE_BY_USERNAME, Kunde.class)
					.setParameter(Kunde.PARAM_KUNDE_USERNAME, kid)
					.getSingleResult();
		return kunde;
		}
	
	
	
	public List<Kunde> findAllKunde(FetchType fetch, Locale local) {
		final List<Kunde> result = em.createNamedQuery(Kunde.FIND_KUNDEN, Kunde.class)
                .getResultList();
		return result;

	}
	
	public List<Kunde> findKundenByNachname(String nachname, FetchType fetch, Locale locale) {
		validateNachname(nachname, locale);
		
		List<Kunde> kunden;
		switch (fetch) {
			case NUR_KUNDE:
				kunden = em.createNamedQuery(Kunde.FIND_KUNDEN_BY_NACHNAME, Kunde.class)
						   .setParameter(Kunde.PARAM_KUNDE_NACHNAME, nachname)
						   .getResultList();
				break;
			
			case MIT_BESTELLUNGEN:
				kunden = em.createNamedQuery(Kunde.FIND_KUNDEN_BY_NACHNAME_FETCH_BESTELLUNGEN, Kunde.class)
						   .setParameter(Kunde.PARAM_KUNDE_NACHNAME, nachname)
						   .getResultList();
				break;

			default:
				kunden = em.createNamedQuery(Kunde.FIND_KUNDEN_BY_NACHNAME, Kunde.class)
						   .setParameter(Kunde.PARAM_KUNDE_NACHNAME, nachname)
						   .getResultList();
				break;
		}

		return kunden;
	}
	private void validateNachname(String nachname, Locale locale) {
		final Validator validator = validationService.getValidator(locale);
		final Set<ConstraintViolation<Kunde>> violations = validator.validateValue(Kunde.class,
				                                                                           "nachname",
				                                                                           nachname,
				                                                                           Default.class);
		if (!violations.isEmpty())
			throw new InvalidNachnameException(nachname, violations);
	}
	public Kunde createKunde(Kunde kunde, Adresse adresse) {
		
		
		kunde.setKId(KEINE_ID);
		kunde.setAdresse(adresse);
		
		
		em.persist(kunde);		
		return kunde;
		
		
	}
	public void setFile(Long kundeId, byte[] bytes, Locale locale) {
		final Kunde kunde = findKundebyID(kundeId, FetchType.NUR_KUNDE, locale);
		if (kunde == null) {
			return;
		}
		final MimeType mimeType = fileHelper.getMimeType(bytes);
		setFile(kunde, bytes, mimeType);
	}
	
	/**
	 * Mit MIME-Type fuer Upload bei Webseiten
	 */
	public void setFile(Kunde kunde, byte[] bytes, String mimeTypeStr) {
		final MimeType mimeType = MimeType.get(mimeTypeStr);
		setFile(kunde, bytes, mimeType);
	}
	
	private void setFile(Kunde kunde, byte[] bytes, MimeType mimeType) {
		if (mimeType == null) {
			throw new NoMimeTypeException();
		}
		
		final String filename = fileHelper.getFilename(kunde.getClass(), kunde.getKId(), mimeType);
		
		// Gibt es noch kein (Multimedia-) File
		File file = kunde.getFile();
		if (file == null) {
			file = new File(bytes, filename, mimeType);
			kunde.setFile(file);
			em.persist(file);
		}
		else {
			file.set(bytes, filename, mimeType);
			em.merge(file);
		}
	}
	
	public Kunde updateKunde(Kunde kunde) {
		
		if (kunde == null)
			return null;
		em.detach(kunde);
		//Kunde eKunde = findKundenByNachname(nachname, fetch, locale)
		em.merge(kunde);
		return kunde;
	}	
}