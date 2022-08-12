package com.rstn.iws.webservice.repository;

import com.rstn.iws.webservice.config.Config;
import com.rstn.iws.webservice.constant.Constant;
import com.rstn.iws.webservice.utils.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@Component
public class SubmitDocumentsToE2IO {
    private static final Logger logger = LogManager.getLogger(SubmitDocumentsToE2IO.class);
    @Autowired
    Config config;

    public SubmitDocumentsToE2IO() {
    }

    public String submitDocumentsByApplication(String infoXml) {
        logger.info("Request: " + infoXml);
        String submissionID = "";
        String returnXml = "";
        String releaseDir = "";
        String applicationNo = "";
        String channel = "";
        String companyCode = "";
        String branchCode = "";
        String insuredName = "";
        String insuredIdType = "";
        String insuredIdNo = "";
        String ownerName = "";
        String ownerIdType = "";
        String ownerIdNo = "";
        String productCode = "";
        String submissionDate = "";

        try {
            returnXml = this.validateRequiredField(infoXml);
            if (returnXml.equalsIgnoreCase("")) {
                logger.info("Request Data Validated");
                logger.info("Reading XML fields");
                DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
                InputSource is = new InputSource();
                is.setCharacterStream(new StringReader(infoXml));
                Document doc = docBuilder.parse(is);
                NodeList submissions = doc.getElementsByTagName("submission");
                Element submission = (Element) submissions.item(0);
                submissionID = submission.getAttribute("submission_id").toString();
                NodeList indexes = doc.getElementsByTagName("index");

                for (int i = 0; i < indexes.getLength(); ++i) {
                    Element index = (Element) indexes.item(i);
                    String attribute = index.getAttribute("name").toString();
                    if (attribute.equalsIgnoreCase("Application No")) {
                        if (index.getFirstChild() != null) {
                            applicationNo = index.getChildNodes().item(0).getNodeValue();
                            logger.info("Application No: " + applicationNo);
                        }
                    } else if (attribute.equalsIgnoreCase("Channel")) {
                        if (index.getFirstChild() != null) {
                            channel = index.getChildNodes().item(0).getNodeValue();
                            logger.info("Channel: " + channel);
                        }
                    } else if (attribute.equalsIgnoreCase("Company Code")) {
                        if (index.getFirstChild() != null) {
                            companyCode = index.getChildNodes().item(0).getNodeValue();
                            logger.info("Company Code: " + companyCode);
                        }
                    } else if (attribute.equalsIgnoreCase("Branch Code")) {
                        if (index.getFirstChild() != null) {
                            branchCode = index.getChildNodes().item(0).getNodeValue();
                            logger.info("Branch Code: " + branchCode);
                        }
                    } else if (attribute.equalsIgnoreCase("Insured Name")) {
                        if (index.getFirstChild() != null) {
                            insuredName = index.getChildNodes().item(0).getNodeValue();
                            logger.info("Insured Name: " + insuredName);
                        }
                    } else if (attribute.equalsIgnoreCase("Insured ID Type")) {
                        if (index.getFirstChild() != null) {
                            insuredIdType = index.getChildNodes().item(0).getNodeValue();
                            logger.info("Insured ID Type: " + insuredIdType);
                        }
                    } else if (attribute.equalsIgnoreCase("Insured ID No")) {
                        if (index.getFirstChild() != null) {
                            insuredIdNo = index.getChildNodes().item(0).getNodeValue();
                            logger.info("Insured ID No: " + insuredIdNo);
                        }
                    } else if (attribute.equalsIgnoreCase("Owner Name")) {
                        if (index.getFirstChild() != null) {
                            ownerName = index.getChildNodes().item(0).getNodeValue();
                            logger.info("Owner Name: " + ownerName);
                        }
                    } else if (attribute.equalsIgnoreCase("Owner ID Type")) {
                        if (index.getFirstChild() != null) {
                            ownerIdType = index.getChildNodes().item(0).getNodeValue();
                            logger.info("Owner ID Type: " + ownerIdType);
                        }
                    } else if (attribute.equalsIgnoreCase("Owner ID No")) {
                        if (index.getFirstChild() != null) {
                            ownerIdNo = index.getChildNodes().item(0).getNodeValue();
                            logger.info("Owner ID No: " + ownerIdNo);
                        }
                    } else if (attribute.equalsIgnoreCase("Product Code")) {
                        if (index.getFirstChild() != null) {
                            productCode = index.getChildNodes().item(0).getNodeValue();
                            logger.info("Product Code: " + productCode);
                        }
                    } else if (attribute.equalsIgnoreCase("Submission Date") && index.getFirstChild() != null) {
                        submissionDate = index.getChildNodes().item(0).getNodeValue();
                        logger.info("Submission Date: " + submissionDate);
                    }
                }

                logger.info("Finished Reading XML fields");

                logger.info("Properties File Loaded");
                String newSubmissionDir = config.getNewSubmissionDir();
                Date date = new Date();
                DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                String currentTime = dateFormat.format(date);
                logger.info("currentTime: " + currentTime);
                dateFormat = new SimpleDateFormat("yyyyMMdd");
                String currentDate = dateFormat.format(date);
                logger.info("currentDate: " + currentDate);
                String profile = doc.getElementsByTagName("profile").item(0).getChildNodes().item(0).getNodeValue();
                logger.info("profile: " + profile);
                if (!profile.equalsIgnoreCase("New Submission")) {
                    throw new Exception("Profile is not New Submission");
                }

                logger.info("Creating Parent folder");
                String folder = "";
                String documentDir = "";
                folder = applicationNo + "_" + channel + "_" + companyCode + "_" + branchCode + "_" + insuredName + "_" + insuredIdType + "_" + insuredIdNo + "_" + ownerName + "_" + ownerIdType + "_" + ownerIdNo + "_" + productCode + "_" + submissionDate + "_" + currentTime + "\\";
                documentDir = newSubmissionDir + folder;
                logger.info("releaseDir: " + newSubmissionDir);
                logger.info("folder: " + folder);
                logger.info("documentDir: " + documentDir);
                File file = new File(documentDir);
                file.mkdirs();
                logger.info("Created parent folder");
                logger.info("Creating file " + documentDir + "NB000008_" + currentDate + "_1.txt");
                this.createFile(documentDir + "NB000008_" + currentDate + "_1.txt", applicationNo, currentTime);
                returnXml = this.successMessage(submissionID);

            }
        } catch (Exception var34) {
            returnXml = "<responses><response submission_id=\"" + submissionID + "\"><status>FAILED</status>" + "<error>" + "<errorCode>ERR401</errorCode>" + "<errorDescription>" + var34.getMessage() + "</errorDescription>" + "</error>" + "</response></responses>";
            logger.info("Exception Message: " + var34.getMessage());
            logger.info("StackTrace: " + var34.getStackTrace());
        }

        logger.info(returnXml);
        return returnXml;
    }

