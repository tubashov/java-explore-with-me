package ru.practicum.ewm.repository;


import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.model.User;

import java.util.Collection;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    // 21 Получение информации о пользователях
    List<User> findAllByIdIn(Collection<Long> ids, Pageable pageable);

    @Query("select u.id from User u")
    List<Long> findAllIds();
}
