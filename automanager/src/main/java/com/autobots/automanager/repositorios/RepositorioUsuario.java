package com.autobots.automanager.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import com.autobots.automanager.entidades.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;

public interface RepositorioUsuario extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByEmailIgnoreCase(String email);
    @EntityGraph(attributePaths = { "credenciais", "perfis" })
    Optional<Usuario> findWithCredenciaisByEmailIgnoreCase(String email);
} 