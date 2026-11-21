package br.com.locus.admin;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AdminEmailRepository extends JpaRepository<AdminEmail, Long> {

    Optional<AdminEmail> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    List<AdminEmail> findAllByOrderByEmailAsc();
}
