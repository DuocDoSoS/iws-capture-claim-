package com.rstn.iws.webservice.controller;

import com.rstn.iws.webservice.entity.ClaimEntity;
import com.rstn.iws.webservice.repository.Claimrepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;
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
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ClaimController {

    @Autowired
    private Claimrepository claimRepository;

    @Autowired
    private JmsTemplate jmsTemplate;

    @GetMapping("/getALlClaim")
    public ResponseEntity<List<ClaimEntity>> getAllClaim(){
        return new ResponseEntity<>(claimRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<ClaimEntity>> getClaimById(@PathVariable Long id){
        Optional<ClaimEntity> foundClaim = claimRepository.findById(id);
        if(foundClaim.isPresent()){
            return new ResponseEntity<>(foundClaim,HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(null,HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/insertClaim")
    public ResponseEntity<Object> insertClaim(@RequestBody ClaimEntity newClaim){
        List<ClaimEntity> foundClaim = claimRepository.findByApplicationNumber(newClaim.getApplicationNumber());
        if(foundClaim.size() > 0){
            return new ResponseEntity<>("false because have same applicationNumber",HttpStatus.NOT_IMPLEMENTED);
        }
        else{
            newClaim.setTimeUpdate1();
            newClaim.setTimeCreate(newClaim.formatDate(new Date()));
            System.out.println(newClaim.getTimeCreate() + " " + newClaim.getTimeUpdate());
            ClaimEntity claim = claimRepository.save(newClaim);
            return new ResponseEntity<>(claim,HttpStatus.OK);
        }
    }

    @PutMapping("/upsertClaim/{id}")
    public ResponseEntity<ClaimEntity> upsertClaim(@RequestBody ClaimEntity newClaim,@PathVariable Long id){
        newClaim.setTimeCreate(newClaim.formatDate(new Date()));
        newClaim.setTimeUpdate1();
        ClaimEntity upsertClaim = claimRepository.findById(id)
                .map(claim -> {
                    claim.setDelete(newClaim.isDelete());
                    claim.setPolicyNumber((newClaim.getPolicyNumber()));
                    claim.setApplicationNumber(newClaim.getApplicationNumber());
                    claim.setTimeUpdate();
                    return claimRepository.save(claim);
                }).orElseGet(()->
                        claimRepository.save(newClaim)
                );
        return new ResponseEntity<>(upsertClaim,HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteClaim(@PathVariable Long id){
        boolean exists = claimRepository.existsById(id);
        if(!exists){
            return new ResponseEntity<>("deleted",HttpStatus.NOT_FOUND);
        }
        else {
            claimRepository.deleteById(id);
            return new ResponseEntity<>("delete",HttpStatus.OK);
        }
    }

    @GetMapping("/sentClaim/{id}")
    public ResponseEntity<Object> SendClaim(@PathVariable Long id){
        try {
            Optional<ClaimEntity> foundClaim = claimRepository.findById(id);
            if(foundClaim.isPresent()){
                List<ClaimEntity> claims = claimRepository.findByApplicationNumber(foundClaim.get().getApplicationNumber());
                jmsTemplate.convertAndSend("claim-queue1",claims);
                return new ResponseEntity<>("send",HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>("false sent because not find by id: " + id,HttpStatus.NOT_FOUND);
            }
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/hello")
    public String hello(){
        return "Hello";
    }
}
