package com.rstn.iws.webservice.repository;

import com.rstn.iws.webservice.config.Config;
import com.rstn.iws.webservice.model.DocumentPropertyModel;
import com.rstn.iws.webservice.model.PropertyModel;
import com.rstn.iws.webservice.utils.ErrorUtils;
import com.rstn.iws.webservice.utils.PropertyConfig;
import jcifs.CIFSContext;
import jcifs.Configuration;
import jcifs.config.PropertyConfiguration;
import jcifs.context.BaseContext;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.activation.DataHandler;
import javax.annotation.PostConstruct;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.apache.commons.io.FileUtils.deleteDirectory;

@Component
public class CaptureRepository {
    @Autowired
    Config config;

    @PostConstruct
    public void initData() {
        // initialize countries map
    }

    private static final Logger logger = LogManager.getLogger(CaptureRepository.class);

    private static final String SUBMISSION = "submission";
    private static final String PROFILE = "profile";
    private static final String NEW_SUBMISSION = "New Submission";
    private static final String ADDITIONAL = "Additional";
    private static final String FILLING = "Filling";
    private static final String DOCUMENT_TYPE = "document_type";
    private static final String DOCUMENT_TAG = "document";
    private static final String EMPTY = "";
    private static final String ELEMENT_TAG = "index";
    private static final String SUB_PROCESS = "sub_process";
    private static final String DOC_SOURCE = "doc_source";

    private static final String ERR106 = "ERR106";

    private static final String ERR300 = "ERR300";

    public static String getResponse(String submissionID, String errorCode, String errorDescription, String errorStackTrace, String DocumentID) {
        String submission = "<responses><response submission_id=\"" + submissionID + "\"><status>FAILED</status>" + "<error>" + "<errorCode>" + errorCode + "</errorCode>" + "<errorDescription>" + errorDescription + DocumentID + "</errorDescription>" + "<errorStackTrace>" + errorStackTrace + "</errorStackTrace>" + "</error>" +
                "</response></responses>";
        return submission;
    }

    private String releaseDir = "";
    private String folder = "";
    private String documentDir = "";
    private String applicationNo = "";
    private String channel = "";
    private String companyCode = "";
    private String branchCode = "";
    private String insuredName = "";
    private String insuredIdType = "";
    private String insuredIdNo = "";
    private String ownerName = "";
    private String ownerIdType = "";
    private String ownerIdNo = "";
    private String productCode = "";
    private String submissionDate = "";
    //String region = "";
    private String agentCode = "";
    private String newSubmissionDir;
    private String additionalDir;
    private String fillingOnlyDir;
    private String submissionID = "";
    private String returnXml = "";

    private String serverReleaseDir = "";

    private String serverNewSubmissionDir;
    private String serverAdditionalDir;
    private String serverFillingOnlyDir;
    private String targetFileDir = "";
    Document doc = null;
    List<PropertyModel> listNewProperty = new ArrayList<PropertyModel>();
    List<DocumentPropertyModel> listDocProperty = new ArrayList<DocumentPropertyModel>();


    private void setApplicationNo(String attribute, Element index) {
        if (attribute.equalsIgnoreCase("Application No")) {
            if (index.getFirstChild() != null) {
                applicationNo = index.getChildNodes().item(0).getNodeValue();
                logger.info("Application No: " + applicationNo);
            }
        }
    }

    private void setChannel(String attribute, Element index) {
        if (attribute.equalsIgnoreCase("Channel")) {
            if (index.getFirstChild() != null) {
                channel = index.getChildNodes().item(0).getNodeValue();
                logger.info("Channel: " + channel);
            }
        }
    }

    private void setCompanyCode(String attribute, Element index) {
        if (attribute.equalsIgnoreCase("Company Code")) {
            if (index.getFirstChild() != null) {
                companyCode = index.getChildNodes().item(0).getNodeValue();
                logger.info("Company Code: " + companyCode);
            }
        }
    }

    private void setBrandCode(String attribute, Element index) {
        if (attribute.equalsIgnoreCase("Branch Code")) {
            if (index.getFirstChild() != null) {
                branchCode = index.getChildNodes().item(0).getNodeValue();
                logger.info("Branch Code: " + branchCode);
            }
        }
    }

