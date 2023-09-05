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

@Entity(name = "account")
@Table(name = "account")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountEntity {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column (name = "sales_name", length = 50)
    private String salesName;

    @Column(name = "status", length = 10 , nullable = false)
    private String status;

    @Column(name = "rating", length = 10, nullable = false)
    private String rating;

    @Column(name = "industry")
    private Integer industry;

    @Column(name = "sub_industry")
    private Integer subIndustry;

    @Column(name = "no_of_emp")
    private Integer noOfEmployees;

    @Column(name = "revenue_amt")
    private Double revenueAmt;

    @Column(name = "revenue_cur")
    private Integer revenueCur;

    @ManyToOne
    @JoinColumn(name = "parent_company", referencedColumnName = "id")
    private AccountEntity parentCompany;

    @Column(name = "website")
    private String website;

    @Column(name = "source", length = 50 , nullable = false)
    private String accountSource;

    @Column(name = "landline_country")
    private Integer landlineCountry;

    @Column(name = "landline_number")
    private Integer landlineNumber;

    @Column(name = "secondary_owner", length = 50)
    private String secondaryOwner;

    @Column(name = "msa", columnDefinition = "smallint")
    private Integer msa;

    @Column (name = "lead_sales_name", length = 50 , nullable = false)
    private String leadSalesName;

    @Column (name = "lead_source", length = 50)
    private String leadSource;

    @Column (name = "account_name", length = 50 , nullable = false)
    private String accountName;

    @Column (name = "address")
    private Integer address;

    @Column (name = "billing_address")
    private Integer billingAddress;

    @Column (name = "remarks", columnDefinition = "text")
    private String remarks;

    @Column (name = "is_deleted", columnDefinition = "boolean default false")
    private boolean isDeleted;

    @Column (name = "msp")
    private Double msp;

    @Column (name = "markup")
    private Double markup;

}
