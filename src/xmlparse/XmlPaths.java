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
package xmlparse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.LinkedHashSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

/**
 * @author jagathan Class for parsing XML files and getting all possible paths
 */
public class XmlPaths {

    private LinkedHashSet<String> paths;	//HashSet that holds all possible paths (unique) from XML file

    private String fileName; //File that holds the paths of the XML

    private String filePath;

    private final static String FILE_TYPE = ".txt";

    /**
     * default constructor
     */
    public XmlPaths() {
        paths = new LinkedHashSet<String>();
    }

    /**
     * returns class member pathSet
     *
     * @return HashSet<String>
     */
    public LinkedHashSet<String> getPathSet() {
        return this.paths;
    }

    /**
     * sets class member pathSet
     *
     * @param pathSet
     */
    public void setPathSet(LinkedHashSet<String> pathSet) {
        this.paths = pathSet;
    }

    /**
     * sets class member fileName
     *
     * @param fileName
     */
    public void setFileName(String fileName) {
        this.fileName = this.filePath + fileName + FILE_TYPE;
    }

    /**
     * returns class member fileName
     *
     * @return
     */
    public String getFileName() {
        return this.fileName;
    }

    /**
     * returns class member fileName
     *
     * @return
     */
    public String getFilePath() {
        return this.filePath;
    }

    /**
     * sets class member filePath
     *
     * @param path
     */
    public void setFilePath(String path) {
        this.filePath = path;
    }

    /**
     * Reads the paths from fileName member and returns a LinkedHashSet with the
     * paths
     *
     * @return
     */
    public LinkedHashSet<String> getPaths() {
        FileReader fr;
        if (!new File(this.fileName).exists()) {
            return this.paths;
        }
        try {
            fr = new FileReader(this.fileName);
            BufferedReader buffer = new BufferedReader(fr);
            String line;
            while ((line = buffer.readLine()) != null) {
                this.paths.add(line);
            }
            buffer.close();
            fr.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this.paths;
    }

    /**
     * Parses the XML file given as parameter and writes all the paths in file
     * fileName
     *
     * @param fileName
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public void ParseXMLFile(String fileName) throws IOException, ParserConfigurationException, SAXException {
        File xmlFile = new File(fileName);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(xmlFile);

        Element root = doc.getDocumentElement();
        String rootTag = root.getTagName();
        this.paths.add(rootTag);	//adds root tag in the list

        NodeList kids = root.getChildNodes();	//get child nodes from root

        //For each child of root find all possible paths
        for (int i = 0; i < kids.getLength(); i++) {
            String nextTag = "";
            if (kids.item(i) instanceof Element) {
                nextTag = rootTag + "/" + kids.item(i).getNodeName();
                this.paths.add(nextTag);
                processChildNode(kids.item(i).getChildNodes(), nextTag);
            }
        }

        WriteToFile();
    }

    /**
     * walks recursively through every path of the listOfNodes and adds each
     * path to the pathList
     *
     * @param listOfNodes
     * @param tags
     */
    public void processChildNode(NodeList listOfNodes, String tags) {
        for (int i = 0; i < listOfNodes.getLength(); i++) {
            if (listOfNodes.item(i) instanceof Element) {
                String tagPath = tags + "/" + listOfNodes.item(i).getNodeName();
                this.paths.add(tagPath);

                if (listOfNodes.item(i).getChildNodes().getLength() >= 1) {
                    processChildNode(listOfNodes.item(i).getChildNodes(), tagPath);
                }
            } else if (listOfNodes.item(i) instanceof Text && listOfNodes.getLength() == 1) {
                return;
            }
        }
    }

    /**
     * creates a file and writes all the paths from pathList
     */
    public void WriteToFile() {
        Iterator<String> it = this.paths.iterator();
        File xmlFile = new File(this.fileName);
        PrintWriter writer;
        try {
            writer = new PrintWriter(xmlFile.getAbsoluteFile(), "UTF-8");
            for (; it.hasNext();) {
                String val = it.next();
                writer.println(val);
            }
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
