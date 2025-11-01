# =� Send Notification Library - FIAP Unified Service Core

Biblioteca para envio de notifica��es por email com templates HTML customiz�veis e suporte a envio ass�ncrono.

## =� Instala��o

### Maven
```xml
<dependency>
    <groupId>com.github.OtavioValadao</groupId>
    <artifactId>lib-send-notification</artifactId>
    <version>1.4.1</version>
</dependency>
```

### Gradle
```gradle
implementation 'com.github.OtavioValadao:lib-send-notification:1.4.1'
```

## =� Uso

### 1. Configura��o de Email

Configure as credenciais de email no `application.yml`:

```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: ${MAIL_USERNAME:seu-email@gmail.com}
    password: ${MAIL_PASSWORD:sua-senha-app}
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
```

**Para Gmail:**
1. Ative a verifica��o em 2 fatores na sua conta Google
2. Gere uma "Senha de app" em: https://myaccount.google.com/apppasswords
3. Use a senha de app gerada no campo `MAIL_PASSWORD`

**Para outros provedores:**
- **Outlook/Hotmail**: `smtp.office365.com:587`
- **Yahoo**: `smtp.mail.yahoo.com:587`
- **SendGrid**: `smtp.sendgrid.net:587`
- **AWS SES**: `email-smtp.us-east-1.amazonaws.com:587`

---

### 2. Envio de Email de Boas-Vindas

```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final SendEmailNotification emailNotification;

    @PostMapping
    public ResponseEntity<UserResponse> create(@RequestBody UserRequest request) {
        User user = userService.create(request);

        // Envio ass�ncrono de email de boas-vindas
        ClientDto client = new ClientDto(
            user.getName(),    // nickName
            user.getEmail()    // email
        );
        emailNotification.sendEmailWelcome(client);

        return ResponseEntity.status(201).body(userMapper.toResponse(user));
    }
}
```

**Template usado:** `template/email_welcome_content.html`

**Vari�veis do template:**
- `{{cliente}}` - Nome do cliente

**Email enviado:**
```
Para: user@example.com
Assunto: Seja muito bem vindo!!!!!
Corpo: Email HTML estilizado com boas-vindas
```

---

### 3. Envio de Email de Ordem de Servi�o Finalizada

```java
@Service
public class ServiceOrderService {

    private final SendEmailNotification emailNotification;

    @LogOperation("Finalizar ordem de servi�o")
    public void finalizeServiceOrder(Long orderId) {
        ServiceOrder order = serviceOrderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("ServiceOrder", orderId));

        order.setStatus(ServiceOrderStatus.COMPLETED);
        order.setCompletionDate(LocalDateTime.now());
        serviceOrderRepository.save(order);

        // Envio ass�ncrono de email de OS finalizada
        ServiceOrderDto orderDto = ServiceOrderDto.builder()
            .orderNumber(order.getOrderNumber())
            .client(new ClientDto(
                order.getClient().getName(),
                order.getClient().getEmail()
            ))
            .vehicleDto(new VehicleDto(
                order.getVehicle().getPlate(),
                new ModelDto(
                    order.getVehicle().getModel().getYear(),
                    order.getVehicle().getModel().getName(),
                    order.getVehicle().getModel().getBrand()
                )
            ))
            .completionDate(order.getCompletionDate().format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            ))
            .build();

        emailNotification.sendServiceOrderFinalizedEmail(orderDto);
    }
}
```

**Template usado:** `template/service_order_finalized_email.html`

**Vari�veis do template:**
- `{{cliente}}` - Nome do cliente
- `{{osNumero}}` - N�mero da ordem de servi�o
- `{{veiculo}}` - Informa��es do ve�culo (marca modelo ano - placa)
- `{{dataFinalizacao}}` - Data de finaliza��o

**Email enviado:**
```
Para: client@example.com
Assunto: Sua OS est� pronta para retirada! - OS 12345
Corpo: Email HTML estilizado com detalhes da OS
```

---

## =� DTOs Dispon�veis

### ClientDto
```java
public record ClientDto(
    String nickName,  // Nome do cliente
    String email      // Email do destinat�rio
) {}
```

### ServiceOrderDto
```java
public record ServiceOrderDto(
    String orderNumber,      // N�mero da OS
    ClientDto client,        // Dados do cliente
    VehicleDto vehicleDto,   // Dados do ve�culo
    String completionDate    // Data de finaliza��o (formato String)
) {}
```

### VehicleDto
```java
public record VehicleDto(
    String plate,       // Placa do ve�culo
    ModelDto model      // Modelo do ve�culo
) {}
```

