-- objektové schéma


-- Země
DROP TYPE T_Zeme FORCE;
CREATE OR REPLACE TYPE T_Zeme AS OBJECT
(
  iso_3166_1 char(3 CHAR),
  nazev varchar2(255),
  zemeID number(10)
);
/

-- Adresa
DROP TYPE T_Adresa FORCE;
CREATE OR REPLACE TYPE T_Adresa AS OBJECT
(
	psc number(5),
	kraj varchar2(255),
	mesto varchar2(255),
	ulice varchar2(255),
	adresaID number(10),
	zeme_ref REF T_Zeme
);
/

-- Zaměstnanec
DROP TYPE T_Zamestnanec FORCE;
CREATE OR REPLACE TYPE T_Zamestnanec AS OBJECT
(
  zamestnanecID number(10),
  jmeno varchar2(255),
  prijmeni varchar2(255),
  adresa_ref REF T_Adresa
);
/

-- Lokalita
DROP TYPE T_Lokalita FORCE;
CREATE OR REPLACE TYPE T_Lokalita AS OBJECT
(
  lokalitaID number(10),
  nazev varchar2(255),
  popis varchar2(255),
  typ varchar2(150),
  adresa_ref REF T_Adresa
);
/

-- Organizační jednotka
DROP TYPE T_OrgJednotka FORCE;
CREATE OR REPLACE TYPE T_OrgJednotka AS OBJECT
(
  id number(4),
  nazev varchar2(255),
  orgJednotkaID number(4),
  adresa_ref REF T_Adresa,
  zamestnanec_ref REF T_Zamestnanec
);
/

-- Oddělení
DROP TYPE T_Oddeleni FORCE;
CREATE OR REPLACE TYPE T_Oddeleni AS OBJECT
(
  nazev varchar2(255),
  oddeleniID number(10),
  orgJednotka_ref REF T_OrgJednotka
) ;
/

-- Pracovní poměry
DROP TYPE T_PracovniPomer FORCE;
CREATE OR REPLACE TYPE T_PracovniPomer AS OBJECT
(
  pracovniPomerID number(10),
  do date,
  od date,
  uvazek number(2),
  pozice varchar2(150),
  zamestnanec_ref REF T_Zamestnanec
) ;
/

-- Zam. poměr v oddělení
DROP TYPE T_pomerOddeleni FORCE;
CREATE OR REPLACE TYPE T_pomerOddeleni AS OBJECT
(
  oddeleni_ref REF T_Oddeleni,
  pracovniPomer_ref REF T_PracovniPomer
) ;
/

-- Obchod
DROP TYPE T_Obchod FORCE;
CREATE OR REPLACE TYPE T_Obchod AS OBJECT
(
  obchodID number(10),
  nazev varchar2(255),
  oddeleni_ref REF T_Oddeleni,
  lokalita_ref REF T_Lokalita
) ;
/

-- Kategorie
DROP TYPE T_Kategorie FORCE;
CREATE OR REPLACE TYPE T_Kategorie AS OBJECT
(
  kategorieID number(10),
  nazev varchar2(150)
)  ;
/

-- Subkategorie
DROP TYPE T_Subkategorie FORCE;
CREATE OR REPLACE TYPE T_Subkategorie AS OBJECT
(
  subkategorieID number(10),
  nazev varchar2(150),
  kategorie_ref REF T_Kategorie
)  ;
/

-- Produkt
DROP TYPE T_Produkt FORCE;
CREATE OR REPLACE TYPE T_Produkt AS OBJECT
(
  produktID number(10),
  cena number(8,2),
  id varchar2(48),
  nazev varchar2(255),
  kategorie_ref REF T_Kategorie
--  obchod_ref REF T_Obchod,
--  subkategorie_ref REF T_Subkategorie
)  ;
/

-- Produkt v obchodě
DROP TYPE T_produktVObchode FORCE;
CREATE OR REPLACE TYPE T_produktVObchode AS OBJECT
(
  produkt_ref REF T_Produkt,
  obchod_ref REF T_Obchod
) ;
/

-- Produkt podtypu
DROP TYPE T_produktPodtypu FORCE;
CREATE OR REPLACE TYPE T_produktPodtypu AS OBJECT
(
  subkategorie_ref REF T_Subkategorie,
  produkt_ref REF T_Produkt
) ;
/