    private void setInsuredName(String attribute, Element index) {
        if (attribute.equalsIgnoreCase("Insured Name")) {
            if (index.getFirstChild() != null) {
                insuredName = index.getChildNodes().item(0).getNodeValue();
                logger.info("Insured Name: " + insuredName);
            }
        }
    }

    private void setInsuredIdType(String attribute, Element index) {
        if (attribute.equalsIgnoreCase("Insured ID Type")) {
            if (index.getFirstChild() != null) {
                insuredIdType = index.getChildNodes().item(0).getNodeValue();
                logger.info("Insured ID Type: " + insuredIdType);
            }
        }
    }

    private void setInsuredIdNo(String attribute, Element index) {
        if (attribute.equalsIgnoreCase("Insured ID No")) {
            if (index.getFirstChild() != null) {
                insuredIdNo = index.getChildNodes().item(0).getNodeValue();
                logger.info("Insured ID No: " + insuredIdNo);
            }
        }
    }

    private void setOwnerName(String attribute, Element index) {
        if (attribute.equalsIgnoreCase("Owner Name")) {
            if (index.getFirstChild() != null) {
                ownerName = index.getChildNodes().item(0).getNodeValue();
                logger.info("Owner Name: " + ownerName);
            }
        }
    }

    private void setOwnerIdType(String attribute, Element index) {
        if (attribute.equalsIgnoreCase("Owner ID Type")) {
            if (index.getFirstChild() != null) {
                ownerIdType = index.getChildNodes().item(0).getNodeValue();
                logger.info("Owner ID Type: " + ownerIdType);
            }
        }
    }

    private void setOwnerIdNo(String attribute, Element index) {
        if (attribute.equalsIgnoreCase("Owner ID No")) {
            if (index.getFirstChild() != null) {
                ownerIdNo = index.getChildNodes().item(0).getNodeValue();
                logger.info("Owner ID No: " + ownerIdNo);
            }
        }
    }

    private void setProductCode(String attribute, Element index) {
        if (attribute.equalsIgnoreCase("Product Code")) {
            if (index.getFirstChild() != null) {
                productCode = index.getChildNodes().item(0).getNodeValue();
                logger.info("Product Code: " + productCode);
            }
        }
    }

    private void setSubmissionDate(String attribute, Element index) {
        if (attribute.equalsIgnoreCase("Submission Date")) {
            if (index.getFirstChild() != null) {
                submissionDate = index.getChildNodes().item(0).getNodeValue();
                logger.info("Submission Date: " + submissionDate);
            }
        }
    }

    private void setAgentCode(String attribute, Element index) {
        if (attribute.equalsIgnoreCase("Agent Code")) {
            if (index.getFirstChild() != null) {
                agentCode = index.getChildNodes().item(0).getNodeValue();
                logger.info("Agent Code: " + agentCode);
            }
        }
    }

    private void setNewProperty(String attribute, Element index) {
        PropertyModel model = new PropertyModel();
        String value = "";
        if (index.getFirstChild() != null) {
            value = index.getChildNodes().item(0).getNodeValue().trim();
            logger.info(index.getChildNodes().item(0).getNodeValue() + ": " + value);
        }
        model.setName(attribute);
        model.setValue(value);
        listNewProperty.add(model);

    }


    private void parseCaptureData(String infoXml) throws IOException, ParserConfigurationException, SAXException {
        logger.info("Xml data validated");
        // Load properties file
        Properties properties = new Properties();

        newSubmissionDir = config.getNewSubmissionDir();
        additionalDir = config.getAdditionalDir();
        fillingOnlyDir = config.getFillingOnlyDir();

        serverNewSubmissionDir = config.getServerNewSubmissionDir();
        serverAdditionalDir = config.getServerAdditionalDir();
        serverFillingOnlyDir = config.getServerFillingOnlyDir();


        logger.info("Reading XML fields");
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setFeature(
                XMLConstants.FEATURE_SECURE_PROCESSING, true);
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(infoXml));
        doc = docBuilder.parse(is);

        NodeList submissions = doc.getElementsByTagName(SUBMISSION);
        Element submission = (Element) submissions.item(0);
        submissionID = submission.getAttribute("submission_id").toString();

