package com.messager.messager.security;

import com.messager.messager.model.Account;
import com.messager.messager.service.AccountService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Arrays; 

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private AccountService accountService; // Assuming AccountService is now named AuthService

    private static final String[] PUBLIC_PATHS = {
        "/api/auth/**",
        "/h2-console/**",
        "/swagger-ui/**",
        "/v3/api-docs/**"
        // Any other paths that should NOT require JWT authentication
    };

    /**
     * Determines whether this filter should NOT be applied to the given request.
     * If this method returns true, doFilterInternal is skipped.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String requestURI = request.getRequestURI();
        boolean isPublicPath = Arrays.stream(PUBLIC_PATHS)
                .anyMatch(publicPath -> pathMatcher.match(publicPath, requestURI));

        if (isPublicPath) {
            logger.debug("Skipping JWT filter for public path: {}", requestURI);
        } else {
             logger.debug("Applying JWT filter for path: {}", requestURI);
        }

        return isPublicPath; // Return true if the path IS public (should NOT filter)
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // If shouldNotFilter returned false, we are here and need to process the token
        logger.debug("Executing doFilterInternal for {}", request.getRequestURI());

        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                String username = tokenProvider.getUsernameFromJWT(jwt);

                // This method should fetch your Account entity based on username
                Account account = accountService.loadAccountByUsername(username);

                if (account != null) {
                    // Derive authorities from your Account entity
                    Set<String> roleStrings = account.getRoles(); // Assuming getRoles() method
                    Collection<? extends GrantedAuthority> authorities = roleStrings.stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

                    // Create Authentication token with your Account object as principal
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    account,       // Principal (your custom Account entity)
                                    null,          // Credentials (null after verification)
                                    authorities    // Authorities
                            );

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Set the Authentication object in the SecurityContextHolder
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                     logger.debug("User '{}' authenticated successfully. Setting security context.", username);
                } else {
                    logger.warn("Account not found for username '{}' from JWT, though token was valid. Authentication NOT set.", username);
                     // Importantly, do NOT set authentication if the user is not found
                     // SecurityContextHolder.clearContext(); // Optional: ensure context is clear
                }
            } else {
                logger.debug("No valid JWT token found or token validation failed for {}. Proceeding without authentication.", request.getRequestURI());
            }
        } catch (Exception ex) {
             // Catch exceptions during token processing (e.g., expired token, invalid signature)
            logger.error("Authentication error during JWT processing for {}: {}", request.getRequestURI(), ex.getMessage());
            // Don't set authentication if an error occurs
             // SecurityContextHolder.clearContext(); // Optional: ensure context is clear
        }

        // Continue the filter chain regardless of whether authentication was successful or not.
        // Spring Security's authorization filters later in the chain will check if
        // the path requires authentication and if the user has the necessary authorities.
        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

     // Add setters if you are manually instantiating the filter in SecurityConfig
     // (This is NOT needed if it's a @Component and @Autowired)
     // public void setTokenProvider(JwtTokenProvider tokenProvider) { this.tokenProvider = tokenProvider; }
     // public void setAuthService(AuthService authService) { this.authService = authService; }
}