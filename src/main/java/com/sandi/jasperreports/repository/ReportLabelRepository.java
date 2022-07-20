package com.sandi.jasperreports.repository;

import com.sandi.jasperreports.modal.ReportLabel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportLabelRepository extends JpaRepository<ReportLabel, Integer> {
}
