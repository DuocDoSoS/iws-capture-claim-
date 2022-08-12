package com.rstn.iws.webservice.consumer.component;

import com.rstn.iws.webservice.entity.ClaimEntity;
import jcifs.CIFSContext;
import jcifs.Configuration;
import jcifs.config.PropertyConfiguration;
import jcifs.context.BaseContext;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
import java.util.Date;
import java.util.Properties;

import static org.apache.commons.io.FileUtils.deleteDirectory;

@Component
public class ClaimConsumer {

    private String releaseFolder = "";
    private String documentDir = "";
    private String folder = "";
    private String serverDir = "";
    private String serverReleaseDir = "";

    private static String returnResult = "";

    private static final Logger LOGGER = LoggerFactory.getLogger(ClaimConsumer.class);

    @JmsListener(destination = "claim-queue1")
    public void listenerClaim(ClaimEntity claim) throws IOException, ParserConfigurationException, TransformerException {
        Date date = new Date();
        DateFormat dateTimeFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String currentTime = dateTimeFormat.format(date);
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String currentDate = dateFormat.format(date);
        if(validateStatus(claim)){
            LOGGER.info(validateClaim(claim));
        }
        else{
            setReleaseFolder(claim,currentTime);
            File file = new File(documentDir);
            file.mkdirs();
            writeFileDocumnent(claim,currentDate,documentDir);
            writeFileXML(claim,documentDir);

            moveFolder(documentDir,serverDir);
        }
    }

    public void writeFileDocumnent(ClaimEntity claim,String CurrentDate,String documentDir) throws IOException {
        String fileName = "";
        fileName = claim.getId() + "_" + CurrentDate + "_" + claim.getPolicyNumber() + ".docx";
        File file = new File(documentDir + fileName);
        OutputStream outputStream = new FileOutputStream(file);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
        outputStreamWriter.write(claim.toString());
        outputStreamWriter.flush();
        outputStreamWriter.close();

    }
    public void setReleaseFolder(ClaimEntity claim,String currentTime){
        releaseFolder = "D:\\untitled\\";
        serverReleaseDir = "//192.168.0.210/data/untitled/";
        folder = claim.getId() + "_" + claim.getPolicyNumber() + "_" + currentTime;
        documentDir = releaseFolder + folder + "\\";
        serverDir = serverReleaseDir + folder + "/";

    }

    public void ElementInsert(String value, String string, Document document, Element root){
        Element indexXML = document.createElement("index");
        root.appendChild(indexXML);
        Attr attr = document.createAttribute("name");
        attr.setValue(value);
        indexXML.setAttributeNode(attr);
        indexXML.appendChild(document.createTextNode(string));
    }
    public void writeFileXML(ClaimEntity claimEntity, String documentDir)throws ParserConfigurationException, TransformerException, IOException {
        try{
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();
            Element root = document.createElement("data");
            document.appendChild(root);
            ElementInsert("id",claimEntity.getId().toString(),document,root);
            ElementInsert("applicationNumber",claimEntity.getApplicationNumber(),document,root);
            ElementInsert("policyNumber",claimEntity.getPolicyNumber(),document,root);
            ElementInsert("timeCreate",claimEntity.getTimeCreate(),document,root);
            ElementInsert("timeUpdate",claimEntity.getTimeUpdate(),document,root);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new FileOutputStream(documentDir + "propertyConfig.xml"));
            transformer.transform(domSource, streamResult);
            streamResult.getOutputStream().close();
        }catch (Exception e){
            System.out.println(e);
        }
    }

    public void moveFolder(String sourceFilePath, String targetFilePath) throws IOException {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            LOGGER.info("Start login");
            String user = "Administrator";
            String pass = "Admin@123";
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
            LOGGER.info("Login success");
            LOGGER.info(remoteFolderPath);
            LOGGER.info(pathSourceFile);
            LOGGER.info("Get list file in folder and copy to server");
            File folder = new File(pathSourceFile);
            File[] listOfFiles = folder.listFiles();
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    LOGGER.info("File " + file.getName());
                    inputStream = new FileInputStream(pathSourceFile + file.getName());
                    remoteFolder = new SmbFile("smb:" + remoteFolderPath + file.getName(), authed1);
                    outputStream = remoteFolder.getOutputStream();
                    copyFile(inputStream, outputStream);
                }
            }
            LOGGER.info("Get list file in folder and copy to server success");
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
                outputStream.flush();
            }
//            deleteDirectory(folder);
        } catch (
                IOException e) {
            LOGGER.error("Error: " + ExceptionUtils.getStackTrace(e));
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

    public Boolean validateStatus(ClaimEntity claim){
        if(claim.getApplicationNumber() == null){
            returnResult = "<responses><response><status>FAILED</status>" +
                    "<error>" +
                    "<errorCode>ERR100</errorCode>" +
                    "<errorDescription>FALSE</errorDescription>" +
                    "<errorStackTrace></errorStackTrace>" +
                    "</error>" +
                    "</response></responses>";
            LOGGER.info(returnResult);
            return true;
        }
        else if(claim.getPolicyNumber() == null) {
            returnResult = "<responses><response><status>FAILED</status>" +
                    "<error>" +
                    "<errorCode>ERR100</errorCode>" +
                    "<errorDescription>FALSE</errorDescription>" +
                    "<errorStackTrace></errorStackTrace>" +
                    "</error>" +
                    "</response></responses>";
            LOGGER.info(returnResult);
            return true;
        }
        else if(claim.getTimeCreate() == null) {
            returnResult = "<responses><response><status>FAILED</status>" +
                    "<error>" +
                    "<errorCode>ERR100</errorCode>" +
                    "<errorDescription>FALSE</errorDescription>" +
                    "<errorStackTrace></errorStackTrace>" +
                    "</error>" +
                    "</response></responses>";
            LOGGER.info(returnResult);
            return true;
        }
        returnResult = "<responses><response><status>OK</status>";
        LOGGER.info(returnResult);
        return false;
    }

    public String validateClaim(ClaimEntity claim){
        if(claim.getApplicationNumber() == null){
            returnResult = "<responses><response><status>FAILED</status>" +
                    "<error>" +
                    "<errorCode>ERR100</errorCode>" +
                    "<errorDescription>FALSE</errorDescription>" +
                    "<errorStackTrace></errorStackTrace>" +
                    "</error>" +
                    "</response></responses>";
            return returnResult;
        }
        else if(claim.getPolicyNumber() == null) {
            returnResult = "<responses><response><status>FAILED</status>" +
                    "<error>" +
                    "<errorCode>ERR100</errorCode>" +
                    "<errorDescription>FALSE</errorDescription>" +
                    "<errorStackTrace></errorStackTrace>" +
                    "</error>" +
                    "</response></responses>";
            return returnResult;
        }
        else if(claim.getTimeCreate() == null) {
            returnResult = "<responses><response><status>FAILED</status>" +
                    "<error>" +
                    "<errorCode>ERR100</errorCode>" +
                    "<errorDescription>FALSE</errorDescription>" +
                    "<errorStackTrace></errorStackTrace>" +
                    "</error>" +
                    "</response></responses>";
            return returnResult;
        }
        returnResult = "<responses><response><status>OK</status>";
        return returnResult;
    }
}
