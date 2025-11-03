# 📧 Send Notification Library - FIAP Unified Service Core

Biblioteca para envio de notificações por email com templates HTML customizáveis, suporte a envio assíncrono e **auto-configuração automática Spring Boot**.

## ✨ Características

- ⚡ **Zero Configuração Manual**: Auto-configuração automática via Spring Boot
- 🚀 **Plug and Play**: Adicione a dependência e configure as properties - pronto!
- 📨 **Envio Assíncrono**: Emails enviados em background via `CompletableFuture`
- 🎨 **Templates HTML**: Suporte completo a templates responsivos
- 🔧 **Totalmente Configurável**: Customize tudo via `application.yml`
- 🔒 **Seguro**: Credenciais via environment variables
- ✅ **Type Safe**: DTOs com Java Records

## 📦 Instalação

### Maven
```xml
<dependency>
    <groupId>com.github.OtavioValadao</groupId>
    <artifactId>lib-send-notification</artifactId>
    <version>1.4.7</version>
</dependency>
```

### Gradle
```gradle
implementation 'com.github.OtavioValadao:lib-send-notification:1.4.7'
```

## 🚀 Início Rápido

### 1. Configure o `application.yml`

```yaml
notification:
  enabled: true  # Opcional: padrão é true
  mail:
    host: smtp.gmail.com
    port: 587
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}
    from:
      address: noreply@example.com
      name: "My Application"
    templates:
      welcome:
        path: template/email_welcome_content.html
        subject: "Bem-vindo!"
      service-order-finalized:
        path: template/service_order_finalized_email.html
        subject: "Sua OS está pronta para retirada!"
```

**Configurações Opcionais:**
```yaml
notification:
  mail:
    protocol: smtp  # Padrão: smtp
    default-encoding: UTF-8  # Padrão: UTF-8
    properties:
      mail.smtp.auth: true
      mail.smtp.starttls.enable: true
      mail.smtp.starttls.required: true
      mail.smtp.ssl.trust: "*"
```

### 2. Configure Credenciais de Email

**Para Gmail:**
1. Ative a verificação em 2 fatores: https://myaccount.google.com/security
2. Gere uma "Senha de app": https://myaccount.google.com/apppasswords
3. Use a senha gerada como `MAIL_PASSWORD`

**Outros Provedores:**
- **Outlook/Hotmail**: `smtp.office365.com:587`
- **Yahoo**: `smtp.mail.yahoo.com:587`
- **SendGrid**: `smtp.sendgrid.net:587`
- **AWS SES**: `email-smtp.us-east-1.amazonaws.com:587`

### 3. Configure Variáveis de Ambiente

```bash
# Linux/Mac
export MAIL_USERNAME="seu-email@gmail.com"
export MAIL_PASSWORD="sua-senha-app"

# Windows (CMD)
set MAIL_USERNAME=seu-email@gmail.com
set MAIL_PASSWORD=sua-senha-app

# Windows (PowerShell)
$env:MAIL_USERNAME="seu-email@gmail.com"
$env:MAIL_PASSWORD="sua-senha-app"
```

---

## 💻 Como Usar

### 1. Injete o Serviço

**Não é necessário criar nenhuma configuração manual!** A biblioteca usa auto-configuração do Spring Boot.

Simplesmente injete o `SendEmailNotification`:

```java
@Service
@RequiredArgsConstructor
public class UserService {

    private final SendEmailNotification emailNotification;

    // Use os métodos diretamente!
}
```

### 2. Envio de Email de Boas-Vindas

```java
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final SendEmailNotification emailNotification;

    @PostMapping
    public ResponseEntity<UserResponse> create(@RequestBody UserRequest request) {
        User user = userService.create(request);

        // Envio assíncrono de email de boas-vindas
        CustomerRecord customer = new CustomerRecord(
            user.getName(),    // nickName
            user.getEmail()    // email
        );
        emailNotification.sendEmailWelcome(customer);

        return ResponseEntity.status(201).body(userMapper.toResponse(user));
    }
}
```

**Template usado:** Configurável via `notification.mail.templates.welcome.path`
**Variáveis do template:** `{{cliente}}` - Nome do cliente
**Assunto:** Configurável via `notification.mail.templates.welcome.subject`

---

### 3. Envio de Email de Ordem de Serviço Finalizada

