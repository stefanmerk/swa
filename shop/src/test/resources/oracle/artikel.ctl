OPTIONS(direct=true)
UNRECOVERABLE LOAD DATA
INTO TABLE artikel
APPEND
FIELDS TERMINATED BY ';' OPTIONALLY ENCLOSED BY '"' (
	a_id,
	bezeichnung,
	preis,
	verfuegbarkeit,
	erzeugt,
	aktualisiert)
