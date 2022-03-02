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

import java.io.Serializable;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Třída reprezentující jeden samostatný Digitální Učební Materiál.
 * 
 * @author Jan Hladěna
 */
public class DLM implements Serializable {

	/** Generované UID */
	private static final long serialVersionUID = -3180837917474725894L;
	
	/** Identifikátor materiálu. */
	private String identifier;
	/** Název materiálu. */
	private String name;
	/** Autor materiálu. */
	private String author;
	/** URL materiálu. */
	private String url;
	/** Anotace materiálu. */
	private String annotation;
	/** Předmět. */
	private String subject;
	/** Datum odpilotování. */
	private Date date;
	/** Ročník. */
	private int grade;
	
	/**
	 * Konstruktor
	 */
	public DLM() {
		
	}
	

	/**
	 * Konstruktor s parametry.
	 * 
	 * @param identifier Identifikátor
	 * @param name Název materiálu
	 * @param author Autor materiálu
	 * @param url URL
	 * @param annotation Anotace
	 * @param subject Předmět
	 * @param date Datum odpilotování
	 * @param grade Třída
	 */
	public DLM(String identifier, String name, String author, String url,
			String annotation, String subject, Date date, int grade) {
		super();
		this.identifier = identifier;
		this.name = name;
		this.author = author;
		this.url = url;
		this.annotation = annotation;
		this.subject = subject;
		this.date = date;
		this.grade = grade;
	}



	/**
	 * @return the identifier
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * @param identifier the identifier to set
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the author
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * @param author the author to set
	 */
	public void setAuthor(String author) {
		this.author = author;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the annotation
	 */
	public String getAnnotation() {
		return annotation;
	}

	/**
	 * @param annotation the annotation to set
	 */
	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}

	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject.toUpperCase();
	}

	/**
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject.substring(0, 4).toUpperCase();
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}
	
	/**
	 * Vrací datum v textovém formátu DD-MM-RRRR
	 * 
	 * @return datum
	 */
	public String getDateString() {
		String dateString = new SimpleDateFormat("dd.MM.yyyy").format(this.date);
		return dateString;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}
	
	/**
	 * Nastavení data pomocí řetězce RRRR-MM-DD
	 * 
	 * @param date Datum ve formátu RRRR-MM-DD
	 * @throws ParseException Výjimka při zpracování data
	 */
	public void setDate(String date) throws ParseException {
		this.date = new SimpleDateFormat("dd.MM.yyyy").parse(date);
	}

	/**
	 * @return the grade
	 */
	public int getGrade() {
		return grade;
	}

	/**
	 * @param grade the grade to set
	 */
	public void setGrade(int grade) {
		this.grade = grade;
	}
	

}
