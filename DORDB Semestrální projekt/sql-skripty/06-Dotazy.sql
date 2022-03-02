SET AUTOTRACE ON
SPOOL /tmp/06-Dotazy.txt

-- tři nejstarší zaměstnanci
SELECT * FROM (
  SELECT Zamestnanec.prijmeni, Zamestnanec.jmeno, PracovniPomer.od, Oddeleni.nazev AS vOddeleni

  FROM PracovniPomer
  LEFT JOIN Zamestnanec ON (Zamestnanec.zamestnanecID = PracovniPomer.zamestnanecID)
  LEFT JOIN pomerOddeleni ON (PracovniPomer.pracovniPomerID = pomerOddeleni.pracovniPomerID)
  LEFT JOIN Oddeleni ON (oddeleni.oddeleniID = pomerOddeleni.oddeleniID)

ORDER BY PracovniPomer.od ASC

) WHERE ROWNUM <= 3;

-- letištní sortiment
SELECT Kategorie.nazev AS hlKategorie, Produkt.nazev

FROM Lokalita whLokalita, Produkt

INNER JOIN Kategorie ON (Produkt.kategorieID = Kategorie.kategorieID)
INNER JOIN produktVObchode ON (Produkt.produktID = produktVObchode.produktID)
INNER JOIN Obchod ON (produktVObchode.obchodID = Obchod.obchodID)
INNER JOIN Lokalita ON (Obchod.lokalitaID = Lokalita.lokalitaID)

WHERE whLokalita.typ LIKE '%airport%'

GROUP BY Kategorie.nazev, Produkt.nazev;

-- adresy všech čajoven
SELECT Obchod.nazev AS prodejna, Adresa.ulice, Adresa.psc, Adresa.mesto, Zeme.nazev AS stat

FROM Obchod
LEFT JOIN Lokalita ON (Obchod.lokalitaID = Lokalita.lokalitaID)
LEFT JOIN Adresa ON (Lokalita.adresaID = Adresa.adresaID)
LEFT JOIN Zeme ON (Zeme.zemeID = Adresa.zemeID)

WHERE Lokalita.typ LIKE '%tea room%'

GROUP BY Obchod.nazev, Adresa.ulice, Adresa.psc, Adresa.mesto, Zeme.nazev

ORDER BY prodejna ASC;


-- bývalí zaměstnanci
SELECT Zamestnanec.Jmeno, Zamestnanec.Prijmeni, Adresa.ulice, Adresa.psc, Adresa.mesto, Zeme.nazev AS stat

FROM Zamestnanec
LEFT JOIN Adresa ON (Adresa.adresaID = Zamestnanec.AdresaID)
LEFT JOIN Zeme ON (Zeme.zemeID = Adresa.zemeID)

WHERE zamestnanecID IN (
	SELECT DISTINCT zamestnanecID FROM PracovniPomer WHERE (do IS NOT NULL AND do < (SELECT current_date FROM DUAL) )
)
AND
zamestnanecID NOT IN (
	SELECT zamestnanecID FROM OrgJednotka	
);

-- Manažeři všech organizačních jednotek
SELECT OrgJednotka.id, OrgJednotka.nazev AS nazevOU, Zeme.nazev AS zakladna, CONCAT( CONCAT( Zamestnanec.jmeno, ' ') , Zamestnanec.prijmeni ) AS manazer

FROM OrgJednotka
LEFT JOIN Adresa ON (Adresa.adresaID = OrgJednotka.adresaID)
LEFT JOIN Zeme ON (Zeme.zemeID = Adresa.zemeID)
LEFT JOIN Zamestnanec ON (Zamestnanec.zamestnanecID = OrgJednotka.zamestnanecID);


SPOOL OF
SET AUTOTRACE OFF