### ModelDto
```java
public record ModelDto(
    Integer year,    // Ano do modelo
    String model,    // Nome do modelo
    String brand     // Marca
) {}
```

---

## <� Templates HTML

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
    <p>Estamos muito felizes em ter voc� conosco. <�</p>
    <p>� um prazer contar com a sua presen�a em nossa comunidade.</p>
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
    <h1>OS pronta para retirada <�</h1>
    <p>Ol�, {{cliente}}!</p>
    <p>Sua Ordem de Servi�o <strong>{{osNumero}}</strong> foi finalizada em <strong>{{dataFinalizacao}}</strong>.</p>
    <div class="info">
        <div><span class="label">Ve�culo:</span> {{veiculo}}</div>
        <div><span class="label">Situa��o:</span> Servi�os conclu�dos. Aguardando retirada.</div>
    </div>
</div>
</body>
</html>
```

---

## =' Customiza��o de Templates

### Criando Templates Personalizados

1. **Crie seu template HTML** em `src/main/resources/template/`:

```html
<!-- src/main/resources/template/custom_notification.html -->
<!doctype html>
<html>
<body>
    <h1>Ol�, {{nome}}!</h1>
    <p>Seu pedido {{numeroPedido}} foi {{status}}.</p>
    <p>Valor: R$ {{valor}}</p>
</body>
</html>
```

2. **Crie um m�todo customizado** na classe `SendEmailNotification`:

```java
@Component
@RequiredArgsConstructor
public class CustomNotificationService {

    private final JavaMailSender mailSender;
    private final LoadTemplateConfig loadTemplateConfig;

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
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

