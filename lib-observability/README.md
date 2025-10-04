# 🔍 Observability Library - FIAP Unified Service Core

Biblioteca de observabilidade arquiteturalmente agnóstica para aplicações Spring Boot com logging automático e estruturado.

## 📦 Instalação

### Maven
```xml
<dependency>
    <groupId>com.github.OtavioValadao.unified-service-core-libs</groupId>
    <artifactId>lib-observability</artifactId>
    <version>v2.0.0</version>
</dependency>
```

### Gradle
```gradle
implementation 'com.github.OtavioValadao.unified-service-core-libs:lib-observability:v2.0.0'
```

## 🚀 Uso

### 1. Logging de HTTP com @LogHttp

Use `@LogHttp` em **qualquer ponto de entrada HTTP**, independente da arquitetura:
- Controllers (Layered Architecture)
- Adapters (Hexagonal Architecture)
- Presenters (Clean Architecture)
- API Layer (Onion Architecture)

```java
@RestController
@RequestMapping("/api/users")
@LogHttp  // Aplica a todos os métodos da classe
public class UserController {
    
    @GetMapping("/{id}")
    @LogHttp(value = "Buscar usuário por ID", logResult = true)
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }
    
    @PostMapping
    @LogHttp(value = "Criar novo usuário", maxLength = 500)
    public ResponseEntity<User> createUser(@RequestBody UserDTO dto) {
        return ResponseEntity.status(201).body(userService.create(dto));
    }
}
```

**Output:**
```
🔗 [⯈ IN ] GET /api/users/123 → Buscar usuário por ID [123]
✅ [⬅ OUT] GET /api/users/123 ✓ 45ms → User{id=123, name=João Silva}
```

**Em caso de erro:**
```
🔗 [⯈ IN ] GET /api/users/999 → Buscar usuário por ID [999]
⚠️ [⬅ OUT] GET /api/users/999 ✗ 12ms - UserNotFoundException
```

---

### 2. Logging de Operações com @LogOperation

Use `@LogOperation` em **qualquer camada de negócio**:
- Services (Layered)
- Use Cases (Clean Architecture)
- Ports/Adapters (Hexagonal)
- Application Services (DDD)
- Domain Services
- Repositories

```java
@Service
@LogOperation  // Aplica a todos os métodos da classe
public class UserService {
    
    @LogOperation(value = "Buscar usuário no banco", logArgs = true)
    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
    }
    
    @LogOperation(value = "Criar usuário", logResult = true, maxLength = 300)
    public User create(UserDTO dto) {
        User user = userMapper.toEntity(dto);
        return userRepository.save(user);
    }
    
    @LogOperation("Validar senha do usuário")
    public boolean validatePassword(Long userId, String password) {
        // Dados sensíveis são automaticamente sanitizados
        User user = findById(userId);
        return passwordEncoder.matches(password, user.getPassword());
    }
}
```

**Output:**
```
⏰ [▶ START] Buscar usuário no banco → args: [123]
✅ [✓ SUCCESS] Buscar usuário no banco ✓ 23ms → result: User{id=123, name=João}
```

**Em caso de erro:**
```
⏰ [▶ START] Buscar usuário no banco → args: [999]
❌ [✗ ERROR] Buscar usuário no banco ✗ 15ms - UserNotFoundException: User not found
```

**Dados sensíveis sanitizados:**
```
⏰ [▶ START] Validar senha do usuário → args: [123, password=***]
✅ [✓ SUCCESS] Validar senha do usuário ✓ 180ms → result: true
```

---

## ⚙️ Configuração

### application.yml

```yaml
observability:
  enabled: true  # Habilita/desabilita toda a lib (padrão: true)
  
  http:
    enabled: true           # Habilita logging HTTP (padrão: true)
    max-length: 200         # Tamanho máximo de args/result nos logs (padrão: 200)
    order: -999000          # Ordem de execução do aspect (padrão: LOWEST_PRECEDENCE - 1000)
  
  operation:
    enabled: true           # Habilita logging de operações (padrão: true)
    max-length: 200         # Tamanho máximo de args/result nos logs (padrão: 200)
    order: -999500          # Ordem de execução do aspect (padrão: LOWEST_PRECEDENCE - 500)
```

