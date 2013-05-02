package de.shop.bestellverwaltung.domain;

import static de.shop.util.Constants.ERSTE_VERSION;
import static de.shop.util.Constants.KEINE_ID;
import static de.shop.util.Constants.LONG_ANZ_ZIFFERN;
import static de.shop.util.Constants.MIN_ID;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.net.URI;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PostPersist;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.jboss.logging.Logger;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.util.IdGroup;


/**
 * The persistent class for the bestellposition database table.
 * 
 */
@Entity
@Table(name = "bestellposition")
public class Bestellposition implements Serializable {
	private static final long serialVersionUID = 2341718863103646289L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());


	@Id
	@GeneratedValue
	@Column(name = "bp_id", unique = true, nullable = false, updatable = false, precision = LONG_ANZ_ZIFFERN)
	@Min(value = MIN_ID, message = "{bestelverwaltung.bestellposition.id.min}", groups = IdGroup.class)
	private Long bpId = KEINE_ID;

	@Column(nullable = false)
	private short anzahl;
 
	@OneToOne
	@JoinColumn(name = "artikel_fk", nullable = false)
	@NotNull
	@JsonIgnore
	private Artikel artikel;
	
	@Transient
	private URI artikelUri;
	
	@JsonIgnore 
	@ManyToOne(optional = false)
	@JoinColumn(name = "bestellung_fk", nullable = false,
	insertable = false, updatable = false)
	private Bestellung bestellung;
/*
	@Column(nullable = true ,insertable = false, updatable = false)
	private short idx;*/

	@Version
	@Basic(optional = false)
	private int version = ERSTE_VERSION;
	
	@PostPersist
	private void postPersist() {
		LOGGER.debugf("Neue Bestellposition mit ID=%d", bpId);
	}
	
	public Long getBpId() {
		return this.bpId;
	}

	public void setBpId(Long bpId) {
		this.bpId = bpId;
	}

	public int getVersion() {
		return this.version;
	}
	
	public void setVersion(int version) {
		this.version = version;	
	}
	
	public short getAnzahl() {
		return this.anzahl;
	}

	public void setAnzahl(short anzahl) {
		this.anzahl = anzahl;
	}

	public Artikel getArtikel() {
		return artikel;
		}
	
	public void setArtikel(Artikel artikel) {
		this.artikel = artikel;
		}
	
	public URI getArtikelUri() {
		return artikelUri;
	}
	
	public void setArtikelUri(URI artikelUri) {
		this.artikelUri = artikelUri;
	}


	public Bestellung getBestellung() {
		return bestellung;
		}
	
	public void setBestellung(Bestellung bestellung) {
		this.bestellung = bestellung;
		}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bpId == null) ? 0 : bpId.hashCode());
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
		final Bestellposition other = (Bestellposition) obj;
		if (bpId == null) {
			if (other.bpId != null)
				return false;
		} 
		else if (!bpId.equals(other.bpId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Bestellposition [bpId=" + bpId + ", anzahl=" + anzahl;
	}
	
	public void setValues(Bestellposition kopie) {
		this.artikel.setValues(kopie.artikel);
		this.anzahl = kopie.anzahl;
		this.bestellung.setValues(kopie.bestellung);
		this.version = kopie.version;
		
	}
}
	
