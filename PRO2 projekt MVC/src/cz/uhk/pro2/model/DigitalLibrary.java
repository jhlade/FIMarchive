/**
 * Copyright (c) 2013, Jan Hladěna
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */
package cz.uhk.pro2.model;

import cz.uhk.pro2.model.DLM;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Digitální knihovna. Jedná se o hlavní seznam všech záznamů Digitálních Učebních Materiálů. Třída
 * poskytuje základní operace pro manipulaci s tímto seznamem. Binární formát dát je komprimován
 * gzipem, třída rovněž poskytuje podporu pro import a export dat ve formátu XML.
 * 
 * @author Jan Hladěna
 */
public class DigitalLibrary implements Serializable {

	
	/** Generované UID */
	private static final long serialVersionUID = 543695132899426097L;
	
	/** Interní archiv */
	private List<DLM> digitalLibrary = new ArrayList<>();

	/**
	 * Hlavní veřejné rozhraní / přístupový bod pro práci se záznamy.
	 * 
	 * @return Ukazatel na seznam DUM
	 */
	public List<DLM> getDLMs() {
		return digitalLibrary;
	}
	
	// TODO smazat
	public void testData() throws IOException {
		DLM petra = new DLM();
		petra.setAuthor("Ing. Petra Andrlová");
		petra.setGrade(9);
		petra.setSubject("SEEK");
		petra.setAnnotation("Tento materiál...");
		petra.setIdentifier("VY_INOVACE_SADA666_TEST1");
		petra.setName("Komunikace v EU");
		petra.setUrl("http://www.zs-studanka.cz/dum/9roc/seek/komunikace-v-eu.ppt");
		try {
			petra.setDate("21.12.2012");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		DLM paja = new DLM();
		paja.setAuthor("Mgr. Pavlína Marková");
		paja.setGrade(7);
		paja.setSubject("PRIR");
		paja.setAnnotation("Opakování učiva čmeláci");
		paja.setIdentifier("VY_INOVACE_SADA666_TEST2");
		paja.setName("Čmelí včelíci");
		paja.setUrl("http://www.zs-studanka.cz/dum/7roc/prir/opakovani-uciva-cmelaci.pptx");
		try {
			paja.setDate("23.06.2013");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		DLM jirka = new DLM();
		jirka.setAuthor("Mgr. Jiří Mandys");
		jirka.setGrade(8);
		jirka.setSubject("PCGR");
		jirka.setAnnotation("Opakování učiva grafika - inovační materiál na téma Památka zesnulých");
		jirka.setIdentifier("VY_INOVACE_SADA666_TEST3");
		jirka.setName("Grafika v pojetí");
		jirka.setUrl("http://www.zs-studanka.cz/dum/8roc/pcgr/opakovani-uciva-grafika-pamatka-zesnulych.pdf");
		try {
			jirka.setDate("02.11.2010");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		
		//this.addDLM(petra);
		//this.addDLM(paja);
		this.addDLM(jirka);
		
		//this.saveLibrary("demo.dat");
	}
	
	/**
	 * Odstraní všechny prvky seznamu.
	 */
	public void clearAll() {
		digitalLibrary.clear();
	}
	
	/**
	 * Metoda pro přidání záznamu - jednoho Digitálního Učebního Materiálu.
	 * 
	 * @param dlm
	 */
	public void addDLM(DLM dlm) {
		digitalLibrary.add(dlm);
	}
	
	/**
	 * Metoda pro odstranění záznamu na zadaném indexu.
	 * 
	 * @param index
	 */
	public void removeDLM(int index)
	{
		digitalLibrary.remove(index);
	}
	
	/**
	 * Načtení kompletních dat z komprimovaného binárního formátu. Otevřený soubor je dekomprimován
	 * a jeho obsah deserializován.
	 * 
	 * @param dataFile Cesta k datovému souboru
	 * @throws IOException Chyba při manipulaci se souborem
	 * @throws ClassNotFoundException  Chybný formát datového souboru
	 */
	@SuppressWarnings("unchecked")
	public void loadLibrary(String dataFile) throws IOException, ClassNotFoundException {

		// otevření souboru pro čtení
		FileInputStream fileInput = new FileInputStream(new File(dataFile));
		// dekomprese získaných dat
		GZIPInputStream gzipInput = new GZIPInputStream(fileInput);
		// schránka načtených dat
		ObjectInputStream input = new ObjectInputStream(gzipInput);
		
		// vytažení čistých objektů deserializačnictvím
		this.digitalLibrary = (List<DLM>) input.readObject();
		
		input.close();
		gzipInput.close();
		fileInput.close();
	}
	
	/**
	 * Uložení dat do komprimovaného binárního formátu. Objekt je serializován,
	 * zkomprimován a uložen do požadovaného souboru.
	 * 
	 * @param dataFile Cesta k cílovému souboru
	 * @throws IOException Chyba při manipulaci se souborem
	 */
	public void saveLibrary(String dataFile) throws IOException {
		
		// korekce přípony
		if (!dataFile.toLowerCase().endsWith(".dlm")) {
			dataFile += ".dlm";
		}
		
		// otevření souboru k zápisu
		FileOutputStream fileOutput = new FileOutputStream(new File(dataFile));
		// komprese dat gzipem
		GZIPOutputStream gzipOutput = new GZIPOutputStream(fileOutput);
		// serializační schránka pro objekty
		ObjectOutputStream output = new ObjectOutputStream(gzipOutput);
		
		// odeslání aktuálních dat do serializačníku
		output.writeObject(this.digitalLibrary);

		output.flush();		
		output.close();
		
		gzipOutput.close();
		fileOutput.close();
		
	}
	
	/**
	 * Načtení ukázkových dat
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	@SuppressWarnings("unchecked")
	public void loadDemo() throws IOException, ClassNotFoundException {

		InputStream resource = DigitalLibrary.class.getResourceAsStream("/cz/uhk/pro2/dat/demo.dlm");
		GZIPInputStream gzipInput = new GZIPInputStream(resource);
		ObjectInputStream input = new ObjectInputStream(gzipInput);

		this.digitalLibrary = (List<DLM>) input.readObject();
				
		input.close();
		gzipInput.close();
		resource.close();
	}
	
	/**
	 * Export dat do XML dokumentu.
	 * 
	 * @param xmlFile Název cílového souboru.
	 * @throws ParserConfigurationException 
	 * @throws TransformerFactoryConfigurationError 
	 * @throws TransformerException 
	 */
	public void exportXML(String xmlFile) throws ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException {
		
		// korekce přípony
		if (!xmlFile.toLowerCase().endsWith(".xml")) {
			xmlFile += ".xml";
		}
		
		DocumentBuilder builder = null;
		builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		
		Document archiveXML = builder.newDocument();
		Element root = archiveXML.createElement("archive");
		archiveXML.appendChild(root);
		
		for (DLM item : this.digitalLibrary)
		{
			Element dlm = archiveXML.createElement("dlm");
			
			// identifikátor
			Element identifier = archiveXML.createElement("identifier");
			identifier.setTextContent(item.getIdentifier());
			dlm.appendChild(identifier);

			// název
			Element name = archiveXML.createElement("name");
			name.setTextContent(item.getIdentifier());
			dlm.appendChild(name);
			
			// anotace
			Element annotation = archiveXML.createElement("annotation");
			annotation.setTextContent(item.getAnnotation());
			dlm.appendChild(annotation);
			
			// předmět
			Element subject = archiveXML.createElement("subject");
			subject.setTextContent(item.getSubject());
			dlm.appendChild(subject);
			
			// ročník
			Element grade = archiveXML.createElement("grade");
			grade.setTextContent(((Integer) item.getGrade()).toString());
			dlm.appendChild(grade);
			
			// autor
			Element author = archiveXML.createElement("author");
			author.setTextContent(item.getAuthor());
			dlm.appendChild(author);
			
			// datum
			Element date = archiveXML.createElement("date");
			date.setTextContent(item.getDateString());
			dlm.appendChild(date);
			
			// URL
			Element url = archiveXML.createElement("url");
			url.setTextContent(item.getUrl());
			dlm.appendChild(url);
			
			root.appendChild(dlm);
		}
		
		// uložení XML do souboru
		DOMSource source = new DOMSource(archiveXML);
		StreamResult result = new StreamResult(new File(xmlFile));

		// vytvoření transformátoru dat z továrny
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		// odsazování pro lepší čitelnost
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		// jeden odskok = 4 mezery
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		transformer.transform(source, result);
		
	}
	
	/**
	 * Import dat z XML dokumentu - v tom smyslu, že importovaná data jsou přidána
	 * ke stávajícímu seznamu a nenahrazují ta současná.
	 * 
	 * @param xmlFile Cesta k XML dokumentu.
	 */
	public void importXML(String xmlFile) {
		// TODO 
	}

	/**
	 * Konstruktor
	 */
	public DigitalLibrary() {
		// TODO smazat
		/*
		try {
			this.testData();
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
	}

}
