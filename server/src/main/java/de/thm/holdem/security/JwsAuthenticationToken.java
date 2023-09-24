package de.thm.holdem.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Authentication token for web socket connections.
 *
 * @author Valentin Laucht
 * @version 1.0
 */
public class JwsAuthenticationToken extends AbstractAuthenticationToken implements Authentication
{

    /** The token. */
    private final String token;

    /** The user id from token "sub" claim. */
    private final String principal;

    public JwsAuthenticationToken(String token) {
        this(token, null, null);
    }

    public JwsAuthenticationToken(String token, String principal, Collection<GrantedAuthority> authorities)
    {
        super(authorities);
        this.token = token;
        this.principal = principal;
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}
