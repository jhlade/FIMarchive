-- testy balíku API

SET AUTOTRACE ON
SPOOL /tmp/05-API-verify.txt

-- 1) změna oddělení zaměstnance

-- vytvoření pokusného zaměstnance
INSERT INTO Zamestnanec (zamestnanecID, jmeno, prijmeni, adresaID) VALUES (10001, 'Franta', 'Novák TESTAPI1', 1);

-- vytvoření pracovního poměru 1
INSERT INTO PracovniPomer (do,od,uvazek,pozice,zamestnanecID)
VALUES (
to_date('2030-12-30', 'YYYY-MM-DD'),
to_date('2000-01-01', 'YYYY-MM-DD'),
35, 'Tester API1',
(SELECT zamestnanecID FROM Zamestnanec WHERE prijmeni LIKE '%TESTAPI1')
);

-- vytvoření pracovního poměru 2
INSERT INTO PracovniPomer (do,od,uvazek,pozice,zamestnanecID)
VALUES (
to_date('2050-07-07', 'YYYY-MM-DD'),
to_date('2010-06-06', 'YYYY-MM-DD'),
5, 'Loskuták API1',
(SELECT zamestnanecID FROM Zamestnanec WHERE prijmeni LIKE '%TESTAPI1')
);

-- přiřazení pracovního poměru 1 k prvnímu oddělení
INSERT INTO pomerOddeleni (oddeleniID, pracovniPomerID)
VALUES (1,
(SELECT pracovniPomerID FROM PracovniPomer WHERE pozice LIKE 'Tester API1')
);

-- přiřazení pracovního poměru 2 k šestému oddělení
INSERT INTO pomerOddeleni (oddeleniID, pracovniPomerID)
VALUES (6,
(SELECT pracovniPomerID FROM PracovniPomer WHERE pozice LIKE 'Loskuták API1')
);

-- ověření původního stavu
SELECT Zamestnanec.jmeno, Zamestnanec.prijmeni, PracovniPomer.uvazek, PracovniPomer.od, PracovniPomer.do, Oddeleni.nazev AS predchoziODD
FROM Zamestnanec
LEFT JOIN PracovniPomer ON (PracovniPomer.zamestnanecID = Zamestnanec.zamestnanecID)
LEFT JOIN PomerOddeleni ON (PomerOddeleni.pracovniPomerID = PracovniPomer.pracovniPomerID)
LEFT JOIN Oddeleni ON (Oddeleni.oddeleniID = PomerOddeleni.oddeleniID)
WHERE Zamestnanec.prijmeni LIKE '%API1';

-- samotná změna oddělení uživatele 10001 z 1 do 2
BEGIN
	kavarny_api.zmena_odd(10001, 1, 2);
END;
/

-- ověření nového stavu
SELECT Zamestnanec.jmeno, Zamestnanec.prijmeni, PracovniPomer.uvazek, PracovniPomer.od, PracovniPomer.do, Oddeleni.nazev AS noveODD
FROM Zamestnanec
LEFT JOIN PracovniPomer ON (PracovniPomer.zamestnanecID = Zamestnanec.zamestnanecID)
LEFT JOIN PomerOddeleni ON (PomerOddeleni.pracovniPomerID = PracovniPomer.pracovniPomerID)
LEFT JOIN Oddeleni ON (Oddeleni.oddeleniID = PomerOddeleni.oddeleniID)
WHERE Zamestnanec.prijmeni LIKE '%API1';

-- opětovné odstranění pokusného zaměstnance
DELETE FROM PomerOddeleni WHERE pracovniPomerID IN (
SELECT pracovniPomerID FROM PracovniPomer WHERE zamestnanecID IN (
SELECT zamestnanecID FROM Zamestnanec WHERE prijmeni LIKE '%API1'));

DELETE FROM PracovniPomer WHERE zamestnanecID IN (SELECT zamestnanecID FROM Zamestnanec WHERE prijmeni LIKE '%API1');

DELETE FROM Zamestnanec WHERE prijmeni LIKE '%API1';

------------------------------------------------------------------------------------------

-- 2) vyvolání manažera

-- vytvoření pokusného zaměstnance
INSERT INTO Zamestnanec (zamestnanecID, jmeno, prijmeni, adresaID) VALUES (10002, 'Pepa', 'Novák TESTAPI2', 1);

-- vytvoření pracovního poměru 1 - úvazek 35
INSERT INTO PracovniPomer (do,od,uvazek,pozice,zamestnanecID)
VALUES (
to_date('2030-12-30', 'YYYY-MM-DD'),
to_date('2000-01-01', 'YYYY-MM-DD'),
35, 'Tester API2',
(SELECT zamestnanecID FROM Zamestnanec WHERE prijmeni LIKE '%TESTAPI2')
);

-- vytvoření pracovního poměru 2 - úvazek 5
INSERT INTO PracovniPomer (do,od,uvazek,pozice,zamestnanecID)
VALUES (
to_date('2050-07-07', 'YYYY-MM-DD'),
to_date('2010-06-06', 'YYYY-MM-DD'),
5, 'Loskuták API2',
(SELECT zamestnanecID FROM Zamestnanec WHERE prijmeni LIKE '%TESTAPI2')
);

-- přiřazení pracovního poměru 1 k prvnímu oddělení
INSERT INTO pomerOddeleni (oddeleniID, pracovniPomerID)
VALUES (1,
(SELECT pracovniPomerID FROM PracovniPomer WHERE pozice LIKE 'Tester API2')
);

-- přiřazení pracovního poměru 2 k šestému oddělení
INSERT INTO pomerOddeleni (oddeleniID, pracovniPomerID)
VALUES (6,
(SELECT pracovniPomerID FROM PracovniPomer WHERE pozice LIKE 'Loskuták API2')
);

-- celkový úvazek 35+5 = 40, manažer může mít jen 20

-- 75 - In Tempus Eu Incorporated, manažer 293 (Stella Young)
SELECT CONCAT(CONCAT(Zamestnanec.jmeno, ' '), Zamestnanec.prijmeni) AS manazer, OrgJednotka.orgJednotkaID, OrgJednotka.nazev
FROM OrgJednotka
LEFT JOIN Zamestnanec ON (OrgJednotka.zamestnanecID = Zamestnanec.zamestnanecID)
WHERE OrgJednotka.orgJednotkaID = 75;

-- vyrobení manažera
BEGIN
	kavarny_api.novy_manager(10002, 75);
END;
/

-- ověření
SELECT CONCAT(CONCAT(Zamestnanec.jmeno, ' '), Zamestnanec.prijmeni) AS manazer, OrgJednotka.orgJednotkaID, OrgJednotka.nazev
FROM OrgJednotka
LEFT JOIN Zamestnanec ON (OrgJednotka.zamestnanecID = Zamestnanec.zamestnanecID)
WHERE OrgJednotka.orgJednotkaID = 75;


-- vrácení zpátky
BEGIN
	kavarny_api.novy_manager(293, 75);
END;
/

-- opětovné odstranění pokusného zaměstnance
DELETE FROM PomerOddeleni WHERE pracovniPomerID IN (
SELECT pracovniPomerID FROM PracovniPomer WHERE zamestnanecID IN (
SELECT zamestnanecID FROM Zamestnanec WHERE prijmeni LIKE '%API2'));

DELETE FROM PracovniPomer WHERE zamestnanecID IN (SELECT zamestnanecID FROM Zamestnanec WHERE prijmeni LIKE '%API2');

DELETE FROM Zamestnanec WHERE prijmeni LIKE '%API2';


SET AUTOTRACE OFF