-- Objektové tabulky
DROP TABLE OBJ_Zeme;
CREATE TABLE OBJ_Zeme OF T_Zeme;
DROP TABLE OBJ_Adresa;
CREATE TABLE OBJ_Adresa OF T_Adresa;
DROP TABLE OBJ_Zamestnanec;
CREATE TABLE OBJ_Zamestnanec OF T_Zamestnanec;
DROP TABLE OBJ_Lokalita;
CREATE TABLE OBJ_Lokalita OF T_Lokalita;
DROP TABLE OBJ_OrgJednotka;
CREATE TABLE OBJ_OrgJednotka OF T_OrgJednotka;
DROP TABLE OBJ_Oddeleni;
CREATE TABLE OBJ_Oddeleni OF T_Oddeleni;
DROP TABLE OBJ_PracovniPomer;
CREATE TABLE OBJ_PracovniPomer OF T_PracovniPomer;
DROP TABLE OBJ_pomerOddeleni;
CREATE TABLE OBJ_pomerOddeleni OF T_pomerOddeleni;
DROP TABLE OBJ_Obchod;
CREATE TABLE OBJ_Obchod OF T_Obchod;
DROP TABLE OBJ_Kategorie;
CREATE TABLE OBJ_Kategorie OF T_Kategorie;
DROP TABLE OBJ_Subkategorie;
CREATE TABLE OBJ_Subkategorie OF T_Subkategorie;
DROP TABLE OBJ_Produkt;
CREATE TABLE OBJ_Produkt OF T_Produkt;
DROP TABLE OBJ_produktVObchode;
CREATE TABLE OBJ_produktVObchode OF T_produktVObchode;
DROP TABLE OBJ_produktPodtypu;
CREATE TABLE OBJ_produktPodtypu OF T_produktPodtypu;


-- import dat

INSERT INTO OBJ_Zeme SELECT * FROM Zeme;

INSERT INTO OBJ_Adresa
SELECT a.psc, a.kraj, a.mesto, a.ulice, a.adresaID, (SELECT REF(objz) FROM OBJ_Zeme objz WHERE objz.zemeID = a.zemeID) as zeme_ref
FROM Adresa a;

INSERT INTO OBJ_Zamestnanec
SELECT z.zamestnanecID, z.jmeno, z.prijmeni, (SELECT REF(obja) FROM OBJ_Adresa obja WHERE obja.adresaID = z.adresaID) as adresa_ref
FROM Zamestnanec z;

INSERT INTO OBJ_Lokalita
SELECT l.lokalitaID, l.nazev, l.popis, l.typ, (SELECT REF(obja) FROM OBJ_Adresa obja WHERE obja.adresaID = l.adresaID) as adresa_ref
FROM Lokalita l;

INSERT INTO OBJ_OrgJednotka
SELECT o.id, o.nazev, o.orgJednotkaID, (SELECT REF(obja) FROM OBJ_Adresa obja WHERE obja.adresaID = o.adresaID) as adresa_ref,
  (SELECT REF(objz) FROM OBJ_Zamestnanec objz WHERE objz.zamestnanecID = o.zamestnanecID) as zamestnanec_ref
FROM OrgJednotka o;

INSERT INTO OBJ_Oddeleni
SELECT o.nazev, o.oddeleniID, (SELECT REF(objo) FROM OBJ_OrgJednotka objo WHERE objo.orgJednotkaID = o.orgJednotkaID) as orgJednotka_ref
FROM Oddeleni o;

INSERT INTO OBJ_PracovniPomer
SELECT p.pracovniPomerId, p.do, p.od, p.uvazek, p.pozice,
(SELECT REF(objz) FROM OBJ_Zamestnanec objz WHERE objz.zamestnanecID = p.zamestnanecID) AS zamestnanec_ref
FROM PracovniPomer p;

INSERT INTO OBJ_pomerOddeleni
SELECT (SELECT REF(objo) FROM OBJ_Oddeleni objo WHERE objo.oddeleniID = p.oddeleniID) AS oddeleni_ref,
(SELECT REF(objp) FROM OBJ_PracovniPomer objp WHERE objp.pracovniPomerID = p.pracovniPomerID) AS pracovnipomer_ref
FROM pomerOddeleni p;

INSERT INTO OBJ_Obchod
SELECT ob.obchodID, ob.nazev,
(SELECT REF(objo) FROM OBJ_Oddeleni objo WHERE objo.oddeleniID = ob.obchodID) AS oddeleni_ref,
(SELECT REF(objl) FROM OBJ_Lokalita objl WHERE objl.lokalitaID = ob.lokalitaID) AS lokalita_ref
FROM Obchod ob;

INSERT INTO OBJ_Kategorie
SELECT k.kategorieID, k.nazev
FROM Kategorie k;

INSERT INTO OBJ_Subkategorie
SELECT s.subkategorieID, s.nazev,
(SELECT REF(objk) FROM OBJ_Kategorie objk WHERE objk.kategorieID = s.kategorieID) AS kategorie_ref
FROM Subkategorie s;

INSERT INTO OBJ_Produkt
SELECT p.produktID, p.cena, p.id, p.nazev,
(SELECT REF(objk) FROM OBJ_Kategorie objk WHERE objk.kategorieID = p.kategorieID) AS kategorie_ref
FROM Produkt p;

INSERT INTO OBJ_produktVObchode
SELECT
(SELECT REF(objp) FROM OBJ_Produkt objp WHERE objp.produktID = v.produktID) AS produkt_ref,
(SELECT REF(objo) FROM OBJ_Obchod objo WHERE objo.obchodID = v.obchodID) AS obchod_ref
FROM produktVObchode v;

INSERT INTO OBJ_produktPodtypu
SELECT
(SELECT REF(objs) FROM OBJ_Subkategorie objs WHERE objs.subkategorieID = sp.subkategorieID) AS subkategorie_ref,
(SELECT REF(objp) FROM OBJ_Produkt objp WHERE objp.produktID = sp.produktID) AS produkt_ref
FROM produktPodtypu sp;