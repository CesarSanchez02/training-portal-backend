package com.training.portal.repository;

import com.training.portal.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
    Optional<Category> findById(Long id);

}
