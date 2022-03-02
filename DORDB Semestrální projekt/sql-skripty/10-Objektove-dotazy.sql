SET AUTOTRACE ON
SPOOL /tmp/10-Objektove-dotazy.txt

-- (o) tři nejstarší zaměstnanci
SELECT * FROM (
  SELECT pp.zamestnanec_ref.prijmeni, pp.zamestnanec_ref.jmeno, pp.od,
  po.oddeleni_ref.nazev AS vOddeleni
  FROM OBJ_PracovniPomer pp, OBJ_pomerOddeleni po
  WHERE po.pracovnipomer_ref.pracovnipomerid = pp.pracovnipomerid
ORDER BY pp.od ASC

) WHERE ROWNUM <= 3;

-- (o) letištní sortiment
SELECT p.kategorie_ref.nazev AS hlKategorie, p.nazev
FROM OBJ_Lokalita l, OBJ_Produkt p
WHERE l.typ LIKE '%airport%'
GROUP BY p.kategorie_ref.nazev, p.nazev;

-- (o) adresy všech čajoven
SELECT o.nazev AS prodejna, o.lokalita_ref.adresa_ref.ulice, o.lokalita_ref.adresa_ref.psc, o.lokalita_ref.adresa_ref.mesto, o.lokalita_ref.adresa_ref.zeme_ref.nazev AS stat
FROM OBJ_Obchod o
WHERE o.lokalita_ref.typ LIKE '%tea room%'
GROUP BY o.nazev, o.lokalita_ref.adresa_ref.ulice, o.lokalita_ref.adresa_ref.psc, o.lokalita_ref.adresa_ref.mesto, o.lokalita_ref.adresa_ref.zeme_ref.nazev;


-- (o) bývalí zaměstnanci
SELECT z.jmeno, z.prijmeni, z.adresa_ref.ulice, z.adresa_ref.psc, z.adresa_ref.mesto, z.adresa_ref.zeme_ref.nazev AS stat
FROM OBJ_Zamestnanec z
WHERE
z.zamestnanecid IN (SELECT pp.zamestnanec_ref.zamestnanecid FROM OBJ_PracovniPomer pp WHERE (pp.do IS NOT NULL AND pp.do < (SELECT current_date FROM DUAL) ) )
AND
z.zamestnanecid NOT IN (SELECT ou.zamestnanec_ref.zamestnanecid FROM OBJ_OrgJednotka ou);

-- (o) Manažeři všech organizačních jednotek
SELECT
ou.id, ou.nazev AS nazevOU, ou.adresa_ref.zeme_ref.nazev AS zakladna, CONCAT( CONCAT(ou.zamestnanec_ref.jmeno, ''), ou.zamestnanec_ref.prijmeni) AS manazer 
FROM OBJ_OrgJednotka ou;


SPOOL OF
SET AUTOTRACE OFF