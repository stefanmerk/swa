package de.shop.util;


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


  
/**
 * The persistent class for the hibernate_sequence database table.
 * 
 */
@Entity
@Table(name = "hibernate_sequence")
public class HibernateSequence implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "next_val", nullable = false)
	private String nextVal;

	public HibernateSequence() {
	}

	public String getNextVal() {
		return this.nextVal;
	}

	public void setNextVal(String nextVal) {
		this.nextVal = nextVal;
	}

	@Override
	public String toString() {
		return "HibernateSequence [nextVal=" + nextVal + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nextVal == null) ? 0 : nextVal.hashCode());
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
		HibernateSequence other = (HibernateSequence) obj;
		if (nextVal == null) {
			if (other.nextVal != null)
				return false;
		} 
		else if (!nextVal.equals(other.nextVal))
			return false;
		return true;
	}

	
}