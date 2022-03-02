-- Triviální integritní omezení

-- Organizační jednotka
-- Tabulka OrgJednotka

-- ID org. jednotky musí být jedinečné
ALTER TABLE OrgJednotka
	ADD CONSTRAINT ojid_unique UNIQUE (id);
	
-- ID org. jednotky musí být kladné a čtyřmístné
ALTER TABLE OrgJednotka
	ADD CONSTRAINT ojid_pos CHECK (id >= 1000 AND id <= 9999);
	
-- Pracovní poměr
-- Tabulka PracovniPomer

-- úvazek musí být vždy kladný a menší než 40 hodin
ALTER TABLE PracovniPomer
	ADD CONSTRAINT pps_uvazek CHECK (uvazek >= 0 AND uvazek <= 40);
	
-- Každý pracovní poměr musí končit později, než začne
ALTER TABLE PracovniPomer
	ADD CONSTRAINT pps_dates CHECK ((do IS NULL) OR (do >= od));
	
-- Produkt
-- Tabulka Produkt

-- cena musí být kladná
ALTER TABLE Produkt
	ADD CONSTRAINT prod_value CHECK (cena > 0);
	
-- uuid produktu musí být jedinečné
ALTER TABLE Produkt
	ADD CONSTRAINT prod_id_unique UNIQUE (id);
	
	