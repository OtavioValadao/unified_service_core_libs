# 📋 Versionamento - Unified Service Core Libraries

## 🎯 Estratégia de Versionamento

### Versão Única (Recomendado)
Todas as libs seguem a **mesma versão** para facilitar manutenção e compatibilidade.

```
unified-service-core-libs: 1.0.0
├── lib-observability: 1.0.0
├── lib-exception-handler: 1.0.0
└── unified-service-core-starter: 1.0.0
```

## 🚀 Como Fazer Release

### 1. Preparar Release
```bash
# Atualizar versão no pom.xml pai
mvn versions:set -DnewVersion=1.1.0

# Ou usar o plugin de release (recomendado)
mvn release:prepare
```

### 2. Deploy
```bash
# Build e deploy
mvn clean deploy

# Ou apenas build local
mvn clean package
```

### 3. Criar Tag
```bash
# O plugin de release cria automaticamente
git tag v1.1.0
git push origin v1.1.0
```

## 📦 Como Usar nos Projetos

### Opção 1: BOM (Recomendado)
```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.github.OtavioValadao</groupId>
            <artifactId>unified-service-core-libs</artifactId>
            <version>1.0.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
    <!-- Sem versão - pega do BOM -->
    <dependency>
        <groupId>com.github.OtavioValadao</groupId>
        <artifactId>lib-observability</artifactId>
    </dependency>
    
    <dependency>
        <groupId>com.github.OtavioValadao</groupId>
        <artifactId>lib-exception-handler</artifactId>
    </dependency>
</dependencies>
```

### Opção 2: Starter (Mais Simples)
```xml
<dependency>
    <groupId>com.github.OtavioValadao</groupId>
    <artifactId>unified-service-core-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 🏷️ Convenção de Tags

- **Formato**: `v{MAJOR}.{MINOR}.{PATCH}`
- **Exemplos**: `v1.0.0`, `v1.1.0`, `v2.0.0`
- **Semantic Versioning**: MAJOR.MINOR.PATCH
  - **MAJOR**: Breaking changes
  - **MINOR**: New features (backward compatible)
  - **PATCH**: Bug fixes (backward compatible)

## 🔄 Workflow de Desenvolvimento

### 1. Desenvolvimento
```bash
# Trabalhar na branch develop
git checkout develop
# Fazer mudanças
git commit -m "feat: nova funcionalidade"
```

### 2. Release
```bash
# Preparar release
mvn release:prepare
# Deploy
mvn release:perform
```

### 3. Hotfix
```bash
# Para correções urgentes
git checkout main
git checkout -b hotfix/1.0.1
# Fazer correção
mvn versions:set -DnewVersion=1.0.1
mvn clean deploy
```

## 📊 Histórico de Versões

| Versão | Data | Mudanças |
|--------|------|----------|
| 1.0.0 | 2025-01-06 | Release inicial com observability e exception-handler |

## 🛠️ Comandos Úteis

```bash
# Verificar versões
mvn versions:display-dependency-updates

# Atualizar versão
mvn versions:set -DnewVersion=1.1.0

# Reverter versão
mvn versions:revert

# Commit versão
mvn versions:commit
```

## ⚠️ Importante

- **Sempre** teste antes de fazer release
- **Nunca** quebre compatibilidade em versões MINOR
- **Documente** breaking changes em versões MAJOR
- **Use** conventional commits para facilitar changelog automático
