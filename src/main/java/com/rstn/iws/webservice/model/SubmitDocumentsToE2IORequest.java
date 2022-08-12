package com.rstn.iws.webservice.model;

import javax.activation.DataHandler;
import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "http://webservice.iws.rstn.com", name = "", propOrder = {
        "infoXml"
})
@XmlRootElement(namespace = "http://webservice.iws.rstn.com", name = "submitDocumentsByApplication")
public class SubmitDocumentsToE2IORequest {
    @XmlElement(namespace = "http://webservice.iws.rstn.com", name = "infoXml", required = true)
    private String infoXml;


    public String getInfoXml() {
        return infoXml;
    }

    public void setInfoXml(String infoXml) {
        this.infoXml = infoXml;
    }


}

