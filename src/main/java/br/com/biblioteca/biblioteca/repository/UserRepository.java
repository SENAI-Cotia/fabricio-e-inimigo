package br.com.biblioteca.biblioteca.repository;

import br.com.biblioteca.biblioteca.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{

        Optional<User> findByEmail(String email);
        Optional<User> findById(Long id);
        @Query("SELECT livros FROM User u JOIN u.livrosAlugados livros")
        List<Long> findAllLivrosAlugados();
        boolean existsByLivrosAlugadosContaining(Long idLivro);
        List<User> findByEmailContainingIgnoreCase(String email);

}