```java
@Service
@RequiredArgsConstructor
public class ServiceOrderService {

    private final SendEmailNotification emailNotification;

    @LogOperation("Finalizar ordem de serviço")
    public void finalizeServiceOrder(Long orderId) {
        ServiceOrder order = serviceOrderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("ServiceOrder", orderId));

        order.setStatus(ServiceOrderStatus.COMPLETED);
        order.setCompletionDate(LocalDateTime.now());
        serviceOrderRepository.save(order);

        // Envio assíncrono de email de OS finalizada
        ServiceOrderRecord orderRecord = new ServiceOrderRecord(
            order.getOrderNumber(),
            new CustomerRecord(
                order.getClient().getName(),
                order.getClient().getEmail()
            ),
            new VehicleRecord(
                order.getVehicle().getPlate(),
                new ModelRecord(
                    order.getVehicle().getModel().getBrand(),
                    order.getVehicle().getModel().getName(),
                    order.getVehicle().getModel().getYear()
                )
            ),
            order.getCompletionDate().format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            )
        );

        emailNotification.sendServiceOrderFinalizedEmail(orderRecord);
    }
}
```

**Template usado:** Configurável via `notification.mail.templates.service-order-finalized.path`
**Variáveis do template:**
- `{{cliente}}` - Nome do cliente
- `{{osNumero}}` - Número da ordem de serviço
- `{{veiculo}}` - Informações do veículo (marca modelo ano - placa)
- `{{dataFinalizacao}}` - Data de finalização
**Assunto:** Configurável via `notification.mail.templates.service-order-finalized.subject`

---

## 📋 DTOs Disponíveis

### CustomerRecord
```java
public record CustomerRecord(
    String nickName,  // Nome do cliente
    String email      // Email do destinatário
) {}
```

### ServiceOrderRecord
```java
public record ServiceOrderRecord(
    String orderNumber,          // Número da OS
    CustomerRecord client,       // Dados do cliente
    VehicleRecord vehicleRecord, // Dados do veículo
    String completionDate        // Data de finalização (formato String)
) {}
```

### VehicleRecord
```java
public record VehicleRecord(
    String plate,        // Placa do veículo
    ModelRecord model    // Modelo do veículo
) {}
```

### ModelRecord
```java
public record ModelRecord(
    String brand,     // Marca
    String model,     // Nome do modelo
    Integer year      // Ano do modelo
) {}
```

---

## 🎨 Templates HTML

A biblioteca inclui 2 templates prontos para uso:

### 1. Email de Boas-Vindas (`email_welcome_content.html`)

```html
<!doctype html>
<html lang="pt-BR">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Email de Boas-vindas</title>
    <style>
        body { font-family: Arial, sans-serif; background-color: #f5f7fb; }
        .container { max-width: 600px; margin: 0 auto; background-color: #ffffff; }
        h1 { color: #0b63d6; }
    </style>
</head>
<body>
<div class="container">
    <h1>Bem-vindo(a), {{cliente}}!</h1>
    <p>Estamos muito felizes em ter você conosco.</p>
    <p>É um prazer contar com a sua presença em nossa comunidade.</p>
</div>
</body>
</html>
```

### 2. Email de OS Finalizada (`service_order_finalized_email.html`)

```html
<!doctype html>
<html lang="pt-BR">
<head>
    <meta charset="utf-8">
    <title>OS Finalizada</title>
    <style>
        body { font-family: Arial, sans-serif; }
        .container { max-width: 600px; margin: 0 auto; }
        h1 { color: #16a34a; }
    </style>
</head>
<body>
<div class="container">
    <h1>OS pronta para retirada!</h1>
    <p>Olá, {{cliente}}!</p>
    <p>Sua Ordem de Serviço <strong>{{osNumero}}</strong> foi finalizada em <strong>{{dataFinalizacao}}</strong>.</p>
    <div class="info">
        <div><span class="label">Veículo:</span> {{veiculo}}</div>
        <div><span class="label">Situação:</span> Serviços concluídos. Aguardando retirada.</div>
    </div>
</div>
</body>
</html>
```

---

## 🎨 Customização de Templates

### Criando Templates Personalizados

1. **Crie seu template HTML** em `src/main/resources/template/`:

```html
<!-- src/main/resources/template/custom_notification.html -->
<!doctype html>
<html lang="pt-BR">
<head>
    <meta charset="utf-8">
    <title>Atualização de Pedido</title>
</head>
<body>
    <h1>Olá, {{nome}}!</h1>
    <p>Seu pedido <strong>{{numeroPedido}}</strong> foi {{status}}.</p>
    <p>Valor: R$ {{valor}}</p>
</body>
</html>
```

