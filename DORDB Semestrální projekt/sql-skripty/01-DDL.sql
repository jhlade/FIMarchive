-- autocommit
SET autocommit=1;
-- chyby při kompilaci triggerů
ALTER SESSION SET PLSCOPE_SETTINGS = 'IDENTIFIERS:NONE';


-- čistý stůl
DROP TABLE Adresa CASCADE CONSTRAINTS;
DROP SEQUENCE Adresa_seq;
DROP TABLE Kategorie CASCADE CONSTRAINTS;
DROP SEQUENCE Kategorie_seq;
DROP TABLE Lokalita CASCADE CONSTRAINTS;
DROP SEQUENCE Lokalita_seq;
DROP TABLE Obchod CASCADE CONSTRAINTS;
DROP TABLE Oddeleni CASCADE CONSTRAINTS;
DROP SEQUENCE Oddeleni_seq;
DROP TABLE OrgJednotka CASCADE CONSTRAINTS;
DROP SEQUENCE OrgJednotka_seq;
DROP TABLE pomerOddeleni CASCADE CONSTRAINTS;
DROP TABLE PracovniPomer CASCADE CONSTRAINTS;
DROP SEQUENCE PracovniPomer_seq;
DROP TABLE Produkt CASCADE CONSTRAINTS;
DROP SEQUENCE Produkt_seq;
DROP TABLE produktPodtypu CASCADE CONSTRAINTS;
DROP TABLE produktVObchode CASCADE CONSTRAINTS;
DROP TABLE Subkategorie CASCADE CONSTRAINTS;
DROP SEQUENCE Subkategorie_seq;
DROP TABLE Zamestnanec CASCADE CONSTRAINTS;
DROP SEQUENCE Zamestnanec_seq;
DROP TABLE Zeme CASCADE CONSTRAINTS;
DROP SEQUENCE Zeme_seq;

-- Adresa
CREATE TABLE Adresa (
  psc number(5) DEFAULT NULL,
  kraj varchar2(255) DEFAULT NULL,
  mesto varchar2(255) DEFAULT NULL,
  ulice varchar2(255) DEFAULT NULL,
  adresaID number(10) NOT NULL, -- PK
  zemeID number(10) DEFAULT NULL, -- FK (Zeme)
  PRIMARY KEY (adresaID)
)  ;

CREATE SEQUENCE Adresa_seq START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER Adresa_seq_tr
 BEFORE INSERT ON Adresa FOR EACH ROW
 WHEN (NEW.adresaID IS NULL)
BEGIN
 SELECT Adresa_seq.NEXTVAL INTO :NEW.adresaID FROM DUAL;
END;
/

-- FK (Zeme)
CREATE INDEX zemeID ON Adresa (zemeID);


-- Kategorie
CREATE TABLE Kategorie (
  nazev varchar2(150) DEFAULT NULL,
  kategorieID number(10) NOT NULL, -- PK
  PRIMARY KEY (kategorieID)
)  ;

CREATE SEQUENCE Kategorie_seq START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER Kategorie_seq_tr
 BEFORE INSERT ON Kategorie FOR EACH ROW
 WHEN (NEW.kategorieID IS NULL)
BEGIN
 SELECT Kategorie_seq.NEXTVAL INTO :NEW.kategorieID FROM DUAL;
END;
/


-- Lokalita
CREATE TABLE Lokalita (
  nazev varchar2(255) DEFAULT NULL,
  popis varchar2(255) DEFAULT NULL,
  typ varchar2(150) DEFAULT NULL,
  adresaID number(10) NOT NULL,
  lokalitaID number(10) NOT NULL,
  PRIMARY KEY (lokalitaID)
)  ;

CREATE SEQUENCE Lokalita_seq START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER Lokalita_seq_tr
 BEFORE INSERT ON Lokalita FOR EACH ROW
 WHEN (NEW.lokalitaID IS NULL)
BEGIN
 SELECT Lokalita_seq.NEXTVAL INTO :NEW.lokalitaID FROM DUAL;
