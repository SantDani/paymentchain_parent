/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/RestController.java to edit this template
 */
package com.paymentchain.transactions.controller;


import com.paymentchain.transactions.entities.Transaction;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import com.paymentchain.transactions.repository.TransactionRepository;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author SantiagoSRP
 */
@RestController
@RequestMapping("/transaction")
public class TransactionRestController {

    @Autowired
    TransactionRepository transactionRepository;
    

    @GetMapping()
    public ResponseEntity<List<Transaction>> list() {
        List<Transaction> transactions = transactionRepository.findAll();
        
        if(transactions == null || transactions.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(transactions);

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable(name = "id") long id) {
        Optional<Transaction> customer = transactionRepository.findById(id);
        if (customer.isPresent()) {
            return new ResponseEntity<>(customer.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    @GetMapping("/customer/transactions/{ibanAccount}")
    public List<Transaction> get(@PathVariable(name = "ibanAccount") String ibanAccount) {
      return transactionRepository.findByIbanAccount(ibanAccount);      
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable(name = "id") long id, @RequestBody Transaction input) {
        Optional<Transaction> optionalTransaction = transactionRepository.findById(id);
        if (optionalTransaction.isPresent()) {
            Transaction newTransaction = optionalTransaction.get();
            newTransaction.setIbanAccount(input.getIbanAccount());
            newTransaction.setAmount(input.getAmount());
            newTransaction.setDate(input.getDate());
            newTransaction.setDescription(input.getDescription());
            newTransaction.setFee(input.getFee());
            newTransaction.setReference(input.getReference());
            Transaction save = transactionRepository.save(newTransaction);
            return new ResponseEntity<>(save, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<?> post(@RequestBody Transaction input) {
        Transaction save = transactionRepository.save(input);
        return new ResponseEntity<>(save, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable(name = "id") long id) {
        Optional<Transaction> optionalTransaction = transactionRepository.findById(id);
        if (optionalTransaction.isPresent()) {
            transactionRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }   
    }
}
