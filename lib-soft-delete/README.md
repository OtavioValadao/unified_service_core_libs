# 🗑️ Soft Delete Library - FIAP Unified Service Core

Biblioteca para operações de soft delete e soft activate em entidades JPA, seguindo padrões de Clean Architecture e DDD. Fornece interfaces e helpers para deleção lógica de entidades sem remover fisicamente do banco de dados.

## ✨ Características

- ✅ **Interface Marker**: `SoftDeletable` para entidades que suportam soft delete
- 🔧 **Helper Service**: `SoftDeleteHelper` para operações padronizadas
- 📦 **Repository Base**: `SoftDeleteRepository` com métodos úteis
- 🏗️ **Clean Architecture**: Compatível com múltiplas arquiteturas
- 🔒 **Type Safe**: Generics Java para type safety
- 📊 **Auditoria**: Suporte a `deletedAt` e `isActive` para rastreamento

## 📦 Instalação

### Maven

```xml
<dependency>
    <groupId>com.github.OtavioValadao.unified-service-core-libs</groupId>
    <artifactId>lib-soft-delete</artifactId>
    <version>v1.5.2</version>
</dependency>
```

### Gradle

```gradle
implementation 'com.github.OtavioValadao.unified-service-core-libs:lib-soft-delete:v1.5.2'
```

## 🚀 Uso

### 1. Implementar SoftDeletable na Entidade

Primeiro, faça sua entidade implementar a interface `SoftDeletable`:

```java
import com.fiap.libs.softdelete.SoftDeletable;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@MappedSuperclass
@Data
public abstract class EntityBase implements SoftDeletable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    @Column(name = "isActive", nullable = false)
    private Boolean isActive = true;
}

@Entity
@Table(name = "customers")
public class CustomerEntity extends EntityBase {
    // Campos específicos do domínio
    private String name;
    private String email;
}
```

### 2. Usar SoftDeleteHelper no Repository Adapter

Use o `SoftDeleteHelper` para executar operações de soft delete/activate:

```java
import com.fiap.libs.softdelete.SoftDeleteHelper;
import com.fiap.libs.exception.api.exceptions.resource.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@AllArgsConstructor
@Slf4j
public class CustomerRepositoryAdapter implements CustomerRepository {
    
    private final JpaCustomerRepository jpaRepository;
    private final SoftDeleteHelper softDeleteHelper;
    
    @Override
    @Transactional
    public void softDelete(Long id) {
        log.info("Soft deleting customer: {}", id);
        
        CustomerEntity entity = jpaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Customer", id));
        
        softDeleteHelper.softDelete(entity);
        jpaRepository.save(entity);
    }
    
    @Override
    @Transactional
    public void softActivate(Long id) {
        log.info("Soft activating customer: {}", id);
        
        CustomerEntity entity = jpaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Customer", id));
        
        softDeleteHelper.softActivate(entity);
        jpaRepository.save(entity);
    }
}
```

### 3. Usar SoftDeleteRepository (Opcional)

Para repositórios JPA, você pode estender `SoftDeleteRepository`:

```java
import com.fiap.libs.softdelete.SoftDeleteRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaCustomerRepository extends SoftDeleteRepository<CustomerEntity, Long> {
    
    // Métodos customizados
    Optional<CustomerEntity> findByEmail(String email);
    
    // Métodos herdados de SoftDeleteRepository:
    // - findByIdAndActive(Long id)
    // - findByIdAndNotDeleted(Long id)
}

// Uso:
@Repository
public class CustomerRepositoryAdapter {
    
    private final JpaCustomerRepository repository;
    
    public Optional<Customer> findActiveById(Long id) {
        return repository.findByIdAndActive(id)
            .map(mapper::toDomain);
    }
}
```

## 📋 Exemplos Completos

### Exemplo 1: Repository Adapter Completo

```java
package com.example.infrastructure.persistence.adapter;

import com.fiap.libs.softdelete.SoftDeleteHelper;
import com.fiap.libs.exception.api.exceptions.resource.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
@Slf4j
public class CustomerRepositoryAdapter implements CustomerRepository {
    
    private final JpaCustomerRepository jpaRepository;
    private final SoftDeleteHelper softDeleteHelper;
    private final CustomerMapper mapper;
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Customer> findById(Long id) {
        return jpaRepository.findByIdAndActive(id)
            .map(mapper::toDomain);
    }
    
    @Override
    @Transactional
    public void softDelete(Long id) {
        log.info("Soft deleting customer: {}", id);
        
        CustomerEntity entity = jpaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Customer", id));
        
        softDeleteHelper.softDelete(entity);
        jpaRepository.save(entity);
    }
    
    @Override
    @Transactional
    public void softActivate(Long id) {
        log.info("Soft activating customer: {}", id);
        
        CustomerEntity entity = jpaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Customer", id));
        
        softDeleteHelper.softActivate(entity);
        jpaRepository.save(entity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Customer> findAllActive() {
        return jpaRepository.findAll().stream()
            .filter(SoftDeletable::isActive)
            .map(mapper::toDomain)
            .toList();
    }
}
```

### Exemplo 2: Use Case com Soft Delete

```java
package com.example.application.usecases.command;

import com.fiap.libs.usecase.Command;
import com.fiap.libs.observability.annotation.LogOperation;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DeleteCustomerUseCase implements Command<Long, Void> {
    
    private final CustomerRepository repository;
    
    @Override
    @LogOperation("Delete customer by ID")
    public Void execute(Long id) {
        repository.softDelete(id);
        return null;
    }
}
```