END;
/

CREATE INDEX adresaID ON Lokalita (adresaID);


-- Obchod
CREATE TABLE Obchod (
  nazev varchar2(255) DEFAULT NULL,
  obchodID number(10) NOT NULL,
  lokalitaID number(10) DEFAULT NULL,
  PRIMARY KEY (obchodID)
)  ;

CREATE INDEX lokalitaID ON Obchod (lokalitaID);

-- Oddělení
CREATE TABLE Oddeleni (
  nazev varchar2(255) DEFAULT NULL,
  oddeleniID number(10) NOT NULL,
  orgJednotkaID number(10) DEFAULT NULL,
  PRIMARY KEY (oddeleniID)
)  ;

CREATE SEQUENCE Oddeleni_seq START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER Oddeleni_seq_tr
 BEFORE INSERT ON Oddeleni FOR EACH ROW
 WHEN (NEW.oddeleniID IS NULL)
BEGIN
 SELECT Oddeleni_seq.NEXTVAL INTO :NEW.oddeleniID FROM DUAL;
END;
/

CREATE INDEX orgJednotkaID ON Oddeleni (orgJednotkaID);

-- Organizační jednotka
CREATE TABLE OrgJednotka (
  id number(4) DEFAULT NULL,
  nazev varchar2(255) DEFAULT NULL,
  orgJednotkaID number(4) NOT NULL,
  adresaID number(10) NOT NULL,
  zamestnanecID number(10) NOT NULL,
  PRIMARY KEY (orgJednotkaID)
)  ;

CREATE SEQUENCE OrgJednotka_seq START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER OrgJednotka_seq_tr
 BEFORE INSERT ON OrgJednotka FOR EACH ROW
 WHEN (NEW.orgJednotkaID IS NULL)
BEGIN
 SELECT OrgJednotka_seq.NEXTVAL INTO :NEW.orgJednotkaID FROM DUAL;
END;
/

CREATE INDEX sidloOJ ON OrgJednotka (adresaID);
CREATE INDEX zamestnanecID ON OrgJednotka (zamestnanecID);


-- Zam. poměr v oddělení
CREATE TABLE pomerOddeleni (
  oddeleniID number(10) DEFAULT NULL,
  pracovniPomerID number(10) DEFAULT NULL
) ;

CREATE INDEX oddeleniID ON pomerOddeleni (oddeleniID);
CREATE INDEX pracovniPomerID ON pomerOddeleni (pracovniPomerID);


-- Pracovní poměry
CREATE TABLE PracovniPomer (
  do date DEFAULT NULL,
  od date NOT NULL,
  uvazek number(2) DEFAULT NULL,
  pozice varchar2(150) DEFAULT NULL,
  pracovniPomerID number(10) NOT NULL,
  zamestnanecID number(10) DEFAULT NULL,
  PRIMARY KEY (pracovniPomerID)
)  ;

CREATE SEQUENCE PracovniPomer_seq START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER PracovniPomer_seq_tr
 BEFORE INSERT ON PracovniPomer FOR EACH ROW
 WHEN (NEW.pracovniPomerID IS NULL)
BEGIN
 SELECT PracovniPomer_seq.NEXTVAL INTO :NEW.pracovniPomerID FROM DUAL;
END;
/

CREATE INDEX pracovnik ON PracovniPomer (zamestnanecID);

-- Produkt
CREATE TABLE Produkt (
  cena number(8,2) DEFAULT NULL,
  id varchar2(48) DEFAULT NULL,
  nazev varchar2(255) DEFAULT NULL,
  produktID number(10) NOT NULL,
  kategorieID number(10) DEFAULT NULL,
  PRIMARY KEY (produktID)
)  ;

CREATE SEQUENCE Produkt_seq START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER Produkt_seq_tr
 BEFORE INSERT ON Produkt FOR EACH ROW
 WHEN (NEW.produktID IS NULL)
BEGIN
 SELECT Produkt_seq.NEXTVAL INTO :NEW.produktID FROM DUAL;
