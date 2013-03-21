OPTIONS(direct=true)
UNRECOVERABLE LOAD DATA
INTO TABLE bestellposition
APPEND
FIELDS TERMINATED BY ';' OPTIONALLY ENCLOSED BY '"' (
	bp_id,
	bestellung_fk,
	artikel_fk,
	anzahl,
	idx)
