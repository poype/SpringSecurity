package com.poype.spring_security.service;

import com.poype.spring_security.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DBUserDetailServiceImpl implements UserDetailsService {

    private final Map<String, User> cache = new HashMap<>();

    public DBUserDetailServiceImpl() {
        cache.put("jack", new User("jack", "{bcrypt}$2a$10$PMXeB.WzaukbRM519yTLCOjz1Tv6feBU7oy5doUF59ZYvJ3C1nUD2"));
        cache.put("lucy", new User("lucy", "{bcrypt}$2a$10$PMXeB.WzaukbRM519yTLCOjz1Tv6feBU7oy5doUF59ZYvJ3C1nUD2"));
        cache.put("tom", new User("tom", "{bcrypt}$2a$10$PMXeB.WzaukbRM519yTLCOjz1Tv6feBU7oy5doUF59ZYvJ3C1nUD2"));
        cache.put("terry", new User("terry", "{bcrypt}$2a$10$PMXeB.WzaukbRM519yTLCOjz1Tv6feBU7oy5doUF59ZYvJ3C1nUD2"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (!cache.containsKey(username)) {
            throw new UsernameNotFoundException(username);
        }

        return cache.get(username);
    }
}