END;
/

CREATE INDEX kategorieID ON Produkt (kategorieID);

-- Produkt podtypu
CREATE TABLE produktPodtypu (
  subkategorieID number(10) DEFAULT NULL,
  produktID number(10) DEFAULT NULL
) ;

CREATE INDEX subkategorieID ON produktPodtypu (subkategorieID);
CREATE INDEX produktID ON produktPodtypu (produktID);


-- Produkt v obchodě (M:N vazební)
CREATE TABLE produktVObchode (
  produktID number(10) DEFAULT NULL,
  obchodID number(10) DEFAULT NULL
) ;

CREATE INDEX produktObch ON produktVObchode (produktID);
CREATE INDEX obchodID ON produktVObchode (obchodID);

-- Subkategorie
CREATE TABLE Subkategorie (
  nazev varchar2(150) DEFAULT NULL,
  subkategorieID number(10) NOT NULL,
  kategorieID number(10) DEFAULT NULL,
  PRIMARY KEY (subkategorieID)
)  ;

CREATE SEQUENCE Subkategorie_seq START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER Subkategorie_seq_tr
 BEFORE INSERT ON Subkategorie FOR EACH ROW
 WHEN (NEW.subkategorieID IS NULL)
BEGIN
 SELECT Subkategorie_seq.NEXTVAL INTO :NEW.subkategorieID FROM DUAL;
END;
/

CREATE INDEX hlavni ON Subkategorie (kategorieID);


-- Zaměstnanec
CREATE TABLE Zamestnanec (
  jmeno varchar2(255) DEFAULT NULL,
  prijmeni varchar2(255) NOT NULL,
  adresaID number(10) NOT NULL,
  zamestnanecID number(10) NOT NULL,
  PRIMARY KEY (zamestnanecID)
)  ;

CREATE SEQUENCE Zamestnanec_seq START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER Zamestnanec_seq_tr
 BEFORE INSERT ON Zamestnanec FOR EACH ROW
 WHEN (NEW.zamestnanecID IS NULL)
BEGIN
 SELECT Zamestnanec_seq.NEXTVAL INTO :NEW.zamestnanecID FROM DUAL;
END;
/

CREATE INDEX bydliste ON Zamestnanec (adresaID);
CREATE INDEX prijmeni ON Zamestnanec (prijmeni);



-- Země
CREATE TABLE Zeme (
  iso_3166_1 char(3) DEFAULT NULL,
  nazev varchar2(255) DEFAULT NULL,
  zemeID number(10) NOT NULL,
  PRIMARY KEY (zemeID)
)  ;

-- Generate ID using sequence and trigger
CREATE SEQUENCE Zeme_seq START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER Zeme_seq_tr
 BEFORE INSERT ON Zeme FOR EACH ROW
 WHEN (NEW.zemeID IS NULL)
BEGIN
 SELECT Zeme_seq.NEXTVAL INTO :NEW.zemeID FROM DUAL;
END;
/


------- integrita ---
ALTER TABLE Adresa DROP CONSTRAINT vZemi;
ALTER TABLE Adresa
  ADD CONSTRAINT vZemi FOREIGN KEY (zemeID) REFERENCES Zeme (zemeID) ON DELETE CASCADE;

ALTER TABLE Lokalita DROP CONSTRAINT lokalitaNaAdrese;
ALTER TABLE Lokalita
  ADD CONSTRAINT lokalitaNaAdrese FOREIGN KEY (adresaID) REFERENCES Adresa (adresaID) ON DELETE CASCADE;

ALTER TABLE Obchod DROP CONSTRAINT FK_Ob_Odd;
ALTER TABLE Obchod
  ADD CONSTRAINT FK_Ob_Odd FOREIGN KEY (obchodID) REFERENCES Oddeleni (oddeleniID) ON DELETE CASCADE;