    private String validateRequiredField(String xml) {
        logger.info("Validating xml data");
        String submissionID = "";
        String returnXml = "";

        try {
            boolean missingApplicationNumber = true;
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            Document doc = docBuilder.parse(is);
            if (doc.getElementsByTagName("submission").item(0) == null) {
                returnXml = "<responses><response><status>FAILED</status><error><errorCode>ERR100</errorCode><errorDescription>Missing Submission</errorDescription><errorStackTrace></errorStackTrace></error></response></responses>";
                logger.info("There is no submission tag in the request");
                return returnXml;
            } else {
                NodeList submissions = doc.getElementsByTagName("submission");
                Element submission = (Element) submissions.item(0);
                submissionID = submission.getAttribute("submission_id").toString();
                if (!submissionID.equalsIgnoreCase("") && submissionID != null) {
                    if (doc.getElementsByTagName("profile").item(0).getFirstChild() == null) {
                        returnXml = "<responses><response submission_id=\"" + submissionID + "\"><status>FAILED</status>" + "<error>" + "<errorCode>ERR102</errorCode>" + "<errorDescription>Missing Profile</errorDescription>" + "<errorStackTrace></errorStackTrace>" + "</error>" + "</response></responses>";
                        logger.info("Missing Profile");
                        return returnXml;
                    } else {
                        String profile = doc.getElementsByTagName("profile").item(0).getChildNodes().item(0).getNodeValue();
                        if (!profile.equalsIgnoreCase("New Submission")) {
                            returnXml = "<responses><response submission_id=\"" + submissionID + "\"><status>FAILED</status>" + "<error>" + "<errorCode>ERR103</errorCode>" + "<errorDescription>Profile should be New Submission</errorDescription>" + "<errorStackTrace></errorStackTrace>" + "</error>" + "</response></responses>";
                            logger.info("Profile should be New Submission");
                            return returnXml;
                        } else {
                            NodeList indexes = doc.getElementsByTagName("index");

                            for (int i = 0; i < indexes.getLength(); ++i) {
                                Element index = (Element) indexes.item(i);
                                String attribute = index.getAttribute("name").toString();
                                if (attribute.equalsIgnoreCase("Application No") && index.getFirstChild() != null) {
                                    missingApplicationNumber = false;
                                    if (index.getChildNodes().item(0).getNodeValue() == null && index.getChildNodes().item(0).getNodeValue().isEmpty()) {
                                        returnXml = "<responses><response submission_id=\"" + submissionID + "\"><status>FAILED</status>" + "<error>" + "<errorCode>ERR107</errorCode>" + "<errorDescription>Application No should be 15 characters and starts with letter: A </errorDescription>" + "<errorStackTrace></errorStackTrace>" + "</error>" + "</response></responses>";
                                        logger.info("Application No should be 15 characters and starts with letter: A");
                                        return returnXml;
                                    }
                                }
                            }

                            return returnXml;
                        }
                    }
                } else {
                    returnXml = "<responses><response submission_id=\"" + submissionID + "\"><status>FAILED</status>" + "<error>" + "<errorCode>ERR101</errorCode>" + "<errorDescription>Missing Submission ID</errorDescription>" + "<errorStackTrace></errorStackTrace>" + "</error>" + "</response></responses>";
                    logger.info("There is no submission id provided");
                    return returnXml;
                }
            }
        } catch (Exception var14) {
            returnXml = "<responses><response submission_id=\"" + submissionID + "\"><status>FAILED</status>" + "<error>" + "<errorCode>ERR400</errorCode>" + "<errorDescription>" + var14.getMessage() + "</errorDescription>" + "<errorStackTrace>" + var14.getStackTrace() + "</errorStackTrace>" + "</error>" + "</response></responses>";
            logger.info("Exception Message: " + var14.getMessage());
            logger.info("StackTrace: " + var14.getStackTrace());
            return returnXml;
        }
    }

    private String successMessage(String submission_id) {
        return "<responses><response submission_id=\"" + submission_id + "\">" + "<status>OK</status>" + "</response>" + "</responses>";
    }

    private void createFile(String filePath, String applicationNo, String dateTimeS) throws IOException {
        Writer writer = null;

        try {
            writer = new FileWriter(filePath);
            writer.write("Application " + applicationNo + " was submitted on " + dateTimeS);
            logger.info("File" + filePath + " created successfully");
        } catch (IOException var13) {
            throw var13;
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException var12) {
                    logger.info("Error occured during closing the file");
                }
            }
        }
    }
}
