package com.avensys.rts.accountservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/***
 *
 * @author Koh He Xiang
 * This is the entity class for the account table in the database
 *
 */

@Entity
@Table(name = "accounts")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountEntity {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "status", length = 10 , nullable = false)
    private String status;

    @Column(name = "rating", length = 5, nullable = false)
    private String rating;

    @Column(name = "industry")
    private int industry;

    @Column(name = "sub_industry")
    private int subIndustry;

    @Column(name = "no_of_emp")
    private int noOfEmployees;

    @Column(name = "revenue_amt")
    private double revenueAmt;

    @Column(name = "revenue_cur")
    private int revenueCur;

    @Column(name = "parent_company")
    private int parentCompany;

    @Column(name = "website")
    private String website;

    @Column(name = "source", length = 50 , nullable = false)
    private String accountSource;

    @Column(name = "landline_country")
    private int landlineCountry;

    @Column(name = "landline_number")
    private int landlineNumber;

    @Column(name = "secondary_owner", length = 50)
    private String secondaryOwner;

    @Column(name = "msa", columnDefinition = "smallint")
    private int msa;

    @Column (name = "sales_name", length = 50 , nullable = false)
    private String salesName;

    @Column (name = "lead_source", length = 50)
    private String leadSource;

    @Column (name = "account_name", length = 50 , nullable = false)
    private String accountName;

    @Column (name = "address")
    private int address;

    @Column (name = "billing_address")
    private int billingAddress;

    @Column (name = "remarks", columnDefinition = "text")
    private String remarks;

    @Column (name = "is_deleted", columnDefinition = "boolean default false")
    private boolean isDeleted;

    @Column (name = "msp")
    private double msp;

    @Column (name = "markup")
    private double markup;

}