### application.properties

```properties
# Desabilitar toda a biblioteca
observability.enabled=false

# Configurar apenas HTTP
observability.http.enabled=true
observability.http.max-length=500

# Configurar apenas operações
observability.operation.enabled=true
observability.operation.max-length=300
```

---

## 🎯 Features

### ✅ Recursos Principais

- **Arquitetura Agnóstica**: Funciona com Layered, Hexagonal, Clean, Onion e DDD
- **Zero Configuração**: Funciona out-of-the-box após adicionar a dependência
- **Anotações Flexíveis**: `@LogHttp` e `@LogOperation` com múltiplas opções
- **Sanitização Automática**: Remove dados sensíveis (passwords, tokens, secrets)
- **Performance Tracking**: Mede tempo de execução em milissegundos
- **Error Handling**: Captura e loga exceções automaticamente
- **Logs Estruturados**: Formato padronizado e legível com emojis
- **Configurável**: Customize via properties sem alterar código
- **Truncamento Inteligente**: Limita tamanho dos logs para não degradar performance

### 🔒 Segurança

A biblioteca **automaticamente sanitiza** dados sensíveis nos logs:
- `password` / `senha` / `pwd`
- `token` / `bearer`
- `secret` / `api-key`
- `authorization`

**Exemplo:**
```java
// Input: {username: "joao", password: "S3nh@F0rt3", token: "abc123xyz"}
// Log:   {username: "joao", password: ***, token: ***}
```

---

## 📋 Parâmetros das Anotações

### @LogHttp

| Parâmetro | Tipo | Padrão | Descrição |
|-----------|------|--------|-----------|
| `value` | String | "" | Descrição customizada do endpoint |
| `logArgs` | boolean | true | Se deve logar parâmetros da requisição |
| `logResult` | boolean | true | Se deve logar o response body |
| `maxLength` | int | -1 | Tamanho máximo do log (-1 = usa config global) |

### @LogOperation

| Parâmetro | Tipo | Padrão | Descrição |
|-----------|------|--------|-----------|
| `value` | String | "" | Descrição da operação (vazio = usa nome do método) |
| `logArgs` | boolean | true | Se deve logar argumentos do método |
| `logResult` | boolean | true | Se deve logar o valor de retorno |
| `maxLength` | int | -1 | Tamanho máximo do log (-1 = usa config global) |

---

## 📝 Exemplos Completos

### Exemplo 1: Controller REST Completo

```java
@RestController
@RequestMapping("/api/products")
@LogHttp(value = "ProductController")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    @GetMapping
    @LogHttp(value = "Listar todos os produtos", logResult = false)
    public List<Product> listAll() {
        return productService.findAll();
    }
    
    @GetMapping("/{id}")
    @LogHttp("Buscar produto por ID")
    public Product getById(@PathVariable Long id) {
        return productService.findById(id);
    }
    
    @PostMapping
    @LogHttp(value = "Criar produto", maxLength = 500)
    public Product create(@RequestBody ProductDTO dto) {
        return productService.create(dto);
    }
    
    @PutMapping("/{id}")
    @LogHttp("Atualizar produto")
    public Product update(@PathVariable Long id, @RequestBody ProductDTO dto) {
        return productService.update(id, dto);
    }
    
    @DeleteMapping("/{id}")
    @LogHttp(value = "Deletar produto", logResult = false)
    public void delete(@PathVariable Long id) {
        productService.delete(id);
    }
}
```

