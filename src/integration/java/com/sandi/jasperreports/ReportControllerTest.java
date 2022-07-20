package com.sandi.jasperreports;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class ReportControllerTest extends BaseIT {

    @Autowired
    private TestRestTemplate restClient;

    @Test
    void shouldFetchReport(){
        //when
        final ResponseEntity<String> entity = restClient.exchange (
                "/report",
                HttpMethod.GET,
                new HttpEntity<>(null, getHeaders()),
                String.class
        );

        //then
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(entity.getBody()).isNotNull();
    }

}
