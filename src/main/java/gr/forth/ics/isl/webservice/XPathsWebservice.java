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
 * This file is part of the xPaths webapp.
 */
package gr.forth.ics.isl.webservice;

import gr.forth.ics.isl.xmlparse.XmlPaths;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.codehaus.jettison.json.JSONException;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.xml.sax.SAXException;
import schemareader.SchemaFile;

/**
 * Web Service class
 *
 * @author jagathan
 *
 */
@Path("/")
public class XPathsWebservice {

    @Context
    HttpServletRequest request;
    private final String FS = System.getProperty("file.separator");
    private String path;

    /**
     * Handles request in default WebService path
     *
     * @return
     */
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response defaultService() {
        return Response.status(200).build();
    }

    /**
     * Uploads the file and returns status OK and the XML Paths in JSON
     *
     * @param input
     * @return
     * @throws IOException
     */
    @POST
    @Path("/fileService")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response fileParse(MultipartFormDataInput input) throws IOException {
        String filePath = uploadFile(input);

        XPaths xPaths;
        try {
            xPaths = handleParsing(filePath);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return Response.status(400).entity(handleException(e)).build(); //status ERROR
        } catch (SAXException e) {
            e.printStackTrace();
            return Response.status(400).entity(handleException(e)).build(); //status ERROR
        }

        return Response.status(200).entity(xPaths).build(); //status OK
    }

    /**
     * Parses the file in filePath location Returns OK the XML Paths in JSON
     * when everything is done
     *
     * @return
     */
    @POST
    @Path("/filePathService")
    @Consumes("application/x-www-form-urlencoded")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
//    public Response fileNameParse(@FormParam("fileName") String fileName, @FormParam("root") String root) {
    public Response fileNameParse(String requestBody) {
        String[] params = requestBody.split("&");
        String fileName = "";
        String root = "";

        for (String param : params) {
            if (param.startsWith("fileName=")) {
                fileName = param.substring(9);
            }
            if (param.startsWith("root=")) {
                root = param.substring(5);
            }

        }

        XPaths xPaths;

        if (!fileName.endsWith(".xml") && !fileName.endsWith(".xsd")) { //type error handling
            XPaths error = new XPaths();
            error.setError("Invalid file type. Only xml or xsd files allowed");
            return Response.status(400).entity(error).build(); //status ERROR
        }

        if (fileName.endsWith(".xsd")) {
            if (new File(getHomePath(fileName) + "../xml_schema/" + fileName.replace(".xsd", ".txt")).exists() == false) {
                try {
                    fileName = XSDToXML(fileName, root);
                } catch (NullPointerException e) {
                    return Response.status(400).entity(handleException(e)).build(); //status ERROR
                }
            }
        }

        try {
            xPaths = handleParsing(fileName);

        } catch (IOException e) {
            return Response.status(400).entity(handleException(e)).build(); //status ERROR
        } catch (ParserConfigurationException e) {
            return Response.status(400).entity(handleException(e)).build(); //status ERROR
        } catch (SAXException e) {
            return Response.status(400).entity(handleException(e)).build(); //status ERROR
        }

        return Response.status(200).entity(xPaths).build(); //status OK
    }

    /**
     * Handles GET request Parses the file in filePath location Returns OK the
     * XML Paths in JSON when everything is done
     *
     * @param filePath
     * @return
     * @throws JSONException
     */
    @GET
    @Path("/filePathServiceGet")
    @Produces(MediaType.APPLICATION_JSON)
    public Response fileNameParseGet(@QueryParam("fileName") String fileName, @QueryParam("root") String root) {
//        return fileNameParse(fileName, root);
        return fileNameParse("fileName=" + fileName + "&root=" + root);
    }

    /**
     * handler for thrown exceptions
     *
     * @param e
     * @return
     */
    public XPaths handleException(Exception e) {
        e.printStackTrace();
        XPaths error = new XPaths();

        if (e instanceof IOException) {
            error.setError("fileNotFound");
        } else if ((e instanceof SAXException) || (e instanceof ParserConfigurationException)) {
            error.setError("Invalid XML");
        } else if (e instanceof NullPointerException) {
            error.setError("Wrong root element");
        }

        return error;
    }

