OPTIONS(direct=true)
UNRECOVERABLE LOAD DATA
INTO TABLE adresse
APPEND
FIELDS TERMINATED BY ';' OPTIONALLY ENCLOSED BY '"' (
	a_id,
	plz,
	ort,
	strasse,
	hausnr,
	erzeugt,
	aktualisiert,
	kunde_fk)
