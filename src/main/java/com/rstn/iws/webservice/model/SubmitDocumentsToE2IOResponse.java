package com.rstn.iws.webservice.model;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "http://webservice.iws.rstn.com",name = "", propOrder = {
        "returnXml"
})
@XmlRootElement(namespace = "http://webservice.iws.rstn.com",name = "submitDocumentsByApplicationResponse")
public class SubmitDocumentsToE2IOResponse {

    @XmlElement(namespace = "http://webservice.iws.rstn.com",name = "return", required = true)
    String  returnXml;

    public String getReturnXml() {
        return returnXml;
    }

    public void setReturnXml(String returnXml) {
        this.returnXml = returnXml;
    }
}