    /**
     * helper method to get the home path from web.xml
     *
     * @return
     */
    private String getHomePath(String filename) {
        ServletContext context = request.getSession().getServletContext();
        String pathValue = context.getInitParameter("AppHome");
        if (filename.endsWith("xsd")) {
            pathValue = pathValue + FS + "xml_schema";
        } else {
            pathValue = pathValue + FS + "example_files";
        }
        return pathValue + FS;
    }

    /**
     * Constructs a LinkedHashSet that has a Map<String,String>
     * to accomplish the format we want
     *
     * @param hashSet
     * @return
     */
    private LinkedHashSet<Map<String, String>> FormatHashSet(LinkedHashSet<String> hashSet) {

        LinkedHashSet<Map<String, String>> newHashSet = new LinkedHashSet<Map<String, String>>();
        Iterator<String> it = hashSet.iterator();

        for (; it.hasNext();) {//iterates HashSet 

            //builds a map with 2 key-value pair {"id" : "", "text" : ""} 
            Map<String, String> hashMap = new HashMap<String, String>();
            String path = it.next();
            hashMap.put("id", path);
            hashMap.put("text", path);

            //Stores map in the HashSet that is to be returned
            newHashSet.add(hashMap);
        }
        return newHashSet;
    }

    /**
     * Helper method for the XML file Parsing
     *
     * @param filePath
     * @return
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws IOException
     */
    private XPaths handleParsing(String fileName) throws IOException,
            ParserConfigurationException, SAXException {
        XmlPaths xmlPaths = new XmlPaths();
        XPaths xPaths = new XPaths();	//creates object to be returned

        path = getHomePath(fileName);
        xmlPaths.setFilePath(path);
        xmlPaths.setFileName(fileName.substring(fileName.indexOf(System.getProperty("file.separator")) + 1, fileName.lastIndexOf('.')));
        if (xmlPaths.getPaths().isEmpty()) //if the file has not been uploaded before	
        {
            xmlPaths.ParseXMLFile(path + fileName); //Parse the XML in the filePath
        }
        xPaths.setResult(FormatHashSet(xmlPaths.getPaths()));
        return xPaths;
    }

    /**
     *
     * Helper method that converts XSD to XML
     *
     * @param filePath
     * @param root
     * @return
     */
    private String XSDToXML(String fileName, String root) throws NullPointerException {
        SchemaFile schema = new SchemaFile(getHomePath(fileName) + fileName);
        String xmlString = schema.createXMLSubtree(root, "maximum");

        String xmlFileName = fileName.substring(0, fileName.lastIndexOf(".")) + ".xml";
        String xmlFilePath = getHomePath(fileName) + xmlFileName;
        File xmlFile = new File(xmlFilePath);

        try {
            PrintWriter out = new PrintWriter(xmlFile);
            out.println(xmlString);
            out.close();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        return xmlFileName;
    }

    /**
     * ******FILE UPLOAD Methods********
     */
    /**
     * Uploads the given file
     *
     * @param input
     * @return
     */
    private String uploadFile(MultipartFormDataInput input) {

        String fileName = "";
        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
        List<InputPart> inputParts = uploadForm.get("dataFile");

        for (InputPart inputPart : inputParts) {

            try {

                MultivaluedMap<String, String> header = inputPart.getHeaders();
                fileName = getFileName(header);

                //convert the uploaded file to inputstream
                InputStream inputStream = inputPart.getBody(InputStream.class, null);

                byte[] bytes = IOUtils.toByteArray(inputStream);

                writeFile(bytes, getHomePath(fileName) + fileName);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return fileName;
    }

    /**
     * gets the fileName from the uploaded file
     *
     * @param header
     * @return
     */
    private String getFileName(MultivaluedMap<String, String> header) {

        String[] contentDisposition = header.getFirst("Content-Disposition").split(";");

        for (String filename : contentDisposition) {
            if ((filename.trim().startsWith("filename"))) {

                String[] name = filename.split("=");

                String finalFileName = name[1].trim().replaceAll("\"", "");
                return finalFileName;
            }
        }
        return "unknown";
    }

    /**
     * Write the file
     *
     * @param content
     * @param filePath
     * @throws IOException
     */
    private void writeFile(byte[] content, String filePath) throws IOException {

        File file = new File(filePath);

        if (!file.exists()) {
            file.createNewFile();
        }

        FileOutputStream fop = new FileOutputStream(file);

        fop.write(content);
        fop.flush();
        fop.close();

    }
}