        NodeList indexes = doc.getElementsByTagName(ELEMENT_TAG);
        // Read the field indexes
        for (int i = 0; i < indexes.getLength(); i++) {

            Element index = (Element) indexes.item(i);
            String attribute = index.getAttribute("name").toString();
            if (PropertyConfig.mapPropertyConfig().containsValue(attribute)) {
                setApplicationNo(attribute, index);
                setChannel(attribute, index);
                setCompanyCode(attribute, index);
                setBrandCode(attribute, index);
                setInsuredName(attribute, index);
                setInsuredIdType(attribute, index);
                setInsuredIdNo(attribute, index);
                setOwnerName(attribute, index);
                setOwnerIdType(attribute, index);
                setOwnerIdNo(attribute, index);
                setProductCode(attribute, index);
                setSubmissionDate(attribute, index);
                setAgentCode(attribute, index);
            } else {
                //new property
                setNewProperty(attribute, index);
            }
        }
    }

    private void writePropertyXML(List<PropertyModel> listNewProperty, String documentDir) throws ParserConfigurationException, TransformerException, IOException {
        StreamResult streamResult = null;
        try {
            logger.info("Write xml file");
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();
            Element root = document.createElement("data");
            document.appendChild(root);

            //set new property
            for (PropertyModel property : listNewProperty) {
                Element indexXML = document.createElement(ELEMENT_TAG);
                root.appendChild(indexXML);
                Attr attr = document.createAttribute("name");
                attr.setValue(property.getName());
                indexXML.setAttributeNode(attr);
                indexXML.appendChild(document.createTextNode(property.getValue()));
            }
            Element documentsXML = document.createElement("documents");
            root.appendChild(documentsXML);

            for (DocumentPropertyModel docproperty : listDocProperty) {
                Element documentXML = document.createElement(DOCUMENT_TAG);
                documentsXML.appendChild(documentXML);

                Element documentTypeXML = document.createElement("DocumentType");
                documentXML.appendChild(documentTypeXML);
                documentTypeXML.appendChild(document.createTextNode(docproperty.getDocumentType()));

                Element submissionProcessXML = document.createElement("SubmissionProcess");
                documentXML.appendChild(submissionProcessXML);
                submissionProcessXML.appendChild(document.createTextNode(docproperty.getSubmissionProcess()));

                Element docSourceXML = document.createElement("DocSource");
                documentXML.appendChild(docSourceXML);
                docSourceXML.appendChild(document.createTextNode(docproperty.getDocSource()));
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            streamResult = new StreamResult(new FileOutputStream(new File(documentDir + "PropertyConfig.xml")));
            transformer.transform(domSource, streamResult);
            streamResult.getOutputStream().close();
        } catch (Exception e) {
            if (streamResult != null) {
                streamResult.getOutputStream().close();
            }
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }

    public String uploadDocuments(String infoXml, DataHandler[] dataHandlers) {
        logger.info("Request: " + infoXml);
        try {
            returnXml = validateRequiredField(infoXml, dataHandlers.length);
            if (returnXml.equalsIgnoreCase("")) { // if all fields are valid, proceed with writing indexes/folders/files

                logger.info("Xml data validated");
                listNewProperty.clear();
                parseCaptureData(infoXml);

                logger.info("Finished Reading XML fields");
                // Create timestamps/dates that are used in naming for parent folder/documents
                Date date = new Date();
                DateFormat dateTimeFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                String currentTime = dateTimeFormat.format(date);


                DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                String currentDate = dateFormat.format(date);
                logger.info("currentDate: " + currentDate);

                String profile = doc.getElementsByTagName(PROFILE).item(0).getChildNodes().item(0).getNodeValue();
                logger.info("profile: " + profile);

                boolean isDocumentDirExist = true; // requests < 1 sec will have same satmp yyyyMMddHHmmss
                File file = null;
                while (isDocumentDirExist) {
                    selectReleaseFolder(profile, currentTime);

                    file = new File(documentDir);
                    isDocumentDirExist = file.exists();

                    //wait 1 sec only if DocumentDirExist
                    if (isDocumentDirExist) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            logger.info("Thread 1 sec");
                        }
                        date = new Date();
                        currentTime = dateTimeFormat.format(date);
                    }

                }
                logger.info("currentTime: " + currentTime);
                logger.info("Creating Parent folder");
                logger.info("releaseDir: " + releaseDir);
                logger.info("folder: " + folder);
                logger.info("documentDir: " + documentDir);
                logger.info("targetFileDir: " + targetFileDir);

                file.mkdirs();

                logger.info("Created parent folder");
                logger.info("Writing files in parent folder");
                listDocProperty.clear();

                writeAttachmentFiles(dataHandlers, doc, currentDate, documentDir);
                writePropertyXML(listNewProperty, documentDir);

                //Move folder from local to server
                moveFolder(documentDir, targetFileDir);

                logger.info("Finished writing files in parent folder");

                returnXml = "<responses>" +
                        "<response submission_id=\"" + submissionID + "\">" +
                        "<status>OK</status>" +
                        "</response>" +
                        "</responses>";

            }
        } catch (Exception e) {

            returnXml = getResponse(submissionID, "ERR401", e.getMessage(), String.valueOf(e.getStackTrace()), "");

            logger.info("Exception Message: " + e.getMessage());
            logger.info("StackTrace: " + e.getStackTrace());

        }

        logger.info(returnXml);

        return returnXml;
    }

    private void selectReleaseFolder(String profile, String currentTime) {
        if (profile.equalsIgnoreCase(ADDITIONAL)) {
            releaseDir = additionalDir;
            serverReleaseDir = serverAdditionalDir;
            folder = applicationNo + "_" + branchCode + "_" + agentCode + "_" + currentTime;
        } else if (profile.equalsIgnoreCase(NEW_SUBMISSION)) {
            releaseDir = newSubmissionDir;
            serverReleaseDir = serverNewSubmissionDir;
            // Add RegionCode and AgentCode into folder name
            folder = applicationNo + "_" + channel + "_" + companyCode + "_" + branchCode + "_" + insuredName + "_"
                    + insuredIdType + "_" + insuredIdNo + "_" + ownerName + "_" + ownerIdType + "_" + ownerIdNo + "_"
                    + productCode + "_" + agentCode + "_" + submissionDate + "_" + currentTime;
        } else if (profile.equalsIgnoreCase(FILLING) || profile.equalsIgnoreCase(FILLING)) {
            releaseDir = fillingOnlyDir;
            serverReleaseDir = serverFillingOnlyDir;
            folder = applicationNo + "_" + branchCode + "_" + agentCode + "_" + currentTime;
        }
        documentDir = releaseDir + folder + "\\";
        targetFileDir = serverReleaseDir + folder + "/";
    }

    private void writeAttachmentFiles(DataHandler[] dataHandlers, Document doc, String currentDate, String documentDir) throws IOException {
        String extension = "";
        String documentType = "";
        String docSource = "";
        String subProcess = "";
        String filename = "";
        FileOutputStream fileOut = null;
        BufferedInputStream fileIn = null;
        try {
            int counter = 0;
            for (DataHandler dataHandler : dataHandlers) {
                extension = FilenameUtils.getExtension(doc.getElementsByTagName("document_name").item(counter).getChildNodes().item(0).getNodeValue());
                logger.info("extension: " + extension);
                documentType = doc.getElementsByTagName(DOCUMENT_TYPE).item(counter).getChildNodes().item(0).getNodeValue();
                subProcess = doc.getElementsByTagName(SUB_PROCESS).item(counter).getChildNodes().item(0).getNodeValue().trim().toUpperCase();
                docSource = doc.getElementsByTagName(DOC_SOURCE).item(counter).getChildNodes().item(0).getNodeValue().trim().toUpperCase();
                logger.info("documentType: " + documentType);
                filename = documentType + "_" + currentDate + "_" + counter + "." + extension;
                logger.info("filename: " + filename);
                fileOut = new FileOutputStream(new File(documentDir + filename));

                fileIn = new BufferedInputStream(dataHandler.getInputStream());
                while (fileIn.available() != 0) {
                    fileOut.write(fileIn.read());
                }
                counter++;

                fileOut.flush();
                fileOut.close();
                fileIn.close();

                DocumentPropertyModel model = new DocumentPropertyModel();
                model.setDocSource(docSource);
                model.setDocumentType(documentType);
                model.setSubmissionProcess(subProcess);
                listDocProperty.add(model);
            }
        } catch (Exception e) {
            if (fileOut != null) {
                fileOut.close();
            }
            if (fileIn != null) {
                fileIn.close();
            }
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }

    public void moveFolder(String sourceFilePath, String targetFilePath) throws IOException {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            logger.info("Start login");
            String user = config.getUsernameServer();
            String pass = config.getPasswordServer();
            String domain = "";
            String pathSourceFile = sourceFilePath;
            String remoteFolderPath = targetFilePath;
            Properties prop = new Properties();
            prop.put("jcifs.smb.client.enableSMB2", "true");
            prop.put("jcifs.smb.client.disableSMB1", "false");
            prop.put("jcifs.traceResources", "true");
            Configuration config = new PropertyConfiguration(prop);
            CIFSContext baseContext = new BaseContext(config);
            CIFSContext authed1 = baseContext.withCredentials(new NtlmPasswordAuthentication(baseContext, domain,
                    user, pass));
            SmbFile remoteFolder = new SmbFile("smb:" + remoteFolderPath, authed1);
            if (!remoteFolder.exists()) {
                remoteFolder.mkdir();
            }
            logger.info("Login success");
            logger.info(remoteFolderPath);
            logger.info(pathSourceFile);
            logger.info("Get list file in folder and copy to server");
            File folder = new File(pathSourceFile);
            File[] listOfFiles = folder.listFiles();
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    logger.info("File " + file.getName());
                    inputStream = new FileInputStream(pathSourceFile + file.getName());
                    remoteFolder = new SmbFile("smb:" + remoteFolderPath + file.getName(), authed1);
                    outputStream = remoteFolder.getOutputStream();
                    copyFile(inputStream, outputStream);
                }
            }
            logger.info("Get list file in folder and copy to server success");
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
                outputStream.flush();
            }
            deleteDirectory(folder);
        } catch (
                IOException e) {
            logger.error("Error: " + ExceptionUtils.getStackTrace(e));
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
                outputStream.flush();
            }
        } catch (
                Exception e) {
            throw new RuntimeException(e);
        }

    }
