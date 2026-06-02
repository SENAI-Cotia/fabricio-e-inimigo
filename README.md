# BIBLIOTECH - Sistema de Gerenciamento de Biblioteca

Sistema web desenvolvido para digitalizar o gerenciamento de uma biblioteca escolar, substituindo o controle manual de empréstimos e eliminando problemas como falta de rastreio de livros e ausência de alertas para devoluções em atraso.

---

## Deploy

A aplicação está disponível em produção:  
**[bibliotech.onrender.com](https://bibliotech.onrender.com)**

---

## Tecnologias Utilizadas

### Backend
![Java](https://img.shields.io/badge/Java-21-orange?style=flat&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0-green?style=flat&logo=springboot)
![Spring Security](https://img.shields.io/badge/Spring_Security-7.0-green?style=flat&logo=springsecurity)
![Spring Data JPA](https://img.shields.io/badge/Spring_Data_JPA-4.0-green?style=flat&logo=spring)

### Frontend
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-3.1-green?style=flat&logo=thymeleaf)
![HTML5](https://img.shields.io/badge/HTML5-E34F26?style=flat&logo=html5&logoColor=white)
![CSS3](https://img.shields.io/badge/CSS3-1572B6?style=flat&logo=css3&logoColor=white)

### Banco de Dados
![MySQL](https://img.shields.io/badge/MySQL-8.4-4479A1?style=flat&logo=mysql&logoColor=white)

### Infraestrutura
![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat&logo=docker&logoColor=white)
![Render](https://img.shields.io/badge/Render-46E3B7?style=flat&logo=render&logoColor=white)
![Aiven](https://img.shields.io/badge/Aiven-FF3621?style=flat&logo=aiven&logoColor=white)

---

## Perfis de Acesso

| Perfil | Permissões |
|---|---|
| ![Admin](https://img.shields.io/badge/Perfil-ADMIN-8A3E18?style=flat) | Gerenciar livros e usuários, confirmar devoluções |
| ![Aluno](https://img.shields.io/badge/Perfil-ALUNO-C4703A?style=flat) | Visualizar livros, alugar e acompanhar empréstimos |

---

## Funcionalidades

- Autenticação com email e senha
- Cadastro e gestão de livros com capa, autor e gênero
- Sistema de aluguel com prazo de 7 dias
- Limite de 5 livros por aluno simultaneamente
- Bloqueio automático de usuários com livros em atraso
- Verificação automática de vencimentos a cada hora
- Confirmação de devolução pelo admin
- Filtro de livros por gênero e busca por nome
- Busca de usuários por email e filtro por status

---

## Como Rodar Localmente

### Pré-requisitos

![Java](https://img.shields.io/badge/Java-21+-orange?style=flat&logo=java)
![Maven](https://img.shields.io/badge/Maven-3.9+-C71A36?style=flat&logo=apachemaven&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8+-4479A1?style=flat&logo=mysql&logoColor=white)

### Instalação

1. Clone o repositório:
```bash
git clone https://github.com/SENAI-Cotia/fabricio-e-inimigo
cd fabricio-e-inimigo
```

2. Configure o banco em `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/biblioteca?createDatabaseIfNotExist=true
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
spring.jpa.hibernate.ddl-auto=update
```

3. Execute:
```bash
./mvnw spring-boot:run
```

4. Acesse em `http://localhost:8080`

---

## Rodando com Docker

```bash
docker build -t bibliotech .
docker run -p 8080:8080 bibliotech
```

---

<div align="center">

Desenvolvido por

[![Lucas](https://img.shields.io/badge/LVDEV07-181717?style=flat&logo=github&logoColor=white)](https://github.com/LVDEV07)
[![Fabrício](https://img.shields.io/badge/Fabiz2-181717?style=flat&logo=github&logoColor=white)](https://github.com/Fabiz2)

</div>