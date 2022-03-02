## PGRF1 Úkol 1

Úsečky a kružnice

Aplikace demonstruje různé algoritmy pro kreslení čáry. Implementovány jsou
triviální, DDA a Bresenhamův. Jednotlivé algoritmy je možné za běhu měnit
pomocí přepínačů v panelu na pravé straně okna. Tento panel dále obsahuje
navíc přepínač pro referenční režim kreslení pomocí metod java.awt.Graphics,
zaškrtávací políčko pro aktivaci nebo deaktivaci zobrazování probíhajícího tahu
a tlačítko pro smazání celé kreslící plochy.

Inicializace kreslící plochy je provedena po prvním kliknutí do prostoru nebo
vyvoláním vyčištění kreslící plochy pomocí tlačítka. Kreslení čar se provádí
stisknutím primárního tlačítka myši a jejím následným tažením. Je-li povoleno
zobrazování tahu, jsou šedou barvou kresleny průběžně počítané čáry.
Po uvolnění tlačítka myši dojde k nakreslení čáry černou barvou.

Obdobným postupem jsou za užití sekundárního tlačítka myši kresleny kružnice.
Algoritmus kreslení kružnice je nezávisle na nastavení režimu na pravém panelu
vždy Bresenhamův, vyjímkou je aktivace referenčního kreslení, kdy jsou
i kružnice kresleny pomocí java.awt.Graphics.

Celá aplikace je koncipována jako jednotřídní s důrazem na jednoduchost
a čitelnost. Během interakce generuje spoustu užitečného ladícího výstupu
do stdout.


- - -
Jazyk Java, kódování UTF-8, konce řádků UNIX,
vývojové prostředí Eclipse pro Java 1.7, testováno na Oracle JRE 1.8.0-b129.

Soubor se zdrojovým kódem Grafika1.java je digitálně podepsán pomocí PGP
v ./Grafika1.java.sig.

http://lide.uhk.cz/fim/student/hladeja1/E607EB8A.txt
