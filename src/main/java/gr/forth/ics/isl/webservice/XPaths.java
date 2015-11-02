/*
 * Copyright 2015 Institute of Computer Science,
 * Foundation for Research and Technology - Hellas
 *
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations
 * under the Licence.
 *
 * Contact:  POBox 1385, Heraklio Crete, GR-700 13 GREECE
 * Tel:+30-2810-391632
 * Fax: +30-2810-391638
 * E-mail: isl@ics.forth.gr
 * http://www.ics.forth.gr/isl
 *
 * Authors :  Giannis Agathangelos, Georgios Samaritakis.
 *
 * This file is part of the SourceAnalyzer webapp.
 */
package gr.forth.ics.isl.webservice;

import java.util.LinkedHashSet;
import java.util.Map;

/**
 * Class that holds the paths from the XML
 * @author jagathan
 *
 */
public class XPaths {
	
	private LinkedHashSet<Map<String,String>> path; //HashSet that holds the paths in the JSON format we want
	
	private String error; //String that holds the error that occurs
	
	/**
	 * Default constructor
	 */
	public XPaths(){
		this.path = new LinkedHashSet<Map<String,String>>();
		error = "";
	}
	
	/**
	 * Sets class member path
	 * @param path
	 */
	public void setResult(LinkedHashSet<Map<String,String>> path){
		this.path = path;
	}
	
	/**
	 * Returns class member path
	 * @return
	 */
	public LinkedHashSet<Map<String,String>> getResults(){
		return this.path;
	}
	
	/**
	 * Setter for class member error
	 * @param error
	 */
	public void setError(String error){
		this.error = error;
	}
	
	/**
	 * getter for class member error
	 * @return
	 */
	public String getError(){
		return this.error;
	}
	
}
