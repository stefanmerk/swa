package de.shop.kundenverwaltung.controller;

import static de.shop.util.Constants.JSF_INDEX;
import static de.shop.util.Constants.JSF_REDIRECT_SUFFIX;
import static de.shop.util.Messages.MessagesType.KUNDENVERWALTUNG;
import static javax.ejb.TransactionAttributeType.REQUIRED;
import static javax.ejb.TransactionAttributeType.SUPPORTS;
import static javax.persistence.PersistenceContextType.EXTENDED;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Event;
import javax.faces.context.Flash;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolation;
import javax.xml.bind.DatatypeConverter;

import org.jboss.logging.Logger;
import org.richfaces.cdi.push.Push;
import org.richfaces.event.FileUploadEvent;
import org.richfaces.model.UploadedFile;

import de.shop.auth.controller.AuthController;
import de.shop.kundenverwaltung.domain.Adresse;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.kundenverwaltung.service.EmailExistsException;
import de.shop.kundenverwaltung.service.InvalidKundeException;
import de.shop.kundenverwaltung.service.KundeService;
import de.shop.kundenverwaltung.service.KundeService.FetchType;
import de.shop.util.AbstractShopException;
import de.shop.util.Client;
import de.shop.util.ConcurrentDeletedException;
import de.shop.util.File;
import de.shop.util.FileHelper;
import de.shop.util.Log;
import de.shop.util.Messages;

/**
 * Dialogsteuerung fuer die Kundenverwaltung
 */
@Named("kc")

@Log
@Stateful
@SessionScoped
@TransactionAttribute(SUPPORTS)//
public class KundeController implements Serializable {
	private static final long serialVersionUID = -8817180909526894740L;
	
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	
//	private static final String FLASH_KUNDE = "kunde";
//
//	private static final String MSG_KEY_SELECT_DELETE_KUNDE_BESTELLUNG = "listKunden.deleteKundeBestellung";
//	
//	private static final String CLIENT_ID_DELETE_BUTTON = "form:deleteButton";
//	private static final String MSG_KEY_DELETE_KUNDE_BESTELLUNG = "viewKunde.deleteKundeBestellung";

	private static final int MAX_AUTOCOMPLETE = 5;
	private static final String JSF_KUNDENVERWALTUNG = "/kundenverwaltung/";
	private static final String JSF_VIEW_KUNDE = JSF_KUNDENVERWALTUNG + "viewKunde";
//	private static final String JSF_LIST_KUNDEN = JSF_KUNDENVERWALTUNG + "/kundenverwaltung/listKunden";
//	private static final String JSF_UPDATE_PRIVATKUNDE = JSF_KUNDENVERWALTUNG + "updatePrivatkunde";
//	private static final String JSF_UPDATE_FIRMENKUNDE = JSF_KUNDENVERWALTUNG + "updateFirmenkunde";
//	private static final String JSF_DELETE_OK = JSF_KUNDENVERWALTUNG + "okDelete";
//	
//	private static final String REQUEST_KUNDE_ID = "kundeId";
//
//	private static final String CLIENT_ID_KUNDEID = "form:kundeIdInput";
//	private static final String MSG_KEY_KUNDE_NOT_FOUND_BY_ID = "viewKunde.notFound";
//	
//	private static final String CLIENT_ID_KUNDEN_NACHNAME = "form:nachname";
//	private static final String MSG_KEY_KUNDEN_NOT_FOUND_BY_NACHNAME = "listKunden.notFound";

	private static final String CLIENT_ID_CREATE_EMAIL = "createKundeForm:email";
	private static final String MSG_KEY_CREATE_PRIVATKUNDE_EMAIL_EXISTS = "createPrivatkunde.emailExists";
	
	
	
	private static final String CLIENT_ID_UPDATE_PASSWORD = "updateKundeForm:password";
	private static final String CLIENT_ID_UPDATE_EMAIL = "updateKundeForm:email";
	private static final String MSG_KEY_UPDATE_PRIVATKUNDE_DUPLIKAT = "updatePrivatkunde.duplikat";
	private static final String MSG_KEY_UPDATE_FIRMENKUNDE_DUPLIKAT = "updateFirmenkunde.duplikat";
	private static final String MSG_KEY_UPDATE_PRIVATKUNDE_CONCURRENT_UPDATE = "updatePrivatkunde.concurrentUpdate";
	private static final String MSG_KEY_UPDATE_FIRMENKUNDE_CONCURRENT_UPDATE = "updateFirmenkunde.concurrentUpdate";
	private static final String MSG_KEY_UPDATE_PRIVATKUNDE_CONCURRENT_DELETE = "updatePrivatkunde.concurrentDelete";
	private static final String MSG_KEY_UPDATE_FIRMENKUNDE_CONCURRENT_DELETE = "updateFirmenkunde.concurrentDelete";