                helper.setFrom("noreply@example.com", "Minha Empresa");
                helper.setTo(email);
                helper.setSubject("Atualiza��o do Pedido " + numeroPedido);
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

## � Configura��o Avan�ada

### M�ltiplas Estrat�gias de Carregamento

A classe `LoadTemplateConfig` tenta carregar templates em ordem:

1. **ResourceLoader com classpath:**
   - `classpath:template/email_welcome_content.html`

2. **ClassPathResource**
   - Busca direta no classpath

3. **Thread ClassLoader**
   - �til em ambientes com m�ltiplos classloaders

4. **Class ClassLoader**
   - Fallback final

Isso garante compatibilidade com diferentes ambientes (IDE, JAR, WAR, containers).

---

### Configura��o de SMTP Customizada

```yaml
spring:
  mail:
    host: smtp.custom-provider.com
    port: 465  # Use 465 para SSL ou 587 para TLS
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: false  # Desabilite se usar SSL na porta 465
          ssl:
            enable: true   # Habilite SSL se usar porta 465
        debug: false  # Habilite para debug detalhado
```

---

### Timeout e Retry

```yaml
spring:
  mail:
    properties:
      mail:
        smtp:
          timeout: 5000           # Timeout de conex�o (ms)
          connectiontimeout: 5000 # Timeout de leitura (ms)
          writetimeout: 5000      # Timeout de escrita (ms)
```

---

## <� Features

###  Recursos Principais

- **Envio Ass�ncrono**: Emails s�o enviados em background via `CompletableFuture`
- **Templates HTML**: Suporte completo a templates HTML responsivos
- **M�ltiplas Estrat�gias**: Carregamento de templates com fallback autom�tico
- **Zero Configura��o**: Auto-configura��o via Spring Boot
- **Type Safety**: DTOs com Records do Java 17+
- **Error Handling**: Logs de erro sem quebrar a aplica��o
- **Flex�vel**: F�cil customiza��o de templates e tipos de notifica��o

### = Seguran�a

- **Credenciais via Environment Variables**: Use `${MAIL_USERNAME}` e `${MAIL_PASSWORD}`
- **TLS/SSL**: Suporte a conex�es seguras
- **Sanitiza��o**: Templates s�o processados de forma segura

---

## =� Logs

A biblioteca gera logs informativos durante o envio:

```
INFO  - Send email notification welcome
INFO  - Welcome email sent successfully to user@example.com
```

Em caso de erro:
```
ERROR - Error when send welcome email to UserName: Template not found
java.lang.RuntimeException: Template file does not exist: template/missing.html
```

---

## >� Testes

A biblioteca inclui suite completa de testes:

**LoadTemplateConfigTest** (4 testes):
- Carregamento de template com ResourceLoader
- Exce��o quando template n�o existe
- Carregamento real do classpath
- Suporte a templates multilinha

**SendEmailNotificationTest** (5 testes):
- Envio de email de boas-vindas
- Envio de email de OS finalizada
- Tratamento de email inv�lido
- Carregamento de template
- Tratamento de exce��o no template

**SendNotificationAutoConfigurationTest** (5 testes):
- Auto-configura��o carrega corretamente
- Beans registrados
- JavaMailSender criado
- Configura��o do META-INF

**Resultado:**
```
Tests run: 14, Failures: 0, Errors: 0, Skipped: 0
```

---

## =' Troubleshooting

### L Email n�o est� sendo enviado

**Poss�veis causas:**

1. **Credenciais incorretas**
   - Verifique `MAIL_USERNAME` e `MAIL_PASSWORD`
   - Para Gmail, use senha de app, n�o sua senha normal

2. **Firewall bloqueando porta**
   - Verifique se a porta 587 (TLS) ou 465 (SSL) est� aberta
   - Teste: `telnet smtp.gmail.com 587`

3. **Template n�o encontrado**
   ```
   ERROR - Template file not found: template/custom.html
   ```
   - Verifique o caminho do template
   - Templates devem estar em `src/main/resources/template/`

4. **Configura��o de SMTP incorreta**
   ```yaml
   # Teste com configura��o m�nima:
   spring:
     mail:
       host: smtp.gmail.com
       port: 587
       username: seu-email@gmail.com
       password: sua-senha-app
       properties:
         mail:
           smtp:
             auth: true
             starttls:
               enable: true
   ```

---

### L Email chega na caixa de spam

**Solu��es:**

1. **Configure SPF/DKIM** no seu dom�nio
2. **Use provedores confi�veis** (SendGrid, AWS SES, Mailgun)
3. **Adicione link de unsubscribe** nos emails
4. **Evite palavras spam** (GR�TIS, URGENTE, etc)
5. **Use email remetente v�lido**

---

### L Templates n�o est�o sendo substitu�dos

Verifique se as vari�veis est�o corretas:

```java
// L Errado
String html = template.replace("{cliente}", nome);  // Faltam chaves duplas

//  Correto
String html = template.replace("{{cliente}}", nome);
```

---

## =� Constantes Dispon�veis

```java
// Em MailProperties.java
public static final String FROM_NAME = "Unified Service Core";
public static final String FROM_ADDRESS = "unifiedservicecore@gmail.com";
public static final String WELCOME_SUBJECT = "Seja muito bem vindo!!!!!";
public static final String WELCOME_TEMPLATE_PATH = "template/email_welcome_content.html";
public static final String FINALIZE_TEMPLATE_PATH = "template/service_order_finalized_email.html";
public static final String FINALIZE_SUBJECT = "Sua OS est� pronta para retirada!";
public static final String UTF_8 = "UTF-8";
public static final String CLIENT = "{{cliente}}";
```

---

## =� Exemplo Completo

```java
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SendEmailNotification emailNotification;

    // Exemplo 1: Email de boas-vindas
    public void sendWelcomeEmail(User user) {
        ClientDto client = new ClientDto(user.getName(), user.getEmail());
        emailNotification.sendEmailWelcome(client);
    }

    // Exemplo 2: Email de OS finalizada
    public void notifyServiceOrderCompletion(ServiceOrder order) {
        ServiceOrderDto dto = buildServiceOrderDto(order);
        emailNotification.sendServiceOrderFinalizedEmail(dto);
    }

    private ServiceOrderDto buildServiceOrderDto(ServiceOrder order) {
        ClientDto client = new ClientDto(
            order.getClient().getName(),
            order.getClient().getEmail()
        );

        ModelDto model = new ModelDto(
            order.getVehicle().getModel().getYear(),
            order.getVehicle().getModel().getName(),
            order.getVehicle().getModel().getBrand()
        );

        VehicleDto vehicle = new VehicleDto(
            order.getVehicle().getPlate(),
            model
        );

        return new ServiceOrderDto(
            order.getOrderNumber(),
            client,
            vehicle,
            order.getCompletionDate().format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            )
        );
    }
}
```

---

## =� Suporte

- **Issues**: [GitHub Issues](https://github.com/OtavioValadao/unified_service_core_libs/issues)
- **Documenta��o**: [Wiki](https://github.com/OtavioValadao/unified_service_core_libs/wiki)
- **Email**: notifications@fiap.com.br

---

## =� Licen�a

MIT License - Copyright (c) 2025 FIAP

---

## =e Autores

**FIAP - Unified Service Core Team**
- Vers�o atual: v1.4.1
- Data de lan�amento: Janeiro 2025

---

**<� Dica Final**: Configure as credenciais de email via environment variables para seguran�a e use os templates prontos para come�ar rapidamente! =�=�
