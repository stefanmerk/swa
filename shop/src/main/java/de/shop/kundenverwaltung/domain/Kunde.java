package de.shop.kundenverwaltung.domain;

import static de.shop.util.Constants.ERSTE_VERSION;
import static de.shop.util.Constants.KEINE_ID;
import static de.shop.util.Constants.LONG_ANZ_ZIFFERN;
import static de.shop.util.Constants.MIN_ID;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.TemporalType.TIMESTAMP;
import de.shop.auth.service.AuthService.RolleType;
import javax.persistence.*;
import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
import javax.persistence.PostPersist;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.jboss.logging.Logger;

import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.util.IdGroup;




/**
 * The persistent class for the kunde database table.
 * 
 */

@Entity
@Table(name = "kunde")
@Inheritance
//@DiscriminatorColumn(name = "art", length = 1)
@NamedQueries({
	@NamedQuery(name  = Kunde.FIND_KUNDEN,
                query = "SELECT k"
				        + " FROM   Kunde k"),
	@NamedQuery(name  = Kunde.FIND_KUNDEN_FETCH_BESTELLUNGEN,
				query = "SELECT  DISTINCT k"
						+ " FROM Kunde k LEFT JOIN FETCH k.bestellungen"),
	@NamedQuery(name  = Kunde.FIND_KUNDEN_ORDER_BY_ID,
		        query = "SELECT   k"
				        + " FROM  Kunde k"
		                + " ORDER BY k.id"),
	@NamedQuery(name  = Kunde.FIND_KUNDE_ID,
    			query = "SELECT   k"
    						+ " FROM  Kunde k"
    						+ " where k.id = :" + Kunde.PARAM_K_ID),
    						
  @NamedQuery(name  = Kunde.FIND_IDS_BY_PREFIX,
    		query = "SELECT   k.id"
    				      + " FROM  Kunde k"
    				      + " WHERE CONCAT('', k.id) LIKE :" + Kunde.PARAM_KUNDE_ID_PREFIX
    				      + " ORDER BY k.id"), 	
    				      @NamedQuery(name  = Kunde.FIND_USERNAME_BY_USERNAME_PREFIX,
    		  	            query = "SELECT   CONCAT('', k.id)"
    		  				        + " FROM  AbstractKunde k"
    		   	            		+ " WHERE CONCAT('', k.id) LIKE :" + Kunde.PARAM_USERNAME_PREFIX),
	@NamedQuery(name  = Kunde.FIND_KUNDEN_BY_NACHNAME,
	            query = "SELECT k"
				        + " FROM   Kunde k"
	            		+ " WHERE  UPPER(k.nachname) = UPPER(:" + Kunde.PARAM_KUNDE_NACHNAME + ")"),
	@NamedQuery(name  = Kunde.FIND_NACHNAMEN_BY_PREFIX,
	       	     query = "SELECT   DISTINCT k.nachname"
	    				 + " FROM  Kunde k "
	    	             + " WHERE UPPER(k.nachname) LIKE UPPER(:"
	    	             + Kunde.PARAM_KUNDE_NACHNAME_PREFIX + ")"),
	@NamedQuery(name  = Kunde.FIND_KUNDEN_BY_NACHNAME_FETCH_BESTELLUNGEN,
	            query = "SELECT DISTINCT k"
			            + " FROM   Kunde k LEFT JOIN FETCH k.bestellungen"
			            + " WHERE  UPPER(k.nachname) = UPPER(:" + Kunde.PARAM_KUNDE_NACHNAME + ")"),
	@NamedQuery(name  = Kunde.FIND_KUNDE_BY_ID_FETCH_BESTELLUNGEN,
	            query = "SELECT DISTINCT k"
			            + " FROM   Kunde k LEFT JOIN FETCH k.bestellungen"
			            + " WHERE  k.id = :" + Kunde.PARAM_KUNDE_ID),
 
   	@NamedQuery(name  = Kunde.FIND_KUNDE_BY_EMAIL,
   	            query = "SELECT DISTINCT k"
   			            + " FROM   Kunde k"
   			            + " WHERE  k.email = :" + Kunde.PARAM_KUNDE_EMAIL)
})

public  class  Kunde implements Serializable {
	private static final long serialVersionUID = -9023615284991323369L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
		
