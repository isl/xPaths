Copyright 2015 Institute of Computer Science,
Foundation for Research and Technology - Hellas

Licensed under the EUPL, Version 1.1 or - as soon they will be approved
by the European Commission - subsequent versions of the EUPL (the "Licence");
You may not use this work except in compliance with the Licence.
You may obtain a copy of the Licence at:

http://ec.europa.eu/idabc/eupl

Unless required by applicable law or agreed to in writing, software distributed
under the Licence is distributed on an "AS IS" basis,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the Licence for the specific language governing permissions and limitations
under the Licence.

Contact:  POBox 1385, Heraklio Crete, GR-700 13 GREECE
Tel:+30-2810-391632
Fax: +30-2810-391638
E-mail: isl@ics.forth.gr
http://www.ics.forth.gr/isl

Authors :  Giannis Agathangelos, Georgios Samaritakis.

This file is part of the SourceAnalyzer webapp.

SourceAnalyzer
==============

SourceAnalyzer is a webapp providing RESTful web services to analyze xml files (xml or xsd) and obtain all available xpaths.

## Build - Deploy - Run
Folders src, web and lib contain all the files needed to build the web app and create a war file.
You may use any application server that supports war files. (Has been tested with Apache Tomcat versions 5,6,7,8).

## Configuration
Once you have deployed the SourceAnalyzer war, you should check its web.xml and declare where your source files are stored (parameter named "AppHome").

## Usage
SourceAnalyzer is used by [3MEditor] (https://github.com/isl/3MEditor "3MEditor") as a plugin.
It may also be used on its own.

Examples:

If you want to analyze a stored file and get its paths (3MEditor usage), use:
**http://(server IP):(server port)/SourceAnalyzer/filePathServiceGet?fileName=(actual filename)** 
e.g. **http://localhost:8080/SourceAnalyzer/filePathServiceGet?fileName=nightwatchdo.xml**

There are 2 more services you may use:

filePathService : Same as filePathServiceGet, but using POST HTTP method.
fileService: Uses POST to actually upload file first and then analyze.

Read javadoc for more details.

The SourceAnalyzer webapp dependecies and licenses used are described in file SourceAnalyzer-Dependencies-LicensesUsed.txt 


