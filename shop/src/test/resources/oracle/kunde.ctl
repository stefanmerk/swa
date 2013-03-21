OPTIONS(direct=true)
UNRECOVERABLE LOAD DATA
INTO TABLE kunde
APPEND
FIELDS TERMINATED BY ';' OPTIONALLY ENCLOSED BY '"' (
	k_id,
	nachname,
	vorname,
	geschlecht,
	email,
	password,
	erzeugt,
	aktualisiert)
