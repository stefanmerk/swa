OPTIONS(direct=true)
UNRECOVERABLE LOAD DATA
INTO TABLE bestellung
APPEND
FIELDS TERMINATED BY ';' OPTIONALLY ENCLOSED BY '"'  (
	b_id,
	kunde_fk,
	idx,
	erzeugt,
	aktualisiert)