		private static final String PREFIX = "Kunde.";
		public static final String FIND_KUNDEN = PREFIX + "findKunden";
		public static final String FIND_KUNDEN_FETCH_BESTELLUNGEN = PREFIX + "findKundenFetchBestellungen";
		public static final String FIND_KUNDEN_ORDER_BY_ID = PREFIX + "findKundenOrderById";
		public static final String FIND_KUNDE_ID = PREFIX + "findKundeById";
		public static final String FIND_IDS_BY_PREFIX = PREFIX + "findIdsByPrefix";
		public static final String FIND_KUNDEN_BY_NACHNAME = PREFIX + "findKundenByNachname";
		public static final String FIND_KUNDEN_BY_NACHNAME_FETCH_BESTELLUNGEN =
			                       PREFIX + "findKundenByNachnameFetchBestellungen";
		public static final String FIND_NACHNAMEN_BY_PREFIX = PREFIX + "findNachnamenByPrefix";
		public static final String FIND_KUNDE_BY_USERNAME = PREFIX + "findKundeByUsername";
		public static final String FIND_USERNAME_BY_USERNAME_PREFIX = PREFIX + "findKundeByUsernamePrefix";
		public static final String FIND_KUNDE_BY_ID_FETCH_BESTELLUNGEN =
			                       PREFIX + "findKundeByIdFetchBestellungen";
		public static final String FIND_KUNDE_BY_EMAIL = PREFIX + "findKundeByEmail";
		public static final String FIND_KUNDEN_BY_PLZ = PREFIX + "findKundenByPlz";
		
		
		public static final String PARAM_KUNDE_USERNAME = "username";
		public static final String PARAM_USERNAME_PREFIX = "usernamePrefix";
		public static final String PARAM_KUNDE_ID = "kundeId";
		public static final String PARAM_K_ID = "id";
		public static final String PARAM_KUNDE_ID_PREFIX = "idPrefix";
		public static final String PARAM_KUNDE_NACHNAME = "nachname";
		public static final String PARAM_KUNDE_NACHNAME_PREFIX = "nachnamePrefix";
		public static final String PARAM_KUNDE_ADRESSE_PLZ = "plz";
		public static final String PARAM_KUNDE_EMAIL = "email";
		

		@Id
		@GeneratedValue
		@Column(name = "k_id", unique = true, nullable = false, updatable = false, precision = LONG_ANZ_ZIFFERN)
		@Min(value = MIN_ID, message = "{kundenverwaltung.kunde.id.min}", groups = IdGroup.class)
		 
		private Long kid = KEINE_ID;


	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	@JsonIgnore
	private Date aktualisiert;

	@Column(nullable = false)
	@Pattern(regexp = "[\\w.%-]+@[\\w.%-]+\\.[A-Za-z]{2,5}", message = "{kundenverwaltung.kunde.email.pattern}")
	private String email;

	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	@JsonIgnore
	private Date erzeugt;

	

	private String geschlecht;

	@Column(nullable = false)
	private String nachname;

	private String password;

	@Column(nullable = false)
	private String vorname;
	
	@OneToOne(cascade = { PERSIST, REMOVE }, mappedBy = "kunden") 
	@Valid
	private Adresse adresse;

	@OneToMany
	@JoinColumn(name = "kunde_fk", nullable = true)
	@OrderColumn(name = "idx")
	@JsonIgnore
	private List<Bestellung> bestellungen;
	
	@Transient
	private URI bestellungenUri;
	
	@ElementCollection(fetch = EAGER)
	@CollectionTable(name = "kunde_rolle",
	                 joinColumns = @JoinColumn(name = "kunde_fk", nullable = false),
	                 uniqueConstraints =  @UniqueConstraint(columnNames = { "kunde_fk", "rolle_fk" }))
	@Column(table = "kunde_rolle", name = "rolle_fk", nullable = false)
	private Set<RolleType> rollen;
	
	@Version
	@Basic(optional = false)
	private int version = ERSTE_VERSION;

	
	public Kunde() {
		super();
	}
	
