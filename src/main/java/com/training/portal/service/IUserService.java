package com.training.portal.service;

import com.training.portal.model.User;
import java.util.List;
import java.util.Optional;

public interface IUserService {
    List<User> findAll();
    Optional<User> findByEmail (String email);
    User save (User user);
    void delete(Long id);
    User getUserById(long id);


}

