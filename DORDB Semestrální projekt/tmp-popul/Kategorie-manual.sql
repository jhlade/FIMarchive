-- hlavní kategorie
INSERT INTO Kategorie (nazev) VALUES ('Nápoje');
INSERT INTO Kategorie (nazev) VALUES ('Jídlo');
INSERT INTO Kategorie (nazev) VALUES ('Hračky');
INSERT INTO Kategorie (nazev) VALUES ('Vstupenky');

-- podkategorie
INSERT INTO Subkategorie (nazev, kategorieID) VALUES ('Káva', 1);
INSERT INTO Subkategorie (nazev, kategorieID) VALUES ('Čaj', 1);
INSERT INTO Subkategorie (nazev, kategorieID) VALUES ('Vína', 1);
INSERT INTO Subkategorie (nazev, kategorieID) VALUES ('Piva', 1);

INSERT INTO Subkategorie (nazev, kategorieID) VALUES ('Zákusky', 2);
INSERT INTO Subkategorie (nazev, kategorieID) VALUES ('Pizza', 2);
INSERT INTO Subkategorie (nazev, kategorieID) VALUES ('Maso', 2);

INSERT INTO Subkategorie (nazev, kategorieID) VALUES ('Plyšáci', 3);
INSERT INTO Subkategorie (nazev, kategorieID) VALUES ('Dřevěné hračky', 3);
INSERT INTO Subkategorie (nazev, kategorieID) VALUES ('Puzzle', 3);

INSERT INTO Subkategorie (nazev, kategorieID) VALUES ('Koncerty', 4);
INSERT INTO Subkategorie (nazev, kategorieID) VALUES ('Divadla', 4);
INSERT INTO Subkategorie (nazev, kategorieID) VALUES ('Kina', 4);
INSERT INTO Subkategorie (nazev, kategorieID) VALUES ('Recitály', 4);