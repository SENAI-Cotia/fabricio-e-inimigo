package br.com.biblioteca.biblioteca.controllers;

import br.com.biblioteca.biblioteca.Enums.Cargo;
import br.com.biblioteca.biblioteca.Enums.Status;
import br.com.biblioteca.biblioteca.models.Livro;
import br.com.biblioteca.biblioteca.models.User;
import br.com.biblioteca.biblioteca.repository.LivroRepository;
import br.com.biblioteca.biblioteca.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/")

public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/users")
    public String getUser(Model model,
                          @RequestParam(value = "email", required = false) String email,
                          @RequestParam(value = "status", required = false) String status){
        List<User> users = userRepository.findAll();

        if(email != null && !email.isEmpty()){
            users = userRepository.findByEmailContainingIgnoreCase(email);
        }

        if(status != null && !status.isEmpty() && !status.equals("todos")){
            users = users.stream()
                    .filter(u -> u.getStatus().toString().equals(status))
                    .toList();
        }

        model.addAttribute("users", users);
        model.addAttribute("statuss", Status.values());
        return "index";
    }

    @GetMapping("/")
    public String index(){
        return "redirect:/home";
    }

    @GetMapping( "/cadastro")
    public String cadastroUser(Model model){

        User user = new User();
        user.setCargo(Cargo.ALUNO);
        user.setStatus(Status.EM_DIA);
        model.addAttribute("user", user);
        model.addAttribute("cargos", Cargo.values());
        model.addAttribute("statuss", Status.values());
        return "Cadastro";
    }

    @GetMapping("/editar/{id}")
    public String userEditar(@PathVariable Long id, Model model){

        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        List<Livro> livrosAlugados = livroRepository.findAllById(user.getLivrosAlugados());

        model.addAttribute("user", user);
        model.addAttribute("livrosAlugados", livrosAlugados);
        model.addAttribute("cargos", Cargo.values());
        model.addAttribute("statuss", Status.values());
        return "Cadastro";
    }

    @GetMapping("/login")
    public String loginPage(
            @RequestParam(value = "erro", required = false) String erro,
            @RequestParam(value = "logout", required = false) String logout,
            Model model){

        if(erro != null){
            model.addAttribute("mensagemErro", "E-mail ou senha incorretos!");
        }
        if(logout != null){
            model.addAttribute("mensagemSucesso", "Logout realizado com sucesso!");
        }
        return "login";
    }


    @PostMapping("/salvar")
    public String salvar(@ModelAttribute User userCadastro, RedirectAttributes redirectAttributes){

        Optional<User> userComMesmoEmail = userRepository.findByEmail(userCadastro.getEmail());

        if (userComMesmoEmail.isPresent() && !userComMesmoEmail.get().getId().equals(userCadastro.getId())){
            redirectAttributes.addFlashAttribute("mensagemEmail", "E-mail já está cadastrado");
            return "redirect:/cadastro";
        }

        if(userCadastro.getId() == null) {
            if (userCadastro.getEmail().trim().isEmpty() || userCadastro.getNome().trim().isEmpty() || userCadastro.getSenha().trim().isEmpty() || userCadastro.getStatus() == null || userCadastro.getCargo() == null) {
                redirectAttributes.addFlashAttribute("mensagemErro", "Insira todas as informações");
                return "redirect:/cadastro";
            }
            userCadastro.setSenha(passwordEncoder.encode(userCadastro.getSenha()));
            userRepository.save(userCadastro);

        } else {
            if (userCadastro.getEmail().trim().isEmpty() || userCadastro.getNome().trim().isEmpty() || userCadastro.getStatus() == null || userCadastro.getCargo() == null) {
                redirectAttributes.addFlashAttribute("mensagemErro", "Insira todas as informações");
                return "redirect:/cadastro";
            }

            User userQueVeioDoBancoDeDados = userRepository.findById(userCadastro.getId())
                    .orElseThrow(() -> new RuntimeException("usuario não encontrado"));

            userQueVeioDoBancoDeDados.setNome(userCadastro.getNome());
            userQueVeioDoBancoDeDados.setEmail(userCadastro.getEmail());

            if(userCadastro.getSenha() != null && !userCadastro.getSenha().trim().isEmpty()){
                userQueVeioDoBancoDeDados.setSenha(passwordEncoder.encode(userCadastro.getSenha()));
            }

            userQueVeioDoBancoDeDados.setStatus(userCadastro.getStatus());
            userQueVeioDoBancoDeDados.setCargo(userCadastro.getCargo());

            userRepository.save(userQueVeioDoBancoDeDados);
        }

        redirectAttributes.addFlashAttribute("mensagemSucesso","User cadastrado com sucesso!");
        return "redirect:/users";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes){

        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        if (!user.getLivrosAlugados().isEmpty()) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Não é possível excluir um usuário com livro alugado!");
            return "redirect:/users";
        }

        userRepository.deleteById(id);

        redirectAttributes.addFlashAttribute("mensagemSucesso", "Usuário excluído com sucesso!");
        return "redirect:/users";
    }

    @GetMapping("/devolverLivro/{idUser}/{idLivro}")
    public String devolverLivro(@PathVariable Long idUser, @PathVariable Long idLivro, RedirectAttributes redirectAttributes){

        User user = userRepository.findById(idUser)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        user.getLivrosAlugados().remove(idLivro);
        user.getDatasAluguel().remove(idLivro);
        userRepository.save(user);

        redirectAttributes.addFlashAttribute("mensagemSucesso", "Livro devolvido com sucesso!");
        return "redirect:/editar/" + idUser;
    }

    @Scheduled(fixedRate = 3600000)
    @Transactional
    public void verificarVencimentos(){
        List<User> users = userRepository.findAll();

        for(User user : users){
            for(Map.Entry<Long, LocalDateTime> entry : user.getDatasAluguel().entrySet()){
                if(LocalDateTime.now().isAfter(entry.getValue().plusDays(7))){
                    user.setStatus(Status.EM_ATRASO);
                    userRepository.save(user);
                    break;
                }
            }
        }
    }




}