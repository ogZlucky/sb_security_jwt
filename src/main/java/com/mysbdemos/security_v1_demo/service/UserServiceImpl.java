package com.mysbdemos.security_v1_demo.service;

import com.mysbdemos.security_v1_demo.model.User;
import com.mysbdemos.security_v1_demo.repository.UserRepository;
import com.mysbdemos.security_v1_demo.util.exception.IsAlreadyExistException;
import com.mysbdemos.security_v1_demo.util.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service("UserServiceImpl")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final List<SimpleGrantedAuthority> authorities;


    @Override
    public List<User> getAll() {
        return repository.findAll();
    }

    @Override
    public Optional<User> getById(final UUID id) {
        return repository.findById(id);
    }

    @Override
    public User findByEmail(final String email) {
        return repository.findByEmail(email).orElseThrow(
                () -> new NotFoundException(User.class.getSimpleName(), "Email", email));
    }

    @Override
    public User create(final User user) throws IsAlreadyExistException {
        if (repository.existsByEmailAndId(user.getEmail(), user.getId())) {
            throw new IsAlreadyExistException(User.class, "Email", user.getEmail());
        }
        log.debug("Created new user with email:'{}'", user.getEmail());
        return repository.save(user);
    }

    @Override
    public void deleteById(final UUID id) throws NotFoundException {
        repository.findById(id).ifPresentOrElse(it -> it.setIsEnabled(false),
                () -> {
                    throw new NotFoundException("User", "ID", id);
                });
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
}