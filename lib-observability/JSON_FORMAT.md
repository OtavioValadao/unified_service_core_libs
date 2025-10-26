# 📊 Formato JSON Automático nos Logs

## ✅ O que mudou?

A biblioteca de observabilidade agora **automaticamente serializa objetos em formato JSON** quando loga argumentos e resultados.

## 📝 Exemplo

### Antes (toString):
```
⏰ [▶ START] Criar usuário → args: [UserDTO@5f4c8a99]
✅ [✓ SUCCESS] Criar usuário ✓ 89ms → result: User@7b2c9a45
```

### Agora (JSON automático):
```
⏰ [▶ START] Criar usuário → args: [{"name":"João Silva","email":"joao@email.com","role":"USER"}]
✅ [✓ SUCCESS] Criar usuário ✓ 89ms → result: {"id":123,"name":"João Silva","email":"joao@email.com","createdAt":"2025-01-15T10:30:00"}
```

## 🎯 Benefícios

1. **Logs mais legíveis**: Você vê todos os campos do objeto claramente
2. **Integração com ferramentas de monitoramento**: ELK, Splunk, Datadog, etc podem parsear facilmente
3. **Datas padronizadas**: Formato ISO-8601 automático
4. **Sanitização mantida**: Dados sensíveis continuam sendo ocultados

## 🔒 Segurança

A sanitização de dados sensíveis funciona em JSON também:

```json
// Input
{"username":"joao","password":"S3nh@F0rt3","token":"abc123xyz"}

// Log
{"username":"joao","password":"***","token":"***"}
```

## ⚙️ Configuração

### JSON Compacto (Padrão)

Nenhuma configuração necessária! O JSON compacto é automático:

```yaml
observability:
  log:
    json-pretty-print: false  # Padrão
```

**Resultado:**
```
{"id":123,"name":"João Silva","email":"joao@email.com"}
```

### JSON Formatado (Pretty Print)

Para ativar JSON com quebras de linha e indentação:

```yaml
observability:
  log:
    json-pretty-print: true  # Habilita pretty print
  http:
    max-length: 2000  # Aumenta para acomodar JSONs formatados
  operation:
    max-length: 2000
```

**Resultado:**
```
{
  "id": 0,
  "plate": "VGS8444",
  "model": {
    "id": 0,
    "brand": "Toyota",
    "model": "Corolla",
    "clientCpfCnpj": "12345678900",
    "year": 2024
  },
  "clientCpfCnpj": "12345678900"
}
```

**Recomendações:**
- 🟢 **Desenvolvimento**: Use `json-pretty-print: true` para debugging mais fácil
- 🔵 **Produção**: Use `json-pretty-print: false` para logs compactos e menor overhead

### Desabilitar Truncamento (Sem Limite)

Para logar objetos **sem limite de tamanho**, configure `max-length: 0`:

```yaml
observability:
  log:
    json-pretty-print: true  # ou false
  http:
    max-length: 0  # 👈 0 = SEM LIMITE (log completo)
  operation:
    max-length: 0
```

**Resultado:**
```
⏰ [▶ START] Criar veículo → args: [{ ... JSON COMPLETO SEM TRUNCAMENTO ... }]
```

**Quando usar:**
- ✅ Debugging de objetos muito grandes
- ✅ Desenvolvimento local
- ⚠️ **Cuidado em produção**: logs gigantes podem impactar performance

## 🔧 Comportamento

- **Objetos complexos**: Serializados em JSON
- **Strings**: Retornadas diretas
- **Números/Boolean**: Retornados como toString()
- **Datas**: Formato ISO-8601
- **Erro na serialização**: Fallback automático para toString()
- **Referências circulares**: Fallback automático para toString()

## 📊 Casos de Uso

### 1. DTOs
```java
@PostMapping
@LogHttp
public User create(@RequestBody UserDTO dto) {
    return userService.create(dto);
}
```
**Log:**
```
🔗 [⯈ IN ] POST /api/users → create() [{"name":"João","email":"joao@example.com"}]
```

### 2. Entidades
```java
@LogOperation
public User save(User user) {
    return repository.save(user);
}
```
**Log:**
```
⏰ [▶ START] save → args: [{"id":null,"name":"João","createdAt":"2025-01-15T10:30:00"}]
✅ [✓ SUCCESS] save ✓ 45ms → result: {"id":123,"name":"João","createdAt":"2025-01-15T10:30:00"}
```

### 3. Listas
```java
@LogOperation
public List<Product> findAll() {
    return repository.findAll();
}
```
**Log:**
```
⏰ [▶ START] findAll
✅ [✓ SUCCESS] findAll ✓ 67ms → result: [{"id":1,"name":"Produto A"},{"id":2,"name":"Produto B"}]
```

## ⚠️ Considerações

1. **Performance**: Adiciona ~2-5ms de overhead na serialização
2. **Tamanho**: JSONs são maiores que toString(), ajuste `maxLength` se necessário
3. **Truncamento**: JSONs muito grandes serão truncados (configure `maxLength`)

## 💡 Dicas

- Para endpoints que retornam muitos dados, use `logResult = false`:
```java
@GetMapping("/report")
@LogHttp(value = "Gerar relatório", logResult = false)
public List<BigData> report() { }
```

- Para dados sensíveis, use `logArgs = false`:
```java
@LogOperation(value = "Processar pagamento", logArgs = false)
public void processPayment(PaymentData data) { }
```

