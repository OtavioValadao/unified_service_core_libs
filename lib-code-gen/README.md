# Lib Code Gen

Biblioteca para gerar código automaticamente a partir de especificações OpenAPI/Swagger no Spring Boot 3.

## 📦 O que a biblioteca gera

A partir do seu `swagger.yaml`, a biblioteca gera automaticamente:

- ✅ **Interfaces Controller** (ex: `UserController.java`)
- ✅ **Models (DTOs)** com validações (ex: `UserResponse.java`, `CreateUserRequest.java`)
- ✅ **Documentação OpenAPI** automática
- ✅ **Validações Bean** (@Valid, @NotNull, @Size, etc.)


## 🚀 Como Usar

### 1. Adicione a dependência no `pom.xml`

```xml
<dependencies>
    <!-- Lib Code Gen -->
    <dependency>
        <groupId>com.github.OtavioValadao</groupId>
        <artifactId>lib-code-gen</artifactId>
        <version>1.0.15</version>
    </dependency>
</dependencies>
```

### 2. Configure o plugin OpenAPI Generator

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.openapitools</groupId>
            <artifactId>openapi-generator-maven-plugin</artifactId>
            <version>7.15.0</version>
            <executions>
                <execution>
                    <goals>
                        <goal>generate</goal>
                    </goals>
                    <configuration>
                        <inputSpec>${project.basedir}/src/main/resources/swagger.yaml</inputSpec>
                        <generatorName>spring</generatorName>
                        <apiPackage>com.usa.api.controller</apiPackage>
                        <modelPackage>com.sua.api.model</modelPackage>
                        <apiNameSuffix>Controller</apiNameSuffix>

                        <configOptions>
                            <interfaceOnly>true</interfaceOnly>
                            <useSpringBoot3>true</useSpringBoot3>
                            <skipDefaultInterface>true</skipDefaultInterface>
                            <useTags>true</useTags>
                        </configOptions>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

### 3. Crie o arquivo `swagger.yaml`

Coloque em `src/main/resources/swagger.yaml` seguindo o padrão:

```yaml
openapi: 3.0.3
info:
  title: User Manager API
  version: 1.0.0

paths:
  /users:
    get:
      tags:
        - user  # Tag no singular = UserController
      summary: Listar usuários
      operationId: listUsers
      responses:
        '200':
          description: Lista de usuários
          content:
            application/json:
              schema:
                type: array
                items:
                  $ref: '#/components/schemas/UserResponse'
    
    post:
      tags:
        - user
      summary: Criar usuário
      operationId: createUser
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/CreateUserRequest'
      responses:
        '201':
          description: Usuário criado
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/UserResponse'

  /users/{id}:
    get:
      tags:
        - user
      summary: Buscar usuário
      operationId: getUserById
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
            format: int64
      responses:
        '200':
          description: Usuário encontrado
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/UserResponse'

components:
  schemas:
    # Response
    UserResponse:
      type: object
      properties:
        id:
          type: integer
          format: int64
        name:
          type: string
        email:
          type: string
        createdAt:
          type: string
          format: date-time
    
    # Request de criação
    CreateUserRequest:
      type: object
      required:
        - name
        - email
      properties:
        name:
          type: string
          minLength: 3
          maxLength: 100
        email:
          type: string
          format: email
```

### 4. Gere o código

```bash
mvn clean compile
```

### 5. Implemente a interface gerada

```java
package com.sua.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserControllerImpl implements UserController {

    @Override
    public ResponseEntity<List<UserResponse>> listUsers() {
        // Sua implementação
        return ResponseEntity.ok(users);
    }

    @Override
    public ResponseEntity<UserResponse> createUser(CreateUserRequest request) {
        // Sua implementação
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @Override
    public ResponseEntity<UserResponse> getUserById(Long id) {
        // Sua implementação
        return ResponseEntity.ok(user);
    }
}
```

## 📐 Padrão de Nomenclatura

A biblioteca segue as convenções:

| Tipo | Padrão | Exemplo |
|------|--------|---------|
| **Controller** | `{Tag}Controller` | `UserController.java` |
| **Request Criação** | `Create{Entity}Request` | `CreateUserRequest.java` |
| **Request Atualização** | `Update{Entity}Request` | `UpdateUserRequest.java` |
| **Response** | `{Entity}Response` | `UserResponse.java` |

**Importante:** Use a tag no **singular** no swagger para gerar o nome correto da interface:
- Tag `user` → gera `UserController.java` ✅
- Tag `users` → gera `UsersController.java` ❌

## 📁 Estrutura Gerada

```
sua-api/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/sua/api/
│       │       └── controller/
│       │           └── UserControllerImpl.java (você implementa)
│       └── resources/
│           └── swagger.yaml
└── target/
    └── generated-sources/
        └── openapi/
            └── com/sua/api/
                ├── controller/
                │   └── UserController.java (gerado)
                └── model/
                    ├── UserResponse.java (gerado)
                    └── CreateUserRequest.java (gerado)
```


## 🔧 Troubleshooting

**Erro: package org.hibernate.validator.constraints does not exist**
```xml
<!-- Adicione esta dependência -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

**Código não está sendo gerado**
```bash
mvn clean compile
```

**Interface gerada como "DefaultController"**
- Adicione a tag no swagger.yaml em cada endpoint
- Use tag no **singular** (ex: `user`, não `users`)

**Classes geradas não aparecem na IDE**
- IntelliJ: Botão direito em `target/generated-sources/openapi` → Mark Directory as → Generated Sources Root
- Eclipse: Properties → Java Build Path → Add Folder → `target/generated-sources/openapi`

---

**Versão:** 1.0.15
**Repositório:** [github.com/OtavioValadao/lib-code-gen](https://github.com/OtavioValadao/lib-code-gen)