	public Kunde(String vorname, String nn, String geschlecht,  String emailk ,  String passwd , Adresse adr) {
		this.adresse = adr;
		this.geschlecht = geschlecht;
		this.vorname = vorname;
		this.nachname = nn;
		this.password = passwd;
		this.email = emailk;

		
	}
	public void setValues(Kunde kopie) {
		this.adresse.setValues(kopie.adresse);
		this.geschlecht = kopie.geschlecht;
		this.vorname = kopie.vorname;
		this.nachname = kopie.nachname;
		this.password = kopie.password;
		this.email = kopie.email;
		this.version = kopie.version;
		
	}

	@PrePersist
	private void prePersist() {
		erzeugt = new Date();
		aktualisiert = new Date();
	}
	
	@PostPersist
	private void postPersist() {
		LOGGER.debugf("Neue Adresse mit ID=%d", kid);
	}
	
	@PreUpdate
	private void preUpdate() {
		aktualisiert = new Date();
	}
		
	
	public URI getBestellungenUri() {
		return bestellungenUri;
	}
	public void setBestellungenUri(URI bestellungenUri) {
		this.bestellungenUri = bestellungenUri;
	}

	public long getKId() {
		return this.kid;
	}

	public void setKId(Long kId) {
		this.kid = kId;
	}

	public int getVersion() {
		return this.version;
	}
	
	public void setVersion(int version) {
		this.version = version;	
	}
	public Date getErzeugt() {
		return erzeugt == null ? null : (Date) erzeugt.clone();
	}
	public void setErzeugt(Date erzeugt) {
		this.erzeugt = erzeugt == null ? null : (Date) erzeugt.clone();
	}
	public Date getAktualisiert() {
		return aktualisiert == null ? null : (Date) aktualisiert.clone();
	}
	public void setAktualisiert(Date aktualisiert) {
		this.aktualisiert = aktualisiert == null ? null : (Date) aktualisiert.clone();
	}
	
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getGeschlecht() {
		return this.geschlecht;
	}

	public void setGeschlecht(String geschlecht) {
		this.geschlecht = geschlecht;
	}

	public String getNachname() {
		return this.nachname;
	}
	
	
	public void setNachname(String nachname) {
		this.nachname = nachname;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	public Set<RolleType> getRollen() {
		return rollen;
	}

	public void setRollen(Set<RolleType> rollen) {
		this.rollen = rollen;
	}

	public String getVorname() {
		return this.vorname;
	}

	public void setVorname(String vorname) {
		this.vorname = vorname;
	}

	public Adresse getAdresse() {
		return adresse;
		}
	
	public void setAdresse(Adresse adresse) {
		this.adresse = adresse;
		}
	
	@Valid
	public List<Bestellung> getBestellungen() {
		if (bestellungen == null) {
			return null;
		}
		
		return Collections.unmodifiableList(bestellungen);
	}
	public void setBestellungen(List<Bestellung> bestellungen) {
		if (this.bestellungen == null) {
		this.bestellungen = bestellungen;
		return;
		}
		// Wiederverwendung der vorhandenen Collection
		this.bestellungen.clear();
		if (bestellungen != null) {
		this.bestellungen.addAll(bestellungen);
		}
		}
	public Kunde add(Kunde k) {
		
		if (!kid.equals(k.kid))
			return null;
		return new Kunde(this.vorname, this.nachname, this.geschlecht,  this.email, this.password , this.adresse);
				
		
	}
	public void bestellung(List<Bestellung> bestellungsliste) {
		if (this.bestellungen == null) {
			this.bestellungen = bestellungsliste;
			return;
		}
		this.bestellungen.clear();
	}
	
	public Kunde addBestellung(Bestellung bestellung) {
		if (bestellungen == null) {
			bestellungen = new ArrayList<>();
		}
		bestellungen.add(bestellung);
		return this;
	}
		
	

		

	@Override
	public String toString() {
		return "Kunde [kId=" + kid + ", aktualisiert=" + aktualisiert
				+ ", email=" + email + ", erzeugt=" + erzeugt
				+ ", geschlecht="
				+ geschlecht + ", nachname=" + nachname + ", password="
				+ password + ", vorname=" + vorname + ", version: " + version + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((kid == null) ? 0 : kid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Kunde other = (Kunde) obj;
		if (kid == null) {
			if (other.kid != null)
				return false;
		} 
		else if (!kid.equals(other.kid))
			return false;
		return true;
	}
	
	
}
	

