package br.com.biblioteca.biblioteca.repository;

import br.com.biblioteca.biblioteca.models.Livro;
import br.com.biblioteca.biblioteca.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LivroRepository extends JpaRepository<Livro, Long> {

    Optional<Livro> findByNome(String nome);
    List<Livro> findByGenero(String genero);
    List<Livro> findByNomeContainingIgnoreCase(String nome);

}
