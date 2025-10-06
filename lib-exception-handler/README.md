# 🚫 Exception Handler Library - FIAP Unified Service Core

Biblioteca de tratamento padronizado de erros para aplicações Spring Boot. Converte exceções de domínio em respostas HTTP consistentes com payload estruturado.

## 📦 Instalação

### Maven
```xml
<dependency>
    <groupId>com.github.OtavioValadao.unified-service-core-libs</groupId>
    <artifactId>lib-exception-handler</artifactId>
    <version>v1.0.0</version>
    <scope>compile</scope>
    </dependency>
```

### Gradle
```gradle
implementation 'com.github.OtavioValadao.unified-service-core-libs:lib-exception-handler:v1.0.0'
```

## 🚀 Uso

Adicione a dependência e a biblioteca ativará automaticamente os handlers globais via AutoConfiguration. Basta lançar as exceções disponibilizadas pela lib no seu código.

### Exemplo simples
```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/{id}")
    public UserResponse get(@PathVariable Long id) {
        return userService.findById(id)
            .map(UserMapper::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }
}
```

### Exemplo com validação de campos
```java
@PostMapping
public ResponseEntity<UserResponse> create(@RequestBody UserRequest request) {
    List<CustomFiledError> errors = new ArrayList<>();
    if (request.getEmail() == null) {
        errors.add(new CustomFiledError("email", "must not be null"));
    }
    if (!errors.isEmpty()) {
        throw new ValidationException("Invalid request payload", errors);
    }
    return ResponseEntity.status(201).body(userService.create(request));
}
```

## 🧾 Estrutura do ErrorResponse

Respostas de erro seguem um formato consistente:

```json
{
  "timestamp": "2025-01-01T12:34:56.789Z",
  "status": 404,
  "error": "NOT_FOUND",
  "message": "Resource 'User' not found with id=123",
  "path": "/api/users/123",
  "code": "RESOURCE_NOT_FOUND",
  "fields": [
    { "field": "email", "message": "must not be null" }
  ]
}
```

Campos podem variar conforme a exceção, mas os principais (`timestamp`, `status`, `error`, `message`, `path`, `code`) estarão presentes.

## 📚 Exceções disponíveis

Lance as exceções conforme o cenário. A lib fará o mapeamento para os status HTTP apropriados e preencherá o `ErrorResponse`.

- `BadRequestException` → 400
- `InvalidParameterException` → 400
- `InvalidFormatException` → 400
- `MissingRequiredFieldException` → 400
- `PayloadTooLargeException` → 413
- `UnsupportedMediaTypeException` → 415
- `MethodNotAllowedException` → 405
- `ValidationException` → 422 (com `fields`)
- `ResourceNotFoundException` → 404
- `ResourceAlreadyExistsException` → 409
- `ConflictException` → 409
- `TooManyRequestsException` → 429

Cada exceção utiliza um `ErrorCode` interno (ver `com.fiap.libs.exception.enums.ErrorCode`) para padronizar `code` e `message` base.

## ⚙️ Configuração (opcional)

Em geral, não é necessário configurar nada. Caso deseje ajustar logs/níveis do handler:

```yaml
logging:
  level:
    com.fiap.libs.exception: INFO
```

## 🔧 Troubleshooting

### ❌ Minha resposta não está no formato esperado
1. Verifique se a dependência está presente no classpath (pom/build.gradle)
2. Confirme que nenhuma configuração customizada está sobrescrevendo o handler global
3. Cheque conflitos com outros `@ControllerAdvice`

### ❌ `ValidationException` não retorna `fields`
Garanta que você está populando a lista de `CustomFiledError` ao lançar a exceção.

## 🧩 Dicas de uso

- Use exceções específicas para comunicar corretamente o status HTTP.
- Prefira `ValidationException` quando precisar retornar erros de campo em massa.
- Inclua contexto na mensagem (ex.: nome do recurso, identificadores) para facilitar troubleshooting.

## 📞 Suporte

- Issues: `https://github.com/OtavioValadao/unified-service-core-libs/issues`
- Documentação: `https://github.com/OtavioValadao/unified-service-core-libs/wiki`
- Email: exceptions@fiap.com.br

## 📄 Licença

MIT License — Copyright (c) 2025 FIAP

## 👥 Autores

**FIAP - Unified Service Core Team**
- Versão atual: v1.0.0
- Data de lançamento: 2025