**Logs gerados:**
```
🔗 [⯈ IN ] GET /api/products → Listar todos os produtos
✅ [⬅ OUT] GET /api/products ✓ 67ms

🔗 [⯈ IN ] POST /api/products → Criar produto [ProductDTO{name=Notebook, price=3500.00}]
✅ [⬅ OUT] POST /api/products ✓ 145ms → Product{id=1, name=Notebook, price=3500.00}

🔗 [⯈ IN ] DELETE /api/products/5 → Deletar produto [5]
✅ [⬅ OUT] DELETE /api/products/5 ✓ 34ms
```

---

### Exemplo 2: Service com Use Cases (Clean Architecture)

```java
@Service
public class ProductService {
    
    @LogOperation("Buscar todos os produtos")
    public List<Product> findAll() {
        return productRepository.findAll();
    }
    
    @LogOperation(value = "Buscar produto por ID", logArgs = true, logResult = true)
    public Product findById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException(id));
    }
    
    @LogOperation(value = "Criar produto", logArgs = true)
    public Product create(ProductDTO dto) {
        validateProduct(dto);
        Product product = productMapper.toEntity(dto);
        return productRepository.save(product);
    }
    
    @LogOperation("Atualizar produto")
    public Product update(Long id, ProductDTO dto) {
        Product existing = findById(id);
        productMapper.updateEntity(existing, dto);
        return productRepository.save(existing);
    }
    
    @LogOperation(value = "Deletar produto", logResult = false)
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }
        productRepository.deleteById(id);
    }
    
    // Métodos privados NÃO são logados (limitação do Spring AOP)
    private void validateProduct(ProductDTO dto) {
        if (dto.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidProductException("Price must be positive");
        }
    }
}
```

**Logs gerados:**
```
⏰ [▶ START] Buscar produto por ID → args: [1]
✅ [✓ SUCCESS] Buscar produto por ID ✓ 23ms → result: Product{id=1, name=Notebook}

⏰ [▶ START] Criar produto → args: [ProductDTO{name=Mouse, price=50.00}]
✅ [✓ SUCCESS] Criar produto ✓ 89ms → result: Product{id=2, name=Mouse, price=50.00}

⏰ [▶ START] Deletar produto → args: [999]
❌ [✗ ERROR] Deletar produto ✗ 12ms - ProductNotFoundException: Product not found with id: 999
```

---

### Exemplo 3: Arquitetura Hexagonal (Ports & Adapters)

```java
// Adapter (entrada HTTP)
@RestController
@RequestMapping("/api/orders")
@LogHttp
public class OrderAdapter {
    
    private final CreateOrderUseCase createOrderUseCase;
    
    @PostMapping
    @LogHttp(value = "Receber novo pedido", maxLength = 1000)
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest request) {
        Order order = createOrderUseCase.execute(request);
        return ResponseEntity.status(201).body(orderMapper.toResponse(order));
    }
}

// Use Case (lógica de negócio)
@Component
public class CreateOrderUseCase {
    
    private final OrderPort orderPort;
    private final PaymentPort paymentPort;
    
    @LogOperation("Executar criação de pedido")
    public Order execute(OrderRequest request) {
        Order order = buildOrder(request);
        
        processPayment(order);
        
        return orderPort.save(order);
    }
    
    @LogOperation(value = "Processar pagamento", logArgs = false)
    private void processPayment(Order order) {
        // Pagamento com dados sensíveis - logArgs=false
        paymentPort.process(order.getPayment());
    }
    
    private Order buildOrder(OrderRequest request) {
        // Método privado - não será logado
        return orderMapper.toDomain(request);
    }
}

// Adapter (saída - repositório)
@Component
public class OrderRepositoryAdapter implements OrderPort {
    
    private final OrderJpaRepository jpaRepository;
    
    @LogOperation("Salvar pedido no banco")
    public Order save(Order order) {
        OrderEntity entity = orderMapper.toEntity(order);
        OrderEntity saved = jpaRepository.save(entity);
        return orderMapper.toDomain(saved);
    }
}
```

