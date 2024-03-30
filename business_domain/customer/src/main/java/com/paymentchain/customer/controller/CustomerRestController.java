/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/RestController.java to edit this template
 */
package com.paymentchain.customer.controller;

import com.paymentchain.customer.bussines.transactions.BussinesTransaction;
import com.paymentchain.customer.entities.Customer;
import com.paymentchain.customer.repository.CustomerRepository;
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
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author SantiagoSRP
 */
@RestController
@RequestMapping("/customer")
public class CustomerRestController {

    @Autowired
    CustomerRepository customerRepository;
    
    @Autowired
    BussinesTransaction bussinesTransaction;
    
    @GetMapping()
    public ResponseEntity<List<Customer>> list() {
        List<Customer> customers = customerRepository.findAll();
        
        if(customers == null || customers.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable(name = "id") long id) {
        Optional<Customer> customer = customerRepository.findById(id);
        if (customer.isPresent()) {
            return new ResponseEntity<>(customer.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable(name = "id") long id, @RequestBody Customer input) {
        Optional<Customer> optionalcustomer = customerRepository.findById(id);
        if (optionalcustomer.isPresent()) {
            Customer newcustomer = optionalcustomer.get();
            newcustomer.update(
                    input.getName(),
                    input.getPhone(), 
                    input.getCode(), 
                    input.getIban(), 
                    input.getSurname(), 
                    input.getAddress());
            Customer save = customerRepository.save(newcustomer);
            return new ResponseEntity<>(save, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<?> post(@RequestBody Customer input) {
        Customer save = bussinesTransaction.save(input);
        return new ResponseEntity<>(save, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable(name = "id") long id) {
        customerRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
     
    @GetMapping("/full")
    public Customer getByCode(@RequestParam(name = "code") String code){
        return bussinesTransaction.getByCode(code);
    }
}
