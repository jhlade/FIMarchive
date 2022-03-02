/**
 * (c) 2013-2014 Jan Hladěna
 * jan.hladena@uhk.cz
 * PRO2 K-AI FIM UHK
 * 
 * Digitální Učební Materiály
 * EU Peníze školám, ZŠ Pardubice - Studánka (CZ.1.07/1.4.00/21.2146)
 * 
 * http://www.zs-studanka.cz/dum.html
 * 
 * (c) 2011 Jan Hladěna
 * jan.hladena@zs-studanka.cz
 * 
 */
package cz.uhk.pro2.dat;

/**
 * Konstantní řetězce pro ComboBoxy - výběry.
 * 
 * @author Jan Hladěna
 *
 */
public interface IConstantData {
	
	/** Ročníky */
	static final String[] GRADES = {"Všechny", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
	
	/** Předměny */
	static final String[] SUBJECTS = {
		"Všechny", //
		"ANGL", // Anglický jazyk
		"CESK", // Český jazyk
		"CHEM", // Chemie
		"CLOV", // Člověk a jeho svět
		"DEJE", // Dějepis
		"FYZI", // Fyzika
		"HUDE", // Hudební výchova
		"INFO", // Informatika
		"MATE", // Matematika
		"NEME", // Německý jazyk
		"POCI", // Počítačová grafika
		"PRIR", // Přírodopis
		"RUSK", // Ruský jazyk
		"SEMI", // Seminář z ekonomie
		"SVET", // Svět práce
		"VYCH", // Výchova k občanství
		"VYTV", // Výtvarná výchova
		"ZEME"  // Zeměpis
	};
	
}
