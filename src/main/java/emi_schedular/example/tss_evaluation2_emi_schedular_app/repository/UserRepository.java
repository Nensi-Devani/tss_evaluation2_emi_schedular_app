package emi_schedular.example.tss_evaluation2_emi_schedular_app.repository;

import emi_schedular.example.tss_evaluation2_emi_schedular_app.entity.User;
import emi_schedular.example.tss_evaluation2_emi_schedular_app.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByRole(Role role);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findAllByRole(Role role);

    Optional<User> findByIdAndRole(Long id, Role role);
}
