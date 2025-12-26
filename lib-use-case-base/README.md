# 🎯 Use Case Base Library - FIAP Unified Service Core

Biblioteca base com interfaces para casos de uso seguindo padrões CQRS e Clean Architecture. Fornece interfaces genéricas e type-safe para implementação de comandos e queries.

## ✨ Características

- ✅ **Interfaces Genéricas**: Type-safe com generics Java
- 🎯 **Padrão CQRS**: Separação clara entre Command e Query
- 🏗️ **Clean Architecture**: Compatível com Clean Architecture, Hexagonal Architecture e DDD
- 📦 **Zero Dependências**: Apenas interfaces Java puras
- 🔧 **Flexível**: Permite implementação em qualquer camada (Application, Domain, Infrastructure)

## 📦 Instalação

### Maven

```xml
<dependency>
    <groupId>com.github.OtavioValadao.unified-service-core-libs</groupId>
    <artifactId>lib-use-case-base</artifactId>
    <version>v1.5.1</version>
</dependency>
```

### Gradle

```gradle
implementation 'com.github.OtavioValadao.unified-service-core-libs:lib-use-case-base:v1.5.1'
```

## 🚀 Uso

### 1. Interface Base: UseCase

A interface `UseCase<I, O>` define o contrato básico para execução de casos de uso:

```java
import com.fiap.libs.usecase.UseCase;

public interface CreateUserCommand extends UseCase<UserRequest, UserResponse> {
}

@Service
public class CreateUserUseCase implements CreateUserCommand {
    
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    
    @Override
    @LogOperation("Create new user")
    public UserResponse execute(UserRequest input) {
        User user = userMapper.toEntity(input);
        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }
}
```

### 2. Interface Especializada: Command

Use `Command<I, O>` para operações que modificam o estado do sistema:

```java
import com.fiap.libs.usecase.Command;

// Comando com retorno
public interface CreateUserCommand extends Command<UserRequest, UserResponse> {
}

// Comando sem retorno (Void)
public interface DeleteUserCommand extends Command<Long, Void> {
}

@Service
public class DeleteUserUseCase implements DeleteUserCommand {
    
    private final UserRepository userRepository;
    
    @Override
    @LogOperation("Delete user")
    public Void execute(Long id) {
        userRepository.deleteById(id);
        return null;
    }
}
```

### 3. Interface Especializada: Query

Use `Query<I, O>` para operações de leitura (sem efeitos colaterais):

```java
import com.fiap.libs.usecase.Query;

// Query simples
public interface GetUserByIdQuery extends Query<Long, UserResponse> {
}

// Query com paginação
public interface ListUsersQuery extends Query<Pageable, Page<UserResponse>> {
}

@Service
public class GetUserByIdUseCase implements GetUserByIdQuery {
    
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    
    @Override
    @LogOperation("Find user by ID")
    public UserResponse execute(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", id));
        return userMapper.toResponse(user);
    }
}
```

## 📋 Exemplos Completos

### Exemplo 1: Clean Architecture

```java
// Application Layer - Boundary In
package com.example.application.boundary.in.command;

import com.fiap.libs.usecase.Command;
import com.example.application.boundary.in.dto.UserRequest;
import com.example.application.boundary.in.dto.UserResponse;

public interface CreateUserCommand extends Command<UserRequest, UserResponse> {
}

// Application Layer - Use Case
package com.example.application.usecases.command;

import com.example.application.boundary.in.command.CreateUserCommand;
import com.example.application.boundary.out.UserRepository;
import com.fiap.libs.observability.annotation.LogOperation;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CreateUserUseCase implements CreateUserCommand {
    
    private final UserRepository repository;
    
    @Override
    @LogOperation("Create new user")
    public UserResponse execute(UserRequest input) {
        // Lógica de negócio
        return repository.save(input);
    }
}
```

### Exemplo 2: Hexagonal Architecture (Ports & Adapters)

```java
// Port (Application Layer)
package com.example.application.port.in;

import com.fiap.libs.usecase.Command;
import com.example.domain.dto.CreateOrderRequest;
import com.example.domain.model.Order;

public interface CreateOrderPort extends Command<CreateOrderRequest, Order> {
}

// Adapter (Infrastructure Layer)
package com.example.infrastructure.adapter;

import com.example.application.port.in.CreateOrderPort;
import com.example.application.port.out.OrderRepositoryPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CreateOrderAdapter implements CreateOrderPort {
    
    private final OrderRepositoryPort repository;
    
    @Override
    public Order execute(CreateOrderRequest input) {
        Order order = Order.create(input);
        return repository.save(order);
    }
}
```

