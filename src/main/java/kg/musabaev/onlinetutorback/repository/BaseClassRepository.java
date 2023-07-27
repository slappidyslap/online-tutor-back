package kg.musabaev.onlinetutorback.repository;

import kg.musabaev.onlinetutorback.model.BaseClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BaseClassRepository extends JpaRepository<BaseClass, Long> {
}