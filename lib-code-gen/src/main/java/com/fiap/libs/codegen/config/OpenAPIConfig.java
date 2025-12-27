package com.fiap.libs.codegen.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Configuration
@Slf4j
public class OpenAPIConfig {

    private final ResourceLoader resourceLoader;

    @Value("${server.servlet.context-path:/}")
    private String contextPath;

    @Value("${server.port:8080}")
    private String serverPort;

    @Value("${springdoc.api-docs.path:/v3/api-docs}")
    private String apiDocsPath;

    public OpenAPIConfig(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Bean
    public OpenAPI customOpenAPI() {
        OpenAPI openAPI = new OpenAPI();

        // Tentar carregar informações do arquivo swagger.yaml
        Optional<OpenAPI> yamlOpenAPI = loadOpenAPIFromYaml();
        
        if (yamlOpenAPI.isPresent()) {
            OpenAPI yaml = yamlOpenAPI.get();
            
            // Usar informações do YAML se disponíveis
            if (yaml.getInfo() != null) {
                openAPI.setInfo(yaml.getInfo());
            }
            
            // Configurar servidores dinamicamente com context-path
            openAPI.setServers(buildServers(yaml.getServers()));
        } else {
            // Fallback para valores padrão
            openAPI.setInfo(new Info()
                    .title("Unified Service Core API")
                    .version("1.0.0"));
            openAPI.setServers(buildServers(null));
        }

        // Sempre configurar segurança BearerAuth
        openAPI.setComponents(new Components()
                .addSecuritySchemes("BearerAuth", new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("JWT token for authentication. Available roles:\n" +
                                "- **ROLE_MASTER**: Full access to all endpoints\n" +
                                "- **ROLE_ADMINISTRATOR**: Administrative access\n" +
                                "- **ROLE_USER**: Limited access (view only)")));
        
        openAPI.addSecurityItem(new SecurityRequirement().addList("BearerAuth"));

        return openAPI;
    }

    private Optional<OpenAPI> loadOpenAPIFromYaml() {
        try {
            // Tentar diferentes localizações comuns do arquivo swagger.yaml
            String[] possiblePaths = {
                    "classpath:openapi/swagger.yaml",
                    "classpath:swagger.yaml",
                    "classpath:openapi.yaml"
            };

            for (String path : possiblePaths) {
                Resource resource = resourceLoader.getResource(path);
                if (resource.exists()) {
                    try (InputStream inputStream = resource.getInputStream()) {
                        SwaggerParseResult result = new OpenAPIV3Parser().readContents(
                                new String(inputStream.readAllBytes()), null, null);
                        
                        if (result.getOpenAPI() != null) {
                            log.info("✅ [CODE-GEN] Loaded OpenAPI spec from: {}", path);
                            return Optional.of(result.getOpenAPI());
                        }
                    }
                }
            }
            
            log.warn("⚠️ [CODE-GEN] OpenAPI YAML file not found, using default configuration");
            return Optional.empty();
        } catch (IOException e) {
            log.warn("⚠️ [CODE-GEN] Error loading OpenAPI YAML: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private List<Server> buildServers(List<Server> yamlServers) {
        List<Server> servers = new ArrayList<>();
        
        String localUrl = "http://localhost:" + serverPort + contextPath;
        String productionUrl = "https://api.fiap.com.br" + contextPath;

        // Se há servidores no YAML, usar como base e adicionar context-path
        if (yamlServers != null && !yamlServers.isEmpty()) {
            for (Server yamlServer : yamlServers) {
                String baseUrl = yamlServer.getUrl();
                String description = yamlServer.getDescription() != null 
                        ? yamlServer.getDescription() 
                        : "Server";
                
                // Adicionar context-path se não estiver presente
                String fullUrl = baseUrl.endsWith(contextPath) 
                        ? baseUrl 
                        : baseUrl + contextPath;
                
                servers.add(new Server()
                        .url(fullUrl)
                        .description(description));
            }
        } else {
            // Servidores padrão
            servers.add(new Server()
                    .url(localUrl)
                    .description("Local server"));
            servers.add(new Server()
                    .url(productionUrl)
                    .description("Production server"));
        }

        return servers;
    }
}