**Logs gerados (fluxo completo):**
```
🔗 [⯈ IN ] POST /api/orders → Receber novo pedido [OrderRequest{items=3, total=450.00}]
⏰ [▶ START] Executar criação de pedido → args: [OrderRequest{items=3, total=450.00}]
⏰ [▶ START] Processar pagamento
✅ [✓ SUCCESS] Processar pagamento ✓ 1250ms
⏰ [▶ START] Salvar pedido no banco → args: [Order{id=null, status=PENDING}]
✅ [✓ SUCCESS] Salvar pedido no banco ✓ 45ms → result: Order{id=1, status=CONFIRMED}
✅ [✓ SUCCESS] Executar criação de pedido ✓ 1320ms → result: Order{id=1, status=CONFIRMED}
✅ [⬅ OUT] POST /api/orders ✓ 1350ms → OrderResponse{orderId=1, status=CONFIRMED}
```

---

## 🔧 Troubleshooting

### ❌ Logs não aparecem

**Possíveis causas:**

1. **Biblioteca não está no classpath**
    - Verifique se a dependência foi adicionada corretamente ao pom.xml/build.gradle
    - Execute `mvn dependency:tree` ou `gradle dependencies`

2. **Observability desabilitada**
   ```yaml
   observability:
     enabled: true  # ← Certifique-se que está true
   ```

3. **AspectJ não está habilitado**
    - A biblioteca já inclui `@EnableAspectJAutoProxy` automaticamente
    - Verifique se não há conflitos com outras configurações AOP

4. **Nível de log incorreto**
   ```yaml
   logging:
     level:
       com.fiap.libs.observability: INFO  # ou DEBUG
   ```

---

### ❌ @LogHttp não funciona

**Possíveis causas:**

1. **Não é um contexto HTTP**
    - `@LogHttp` só funciona em requisições HTTP reais
    - Para métodos não-HTTP, use `@LogOperation`

2. **Método privado**
    - Spring AOP só intercepta métodos públicos
    - Torne o método `public` ou `protected`

3. **Self-invocation (chamada interna)**
   ```java
   // ❌ NÃO FUNCIONA
   public void methodA() {
       this.methodB();  // Chamada interna - aspect não intercepta
   }
   
   @LogHttp
   public void methodB() { }
   
   // ✅ FUNCIONA
   @Autowired
   private MeuController self;
   
   public void methodA() {
       self.methodB();  // Chamada via proxy - aspect intercepta
   }
   ```

---

### ❌ @LogOperation não funciona em repositórios

**Solução:**

Use `@LogOperation` em interfaces de repositórios customizados:

```java
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // ❌ Não funciona - métodos JPA padrão não podem ter aspects
    // List<Product> findAll();
    
    // ✅ Funciona - método customizado
    @LogOperation("Buscar produtos por categoria")
    @Query("SELECT p FROM Product p WHERE p.category = :category")
    List<Product> findByCategory(@Param("category") String category);
}
```

---

### ⚠️ Performance degradada

**Se a aplicação ficou lenta após adicionar a lib:**

1. **Reduza o maxLength**
   ```yaml
   observability:
     http:
       max-length: 100  # Ao invés de 500
   ```

2. **Desabilite logResult em endpoints pesados**
   ```java
   @GetMapping("/relatorio")
   @LogHttp(value = "Gerar relatório", logResult = false)  // ← Desabilita log do resultado
   public List<BigData> generateReport() { }
   ```

3. **Use logging seletivo**
   ```java
   // Ao invés de anotar a classe inteira:
   @LogHttp  // ← Remove
   public class MyController {
       
       @LogHttp  // ← Adicione apenas nos métodos críticos
       public void importantMethod() { }
       
       public void lessImportantMethod() { }  // Sem logging
   }
   ```

---

### 🔒 Dados sensíveis ainda aparecem nos logs

**Se a sanitização não está funcionando:**