ALTER TABLE Obchod DROP CONSTRAINT obchodVLok;
ALTER TABLE Obchod
  ADD CONSTRAINT obchodVLok FOREIGN KEY (lokalitaID) REFERENCES Lokalita (lokalitaID) ON DELETE CASCADE;

ALTER TABLE Oddeleni DROP CONSTRAINT oddeleniVOJ;
ALTER TABLE Oddeleni
  ADD CONSTRAINT oddeleniVOJ FOREIGN KEY (orgJednotkaID) REFERENCES OrgJednotka (orgJednotkaID) ON DELETE CASCADE;

ALTER TABLE OrgJednotka DROP CONSTRAINT manazer;
ALTER TABLE OrgJednotka
  ADD CONSTRAINT manazer FOREIGN KEY (zamestnanecID) REFERENCES Zamestnanec (zamestnanecID) ON DELETE CASCADE;
ALTER TABLE OrgJednotka DROP CONSTRAINT orgJednotkaNaAdrese;
ALTER TABLE OrgJednotka
  ADD CONSTRAINT orgJednotkaNaAdrese FOREIGN KEY (adresaID) REFERENCES Adresa (adresaID) ON DELETE CASCADE;

ALTER TABLE pomerOddeleni DROP CONSTRAINT PracovniPomer;
ALTER TABLE pomerOddeleni
  ADD CONSTRAINT PracovniPomer FOREIGN KEY (pracovniPomerID) REFERENCES PracovniPomer (pracovniPomerID) ON DELETE CASCADE;
ALTER TABLE pomerOddeleni DROP CONSTRAINT Oddeleni;
ALTER TABLE pomerOddeleni
  ADD CONSTRAINT Oddeleni FOREIGN KEY (oddeleniID) REFERENCES Oddeleni (oddeleniID) ON DELETE CASCADE;

ALTER TABLE PracovniPomer DROP CONSTRAINT pomerZamestnance;
ALTER TABLE PracovniPomer
  ADD CONSTRAINT pomerZamestnance FOREIGN KEY (zamestnanecID) REFERENCES Zamestnanec (zamestnanecID) ON DELETE CASCADE;

ALTER TABLE Produkt DROP CONSTRAINT produktTypu;
ALTER TABLE Produkt
  ADD CONSTRAINT produktTypu FOREIGN KEY (kategorieID) REFERENCES Kategorie (kategorieID) ON DELETE CASCADE;

ALTER TABLE produktPodtypu DROP CONSTRAINT Produkt;
ALTER TABLE produktPodtypu
  ADD CONSTRAINT Produkt FOREIGN KEY (produktID) REFERENCES Produkt (produktID) ON DELETE CASCADE;
ALTER TABLE produktPodtypu DROP CONSTRAINT Subkategorie;
ALTER TABLE produktPodtypu
  ADD CONSTRAINT Subkategorie FOREIGN KEY (subkategorieID) REFERENCES Subkategorie (subkategorieID) ON DELETE CASCADE;

ALTER TABLE produktVObchode DROP CONSTRAINT ProduktVProdeji;
ALTER TABLE produktVObchode
  ADD CONSTRAINT ProduktVProdeji FOREIGN KEY (produktID) REFERENCES Produkt (produktID) ON DELETE CASCADE;
ALTER TABLE produktVObchode DROP CONSTRAINT Obchod;
ALTER TABLE produktVObchode
  ADD CONSTRAINT Obchod FOREIGN KEY (obchodID) REFERENCES Obchod (obchodID) ON DELETE CASCADE;

ALTER TABLE Subkategorie DROP CONSTRAINT podtypTypu;
ALTER TABLE Subkategorie
  ADD CONSTRAINT podtypTypu FOREIGN KEY (kategorieID) REFERENCES Kategorie (kategorieID) ON DELETE CASCADE;

ALTER TABLE Zamestnanec DROP CONSTRAINT zamestnanecBydliste;
ALTER TABLE Zamestnanec
  ADD CONSTRAINT zamestnanecBydliste FOREIGN KEY (adresaID) REFERENCES Adresa (adresaID) ON DELETE CASCADE;