### Exemplo 3: DDD (Domain-Driven Design)

```java
// Application Service
package com.example.application.service;

import com.fiap.libs.usecase.Command;
import com.example.domain.model.User;
import com.example.domain.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CreateUserService implements Command<UserCreateDTO, User> {
    
    private final UserRepository userRepository;
    
    @Override
    public User execute(UserCreateDTO input) {
        User user = User.create(input.getName(), input.getEmail());
        return userRepository.save(user);
    }
}
```

## 🎯 Benefícios

### 1. Consistência Arquitetural

Todas as operações seguem o mesmo padrão:

```java
// Padrão consistente em todo o projeto
public interface XxxCommand extends Command<Input, Output> {}
public interface XxxQuery extends Query<Input, Output> {}
```

### 2. Type Safety

Generics garantem type safety em tempo de compilação:

```java
// ✅ Compila
CreateUserCommand command = new CreateUserUseCase();
UserResponse response = command.execute(userRequest);

// ❌ Erro de compilação
String wrong = command.execute(userRequest); // Incompatível
```

### 3. Testabilidade

Fácil de mockar em testes:

```java
@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    
    @Mock
    private CreateUserCommand createUserCommand;
    
    @Test
    void shouldCreateUser() {
        when(createUserCommand.execute(any()))
            .thenReturn(userResponse);
        
        // Test implementation
    }
}
```

### 4. Integração com Outras Libs

Funciona perfeitamente com outras bibliotecas do Unified Service Core:

```java
@Service
public class CreateUserUseCase implements CreateUserCommand {
    
    @Override
    @LogOperation("Create new user")  // lib-observability
    public UserResponse execute(UserRequest input) {
        // Validação
        if (userRepository.existsByEmail(input.getEmail())) {
            throw new ResourceAlreadyExistsException("User", "email", input.getEmail());  // lib-exception-handler
        }
        
        User user = userMapper.toEntity(input);
        return userRepository.save(user);
    }
}
```

## 📚 Hierarquia de Interfaces

```
UseCase<I, O>
    ├── Command<I, O>  (modificações)
    └── Query<I, O>   (consultas)
```

## 🔧 Boas Práticas

### ✅ Recomendado

```java
// Use interfaces específicas
public interface CreateUserCommand extends Command<UserRequest, UserResponse> {}
public interface GetUserByIdQuery extends Query<Long, UserResponse> {}

// Implemente em services/use cases
@Service
public class CreateUserUseCase implements CreateUserCommand {
    // Implementação
}
```

### ❌ Evitar

```java
// Não use UseCase diretamente (use Command ou Query)
public interface CreateUserUseCase extends UseCase<UserRequest, UserResponse> {}  // ❌

// Não misture responsabilidades
public interface UserOperations extends Command<UserRequest, UserResponse>, Query<Long, UserResponse> {}  // ❌
```

## 🧪 Testes

### Teste Unitário

```java
@ExtendWith(MockitoExtension.class)
class CreateUserUseCaseTest {
    
    @Mock
    private UserRepository repository;
    
    @InjectMocks
    private CreateUserUseCase useCase;
    
    @Test
    void shouldCreateUser() {
        // Given
        UserRequest request = new UserRequest("John", "john@email.com");
        User savedUser = User.builder().id(1L).name("John").build();
        
        when(repository.save(any())).thenReturn(savedUser);
        
        // When
        UserResponse response = useCase.execute(request);
        
        // Then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("John");
    }
}
```

## 📞 Suporte

- **Issues**: [GitHub Issues](https://github.com/OtavioValadao/unified-service-core-libs/issues)
- **Documentação**: [Wiki](https://github.com/OtavioValadao/unified-service-core-libs/wiki)

## 📄 Licença

MIT License - Copyright (c) 2025 FIAP

## 👥 Autores

**FIAP - Unified Service Core Team**
- Versão atual: v1.5.1
- Data de lançamento: Janeiro 2025

---

**💡 Dica**: Use `Command` para operações que modificam estado e `Query` para operações de leitura. Isso facilita a separação de responsabilidades e melhora a testabilidade! 🚀