	@PersistenceContext(type = EXTENDED)
	private transient EntityManager em;
	
	@Inject
	private KundeService ks;
	
	@Inject
	private Flash flash;
	
	@Inject
	@Client
	private Locale locale;
	
	@Inject
	private AuthController auth;
	
	@Inject
	private Messages messages;
	
	private byte[] bytes;
	private String contentType;
	
	@Inject
	@Push(topic = "marketing")
	private transient Event<String> neuerKundeEvent;
	
	@Inject
	private FileHelper filehelper;
	
	@Inject
	@Push(topic = "updateKunde")
	private transient Event<String> updateKundeEvent;
	
	private Long kundeId;
	private Kunde kunde;
	private Adresse adresse;
	private String nachname;
	private boolean geaendertKunde;  


	private Kunde neuerKunde;
	
	//private List<Kunde> kunden = Collections.emptyList();

	@Override
	public String toString() {
		return "KundeController [kundeId=" + kundeId + "]";
	}

	public void setkundeId(Long kundeId) {
		
		this.kundeId = kundeId;
	}

	public Long getkundeId() {
		return kundeId;
	}

	public void setKunde(Kunde kunde) {
		this.kunde = kunde;
	}
	
	public Kunde getKunde() {
		return this.kunde;
	}

	public Kunde getNeuerKunde() {
		return neuerKunde;
	}
	
	public String getNachname() {
		return nachname;
	}

	public void setNachname(String nachname) {
		this.nachname = nachname;
	}
	/**
	 * Action Methode, um einen Kunden zu gegebener ID zu suchen
	 * @return URL fuer Anzeige des gefundenen Kunden; sonst null
	 */
	@TransactionAttribute(REQUIRED)
	public String findKundeById() {
		// Bestellungen werden durch "Extended Persistence Context" nachgeladen
		kunde = ks.findKundebyID(kundeId, FetchType.MIT_BESTELLUNGEN, locale);
		if (kunde == null) {
			// Kein Kunde zu gegebener ID gefunden
			return "haha";
		}
		
		if (kunde.getFile() != null) {
			LOGGER.infof("Datei: %s", kunde.getFile().getFilename());
		}

		return JSF_VIEW_KUNDE;
	}
	
	
	
	public String resetUpload() {
		kunde = null;
		 bytes = null;
		 contentType = null;
		
		return JSF_INDEX;
	}
	/**
	 * F&uuml;r rich:autocomplete
	 * @return Liste der potenziellen Kunden
	 */
	@TransactionAttribute(REQUIRED)
	public List<Kunde> findKundenByIdPrefix(String idPrefix) {
		List<Kunde> kundenPrefix = null;
		 
		try {
			Long.valueOf(idPrefix);
		}
		catch (NumberFormatException e) {
			return Collections.emptyList();
		}
		
		kundenPrefix = ks.findKundenByIdPrefix(idPrefix);
		if (kundenPrefix == null || kundenPrefix.isEmpty()) {
			return Collections.emptyList();
		}
		
		if (kundenPrefix.size() > MAX_AUTOCOMPLETE) {
			return kundenPrefix.subList(0, MAX_AUTOCOMPLETE);
		}
		
		return kundenPrefix;
	}
	

	public void createEmptyKunde() {
		if (neuerKunde != null) {
			return;
		}

		neuerKunde = new Kunde();
		adresse = new Adresse();
		adresse.setKunde(neuerKunde);
		neuerKunde.setAdresse(adresse);
		
	}
	
	@TransactionAttribute(REQUIRED)
	public String createKunde() {
		try {
			neuerKunde = ks.createKunde(neuerKunde, adresse);
		
		}
		catch (InvalidKundeException | EmailExistsException e) {
			final String outcome = createKundeErrorMsg(e);
			return outcome;
		}

		// Push-Event fuer Webbrowser
		neuerKundeEvent.fire(String.valueOf(neuerKunde.getKId()));
		
		// Aufbereitung fuer viewKunde.xhtml
		kundeId = neuerKunde.getKId();
		kunde = neuerKunde;
		neuerKunde = null;  // zuruecksetzen
		
		
		return JSF_VIEW_KUNDE + JSF_REDIRECT_SUFFIX;
	}
	
	private String createKundeErrorMsg(AbstractShopException e) {
		final Class<? extends AbstractShopException> exceptionClass = e.getClass();
		if (exceptionClass.equals(EmailExistsException.class)) {
			messages.error(KUNDENVERWALTUNG, MSG_KEY_CREATE_PRIVATKUNDE_EMAIL_EXISTS, CLIENT_ID_CREATE_EMAIL);
		}
		else if (exceptionClass.equals(InvalidKundeException.class)) {
			final InvalidKundeException orig = (InvalidKundeException) e;
			messages.error(orig.getViolations(), null);
		}
		
		return null;
	}

