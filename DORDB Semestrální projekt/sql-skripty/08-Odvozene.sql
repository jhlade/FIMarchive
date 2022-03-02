-- smazání
ALTER TABLE Zamestnanec DROP COLUMN celk_uvazek;
-- přidání sloupce
ALTER TABLE Zamestnanec ADD celk_uvazek NUMBER(2) DEFAULT 0;

-- okamžité spočítání - pouze aktuální poměry
BEGIN

	FOR zam IN (SELECT zamestnanecID FROM Zamestnanec)
	LOOP
	
		UPDATE Zamestnanec
		SET celk_uvazek = ( SELECT SUM(uvazek)
                          FROM PracovniPomer
                          WHERE PracovniPomer.zamestnanecID = zam.zamestnanecID
                            AND ( do IS NULL
                              OR (
                                  od <= (SELECT current_date FROM DUAL) AND do >= (SELECT current_date FROM DUAL)
                                )
                            )
                      )
    WHERE Zamestnanec.zamestnanecID = zam.zamestnanecID ;
						
	END LOOP;
	
END;
/


-- trigger - přepočítá jeden počet
CREATE OR REPLACE TRIGGER tr_pomerIn
BEFORE INSERT
ON PomerOddeleni
FOR EACH ROW
BEGIN

	UPDATE Zamestnanec
		SET celk_uvazek = ( SELECT (SUM(uvazek) + :NEW.uvazek)
						FROM PracovniPomer
						WHERE zamestnanecID = :NEW.zamestnanecID
							AND ( do IS NULL
									OR (od <= (SELECT current_date FROM DUAL)
									AND do >= (SELECT current_date FROM DUAL))
								)
						) WHERE zamestnanecID = :NEW.zamestnanecID;

END;
/

CREATE OR REPLACE TRIGGER tr_pomerUp
AFTER UPDATE
ON PomerOddeleni
FOR EACH ROW
BEGIN

	UPDATE Zamestnanec
		SET celk_uvazek = ( SELECT (SUM(uvazek) - :NEW.uvazek)
						FROM PracovniPomer
						WHERE zamestnanecID = :NEW.zamestnanecID
							AND pracovniPomerID <> :NEW.pracovniPomerID
							AND ( do IS NULL
									OR (od <= (SELECT current_date FROM DUAL)
									AND do >= (SELECT current_date FROM DUAL))
								)
						) WHERE zamestnanecID = :NEW.zamestnanecID;

END;
/

----

CREATE OR REPLACE TRIGGER tr_pomerOdd
BEFORE INSERT OR UPDATE
ON PomerOddeleni
BEGIN
	UPDATE Zamestnanec
		SET celk_uvazek = ( SELECT SUM(uvazek)
						FROM PracovniPomer
						WHERE zamestnanecID IN (
												SELECT zamestnanecID FROM PracovniPomer
														WHERE pracovniPomerID IN (SELECT pracovniPomerID FROM PomerOddeleni WHERE oddeleniID = :NEW.oddeleniID)
												)
							AND ( do IS NULL
									OR (od <= (SELECT current_date FROM DUAL)
									AND do >= (SELECT current_date FROM DUAL))
								)
		WHERE zamestnanecID IN (SELECT zamestnanecID FROM PracovniPomer
		WHERE pracovniPomerID IN (SELECT pracovniPomerID FROM PomerOddeleni WHERE oddeleniID = :NEW.oddeleniID));
END;
/