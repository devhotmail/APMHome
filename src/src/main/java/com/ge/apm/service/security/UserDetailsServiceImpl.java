package com.ge.apm.service.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.UserAccount;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.util.StringUtil;

/**
 * An implementation of Spring Security's UserDetailsService.
 */
@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger log = Logger.getLogger(UserDetailsServiceImpl.class);

    private UserAccountRepository accountRepository;

    public UserDetailsServiceImpl() {
    }

    @Autowired
    public UserDetailsServiceImpl(UserAccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Retrieve an account depending on its login this method is not case sensitive.<br>
     * use <code>obtainAccount</code> to match the login to either email, login or whatever is your login logic
     *
     * @param login the account login
     * @return a Spring Security userdetails object that matches the login
     * @see #obtainAccount(String)
     * @throws UsernameNotFoundException when the user could not be found
     * @throws DataAccessException when an error occured while retrieving the account
     */
    @Transactional
    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException, DataAccessException {
        if (login == null || login.trim().isEmpty()) {
            throw new UsernameNotFoundException("Empty login");
        }

        if (log.isDebugEnabled()) {
            log.debug("Security verification for user '" + login + "'");
        }

        UserAccount account = obtainAccount(login);

        if (account == null) {
            if (log.isInfoEnabled()) {
                log.info("Account " + login + " could not be found");
            }
            throw new UsernameNotFoundException("account " + login + " could not be found");
        }

        Collection<GrantedAuthority> grantedAuthorities = obtainGrantedAuthorities(login);

        if (grantedAuthorities == null) {
            grantedAuthorities = toGrantedAuthorities(account.getRoleNames());
        }

        String password = obtainPassword(login);

        if (password == null) {
            password = account.getPassword();
        }

        boolean enabled = true;
        boolean accountNonExpired = true;
        boolean credentialsNonExpired = true;
        boolean accountNonLocked = true;

        return new org.springframework.security.core.userdetails.User(login, password, enabled, accountNonExpired,
                credentialsNonExpired, accountNonLocked, grantedAuthorities);
    }

    /**
     * Return the account depending on the login provided by spring security.
     * @return the account if found
     */
    protected UserAccount obtainAccount(String login) {
        return accountRepository.getByLoginName(login);
    }

    /**
     * Returns null. Subclass may override it to provide their own granted authorities.
     */
    protected Collection<GrantedAuthority> obtainGrantedAuthorities(String username) {
        return null;
    }

    /**
     * Returns null. Subclass may override it to provide their own password.
     */
    protected String obtainPassword(String username) {
        return null;
    }

    public static Collection<GrantedAuthority> toGrantedAuthorities(List<String> roles) {
        List<GrantedAuthority> result = new ArrayList<GrantedAuthority>();

        for (String role : roles) {
            result.add(new GrantedAuthorityImpl(role));
        }

        return result;
    }
}