### Exemplo 3: Query Filtrando Entidades Ativas

```java
package com.example.application.usecases.query;

import com.fiap.libs.usecase.Query;
import com.fiap.libs.observability.annotation.LogOperation;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ListActiveCustomersUseCase implements Query<Void, List<Customer>> {
    
    private final CustomerRepository repository;
    
    @Override
    @LogOperation("List all active customers")
    public List<Customer> execute(Void input) {
        return repository.findAllActive();
    }
}
```

## 🎯 Benefícios

### 1. Consistência

Todas as operações de soft delete seguem o mesmo padrão:

```java
// Padrão consistente em todo o projeto
softDeleteHelper.softDelete(entity);
softDeleteHelper.softActivate(entity);
```

### 2. Type Safety

Generics garantem type safety em tempo de compilação:

```java
// ✅ Compila
SoftDeleteHelper helper = new SoftDeleteHelper();
helper.softDelete(customerEntity);

// ❌ Erro de compilação se CustomerEntity não implementa SoftDeletable
```

### 3. Auditoria

Rastreamento completo de deleções:

```java
// Entidade deletada mantém histórico
entity.getDeletedAt(); // LocalDateTime da deleção
entity.getIsActive();  // false após soft delete
```

### 4. Recuperação

Entidades podem ser reativadas facilmente:

```java
// Reativar entidade deletada
softDeleteHelper.softActivate(entity);
// deletedAt = null, isActive = true
```

## 📚 API Reference

### SoftDeletable Interface

```java
public interface SoftDeletable {
    void setDeletedAt(LocalDateTime deletedAt);
    LocalDateTime getDeletedAt();
    void setIsActive(Boolean isActive);
    Boolean getIsActive();
    default boolean isActive(); // true se ativo e não deletado
}
```

### SoftDeleteHelper Service

```java
@Component
public class SoftDeleteHelper {
    <T extends SoftDeletable> void softDelete(T entity);
    <T extends SoftDeletable> void softActivate(T entity);
    <T extends SoftDeletable> boolean isActive(T entity);
}
```

### SoftDeleteRepository Interface

```java
public interface SoftDeleteRepository<T extends SoftDeletable, ID> 
    extends JpaRepository<T, ID> {
    
    Optional<T> findByIdAndActive(ID id);
    Optional<T> findByIdAndNotDeleted(ID id);
}
```

## 🔧 Boas Práticas

### ✅ Recomendado

```java
// Use SoftDeleteHelper para operações
@Repository
public class CustomerRepositoryAdapter {
    private final SoftDeleteHelper softDeleteHelper;
    
    public void softDelete(Long id) {
        CustomerEntity entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Customer", id));
        
        softDeleteHelper.softDelete(entity);
        repository.save(entity);
    }
}

// Implemente SoftDeletable na entidade base
@MappedSuperclass
public abstract class EntityBase implements SoftDeletable {
    // Campos comuns
}
```

### ❌ Evitar

```java
// Não faça soft delete manualmente
entity.setIsActive(false);
entity.setDeletedAt(LocalDateTime.now()); // ❌ Use SoftDeleteHelper

// Não esqueça de salvar após soft delete
softDeleteHelper.softDelete(entity);
// repository.save(entity); // ❌ Não esqueça de salvar!
```

## 🧪 Testes

### Teste Unitário

```java
@ExtendWith(MockitoExtension.class)
class CustomerRepositoryAdapterTest {
    
    @Mock
    private JpaCustomerRepository jpaRepository;
    
    @Mock
    private SoftDeleteHelper softDeleteHelper;
    
    @InjectMocks
    private CustomerRepositoryAdapter adapter;
    
    @Test
    void shouldSoftDeleteCustomer() {
        // Given
        Long id = 1L;
        CustomerEntity entity = new CustomerEntity();
        entity.setId(id);
        
        when(jpaRepository.findById(id)).thenReturn(Optional.of(entity));
        doNothing().when(softDeleteHelper).softDelete(entity);
        when(jpaRepository.save(entity)).thenReturn(entity);
        
        // When
        adapter.softDelete(id);
        
        // Then
        verify(softDeleteHelper).softDelete(entity);
        verify(jpaRepository).save(entity);
    }
}
```

## 🔄 Migração de Código Existente

### Antes (Código Duplicado)

```java
public void softDelete(Long id) {
    var entity = repository.findById(id).orElseThrow();
    entity.setIsActive(false);
    entity.setDeletedAt(LocalDateTime.now());
    repository.save(entity);
}
```

### Depois (Usando a LIB)

```java
public void softDelete(Long id) {
    var entity = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Entity", id));
    
    softDeleteHelper.softDelete(entity);
    repository.save(entity);
}
```

## 📞 Suporte

- **Issues**: [GitHub Issues](https://github.com/OtavioValadao/unified-service-core-libs/issues)
- **Documentação**: [Wiki](https://github.com/OtavioValadao/unified-service-core-libs/wiki)

## 📄 Licença

MIT License - Copyright (c) 2025 FIAP

## 👥 Autores

**FIAP - Unified Service Core Team**
- Versão atual: v1.5.2
- Data de lançamento: Janeiro 2025

---

**💡 Dica**: Use `SoftDeleteHelper` para garantir consistência nas operações de soft delete. Isso facilita a manutenção e reduz bugs relacionados a deleção lógica! 🚀

