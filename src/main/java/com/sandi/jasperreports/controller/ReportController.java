package com.sandi.jasperreports.controller;

import com.sandi.jasperreports.config.JwtHelper;
import com.sandi.jasperreports.dto.LoginRequest;
import com.sandi.jasperreports.dto.LoginResponse;
import com.sandi.jasperreports.modal.ReportLabel;
import com.sandi.jasperreports.repository.ReportLabelRepository;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

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
    private final JwtHelper jwtHelper;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/report")
    @PreAuthorize("hasAuthority('VIEW_REPORT')")
    public ResponseEntity<String> createReport() throws JRException, SQLException {
        return ResponseEntity.status(HttpStatus.OK).body(generateReport());
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request){
        UserDetails userDetails;
        try{
            userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        }catch(UsernameNotFoundException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
        }

        if(passwordEncoder.matches(request.getPassword(), userDetails.getPassword())){
            Map<String, String> claims = new HashMap<>();
            claims.put("username", request.getUsername());
            List<String> authorities = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            claims.put("authorities", authorities.toString());
            claims.put("userId", String.valueOf(1));
            String jwt = jwtHelper.createJwtForClaims(request.getUsername(), claims);
            LoginResponse response = new LoginResponse();
            response.setJwt(jwt);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
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
