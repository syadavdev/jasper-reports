package java.jasperreports.evaluator;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

import java.io.Serializable;

public class JasperEvaluator implements PermissionEvaluator {

    @Override
    public boolean hasPermission(Authentication authentication, Object screenRight, Object permission) {
        System.out.println("Authentication token" + authentication.getDetails().toString());
        String screenCode = screenRight.toString();
        String right = permission.toString();
        return true;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable serializable, String s, Object o) {
        throw new UnsupportedOperationException();
    }

}