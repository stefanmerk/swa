package de.shop.artikelverwaltung.domain;


import static de.shop.util.Constants.KEINE_ID;
import static de.shop.util.Constants.LONG_ANZ_ZIFFERN;
import static de.shop.util.Constants.MIN_ID;
import static java.util.logging.Level.FINER;
import static javax.persistence.TemporalType.TIMESTAMP;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.Date;
import java.util.logging.Logger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PostPersist;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.constraints.Min;

import org.codehaus.jackson.annotate.JsonIgnore;

import de.shop.util.IdGroup;

 

@Entity
@Table(name = "artikel")
@NamedQueries({
	@NamedQuery(name  = Artikel.FIND_ARTIKEL,
        		query = "SELECT      a"
        	        + " FROM     Artikel a"),
    @NamedQuery(name  = Artikel.FIND_ARTIKEL_ID,
        	    query = "SELECT   a"
        	    	+ " FROM  Artikel a"
        	        + " where a.id = :" + Artikel.PARAM_A_ID),    	        
	@NamedQuery(name  = Artikel.FIND_VERFUEGBARE_ARTIKEL,
            	query = "SELECT      a"
            	        + " FROM     Artikel a"
						+ " WHERE    a.verfuegbarkeit = 'y'"
                        + " ORDER BY a.id ASC"),
	@NamedQuery(name  = Artikel.FIND_ARTIKEL_BY_BEZ,
            	query = "SELECT      a"
                        + " FROM     Artikel a"
						+ " WHERE    a.bezeichnung LIKE :" + Artikel.PARAM_BEZEICHNUNG
						
			 	        + " ORDER BY a.id ASC"),
   	@NamedQuery(name  = Artikel.FIND_ARTIKEL_MIN_PREIS,
            	query = "SELECT      a"
                        + " FROM     Artikel a"
						+ " WHERE    a.preis < :" + Artikel.PARAM_PREIS
			 	        + " ORDER BY a.id DESC")
})


public class Artikel implements Serializable {
	private static final long serialVersionUID = -7366119792815686533L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	private static final String PREFIX = "Artikel.";
	public static final String FIND_ARTIKEL_ID = PREFIX + "findBestellungById";
	public static final String FIND_VERFUEGBARE_ARTIKEL = PREFIX + "findVerfuegbareArtikel";
	public static final String FIND_ARTIKEL_BY_BEZ = PREFIX + "findArtikelByBez";
	public static final String FIND_ARTIKEL_MIN_PREIS = PREFIX + "findArtikelByMinPreis";
	public static final String FIND_ARTIKEL = PREFIX + "findAllArtikel";

	public static final String PARAM_BEZEICHNUNG = "bezeichnung";
	public static final String PARAM_PREIS = "preis";
	public static final String PARAM_A_ID = "id";


	@Id
	@GeneratedValue
	@Column(name = "a_id", unique = true, nullable = false, updatable = false, precision = LONG_ANZ_ZIFFERN)
	@Min(value = MIN_ID, message = "{artikelverwaltung.artikel.id.min}", groups = IdGroup.class)
	private Long aid = KEINE_ID;

	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	@JsonIgnore
	private Date aktualisiert;

	@Column(nullable = false)
	private String bezeichnung;

	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	@JsonIgnore
	private Date erzeugt;

	private double preis;

	@Column(nullable = false)
	private char verfuegbarkeit;

	public Artikel() {
		super();
	}
	
	public Artikel(String bezeichnung, double preis, char ver) {
	
		this.bezeichnung = bezeichnung;
		this.preis = preis;
		this.verfuegbarkeit = ver;
		
		
	}

	@PrePersist
	private void prePersist() {
		erzeugt = new Date();
		aktualisiert = new Date();
	}

	@PostPersist
	private void postPersist() {
		LOGGER.log(FINER, "Neuer Artikel mit ID={0}", aid);
	}
	
	@PreUpdate
	private void preUpdate() {
		aktualisiert = new Date();
	}
	
	public long getAId() {
		return this.aid;
	}

	public void setAId(Long aId) {
		this.aid = aId;
	}

	public String getBezeichnung() {
		return bezeichnung;
	}

	public void setBezeichnung(String bezeichnung) {
		this.bezeichnung = bezeichnung;
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

	public double getPreis() {
		return preis;
	}

	public void setPreis(double preis) {
		this.preis = preis;
	}

	public char getVerfuegbarkeit() {
		return this.verfuegbarkeit;
	}

	public void setVerfuegbarkeit(char verfuegbarkeit) {
		this.verfuegbarkeit = verfuegbarkeit;
	}

	
	public Artikel add(Artikel a) {
		if (!aid.equals(a.aid)) {	
			return null;
		}
		return new Artikel(this.bezeichnung, this.preis, this.verfuegbarkeit);
	
	}

	
	@Override
	public String toString() {
		return "Artikel [aId=" + aid + ", aktualisiert=" + aktualisiert
				+ ", bezeichnung=" + bezeichnung + ", erzeugt=" + erzeugt
				+ ", preis=" + preis + ", verfuegbarkeit=" + verfuegbarkeit
				+ "]";
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
		Artikel other = (Artikel) obj;
		if (aid == null) {
			if (other.aid != null)
				return false;
						} 
		else if (!aid.equals(other.aid))
			return false;
		return true;
	}
}
	