2. **Configure o template no `application.yml`** (opcional):

```yaml
notification:
  mail:
    templates:
      welcome:
        path: template/email_welcome_content.html
        subject: "Bem-vindo!"
      service-order-finalized:
        path: template/service_order_finalized_email.html
        subject: "Sua OS está pronta!"
      # Adicione seu template customizado (se quiser usar via properties)
      custom-notification:
        path: template/custom_notification.html
        subject: "Atualização do Pedido"
```

3. **Crie um serviço customizado** para enviar o email:

```java
@Service
@RequiredArgsConstructor
public class CustomNotificationService {

    private final JavaMailSender mailSender;
    private final LoadTemplateConfig loadTemplateConfig;
    private final NotificationProperties properties;

    public void sendCustomNotification(String nome, String email,
                                       String numeroPedido, String status, String valor) {
        CompletableFuture.runAsync(() -> {
            try {
                String template = loadTemplateConfig.loadTemplate("template/custom_notification.html");

                String htmlBody = template
                    .replace("{{nome}}", nome)
                    .replace("{{numeroPedido}}", numeroPedido)
                    .replace("{{status}}", status)
                    .replace("{{valor}}", valor);

                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    true,
                    properties.getMail().getDefaultEncoding()
                );

                helper.setFrom(
                    properties.getMail().getFrom().getAddress(),
                    properties.getMail().getFrom().getName()
                );
                helper.setTo(email);
                helper.setSubject("Atualização do Pedido " + numeroPedido);
                helper.setText(htmlBody, true);

                mailSender.send(message);

                log.info("Custom notification sent successfully to {}", email);
            } catch (Exception e) {
                log.error("Error sending custom notification: {}", e.getMessage(), e);
            }
        });
    }
}
```

---

## ⚙️ Configuração Avançada

### Desabilitar a Biblioteca

Se necessário, você pode desabilitar a biblioteca completamente:

```yaml
notification:
  enabled: false
```

### Configuração Completa de SMTP

```yaml
notification:
  mail:
    host: smtp.custom-provider.com
    port: 465  # Use 465 para SSL ou 587 para TLS
    protocol: smtp
    default-encoding: UTF-8
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}
    from:
      address: noreply@mycompany.com
      name: "My Company"
    properties:
      mail.smtp.auth: true
      mail.smtp.starttls.enable: false  # false se usar SSL (porta 465)
      mail.smtp.ssl.enable: true        # true se usar SSL (porta 465)
      mail.smtp.ssl.trust: "*"
      mail.debug: false  # true para logs detalhados
    templates:
      welcome:
        path: template/custom_welcome.html
        subject: "Bem-vindo à nossa plataforma!"
      service-order-finalized:
        path: template/custom_order_finalized.html
        subject: "Seu pedido foi finalizado!"
```

### Configuração de Timeouts

```yaml
notification:
  mail:
    properties:
      mail.smtp.timeout: 5000            # Timeout de conexão (ms)
      mail.smtp.connectiontimeout: 5000  # Timeout de leitura (ms)
      mail.smtp.writetimeout: 5000       # Timeout de escrita (ms)
```

### Múltiplas Estratégias de Carregamento

A classe `LoadTemplateConfig` carrega templates automaticamente usando múltiplas estratégias:

1. **ResourceLoader com classpath** - Padrão do Spring
2. **ClassPathResource** - Busca direta no classpath
3. **Thread ClassLoader** - Útil em ambientes com múltiplos classloaders
4. **Class ClassLoader** - Fallback final

Isso garante compatibilidade com diferentes ambientes (IDE, JAR, WAR, containers Docker).

---

## 🔒 Segurança

- **Credenciais via Environment Variables**: Use `${MAIL_USERNAME}` e `${MAIL_PASSWORD}`
- **TLS/SSL**: Suporte a conexões seguras
- **Sanitização**: Templates são processados de forma segura
- **Não expor credenciais**: Nunca commite credenciais no código ou properties

---

## 📝 Logs

A biblioteca gera logs informativos durante o envio:

```
INFO  - 📧 Creating JavaMailSender bean with auto-configuration
INFO  - ✅ JavaMailSender configured - Host: smtp.gmail.com, Port: 587
INFO  - 🚀 [SEND-NOTIFICATION] SendEmailNotification bean created successfully
INFO  - Welcome email sent successfully to user@example.com
```

Em caso de erro:
```
ERROR - Error when send welcome email to UserName: Template not found
java.lang.RuntimeException: Template file does not exist: template/missing.html
```

