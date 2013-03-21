USE shopdb;
SHOW WARNINGS;

DROP TABLE IF EXISTS hibernate_sequence;;
CREATE TABLE hibernate_sequence(
	next_val BIGINT NOT NULL PRIMARY KEY
);

DROP TABLE IF EXISTS kunde;
CREATE TABLE kunde(
	k_id BIGINT NOT NULL PRIMARY KEY,
	nachname NVARCHAR(32) NOT NULL,
	vorname NVARCHAR(32),
	geschlecht CHAR(1),
	email NVARCHAR(128) NOT NULL UNIQUE,
	password NVARCHAR(256),
	erzeugt TIMESTAMP NOT NULL,
	aktualisiert TIMESTAMP NOT NULL	
);
CREATE UNIQUE INDEX kunde_email ON kunde(email);

DROP TABLE IF EXISTS adresse;
CREATE TABLE adresse(
	a_id BIGINT NOT NULL PRIMARY KEY,
	plz CHAR(5) NOT NULL,
	ort NVARCHAR(32) NOT NULL,
	strasse NVARCHAR(32),
	hausnr NVARCHAR(4),
	
	erzeugt TIMESTAMP NOT NULL,
	aktualisiert TIMESTAMP NOT NULL,
	kunde_fk BIGINT NOT NULL REFERENCES kunde(k_id)
);
CREATE INDEX adresse_kunde_index ON adresse(kunde_fk);

DROP TABLE IF EXISTS artikel;
CREATE TABLE artikel(
	a_id BIGINT NOT NULL PRIMARY KEY,
	bezeichnung NVARCHAR(32) NOT NULL,
	preis DOUBLE,
	verfuegbarkeit BOOLEAN NOT NULL,
	erzeugt TIMESTAMP NOT NULL,
	aktualisiert TIMESTAMP NOT NULL
);


DROP TABLE IF EXISTS bestellung;
CREATE TABLE bestellung(
	b_id BIGINT NOT NULL PRIMARY KEY,
	kunde_fk BIGINT  REFERENCES kunde(k_id),
	idx SMALLINT ,
	erzeugt TIMESTAMP NOT NULL,
	aktualisiert TIMESTAMP NOT NULL
);
CREATE INDEX bestellung_kunde_index ON bestellung(kunde_fk);




DROP TABLE IF EXISTS bestellposition;
CREATE TABLE bestellposition(
	bp_id BIGINT NOT NULL PRIMARY KEY,
	bestellung_fk BIGINT NOT NULL REFERENCES bestellung(b_id),
	artikel_fk BIGINT NOT NULL REFERENCES artikel(a_id),
	anzahl SMALLINT NOT NULL,
	idx SMALLINT NOT NULL
);
CREATE INDEX bestpos_bestellung_index ON bestellposition(bestellung_fk);
CREATE INDEX bestpos_artikel_index ON bestellposition(artikel_fk);
