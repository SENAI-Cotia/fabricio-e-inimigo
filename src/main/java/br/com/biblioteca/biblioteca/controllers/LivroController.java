package br.com.biblioteca.biblioteca.controllers;

import br.com.biblioteca.biblioteca.Enums.Cargo;
import br.com.biblioteca.biblioteca.Enums.Status;
import br.com.biblioteca.biblioteca.models.Livro;
import br.com.biblioteca.biblioteca.models.User;
import br.com.biblioteca.biblioteca.repository.LivroRepository;
import br.com.biblioteca.biblioteca.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/home")
public class LivroController {
    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String getLivro(Model model,
                           @RequestParam(value = "genero", required = false) String genero,
                           @RequestParam(value = "nome", required = false) String nome){
        List<Livro> livros = livroRepository.findAll();

        if(nome != null && !nome.isEmpty()){
            livros = livroRepository.findByNomeContainingIgnoreCase(nome);
        }

        if(genero != null && !genero.isEmpty() && !genero.equals("todos")){
            livros = livros.stream()
                    .filter(l -> l.getGenero().equals(genero))
                    .toList();
        }

        List<String> generos = livroRepository.findAll()
                .stream()
                .map(Livro::getGenero)
                .distinct()
                .toList();

        model.addAttribute("livros", livros);
        model.addAttribute("generos", generos);
        model.addAttribute("livrosAlugados", userRepository.findAllLivrosAlugados());
        return "Livro";
    }

    @GetMapping( "/cadastroLivro")
    public String cadastroLivro(Model model){
        model.addAttribute("livro", new Livro());
        return "CadastroLivro";
    }

    @GetMapping("/editarLivro/{id}")
    public String livroEditar(@PathVariable Long id, Model model){

        Livro livro = livroRepository.findById(id).orElseThrow(() -> new RuntimeException("Livro não encontrado"));
        model.addAttribute("livro", livro);
        return "CadastroLivro";
    }

    @PostMapping("/salvarLivro")
    public String salvar(@ModelAttribute Livro livroCadastro, RedirectAttributes redirectAttributes){
        if (livroCadastro.getNome().trim().isEmpty() || livroCadastro.getAutor().trim().isEmpty() || livroCadastro.getGenero().trim().isEmpty() || livroCadastro.getUrlCapa().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Informe o nome, o autor e o genero do livro");

            return "redirect:/home/cadastroLivro";

        }

        if(livroCadastro.getId() == null) {
            livroRepository.save(livroCadastro);

        }
        else{
            Livro livroQueVeioDoBancoDeDados = livroRepository.findById(livroCadastro.getId()).orElseThrow(() -> new RuntimeException("Livro não encontrado"));

            livroQueVeioDoBancoDeDados.setNome(livroCadastro.getNome());
            livroQueVeioDoBancoDeDados.setAutor(livroCadastro.getAutor());
            livroQueVeioDoBancoDeDados.setGenero(livroCadastro.getGenero());
            livroQueVeioDoBancoDeDados.setUrlCapa(livroCadastro.getUrlCapa());

            livroRepository.save(livroQueVeioDoBancoDeDados);
        }

        redirectAttributes.addFlashAttribute("mensagemSucesso","Livro cadastrado com sucesso! ");
        return "redirect:/home";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id){
        livroRepository.deleteById(id);

        return "redirect:/home";
    }

    @PostMapping("/alugar/{idLivro}")
    public String alugar(@PathVariable Long idLivro,
                         @AuthenticationPrincipal UserDetails userDetails,
                         RedirectAttributes redirectAttributes){

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));



        if(user.getStatus() == Status.EM_ATRASO){
            redirectAttributes.addFlashAttribute("mensagemErro", "Você possui livros em atraso!");
            return "redirect:/home";
        }

        if(user.getLivrosAlugados().size() >= 5){
            redirectAttributes.addFlashAttribute("mensagemErro", "Limite de 5 livros atingido!");
            return "redirect:/home";
        }

        if(userRepository.existsByLivrosAlugadosContaining(idLivro)){
            redirectAttributes.addFlashAttribute("mensagemErro", "Livro já está alugado!");
            return "redirect:/home";
        }

        user.getLivrosAlugados().add(idLivro);
        user.getDatasAluguel().put(idLivro, LocalDateTime.now());
        userRepository.save(user);

        redirectAttributes.addFlashAttribute("mensagemSucesso", "Livro alugado com sucesso!");
        return "redirect:/home/meusAlugados";
    }



    @GetMapping("/meusAlugados")
    public String meusAlugados(@AuthenticationPrincipal UserDetails userDetails, Model model){
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        List<Livro> livros = livroRepository.findAllById(user.getLivrosAlugados());

        model.addAttribute("datasAluguel", user.getDatasAluguel());
        model.addAttribute("livros", livros);
        return "MeusAlugados";
    }

}


