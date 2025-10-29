package com.training.portal.repository;

import com.training.portal.model.User;
import com.training.portal.model.Course;
import com.training.portal.model.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {
    boolean existsByUserAndCourse(User user, Course course);
    List<UserBadge> findByUser(User user);
}
