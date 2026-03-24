package ifb.edu.br.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.io.Decoders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import jakarta.annotation.PostConstruct;
@Service
public class TokenService {
    
    @Value("${api.security.token.secret}")
    private String secretKeyBase64;
    
    private SecretKey secretKey;
    private final long expirationTime = 86400000; // 24 horas
    
    
    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKeyBase64));
    }
    
    public String gerarToken(String username, Map<String, Object> claims) {
        return Jwts.builder()
            .claims(claims)
            .subject(username)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expirationTime))
            .signWith(secretKey)
            .compact();
    }
    
    public Claims validarToken(String token) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
}
