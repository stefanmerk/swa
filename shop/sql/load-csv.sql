USE ${dbname.mysql};

LOAD DATA LOCAL INFILE 'C:/Software/eclipse-workspace-git/swa13/shop/src/test/resources/mysql/hibernate_sequence.csv'
INTO TABLE hibernate_sequence
FIELDS TERMINATED BY ';'
OPTIONALLY ENCLOSED BY '"'
IGNORE 1 LINES;

LOAD DATA LOCAL INFILE 'C:/Software/eclipse-workspace-git/swa13/shop/src/test/resources/mysql/kunde.csv'
INTO TABLE kunde
FIELDS TERMINATED BY ';'
OPTIONALLY ENCLOSED BY '"'
IGNORE 1 LINES;

LOAD DATA LOCAL INFILE 'C:/Software/eclipse-workspace-git/swa13/shop/src/test/resources/mysql/adresse.csv'
INTO TABLE adresse
FIELDS TERMINATED BY ';'
OPTIONALLY ENCLOSED BY '"'
IGNORE 1 LINES;

LOAD DATA LOCAL INFILE 'C:/Software/eclipse-workspace-git/swa13/shop/src/test/resources/mysql/artikel.csv'
INTO TABLE artikel
FIELDS TERMINATED BY ';'
OPTIONALLY ENCLOSED BY '"'
IGNORE 1 LINES;

LOAD DATA LOCAL INFILE 'C:/Software/eclipse-workspace-git/swa13/shop/src/test/resources/mysql/bestellung.csv'
INTO TABLE bestellung
FIELDS TERMINATED BY ';'
OPTIONALLY ENCLOSED BY '"'
IGNORE 1 LINES;



LOAD DATA LOCAL INFILE 'C:/Software/eclipse-workspace-git/swa13/shop/src/test/resources/mysql/bestellposition.csv'
INTO TABLE bestellposition
FIELDS TERMINATED BY ';'
OPTIONALLY ENCLOSED BY '"'
IGNORE 1 LINES;