//    public static void main(String[] args) throws Exception {
//        ConnectFolderShare();
//    }

//    public static SmbFile ConnectFolderShare(String remoteFolderPath,String username,String password,String domain) throws Exception {
//        Properties prop = new Properties();
//        prop.put("jcifs.smb.client.enableSMB2", "true");
//        prop.put("jcifs.smb.client.disableSMB1", "false");
//        prop.put("jcifs.traceResources", "true");
//        Configuration config = new PropertyConfiguration(prop);
//        CIFSContext baseContext = new BaseContext(config);
//        CIFSContext authed1 = baseContext.withCredentials(new NtlmPasswordAuthentication(baseContext, domain,
//                username, password));
//        try (SmbFile f = new SmbFile("smb:" + remoteFolderPath, authed1)) {
//            if (f.exists()) {
//                return f;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    private void copyFile(InputStream inputStream, OutputStream outputStream) throws IOException {
        try {
            int bufferSize = 5096;
            byte[] b = new byte[bufferSize];
            int noOfBytes = 0;
            while ((noOfBytes = inputStream.read(b)) != -1) {
                outputStream.write(b, 0, noOfBytes);
            }
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            inputStream.close();
            outputStream.flush();
            outputStream.close();
        }
    }

    private String validateSubmissionTag() {
        if (doc.getElementsByTagName(SUBMISSION).item(0) == null) {
            returnXml = "<responses><response><status>FAILED</status>" +
                    "<error>" +
                    "<errorCode>ERR100</errorCode>" +
                    "<errorDescription>Missing Submission</errorDescription>" +
                    "<errorStackTrace></errorStackTrace>" +
                    "</error>" +
                    "</response></responses>";
            logger.info("There is no submission tag in the request");
            return returnXml;
        } else {
            // Check if there is a submission id
            NodeList submissions = doc.getElementsByTagName(SUBMISSION);
            Element submission = (Element) submissions.item(0);
            submissionID = submission.getAttribute("submission_id");

            if (submissionID.equalsIgnoreCase("") || submissionID == null) {
                returnXml = getResponse(submissionID, "ERR101", "Missing Submission ID", "", "");

                logger.info("There is no submission id provided");
                return returnXml;
            }
        }
        return EMPTY;
    }

    private String validateProfile() {
        if (doc.getElementsByTagName(PROFILE).item(0).getFirstChild() == null) {
            returnXml = getResponse(submissionID, "ERR102", "Missing Profile", "", "");
            logger.info("Missing Profile");
            return returnXml;
        }

        String profile = doc.getElementsByTagName(PROFILE).item(0).getChildNodes().item(0).getNodeValue();
        // Check if the profile is New Submission,Additional or Filling
        if (!(profile.equalsIgnoreCase(NEW_SUBMISSION)) && (!profile.equalsIgnoreCase(ADDITIONAL))
                && (!profile.equalsIgnoreCase(FILLING))) {
            returnXml = getResponse(submissionID, "ERR103", "Profile should be on of the following: New Submission, Additional, Filling", "", "");
            logger.info("Profile should be on of the following: New Submission, Additional, Filling ");
            return returnXml;
        }
        return EMPTY;
    }

    private String validateDocument(Integer filesCount, String documentID) {
        int documentTagscount = doc.getElementsByTagName(DOCUMENT_TAG).getLength();
        // Check if documents/files were sent
        if (documentTagscount < 1 || filesCount < 1) {
            returnXml = getResponse(submissionID, "ERR104", "No documents/files were sent", "", "");
            logger.info("No documents/files were sent");
            return returnXml;
        }

        NodeList documents = doc.getElementsByTagName(DOCUMENT_TAG);
        // Check for missing document type or name
        for (int i = 0; i < documents.getLength(); i++) {

            Element document = (Element) documents.item(i);
            documentID = document.getAttribute("id").toString();

            if (document.getElementsByTagName("document_name").item(0).getFirstChild() == null) {

                returnXml = getResponse(submissionID, "ERR105", "Missing document name for document id:", "", documentID);
                logger.info("Missing document name for document id: " + documentID);
                return returnXml;
            }

            if (document.getElementsByTagName(DOCUMENT_TYPE).item(0).getFirstChild() == null) {

                returnXml = getResponse(submissionID, ERR106, "Missing document type for document id:", "", documentID);

                logger.info("Missing document type for document id: " + documentID);
                return returnXml;
            }
            if (document.getElementsByTagName(SUB_PROCESS).item(0).getFirstChild() == null) {

                returnXml = getResponse(submissionID, ERR106, "Missing sub process for document id:", "", documentID);

                logger.info("Missing sub process for document id: " + documentID);
                return returnXml;
            }
            if (document.getElementsByTagName(DOC_SOURCE).item(0).getFirstChild() == null) {

                returnXml = getResponse(submissionID, ERR106, "Missing document source for document id:", "", documentID);

                logger.info("Missing document source for document id: " + documentID);
                return returnXml;
            }
            String doc_source = document.getElementsByTagName(DOC_SOURCE).item(0).getChildNodes().item(0).getNodeValue().trim();
            if (!(doc_source.matches("((?:[^ !\\\"#$%&'()*+,\\-./:;<=>?@\\[\\]^_`{|}~]){6})"))) {
                returnXml = getResponse(submissionID, ERR300, "Document Source should be not null, 6 characters and not contain special characters for document id:", "", documentID);
                logger.info("Document Source should be not null, 6 characters and not contain special characters for document id: " + documentID);
                return returnXml;
            }
            String sub_process = document.getElementsByTagName(SUB_PROCESS).item(0).getChildNodes().item(0).getNodeValue().trim();
            if (!(sub_process.matches("((?=.*([a-z]|[A-Z])).{1})"))) {
                returnXml = getResponse(submissionID, ERR300, "Sub process should be not null, 1 characters and not contain special characters for document id:", "", documentID);
                logger.info("Sub process should be not null, 1 characters and not contain special characters for document id: " + documentID);
                return returnXml;
            }
        }
        return EMPTY;
    }

    private String validateNewSubmissionDoc() {
        // Check that the new application document (NB000001) should be submitted if the New Submission profile is selected
        String profile = doc.getElementsByTagName(PROFILE).item(0).getChildNodes().item(0).getNodeValue();
        if (profile.equalsIgnoreCase(NEW_SUBMISSION)) {

            NodeList document_types = doc.getElementsByTagName(DOCUMENT_TYPE);

            Boolean missingApplicationForm = true;
            for (int i = 0; i < document_types.getLength(); i++) {
                Element document_type = (Element) document_types.item(i);
                String doc_type = document_type.getChildNodes().item(0).getNodeValue();

                if (doc_type.equalsIgnoreCase("NB000001")) {
                    missingApplicationForm = false;
                }
            }

            if (missingApplicationForm) {

                returnXml = getResponse(submissionID, "ERR301",
                        "You need to send the application form for the New Submission profile", "", "");
                logger.info("You need to send the application form for the New Submission profile");
                return returnXml;
            }
        }
        return EMPTY;
    }

    private String validateApplicationNo() {
        boolean missingApplicationNumber = true;
        NodeList indexes = doc.getElementsByTagName(ELEMENT_TAG);

        for (int i = 0; i < indexes.getLength(); i++) {

            Element index = (Element) indexes.item(i);
            String attribute = index.getAttribute("name").toString();

            if (attribute.equalsIgnoreCase("Application No")) {

                if (index.getFirstChild() != null) {
                    missingApplicationNumber = false;
                    if (index.getChildNodes().item(0).getNodeValue().length() < 10 || !index.getChildNodes().item(0).getNodeValue().startsWith("A") && !index.getChildNodes().item(0).getNodeValue().startsWith("a")) {
                        returnXml = getResponse(submissionID, "ERR107", "Application No should be 10 characters and starts with letter: A ", "", "");
                        logger.info("Application No should be 10 characters and starts with letter: A");
                        return returnXml;
                    }
                }
            }
        }
        // Check for missing application number
        if (missingApplicationNumber) {

            returnXml = getResponse(submissionID, ERR300, "Missing Application Number", "", "");
            logger.info("Missing Application Number");
            return returnXml;
        }
        return EMPTY;
    }

    private String validateSubmissionType() {
        boolean missingSubmissionType = true;
        NodeList indexes = doc.getElementsByTagName(ELEMENT_TAG);

        for (int i = 0; i < indexes.getLength(); i++) {

            Element index = (Element) indexes.item(i);
            String attribute = index.getAttribute("name").toString();

            if (attribute.equalsIgnoreCase("SubmissionType")) {

                if (index.getFirstChild() != null) {
                    missingSubmissionType = false;
                    String submissionType = index.getChildNodes().item(0).getNodeValue().trim().toUpperCase();
                    if (!(submissionType.matches("((?:[^ !\\\"#$%&'()*+,\\-./:;<=>?@\\[\\]^_`{|}~]){6})"))) {
                        returnXml = getResponse(submissionID, ERR300, "SubmissionType should be not null, 6 characters and not contain special characters", "", "");
                        logger.info("SubmissionType should be not null, 6 characters and not contain special characters");
                        return returnXml;
                    }
                }
            }
        }
        // Check for missing DataSource
        if (missingSubmissionType) {

            returnXml = getResponse(submissionID, ERR300, "Missing SubmissionType", "", "");
            logger.info("Missing SubmissionType");
            return returnXml;
        }
        return EMPTY;
    }


    private String validateRequiredField(String xml, int filesCount) {

        logger.info("Validating xml data");

        String submissionID = "";
        String returnXml = "";

        try {
            String documentID = "";

            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            docBuilderFactory.setFeature(
                    XMLConstants.FEATURE_SECURE_PROCESSING, true);
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            doc = docBuilder.parse(is);

            // Check if there is a submission tag
            String rs = validateSubmissionTag();
            if (!EMPTY.equals(rs)) return rs;

            // Check if there is profile
            rs = validateProfile();
            if (!EMPTY.equals(rs)) return rs;

            int documentTagscount = doc.getElementsByTagName(DOCUMENT_TAG).getLength();
            // Check if documents/files were sent
            rs = validateDocument(filesCount, documentID);
            if (!EMPTY.equals(rs)) return rs;


            // Check that the new application document (NB000001) should be submitted if the New Submission profile is selected
            rs = validateNewSubmissionDoc();
            if (!EMPTY.equals(rs)) return rs;

            rs = validateApplicationNo();
            if (!EMPTY.equals(rs)) return rs;

//            rs = validateContestCode();
//            if(!EMPTY.equals(rs)) return rs;

            rs = validateSubmissionType();
            if (!EMPTY.equals(rs)) return rs;

            // Check if number of files sent by MTOM is the same as number of documents in xml
            if (documentTagscount != filesCount) {
                logger.info("Comparing: " + documentTagscount + " : " + filesCount);
                returnXml = getResponse(submissionID, "ERR200", "The number of documents in xml do not match the number of files sent", "", "");
                logger.info("The number of documents in xml do not match the number of files sent");
                return returnXml;
            }
        } catch (Exception e) {
            returnXml = getResponse(submissionID, "ERR400", e.getMessage(), e.getStackTrace().toString(), "");
            logger.error("StackTrace: " + ErrorUtils.stackTraceToString(e));
            logger.info("Exception Message: " + e.getMessage());
            return returnXml;
        }

        return returnXml;
    }
}
