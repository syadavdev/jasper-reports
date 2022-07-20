package com.sandi.jasperreports;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.http.HttpHeaders;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@AutoConfigureTestDatabase(replace = NONE)
public class BaseIT {

    @Autowired
    private RSAPrivateKey privateKey;
    @Autowired
    private RSAPublicKey publicKey;

    static MySQLContainer<?> mysql = new MySQLContainer<>(DockerImageName.parse("mysql:5.7.34"))
        .withDatabaseName("test").withUsername("test")
        .withPassword("test")
        .withFileSystemBind("storage/docker/mysql/data", "/var/lib/mysql")
        .withEnv("MYSQL_ROOT_HOST", "%")
        .withCommand("mysqld",
                "--sql_mode=STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION",
                "--lower_case_table_names=1",
                "--group_concat_max_len=2048",
                "--event_scheduler=ON",
                "--character-set-server=utf8mb4",
                "--collation-server=utf8mb4_unicode_ci"
        )
        .withUrlParam("useUnicode", "true")
        .withUrlParam("characterEncoding", "UTF-8")
        .withUrlParam("connectionCollation", "utf8mb4_unicode_ci")
        .withReuse(true);

    static {
        mysql.start();
    }

    protected HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        Map<String, String> claims = new HashMap<>();
        claims.put("username", "test");
        List<String> authorities = Collections.singletonList("VIEW_REPORT");
        claims.put("authorities", authorities.toString());
        claims.put("userId", String.valueOf(1));

        String token = createJwtForClaims("test", claims);
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        return headers;
    }

    private String createJwtForClaims(String subject, Map<String, String> claims){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Instant.now().toEpochMilli());
        calendar.add(Calendar.DATE, 1);

        JWTCreator.Builder jwtBuilder = JWT.create().withSubject(subject);

        //add claims
        claims.forEach(jwtBuilder::withClaim);

        //add expired and etc
        return jwtBuilder
                .withNotBefore(new Date())
                .withExpiresAt(calendar.getTime())
                .sign(Algorithm.RSA256(publicKey, privateKey));
    }

}