	public void uploadListener(FileUploadEvent event) {
		final UploadedFile uploadedFile = event.getUploadedFile();
		contentType = uploadedFile.getContentType();
		bytes = uploadedFile.getData();
	}
	
	public Date getAktuellesDatum() {		
		final Date datum = new Date();
		return datum;
	}
	
	
	@TransactionAttribute(REQUIRED)
	public String upload() {
		kunde = ks.findKundebyID(kundeId, FetchType.NUR_KUNDE, locale);
		
		ks.setFile(kunde, bytes, contentType);
		// zuruecksetzen
		kundeId = null;
		kunde = null;
		bytes = null;
		contentType = null;
		return JSF_VIEW_KUNDE;
		}
	public void geaendert(ValueChangeEvent e) {
		if (geaendertKunde) {
			return;
		}
		
		if (e.getOldValue() == null) {
			if (e.getNewValue() != null) {
				geaendertKunde = true;
			}
			return;
		}

		if (!e.getOldValue().equals(e.getNewValue())) {
			geaendertKunde = true;				
		}
	}
	public String getFilename(File file) {
		if (file == null) {
			return "";
		}
		
		filehelper.store(file);
		return file.getFilename();
	}
	
	
	public String getBase64(File file) {
		return DatatypeConverter.printBase64Binary(file.getBytes());
	}
	@TransactionAttribute(REQUIRED)
	public String update() {
		auth.preserveLogin();
		
		if (!geaendertKunde || kunde == null) {
			return JSF_INDEX;
		}
		
//		if (kunde.getClass().equals(Kunde.class)) {
//			final Kunde geaenderterkunde = (Kunde) kunde;
//		}
//		
		LOGGER.tracef("Aktualisierter Kunde: %s", kunde);
		try {
			kunde = ks.updateKunde(kunde);
		}
		catch (EmailExistsException | InvalidKundeException
			  | OptimisticLockException | ConcurrentDeletedException e) {
			final String outcome = updateErrorMsg(e, kunde.getClass());
			return outcome;
		}

		// Push-Event fuer Webbrowser
		updateKundeEvent.fire(String.valueOf(kunde.getKId()));
		
		// ValueChangeListener zuruecksetzen
		geaendertKunde = false;
		
		// Aufbereitung fuer viewKunde.xhtml
		kundeId = kunde.getKId();
		
		return JSF_VIEW_KUNDE + JSF_REDIRECT_SUFFIX;
	}
	
	private String updateErrorMsg(RuntimeException e, Class<? extends Kunde> kundeClass) {
		final Class<? extends RuntimeException> exceptionClass = e.getClass();
		if (exceptionClass.equals(InvalidKundeException.class)) {
			// Ungueltiges Password: Attribute wurden bereits von JSF validiert
			final InvalidKundeException orig = (InvalidKundeException) e;
			final Collection<ConstraintViolation<Kunde>> violations = orig.getViolations();
			messages.error(violations, CLIENT_ID_UPDATE_PASSWORD);
		}
		else if (exceptionClass.equals(EmailExistsException.class)) {
			if (kundeClass.equals(Kunde.class)) {
				messages.error(KUNDENVERWALTUNG, MSG_KEY_UPDATE_PRIVATKUNDE_DUPLIKAT, CLIENT_ID_UPDATE_EMAIL);
			}
			else {
				messages.error(KUNDENVERWALTUNG, MSG_KEY_UPDATE_FIRMENKUNDE_DUPLIKAT, CLIENT_ID_UPDATE_EMAIL);
			}
		}
		else if (exceptionClass.equals(OptimisticLockException.class)) {
			if (kundeClass.equals(Kunde.class)) {
				messages.error(KUNDENVERWALTUNG, MSG_KEY_UPDATE_PRIVATKUNDE_CONCURRENT_UPDATE, null);
			}
			else {
				messages.error(KUNDENVERWALTUNG, MSG_KEY_UPDATE_FIRMENKUNDE_CONCURRENT_UPDATE, null);
			}
		}
		else if (exceptionClass.equals(ConcurrentDeletedException.class)) {
			if (kundeClass.equals(Kunde.class)) {
				messages.error(KUNDENVERWALTUNG, MSG_KEY_UPDATE_PRIVATKUNDE_CONCURRENT_DELETE, null);
			}
			else {
				messages.error(KUNDENVERWALTUNG, MSG_KEY_UPDATE_FIRMENKUNDE_CONCURRENT_DELETE, null);
			}
		}
		return null;
	}
}
