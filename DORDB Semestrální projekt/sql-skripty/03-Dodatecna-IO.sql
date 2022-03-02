-- Organizační jednotka
-- Tabulka OrgJednotka

-- Organizační jednotka je v každé zemi jen jedna

CREATE OR REPLACE PROCEDURE kontrola_COU (adresa_id IN NUMBER) AS

nova_zeme NUMBER;
pocet_OU NUMBER;

BEGIN

	-- kde se chce nacházet nová jednotka?
	SELECT zemeID INTO nova_zeme FROM Adresa WHERE adresaID = adresa_id;
	
	-- kolik OU je už v dané zemi?
	SELECT COUNT(orgJednotkaID) INTO pocet_OU FROM OrgJednotka WHERE adresaID IN (SELECT adresaID FROM Adresa WHERE zemeID = nova_zeme);

	IF (pocet_OU > 0) THEN
		RAISE_APPLICATION_ERROR(-20000, 'Organizační jednotka v dané zemi již existuje.');
	END IF;

END;
/

-- trigger OrgJednotka
CREATE OR REPLACE TRIGGER tr_addOU
BEFORE INSERT
ON OrgJednotka
FOR EACH ROW BEGIN
	kontrola_COU(:NEW.adresaID);
END;
/

-- ---------------------------------------------------------------------------------------

-- Obchod
-- Tabulka Obchod

-- Všechny obchody spadají pod hlavní organizační jednotku příslušnou dané zemi,
-- kde se obchod nachází.

CREATE OR REPLACE PROCEDURE kontrola_ObchOU (lokalita_id IN NUMBER, odd_id IN NUMBER) AS

nova_zeme NUMBER;
ou_zeme NUMBER;

BEGIN

	-- Země adresy lokality
	SELECT zemeID INTO nova_zeme FROM Adresa WHERE adresaID IN
		(SELECT adresaID FROM Lokalita WHERE lokalitaID = lokalita_id);

	-- Země OU
	SELECT zemeID INTO ou_zeme FROM Adresa WHERE adresaID IN
		(SELECT AdresaID FROM OrgJednotka WHERE OrgJednotkaID IN
			(SELECT orgJednotkaID FROM Oddeleni WHERE oddeleniID = odd_id)
		);
	
	IF (nova_zeme <> ou_zeme) THEN
		RAISE_APPLICATION_ERROR(-20001, 'Obchod nemůže spadat pod tuto organizační jednotku.');
	END IF;

END;
/

-- trigger Obchod
CREATE OR REPLACE TRIGGER tr_addObchod
BEFORE INSERT OR UPDATE
ON Obchod
FOR EACH ROW BEGIN
	kontrola_ObchOU(:NEW.lokalitaID, :NEW.obchodID);
END;
/

-- ---------------------------------------------------------------------------------------

-- Celkový úvazek každého zaměstnance v každém z překrývajících se období nesmí
-- překročit 40 hodin týdně

CREATE OR REPLACE PROCEDURE kontrola_uvazek (zamestnanec_id IN NUMBER) AS

calc_uvazek NUMBER;

BEGIN

	SELECT SUM(uvazek) INTO calc_uvazek FROM PracovniPomer
						WHERE zamestnanecID = zamestnanec_id
							AND ( do IS NULL
									OR (od <= (SELECT current_date FROM DUAL)
									AND do >= (SELECT current_date FROM DUAL))
								);
						
	IF (calc_uvazek > 40) THEN
		RAISE_APPLICATION_ERROR(-20002, 'Překročen maximální úvazek zaměstnance.');
	END IF;

END;
/

-- trigger pracovniPomer
CREATE OR REPLACE TRIGGER tr_kontrolaPomer
BEFORE INSERT
ON PracovniPomer
FOR EACH ROW BEGIN
	kontrola_uvazek(:NEW.zamestnanecID);
END;
/

-- ---------------------------------------------------------------------------------------

-- Pracovník, který je manažerem některé z organizačních jednotek, nesmí mít v aktuálním
-- období celkový úvazek na jiných pracovních pozicích vyšší než 20 hodin týdně

CREATE OR REPLACE PROCEDURE kontrola_manazer (zamestnanec_id IN NUMBER) AS

calc_uvazek NUMBER;

BEGIN

	SELECT SUM(uvazek) INTO calc_uvazek FROM PracovniPomer
						WHERE zamestnanecID = zamestnanec_id
							AND ( do IS NULL
									OR (od <= (SELECT current_date FROM DUAL)
									AND do >= (SELECT current_date FROM DUAL))
						);

	IF (calc_uvazek > 20) THEN
		RAISE_APPLICATION_ERROR(-20003, 'Překročen maximální úvazek pro manažera.');
	END IF;

END;
/

-- trigger OrgJednotka
CREATE OR REPLACE TRIGGER tr_kontrola_manazerOU
BEFORE INSERT OR UPDATE
ON OrgJednotka
FOR EACH ROW BEGIN
	kontrola_manazer(:NEW.zamestnanecID);
END;
/

-- ---------------------------------------------------------------------------------------