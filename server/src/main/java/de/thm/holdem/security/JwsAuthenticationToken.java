package de.thm.holdem.security;

import de.thm.holdem.model.user.UserExtra;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
public class JwsAuthenticationToken extends AbstractAuthenticationToken implements Authentication
{


    private final String token;
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
    public Object getPrincipal()
    {
        return principal;
    }

}