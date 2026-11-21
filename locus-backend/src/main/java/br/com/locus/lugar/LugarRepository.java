package br.com.locus.lugar;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LugarRepository extends JpaRepository<Lugar, Long> {

    List<Lugar> findByDestinoId(Long destinoId);
}