package de.shop.kundenverwaltung.domain;

import static de.shop.util.Constants.ERSTE_VERSION;
import static de.shop.util.Constants.KEINE_ID;
import static de.shop.util.Constants.LONG_ANZ_ZIFFERN;
import static de.shop.util.Constants.MIN_ID;
import static javax.persistence.TemporalType.TIMESTAMP;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PostPersist;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.jboss.logging.Logger;

import de.shop.util.IdGroup;


/**
 * The persistent class for the adresse database table.
 * 
 */

@Entity
@Table(name = "adresse")
//@XmlAccessorType(FIELD)
public class Adresse implements Serializable {
	private static final long serialVersionUID = 435756438025198742L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

	
	@Id
	@GeneratedValue
	@Column(name = "a_id", unique = true, nullable = false, updatable = false, precision = LONG_ANZ_ZIFFERN)
	@Min(value = MIN_ID, message = "{kundenverwaltung.adresse.id.min}", groups = IdGroup.class)
	 
	private Long aid = KEINE_ID;
	
	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	@JsonIgnore
	private Date aktualisiert;

	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	@JsonIgnore
	private Date erzeugt;

	 
	private String hausnr;
  
	@OneToOne
	@JoinColumn(name = "kunde_fk", nullable = false)
	//@OrderColumn(name = "idx", nullable = false)
	@NotNull(message = "{kundeverwaltung.adresse.kunde.notNull}")
	@JsonIgnore
	private Kunde kunden;

	@Column(nullable = false)
	@NotNull(message = "{kundenverwaltung.adresse.ort.notNull} ")
	 
	private String ort;
	
	@Column(nullable = false)
	@NotNull(message = "{kundenverwaltung.adresse.plz.notNull}")
	@Digits(integer = 7, fraction = 0)
	 
	private String plz;
	
	 
	private String strasse;
	
	@Transient
	 
	private URI kundeUri;
	
	@Version
	@Basic(optional = false)
	private int version = ERSTE_VERSION;
	
	public Adresse() {
		super();
	}
	
	public Adresse(String ort, String plz, String strasse,  String hausnr, Kunde kunde) {
		this.kunden = kunde;
		this.ort = ort;
		this.plz = plz;
		this.strasse = strasse;
		this.hausnr = hausnr;
		
	}

	@PrePersist
	private void prePersist() {
		erzeugt = new Date();
		aktualisiert = new Date();
	}
	
	@PostPersist
	private void postPersist() {
		LOGGER.debugf("Neue Adresse mit ID=%d", aid);
	}
	
	@PreUpdate
	private void preUpdate() {
		aktualisiert = new Date();
	}
	
		
	public Long getAId() {
		return this.aid;
	}

	public void setAId(Long aId) {
		this.aid = aId;
	}

	public Date getErzeugt() {
		return erzeugt == null ? null : (Date) erzeugt.clone();
	}
	public void setErzeugt(Date erzeugt) {
		this.erzeugt = erzeugt == null ? null : (Date) erzeugt.clone();
	}
	public URI getKundeUri() {
		return kundeUri;
	}

	public void setKundeUri(URI kundeUri) {
		this.kundeUri = kundeUri;
	}

	public Date getAktualisiert() {
		return aktualisiert == null ? null : (Date) aktualisiert.clone();
	}
	public void setAktualisiert(Date aktualisiert) {
		this.aktualisiert = aktualisiert == null ? null : (Date) aktualisiert.clone();
	}

	public String getHausnr() {
		return this.hausnr;
	}

	public void setHausnr(String hausnr) {
		this.hausnr = hausnr;
	}

	public Kunde getKunden() {
		if (kunden == null) {
			return null;
		}
		
		return this.kunden;
	}
	
	public void setKunde(Kunde kunde) {
		if (this.kunden == null) {
			this.kunden = kunde;
			return;
		}
		
	}
	
	public Adresse addKunde(Kunde kunde) {
		if (kunden == null) {
			kunden = kunde;
		}
		//kunden.add(kunde);
		return this;
	}
		
	public int getVersion() {
		return this.version;
	}
	
	public void setVersion(int version)
	{
		this.version = version;	
	}	

	public String getOrt() {
		return this.ort;
	}

	public void setOrt(String ort) {
		this.ort = ort;
	}

	public String getPlz() {
		return this.plz;
	}

	public void setPlz(String plz) {
		this.plz = plz;
	}

	public String getStrasse() {
		return this.strasse;
	}

	public void setStrasse(String strasse) {
		this.strasse = strasse;
	}
	
	public void setValues(Adresse kopie) {	
		this.strasse = kopie.strasse;
		
		this.hausnr = kopie.hausnr;
		this.ort = kopie.ort;
		this.plz = kopie.plz;
		
		
	}

	@Override
	public String toString() {
		return "Adresse [aId=" + aid + ", aktualisiert=" + aktualisiert
				+ ", erzeugt=" + erzeugt + ", hausnr=" + hausnr + ", ort="
				+ ort + ", plz=" + plz + ", strasse=" + strasse + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((aid == null) ? 0 : aid.hashCode());
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
		Adresse other = (Adresse) obj;
		if (aid == null) {
			if (other.aid != null)
				return false;
		}
		else if (!aid.equals(other.aid))
			return false;
		return true;
	}
	
	

}
