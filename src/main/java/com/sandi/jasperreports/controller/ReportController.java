package com.sandi.jasperreports.controller;

import com.sandi.jasperreports.controller.modal.ReportLabel;
import com.sandi.jasperreports.controller.repository.ReportLabelRepository;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequestMapping
@RestController
@RequiredArgsConstructor
public class ReportController {

    private final ReportLabelRepository repository;

    @GetMapping("/report")
    public ResponseEntity<String> createReport() throws JRException, SQLException {
        return ResponseEntity.status(HttpStatus.OK).body(generateReport());
    }

    String generateReport() throws JRException, SQLException {
        JasperReport report = getReport();
        JasperPrint jasperPrint = JasperFillManager.fillReport(report, getParams());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }

    JasperReport getReport() throws JRException {
        InputStream reportStream = getClass().getResourceAsStream("/templates/Customer_Report.jrxml");
        return JasperCompileManager.compileReport(reportStream);
    }

    Map<String, Object> getLabels(){
        List<ReportLabel> reportLabels = repository.findAll();
        return reportLabels.stream().collect(Collectors.toMap(ReportLabel::getLabelName, ReportLabel::getLabelValue));
    }

    Map<String, Object> getParams() throws SQLException {
        Map<String, Object> params = new HashMap<>();
        params.put("reportName", "Customer Report");
        params.put("REPORT_CONNECTION", getConnection());
        params.put("labels", getLabels());
        return params;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/customer_svc?useUnicode=true" +
                "&characterEncoding=UTF-8&useSSL=false&user=root&password=root");
    }

}
