# 📚 Unified Service Core Libraries - FIAP

Conjunto de bibliotecas reutilizáveis Spring Boot para padronização e aceleração do desenvolvimento de microserviços.

[![Version](https://img.shields.io/badge/version-1.4.1-blue.svg)](https://github.com/OtavioValadao/unified_service_core_libs/releases/tag/v1.4.1)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](LICENSE)

## 📦 Bibliotecas Disponíveis

### 🔍 [lib-observability](lib-observability/)
Biblioteca de observabilidade com logging automático e estruturado.

**Features:**
- Logging automático de requisições HTTP com `@LogHttp`
- Logging de operações de negócio com `@LogOperation`
- Sanitização automática de dados sensíveis
- Suporte a formato JSON estruturado
- Arquitetura agnóstica (Layered, Hexagonal, Clean, DDD)

```xml
<dependency>
    <groupId>com.github.OtavioValadao</groupId>
    <artifactId>lib-observability</artifactId>
    <version>1.4.1</version>
</dependency>
```

---

### 🚫 [lib-exception-handler](lib-exception-handler/)
Tratamento padronizado de exceções com respostas HTTP consistentes.

**Features:**
- Mapeamento automático de exceções para status HTTP
- Formato padronizado de erro (ErrorResponse)
- Exceções específicas para cada cenário (404, 409, 422, etc)
- Suporte a validação de campos com `ValidationException`
- Handler global via `@ControllerAdvice`

```xml
<dependency>
    <groupId>com.github.OtavioValadao</groupId>
    <artifactId>lib-exception-handler</artifactId>
    <version>1.4.1</version>
</dependency>
```

---

### ⚙️ [lib-code-gen](lib-code-gen/)
Biblioteca para geração de código, mappers e relatórios.

**Features:**
- Integração com MapStruct para mapeamento de DTOs
- Geração de relatórios com JasperReports
- OpenAPI/Swagger code generation
- Utilitários para conversão de dados

```xml
<dependency>
    <groupId>com.github.OtavioValadao</groupId>
    <artifactId>lib-code-gen</artifactId>
    <version>1.4.1</version>
</dependency>
```

---

### 📧 [lib-send-notification](lib-send-notification/)
Biblioteca para envio de notificações por email com templates HTML.

**Features:**
- Envio assíncrono de emails
- Suporte a templates HTML customizáveis
- JavaMailSender com auto-configuração
- Múltiplas estratégias de carregamento de templates
- Templates prontos para welcome e order finalized

```xml
<dependency>
    <groupId>com.github.OtavioValadao</groupId>
    <artifactId>lib-send-notification</artifactId>
    <version>1.4.1</version>
</dependency>
```

---

## 🚀 Quick Start

### 1. Adicione o repositório JitPack

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

### 2. Adicione as dependências desejadas

```xml
<dependencies>
    <!-- Observability -->
    <dependency>
        <groupId>com.github.OtavioValadao</groupId>
        <artifactId>lib-observability</artifactId>
        <version>1.4.1</version>
    </dependency>

    <!-- Exception Handler -->
    <dependency>
        <groupId>com.github.OtavioValadao</groupId>
        <artifactId>lib-exception-handler</artifactId>
        <version>1.4.1</version>
    </dependency>

    <!-- Send Notification -->
    <dependency>
        <groupId>com.github.OtavioValadao</groupId>
        <artifactId>lib-send-notification</artifactId>
        <version>1.4.1</version>
    </dependency>

    <!-- Code Gen -->
    <dependency>
        <groupId>com.github.OtavioValadao</groupId>
        <artifactId>lib-code-gen</artifactId>
        <version>1.4.1</version>
    </dependency>
</dependencies>
```

### 3. Configure (opcional)

As bibliotecas funcionam **out-of-the-box** com configuração padrão via Spring Boot AutoConfiguration.

Exemplo de configuração no `application.yml`:

```yaml
# Observability
observability:
  enabled: true
  http:
    enabled: true
    max-length: 200
  operation:
    enabled: true

# Mail (para lib-send-notification)
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
```

---

## 📖 Exemplo de Uso Completo

```java
@RestController
@RequestMapping("/api/users")
@LogHttp  // Logging automático de HTTP
public class UserController {

    private final UserService userService;
    private final SendEmailNotification emailNotification;

    @PostMapping
    @LogHttp(value = "Criar novo usuário", maxLength = 500)
    public ResponseEntity<UserResponse> create(@RequestBody UserRequest request) {
        // Validação com exception handler
        if (request.getEmail() == null) {
            throw new ValidationException("Invalid request",
                List.of(new CustomFiledError("email", "must not be null")));
        }

        // Lógica de negócio
        User user = userService.create(request);

        // Envio de email assíncrono
        emailNotification.sendEmailWelcome(
            new ClientDto(user.getName(), user.getEmail())
        );

        return ResponseEntity.status(201).body(userMapper.toResponse(user));
    }

    @GetMapping("/{id}")
    @LogHttp("Buscar usuário por ID")
    public UserResponse get(@PathVariable Long id) {
        return userService.findById(id)
            .map(userMapper::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }
}

@Service
public class UserService {

    @LogOperation("Criar usuário no banco")
    public User create(UserRequest request) {
        // Validação de duplicação
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("User", "email", request.getEmail());
        }

        User user = userMapper.toEntity(request);
        return userRepository.save(user);
    }

    @LogOperation(value = "Buscar usuário por ID", logArgs = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
}
```

**Logs gerados automaticamente:**
```
🔗 [⯈ IN ] POST /api/users → Criar novo usuário [UserRequest{name=João, email=joao@email.com}]
⏰ [▶ START] Criar usuário no banco → args: [UserRequest{name=João, email=joao@email.com}]
✅ [✓ SUCCESS] Criar usuário no banco ✓ 45ms → User{id=1, name=João}
INFO  [main] Send email notification welcome
INFO  [async] Welcome email sent successfully to joao@email.com
✅ [⬅ OUT] POST /api/users ✓ 234ms → UserResponse{id=1, name=João}
```

---

## 🏗️ Arquitetura

Todas as bibliotecas seguem os princípios:

- **Auto-configuração**: Spring Boot AutoConfiguration para configuração zero
- **Arquitetura agnóstica**: Funciona com Layered, Hexagonal, Clean, DDD
- **Baixo acoplamento**: Cada lib é independente e pode ser usada isoladamente
- **Convenção sobre configuração**: Configurações sensatas por padrão
- **Extensibilidade**: Permite customização quando necessário

---

## 🔧 Desenvolvimento

### Pré-requisitos

- Java 21+
- Maven 3.8+
- Spring Boot 3.5.5

### Build Local

```bash
# Clone o repositório
git clone https://github.com/OtavioValadao/unified_service_core_libs.git
cd unified_service_core_libs

# Compile e instale localmente
mvn clean install

# Executar apenas os testes
mvn test
```

### Estrutura do Projeto

```
unified_service_core_libs/
├── lib-observability/          # Biblioteca de observabilidade
├── lib-exception-handler/      # Tratamento de exceções
├── lib-code-gen/               # Geração de código
├── lib-send-notification/      # Envio de notificações
├── pom.xml                     # Parent POM
└── README.md                   # Este arquivo
```

---

## 📝 Versionamento

Utilizamos [Semantic Versioning](https://semver.org/):

- **MAJOR**: Mudanças incompatíveis na API
- **MINOR**: Novas funcionalidades retrocompatíveis
- **PATCH**: Correções de bugs retrocompatíveis

### Releases

- **1.4.1** (Atual) - Adicionada lib-send-notification com suporte a email
- **1.0.17** - Melhorias nas libs de observabilidade e exception handler
- **1.0.16** - Correções e ajustes na exception handler
- **1.0.15** - Release inicial com observability, exception handler e code gen

---

## 🤝 Contribuindo

Contribuições são bem-vindas! Para contribuir:

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/MinhaFeature`)
3. Commit suas mudanças (`git commit -m 'feat: adiciona MinhaFeature'`)
4. Push para a branch (`git push origin feature/MinhaFeature`)
5. Abra um Pull Request

### Padrão de Commits

Seguimos [Conventional Commits](https://www.conventionalcommits.org/):

- `feat:` Nova funcionalidade
- `fix:` Correção de bug
- `docs:` Alterações na documentação
- `test:` Adição ou correção de testes
- `chore:` Tarefas de manutenção

---

## 📞 Suporte

- **Issues**: [GitHub Issues](https://github.com/OtavioValadao/unified_service_core_libs/issues)
- **Documentação**: [Wiki](https://github.com/OtavioValadao/unified_service_core_libs/wiki)
- **Discussões**: [GitHub Discussions](https://github.com/OtavioValadao/unified_service_core_libs/discussions)

---

## 📄 Licença

MIT License - Copyright (c) 2025 FIAP

Veja [LICENSE](LICENSE) para mais detalhes.

---

## 👥 Autores

**FIAP - Unified Service Core Team**

- Versão atual: v1.4.1
- Data de lançamento: Janeiro 2025

---

## ⭐ Roadmap

### Próximas Features

- [ ] lib-security: Biblioteca para autenticação e autorização JWT
- [ ] lib-cache: Gerenciamento de cache com Redis
- [ ] lib-messaging: Integração com RabbitMQ/Kafka
- [ ] lib-resilience: Circuit breaker e retry patterns
- [ ] lib-monitoring: Métricas com Prometheus/Micrometer

### Melhorias Planejadas

- [ ] Suporte a Kotlin
- [ ] Exemplos de integração com frameworks adicionais
- [ ] Guias de migração detalhados
- [ ] Performance benchmarks

---

**🎯 Objetivo**: Acelerar o desenvolvimento de microserviços com padrões consistentes e reutilizáveis! 🚀
