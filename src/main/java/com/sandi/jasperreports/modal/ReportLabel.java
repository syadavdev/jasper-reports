package com.sandi.jasperreports.modal;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "report_label")
@Getter
public class ReportLabel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "label_name")
    private String labelName;

    @Column(name = "label_value")
    private String labelValue;

}
