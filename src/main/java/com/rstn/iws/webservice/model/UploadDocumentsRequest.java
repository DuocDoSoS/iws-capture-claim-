package com.rstn.iws.webservice.model;

import javax.activation.DataHandler;
import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "http://webservice.iws.rstn.com",name = "", propOrder = {
        "infoXml","dataHandlers"
})
@XmlRootElement(namespace = "http://webservice.iws.rstn.com", name = "uploadDocuments")
public class UploadDocumentsRequest {
    @XmlElement(namespace = "http://webservice.iws.rstn.com",name = "infoXml",required = true)
    private String infoXml;

    @XmlElement(namespace = "http://webservice.iws.rstn.com",name = "dataHandlers",required = true)
    private DataHandler[] dataHandlers;

    public String getInfoXml() {
        return infoXml;
    }

    public void setInfoXml(String infoXml) {
        this.infoXml = infoXml;
    }

    public DataHandler[] getDataHandlers() {
        return dataHandlers;
    }

    public void setDataHandlers(DataHandler[] dataHandlers) {
        this.dataHandlers = dataHandlers;
    }
}
