package com.rstn.iws.webservice.endpoint;

import com.rstn.iws.webservice.model.SubmitDocumentsToE2IORequest;
import com.rstn.iws.webservice.model.SubmitDocumentsToE2IOResponse;
import com.rstn.iws.webservice.model.UploadDocumentsRequest;
import com.rstn.iws.webservice.model.UploadDocumentsResponse;
import com.rstn.iws.webservice.repository.CaptureRepository;

import com.rstn.iws.webservice.repository.SubmitDocumentsToE2IO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

@Endpoint
public class CaptureEndpoint {
    private static final String NAMESPACE_URI = "http://webservice.iws.rstn.com";
    private static final Logger LOGGER = LoggerFactory.getLogger(CaptureEndpoint.class);
    @Autowired
    private CaptureRepository captureRepository;

    @Autowired
    private SubmitDocumentsToE2IO submitDocumentsToE2IO;

    @Autowired
    public CaptureEndpoint(CaptureRepository captureRepository) {
        this.captureRepository = captureRepository;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "uploadDocuments")
    @ResponsePayload
    public UploadDocumentsResponse uploadDocumentsResponse(@RequestPayload UploadDocumentsRequest uploadDocumentsRequest) {
        LOGGER.info("Start run Endpoint");
        UploadDocumentsResponse uploadDocumentsResponse = new UploadDocumentsResponse();
        uploadDocumentsResponse.setReturnXml(captureRepository.uploadDocuments(uploadDocumentsRequest.getInfoXml(), uploadDocumentsRequest.getDataHandlers()));
        LOGGER.info("End run Endpoint");
        return uploadDocumentsResponse;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "submitDocumentsByApplication")
    @ResponsePayload
    public SubmitDocumentsToE2IOResponse SubmitDocumentsToE2IO(@RequestPayload SubmitDocumentsToE2IORequest submitDocumentsToE2IORequest) throws ParserConfigurationException, IOException, SAXException {
        LOGGER.info("Start run Endpoint SubmitDocumentsToE2IO");
        SubmitDocumentsToE2IOResponse uploadDocumentsResponse = new SubmitDocumentsToE2IOResponse();
        uploadDocumentsResponse.setReturnXml(submitDocumentsToE2IO.submitDocumentsByApplication(submitDocumentsToE2IORequest.getInfoXml()));
        LOGGER.info("End run Endpoint SubmitDocumentsToE2IO");
        return uploadDocumentsResponse;
    }


}
