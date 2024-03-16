/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.paymentchain.transactions.entities;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Date;
import lombok.Data;

/**
 *
 * @author SantiagoSRP
 */
@Data
@Entity
public class Transaction {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private long id;
    private String reference;
    private String ibanAccount;
    private Date date;
    private double amount;
    /* Transaction commission */
    private double fee;
    private String description;

    public enum Status {
        PENDING("01"),
        LIQUIDATED("02"),
        REJECTED("03"),
        CANCELLED("04");

        private final String status;

        Status(String codigo) {
            this.status = codigo;
        }

        public String getCurrentCode() {
            return status;
        }

        public static Status byValue(String value) {
            for (Status status : values()) {
                if (status.getCurrentCode().equals(value)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Código de estado no válido: " + value);
        }
    }

    public enum Channel {
        WEB("WEB"),
        ATM("CAJERO"),
        OFFICE("OFICINA");

        private final String value;

        Channel(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static Channel byValue(String value) {
            for (Channel channel : values()) {
                if (channel.getValue().equalsIgnoreCase(value)) {
                    return channel;
                }
            }
            throw new IllegalArgumentException("Invalid channel value: " + value);
        }
    }
}
