package com.scaler.userserviceapr21capstone.services;

import com.scaler.userserviceapr21capstone.models.Token;
import com.scaler.userserviceapr21capstone.models.User;
import com.scaler.userserviceapr21capstone.repositories.TokenRepository;
import com.scaler.userserviceapr21capstone.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;

@Service
public class UserServiceImpl implements UserService
{
//    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long EXPIRATION_TIME_IN_MS = 10 * 60 * 60 * 1000; // 10 hours

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    BCryptPasswordEncoder bCryptPasswordEncoder;
    SecretKey secretKey;

    public UserServiceImpl(UserRepository userRepository,
                           TokenRepository tokenRepository,
                           BCryptPasswordEncoder bCryptPasswordEncoder,
                           SecretKey secretKey) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.secretKey = secretKey;
    }

    @Override
    public User signup(String name, String email, String password)
    {
        if(userRepository.findByEmail(email).isPresent())
        {
            //throw an exception
            return null;
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        return userRepository.save(user);
    }

    @Override
    public Token login(String email, String password)
    {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if(userOptional.isEmpty())
        {
            // throw an exception
            return null;
        }

        User user = userOptional.get();
        if(!bCryptPasswordEncoder.matches(password, user.getPassword()))
        {
            //throw an exception
            return null;
        }

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME_IN_MS);

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("email", user.getEmail());

        String jsonString = Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        Token token = new Token();
        token.setUser(user);
        token.setTokenValue(jsonString);

//        // Alternative way to generate expiry date
//        Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.DATE, 30);
//        Date date = calendar.getTime();

        token.setExpiryAt(expiryDate);
        return token;
    }

    @Override
    public void logout(Token token) {

    }

    @Override
    public User validateToken(String tokenValue)
    {
        if(tokenValue == null || tokenValue.isEmpty())
        {
            return null;
        }

        Claims claims;
        try
        {
            claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(tokenValue)
                .getBody();
        }catch (io.jsonwebtoken.ExpiredJwtException e)
        {
            System.out.println("Token validation failed. Expired JWT token: " + e.getMessage());
            return null;
        }catch (io.jsonwebtoken.JwtException e)
        {
            System.out.println("Token validation failed. Invalid JWT Token: " + e.getMessage());
            return null;
        }

        String email = claims.getSubject();
        if(email == null || email.isEmpty())
        {
            System.out.println("Token validation failed. Email is null or empty");
            return null;
        }

        Optional<User> userOptional = userRepository.findByEmail(email);
        if(userOptional.isEmpty() || userOptional.get().isDeleted())
        {
            System.out.println("Token validation failed. User from email in token does not exist");
            return null;
        }

        return userOptional.get();
    }

    private User validateNonJwtTokenInDB(String tokenValue)
    {
        /*
        * 1. Exists in DB
        * 2. Not deleted
        * 3. Not expired
        * */

        Optional<Token> tokenOptional
                = tokenRepository.findByTokenValueAndDeletedAndExpiryAtGreaterThan(
                        tokenValue, false, new Date());

        if(tokenOptional.isEmpty())
        {
            //throw an exception
            return null;
        }

        Token token = tokenOptional.get();
        return token.getUser();
    }
}
