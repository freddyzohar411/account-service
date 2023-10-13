package com.avensys.rts.accountservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity(name = "accountNew")
@Table(name = "account_new")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountNewEntity {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column (name = "mark_up")
    private String markUp;

    @Column (name = "msp")
    private String msp;

    @ManyToOne
    @JoinColumn(name = "parent_company", referencedColumnName = "id")
    private AccountEntity parentCompany;

    @Column (name = "is_deleted", columnDefinition = "boolean default false")
    private boolean isDeleted;

    @Column (name = "is_draft")
    private boolean isDraft = true;

    @Column (name = "account_number", length = 10)
    private String accountNumber;

    @Column (name = "created_by")
    private Integer createdBy;

    @CreationTimestamp
    @Column (name = "created_at")
    private LocalDateTime createdAt;

    @Column (name = "updated_by")
    private Integer updatedBy;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name= "form_id")
    private Integer formId;

    @Column(name = "form_submission_id")
    private Integer formSubmissionId;

    @Column(name = "commercial_form_id")
    private Integer commercialFormId;

    @Column(name = "commercial_form_submission_id")
    private Integer commercialFormSubmissionId;
}
