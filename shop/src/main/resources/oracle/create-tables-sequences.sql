CREATE SEQUENCE hibernate_sequence START WITH ${sequence.start};

CREATE TABLE kunde(
	k_id INTEGER NOT NULL PRIMARY KEY,
	nachname VARCHAR2(32) NOT NULL,
	vorname VARCHAR2(32),
	geschlecht CHAR(1),
	email VARCHAR2(128) NOT NULL UNIQUE,
	password VARCHAR2(256),
	erzeugt TIMESTAMP NOT NULL,
	aktualisiert TIMESTAMP NOT NULL
	)CACHE;


CREATE TABLE adresse(
	a_id INTEGER NOT NULL PRIMARY KEY,
	plz CHAR(5) NOT NULL,
	ort VARCHAR2(32) NOT NULL,
	strasse VARCHAR2(32),
	hausnr VARCHAR2(4),
	erzeugt TIMESTAMP NOT NULL,
	aktualisiert TIMESTAMP NOT NULL	,
	kunde_fk INTEGER NOT NULL REFERENCES kunde(k_id)
)CACHE;
CREATE INDEX adresse_kunde_index ON adresse(kunde_fk);

CREATE TABLE artikel(
	a_id INTEGER NOT NULL PRIMARY KEY,
	bezeichnung VARCHAR2(32) NOT NULL,
	preis DOUBLE PRECISION,
	verfuegbarkeit CHAR(1),
	erzeugt TIMESTAMP NOT NULL,
	aktualisiert TIMESTAMP NOT NULL
)CACHE;


CREATE TABLE bestellung(
	b_id INTEGER NOT NULL PRIMARY KEY,
	kunde_fk INTEGER NOT NULL REFERENCES kunde(k_id),
	idx SMALLINT ,
	erzeugt TIMESTAMP NOT NULL,
	aktualisiert TIMESTAMP NOT NULL
)CACHE;
CREATE INDEX bestellung_kunde_index ON bestellung(kunde_fk);




CREATE TABLE bestellposition(
	bp_id INTEGER NOT NULL PRIMARY KEY,
	bestellung_fk INTEGER NOT NULL REFERENCES bestellung(b_id),
	artikel_fk INTEGER NOT NULL REFERENCES artikel(a_id),
	anzahl SMALLINT NOT NULL,
	idx SMALLINT
)CACHE;
CREATE INDEX bestpos_bestellung_index ON bestellposition(bestellung_fk);
CREATE INDEX bestpos_artikel_index ON bestellposition(artikel_fk);
