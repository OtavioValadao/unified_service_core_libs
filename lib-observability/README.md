# 🔍 Observability Library - FIAP Unified Service Core

Biblioteca de observabilidade para aplicações Spring Boot com logging automático de controllers e services.

## 📦 Instalação

### Maven
```xml
<dependency>
    <groupId>com.github.OtavioValadao.unified-service-core-libs</groupId>
    <artifactId>lib-observability</artifactId>
    <version>v1.0.0</version>
</dependency>
```

## 🚀 Uso

### 1. Logging Automático de Controllers

**Ativa automaticamente** para todos os controllers no package padrão `**.controller..*`

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.findById(id);
    }
}
```

**Output:**
```
🔗 [ ⯈ Incoming ] HTTP method: GET - endpoint: /api/users/123 - Controller [getUser] args: [123]
✓ [ ⬅ Outgoing ] Response [GET] in 45ms: User(id=123, name=João)
```

### 2. Logging de Services com @StepLog

```java
@Service
public class UserService {
    
    @StepLog("Finding user by ID")
    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
    }
    
    @StepLog("Creating new user")
    public User create(User user) {
        return userRepository.save(user);
    }
}
```

**Output:**
```
⏰ [STEP-TRY] Try: Finding user by ID -> params=[123]
✅ [STEP-SUCCESS] method: findById Success: Finding user by ID -> return = User(id=123, name=João)
```

**Em caso de erro:**
```
⏰ [STEP-TRY] Try: Finding user by ID -> params=[999]
❌ [STEP-ERROR] Error on Finding user by ID in method findById: User not found with id: 999
```

## ⚙️ Configuração

### application.yml

```yaml
observability:
  enabled: true  # Habilita/desabilita toda a lib (padrão: true)
  
  controller:
    enabled: true  # Habilita logging de controllers (padrão: true)
    base-packages: "com.sua.empresa.*.controller..*"  # Customiza o package pattern
  
  service:
    enabled: true  # Habilita logging de services (padrão: true)
```

### Desabilitar completamente

```yaml
observability:
  enabled: false
```

### Desabilitar apenas controllers

```yaml
observability:
  controller:
    enabled: false
```

### Customizar package dos controllers

```yaml
observability:
  controller:
    base-package: "com.fiap.*.controller..*"
```

## 🎯 Features

- ✅ **Zero configuração**: Funciona out-of-the-box
- ✅ **Auto-discovery**: Detecta automaticamente controllers
- ✅ **Flexible**: Configure via properties ou desabilite completamente
- ✅ **Performance tracking**: Mede tempo de execução dos controllers
- ✅ **Error handling**: Captura e loga exceções automaticamente
- ✅ **Pretty logs**: Emojis e formatação legível

## 📝 Exemplos Completos

### Controller com múltiplos endpoints

```java
@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    @GetMapping
    public List<Product> listAll() {
        return productService.findAll();
    }
    
    @PostMapping
    public Product create(@RequestBody Product product) {
        return productService.create(product);
    }
    
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        productService.delete(id);
    }
}
```

### Service com múltiplas operações

```java
@Service
public class ProductService {
    
    @StepLog("Listing all products")
    public List<Product> findAll() {
        return productRepository.findAll();
    }
    
    @StepLog("Creating product")
    public Product create(Product product) {
        validateProduct(product);
        return productRepository.save(product);
    }
    
    @StepLog("Deleting product")
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }
        productRepository.deleteById(id);
    }
    
    private void validateProduct(Product product) {
        // Validação sem log (método privado)
    }
}
```

## 🔧 Troubleshooting

### Logs não aparecem

1. Verifique se a lib está no classpath
2. Confirme que `observability.enabled=true`
3. Certifique-se que o package dos controllers está correto
4. Verifique se o AspectJ está habilitado no seu projeto

### Controllers não são logados

- Verifique o `base-package` na configuração
- Certifique-se que seus controllers estão no package correto
- Default: `**.controller..*`