1. **Reporte o padrão não coberto**
    - Abra uma issue com exemplo do dado não sanitizado
    - Expandiremos a regex para cobrir mais casos

2. **Desabilite logArgs temporariamente**
   ```java
   @LogOperation(value = "Processar pagamento", logArgs = false)
   public void processPayment(PaymentData data) {
       // Dados sensíveis não serão logados
   }
   ```

3. **Use DTO específico para logging**
   ```java
   @LogOperation("Criar usuário")
   public User create(UserDTO dto) {
       // Crie uma versão "safe" do DTO para logging
       UserSafeDTO safeDto = UserSafeDTO.from(dto);  // Remove campos sensíveis
       // ...
   }
   ```

---

## 🆕 Novidades v2.0.0

### Mudanças importantes da v1.0.0 para v2.0.0:

#### ✨ Novas Features
- **Anotação @LogHttp**: Substituiu o logging automático de controllers por padrão de package
- **Anotação @LogOperation**: Substituiu @StepLog com mais opções
- **Sanitização de dados sensíveis**: Automática em todos os logs
- **Configuração granular**: Controle HTTP e Operation separadamente
- **Suporte multi-arquitetura**: Agnóstico quanto ao estilo arquitetural

#### ⚠️ Breaking Changes

```java
// ❌ v1.0.0 (DEPRECATED)
@StepLog("Minha operação")
public void myMethod() { }

// ✅ v2.0.0 (NOVO)
@LogOperation("Minha operação")
public void myMethod() { }
```

```yaml
# ❌ v1.0.0 (DEPRECATED)
observability:
  controller:
    base-package: "**.controller..*"

# ✅ v2.0.0 (NOVO)
observability:
  http:
    enabled: true
```

#### 🔄 Guia de Migração v1 → v2

1. **Substituir @StepLog por @LogOperation**
   ```bash
   # Buscar e substituir no projeto
   @StepLog → @LogOperation
   ```

2. **Adicionar @LogHttp nos controllers**
   ```java
   @RestController
   @LogHttp  // ← Adicione esta linha
   public class MyController { }
   ```

3. **Atualizar configuração**
   ```yaml
   observability:
     http:
       enabled: true
     operation:
       enabled: true
   ```

4. **Remover configurações antigas**
   ```yaml
   # Remova estas linhas:
   # controller.base-package
   # service.enabled
   ```

---

## 📚 Documentação Adicional

### Ordem de Execução dos Aspects

Os aspects seguem esta ordem de precedência:

1. **HttpLoggingAspect**: `LOWEST_PRECEDENCE - 1000` (executa primeiro)
2. **OperationLoggingAspect**: `LOWEST_PRECEDENCE - 500` (executa depois)

Isso garante que:
- Logs HTTP aparecem antes de logs de operações
- Em caso de múltiplos aspects, você pode controlar a ordem via configuração

```yaml
observability:
  http:
    order: -999000  # Menor = executa primeiro
  operation:
    order: -999500
```

---

### Limitações Conhecidas

1. **Métodos privados não são interceptados** (limitação do Spring AOP)
2. **Self-invocation não dispara aspects** (chamadas internas via `this`)
3. **Overhead de ~1-5ms por invocação** em aplicações de altíssima performance
4. **Logs muito grandes podem impactar performance** (use maxLength adequado)

---

## 📞 Suporte

- **Issues**: [GitHub Issues](https://github.com/OtavioValadao/unified-service-core-libs/issues)
- **Documentação**: [Wiki](https://github.com/OtavioValadao/unified-service-core-libs/wiki)
- **Email**: observability@fiap.com.br

---

## 📄 Licença

MIT License - Copyright (c) 2025 FIAP

---

## 👥 Autores

**FIAP - Unified Service Core Team**
- Versão atual: v2.0.0
- Data de lançamento: 2025

---

**🎯 Dica Final**: Use `@LogHttp` em endpoints HTTP e `@LogOperation` em lógica de negócio. A biblioteca cuida do resto! 🚀