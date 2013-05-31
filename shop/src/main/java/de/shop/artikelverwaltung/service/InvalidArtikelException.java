package de.shop.artikelverwaltung.service;

import java.util.Collection;

import javax.ejb.ApplicationException;
import javax.validation.ConstraintViolation;

import de.shop.artikelverwaltung.domain.Artikel;

@ApplicationException(rollback = true)
public class InvalidArtikelException extends ArtikelValidationException {
	private static final long serialVersionUID = 4255133082483647701L;

	private Long id;
	private String bezeichnung;
	private String vorhanden;


	public InvalidArtikelException(Artikel artikel,
			                     Collection<ConstraintViolation<Artikel>> violations) {
		super(violations);
		if (artikel != null) {
			this.id = artikel.getAId();
			this.bezeichnung = artikel.getBezeichnung();
			this.vorhanden = artikel.getVerfuegbarkeit();
		}
	}
	
	
	public InvalidArtikelException(Long id, Collection<ConstraintViolation<Artikel>> violations) {
		super(violations);
		this.id = id;
	}
	
	public InvalidArtikelException(String bezeichnung, Collection<ConstraintViolation<Artikel>> violations) {
		super(violations);
		this.bezeichnung = bezeichnung;
	}
	
	public Long getId() {
		return id;
	}
	public String getbezeichnung() {
		return bezeichnung;
	}
	public String getvorhanden() {
		return vorhanden;
	}
	
	@Override
	public String toString() {
		return "{bezeichnung=" + bezeichnung + ", vorhanden=" + vorhanden + "}";
	}
}
