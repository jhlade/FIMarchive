-- Porušení integritních omezení

SPOOL ON ~/07-Poruseni-IO.txt

-- OrgJednotka - třímístné číslo

INSERT INTO OrgJednotka (id,nazev,adresaID,zamestnanecID) VALUES (250,'3M jednotka',1,1);

-- Pracovní poměr - 50 hodin

INSERT INTO PracovniPomer (do,od,uvazek,pozice,zamestnanecID)
VALUES (to_date('2018-01-01', 'YYYY-MM-DD'), to_date('2010-01-01', 'YYYY-MM-DD'), 50,'Přepracovník', 1);

-- Pracovní poměr - cesta časem

INSERT INTO PracovniPomer (do,od,uvazek,pozice,zamestnanecID)
VALUES (to_date('2010-01-01', 'YYYY-MM-DD'), to_date('2016-01-01', 'YYYY-MM-DD'), 5,'Pán času', 1);

-- Produkt - záporná cena

INSERT INTO Produkt (cena,id,nazev,kategorieID)
VALUES (-200,'6f1535ce-4d26-4c18-928e-1ede9afbf0a2','Sleva zdarma',4);

SPOOL OFF