---

## 🧪 Testes

A biblioteca inclui suite completa de testes:

**SendNotificationAutoConfigurationTest** (5 testes):
- Auto-configuração carrega corretamente
- Beans registrados (JavaMailSender, LoadTemplateConfig, SendEmailNotification)
- Configuração de properties
- Desabilitação via `notification.enabled=false`

**SendEmailNotificationTest** (5 testes):
- Envio de email de boas-vindas
- Envio de email de OS finalizada
- Tratamento de email inválido
- Carregamento de template
- Tratamento de exceção no template

**LoadTemplateConfigTest** (4 testes):
- Carregamento de template com ResourceLoader
- Exceção quando template não existe
- Carregamento real do classpath
- Suporte a templates multilinha

**Resultado:**
```
Tests run: 14, Failures: 0, Errors: 0, Skipped: 0
```

---

## 🔧 Troubleshooting

### ❌ Email não está sendo enviado

**Possíveis causas:**

1. **Credenciais incorretas**
   - Verifique `MAIL_USERNAME` e `MAIL_PASSWORD`
   - Para Gmail, use senha de app, não sua senha normal

2. **Firewall bloqueando porta**
   - Verifique se a porta 587 (TLS) ou 465 (SSL) está aberta
   - Teste: `telnet smtp.gmail.com 587`

3. **Template não encontrado**
   ```
   ERROR - Template file not found: template/custom.html
   ```
   - Verifique o caminho do template
   - Templates devem estar em `src/main/resources/template/`

4. **Configuração de SMTP incorreta**
   ```yaml
   # Teste com configuração mínima:
   notification:
     mail:
       host: smtp.gmail.com
       port: 587
       username: seu-email@gmail.com
       password: sua-senha-app
       properties:
         mail.smtp.auth: true
         mail.smtp.starttls.enable: true
   ```

---

### ❌ Email chega na caixa de spam

**Soluções:**

1. **Configure SPF/DKIM** no seu domínio
2. **Use provedores confiáveis** (SendGrid, AWS SES, Mailgun)
3. **Adicione link de unsubscribe** nos emails
4. **Evite palavras spam** (GRÁTIS, URGENTE, etc)
5. **Use email remetente válido**

---

### ❌ Templates não estão sendo substituídos

Verifique se as variáveis estão corretas:

```java
// ❌ Errado
String html = template.replace("{cliente}", nome);  // Faltam chaves duplas

// ✅ Correto
String html = template.replace("{{cliente}}", nome);
```

---

## 📦 Exemplo Completo

```java
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SendEmailNotification emailNotification;

    // Exemplo 1: Email de boas-vindas
    public void sendWelcomeEmail(User user) {
        CustomerRecord customer = new CustomerRecord(user.getName(), user.getEmail());
        emailNotification.sendEmailWelcome(customer);
    }

    // Exemplo 2: Email de OS finalizada
    public void notifyServiceOrderCompletion(ServiceOrder order) {
        ServiceOrderRecord dto = buildServiceOrderRecord(order);
        emailNotification.sendServiceOrderFinalizedEmail(dto);
    }

    private ServiceOrderRecord buildServiceOrderRecord(ServiceOrder order) {
        CustomerRecord customer = new CustomerRecord(
            order.getClient().getName(),
            order.getClient().getEmail()
        );

        ModelRecord model = new ModelRecord(
            order.getVehicle().getModel().getBrand(),
            order.getVehicle().getModel().getName(),
            order.getVehicle().getModel().getYear()
        );

        VehicleRecord vehicle = new VehicleRecord(
            order.getVehicle().getPlate(),
            model
        );

        return new ServiceOrderRecord(
            order.getOrderNumber(),
            customer,
            vehicle,
            order.getCompletionDate().format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            )
        );
    }
}
```

---

## 💡 Suporte

- **Issues**: [GitHub Issues](https://github.com/OtavioValadao/unified_service_core_libs/issues)
- **Documentação**: [Wiki](https://github.com/OtavioValadao/unified_service_core_libs/wiki)

---

## 📄 Licença

MIT License - Copyright (c) 2025 FIAP

---

## 👥 Autores

**FIAP - Unified Service Core Team**
- Versão atual: v1.4.7
- Data de lançamento: Janeiro 2025

---

**💡 Dica Final**: Configure as credenciais de email via environment variables para segurança. A biblioteca é **plug-and-play** - não precisa criar nenhuma configuração manual! 🎉🚀
