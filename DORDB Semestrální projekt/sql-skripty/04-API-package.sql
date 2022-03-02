-- balík logiky
CREATE OR REPLACE PACKAGE kavarny_api AS
	PROCEDURE zmena_odd(zam IN NUMBER, old_odd IN NUMBER, new_odd IN NUMBER);
	PROCEDURE novy_manager(zam IN NUMBER, ou IN NUMBER);
END kavarny_api;
/


CREATE OR REPLACE PACKAGE BODY kavarny_api AS

	-- změna oddělení
	PROCEDURE zmena_odd(zam IN NUMBER, old_odd IN NUMBER, new_odd IN NUMBER) AS
	BEGIN
	
		-- ukončení současného poměru ve starém oddělení ke včerejšku
		UPDATE PracovniPomer SET do = (SELECT current_date - 1 FROM DUAL)
		WHERE zamestnanecID = zam
		AND pracovniPomerID IN (SELECT pracovniPomerID FROM PomerOddeleni WHERE oddeleniID = old_odd);
		
		-- nastavení nového poměru ode dneška
		INSERT INTO PracovniPomer (do, od, zamestnanecID, uvazek)
		VALUES (NULL, to_date( (SELECT current_date FROM DUAL), 'YYYY-MM-DD'), zam,
      	( SELECT uvazek FROM PracovniPomer WHERE (do = to_date( (SELECT current_date - 1 FROM DUAL), 'YYYY-MM-DD') AND zamestnanecID = zam) )
    	);
		
		INSERT INTO pomerOddeleni (oddeleniID, pracovniPomerID)
		VALUES (new_odd, (SELECT pracovniPomerID FROM PracovniPomer
			WHERE zamestnanecID = zam AND do IS NULL AND od = to_date( (SELECT current_date FROM DUAL), 'YYYY-MM-DD')
		) );
	
	END zmena_odd;

-- -- -- -- --

	PROCEDURE novy_manager(zam IN NUMBER, ou IN NUMBER) AS
	
	akt_uvazek NUMBER;
	
	BEGIN

    -- celkový úvazek - přepočítání
    UPDATE Zamestnanec
		SET celk_uvazek = ( SELECT SUM(uvazek)
                          FROM PracovniPomer
                          WHERE PracovniPomer.zamestnanecID = zam
                            AND ( do IS NULL
                              OR (
                                  od <= (SELECT current_date FROM DUAL) AND do >= (SELECT current_date FROM DUAL)
                                )
                            )
                      )
    WHERE Zamestnanec.zamestnanecID = zam ;
    
    SELECT celk_uvazek INTO akt_uvazek FROM Zamestnanec WHERE zamestnanecID = zam;
			
		-- přerozdělení úvazku, pokud překračuje 20 hodin
		IF (akt_uvazek > 20) THEN
      
      -- smyčka přes poměry
      FOR pom IN (SELECT pracovniPomerID FROM PracovniPomer
      WHERE (zamestnanecID = zam) AND ( (do IS NULL) OR ( do >= (SELECT current_date FROM DUAL) ) ) )
      LOOP
      
        -- ukončení stávajících poměrů
        UPDATE PracovniPomer SET do = (SELECT current_date - 1 FROM DUAL)
        WHERE pracovnipomerid = pom.pracovnipomerID;
        
        -- vytvoření nových poměrů s rozpočítáním úvazku
        INSERT INTO PracovniPomer (zamestnanecID, uvazek, od, do)
				VALUES
				(zam,
				(SELECT TRUNC( (SELECT TRUNC(uvazek/40*20) FROM PracovniPomer WHERE pracovniPomerID = pom.pracovnipomerid) ) FROM DUAL),
				to_date( (SELECT current_date FROM DUAL), 'YYYY-MM-DD'),
				(SELECT do FROM PracovniPomer WHERE pracovniPomerID = pom.pracovnipomerid)
				);
      
      END LOOP;
		
		END IF;
		
		-- zapsání do pozice manažera
		UPDATE OrgJednotka SET zamestnanecID = zam WHERE orgJednotkaID = ou;
	
	END novy_manager;


END kavarny_api;
/

