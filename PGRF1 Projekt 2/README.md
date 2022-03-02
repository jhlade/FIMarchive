## PGRF1 Úkol 2

Vyplňování oblastí

Aplikace demonstruje algoritmy pro vyplňování rastrových oblastí. Všechny algoritmy
jsou tvořeny samostatně použitelnými funkcemi se stejným rozhraním, které definuje
jejich wrapper. Implementován je flood fill algoritmus pomocí jednoduché rekurze
a jeho optimalizovaná podoba pomocí fronty. Z něho vychází implementace scan-line
algoritmu, která v jediném průchodě skenuje hranice polygonu a okamžitě provádí
vyplňování po segmentech.

Všechny použité algoritmy pracují pouze ve čtyřech směrech.

Aplikace Grafika2 vychází s ovládáním a podobou z přechozího úkolu. Byla zachována
funkce pro kreslení vodorovných čar pomocí primárního tlačítka myši, kliknutím
na sekundární tlačítko myši se vyvolá vyplňování oblasto umístěné pod kurzorem
pomocí algoritmu zvoleného pomocí přepínačů v pravé části aplikace.

Aplikace nepoužívá vlákna, prvotní inicializace plátna je prováděna kliknutím do volné
plochy nebo pomocí tlačítka Vyčistit.

- - -
Jazyk Java, kódování UTF-8, konce řádků UNIX,
vývojové prostředí Eclipse pro Java 1.7, testováno na Oracle JRE 1.8.0_20-ea-b10.

Soubor se zdrojovým kódem Grafika2.java je digitálně podepsán pomocí PGP
v ./Grafika2.java.sig.

http://lide.uhk.cz/fim/student/hladeja1/E607EB8A.txt
