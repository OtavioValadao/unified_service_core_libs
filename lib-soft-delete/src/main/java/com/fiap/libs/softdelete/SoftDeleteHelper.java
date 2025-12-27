package com.fiap.libs.softdelete;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Helper service para executar operações de soft delete e soft activate em entidades.
 * 
 * <p>Este componente fornece métodos utilitários para marcar entidades como deletadas
 * logicamente ou reativá-las, seguindo um padrão consistente em toda a aplicação.
 * 
 * <p>Exemplo de uso:
 * <pre>{@code
 * @Service
 * public class CustomerRepositoryAdapter {
 *     
 *     private final SoftDeleteHelper softDeleteHelper;
 *     private final JpaCustomerRepository repository;
 *     
 *     public void softDelete(Long id) {
 *         CustomerEntity entity = repository.findById(id)
 *             .orElseThrow(() -> new ResourceNotFoundException("Customer", id));
 *         
 *         softDeleteHelper.softDelete(entity);
 *         repository.save(entity);
 *     }
 *     
 *     public void softActivate(Long id) {
 *         CustomerEntity entity = repository.findById(id)
 *             .orElseThrow(() -> new ResourceNotFoundException("Customer", id));
 *         
 *         softDeleteHelper.softActivate(entity);
 *         repository.save(entity);
 *     }
 * }
 * }</pre>
 *
 * @author FIAP - Unified Service Core Team
 * @since 1.5.2
 */
@Slf4j
public class SoftDeleteHelper {
    
    /**
     * Executa soft delete em uma entidade, marcando-a como inativa e definindo deletedAt.
     * 
     * @param entity Entidade a ser deletada logicamente
     * @param <T> Tipo da entidade que implementa SoftDeletable
     * @throws IllegalArgumentException se entity for null
     */
    public <T extends SoftDeletable> void softDelete(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }
        
        log.debug("Soft deleting entity: {}", entity.getClass().getSimpleName());
        
        entity.setIsActive(false);
        entity.setDeletedAt(LocalDateTime.now());
    }
    
    /**
     * Executa soft activate em uma entidade, reativando-a e removendo deletedAt.
     * 
     * @param entity Entidade a ser reativada
     * @param <T> Tipo da entidade que implementa SoftDeletable
     * @throws IllegalArgumentException se entity for null
     */
    public <T extends SoftDeletable> void softActivate(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }
        
        log.debug("Soft activating entity: {}", entity.getClass().getSimpleName());
        
        entity.setIsActive(true);
        entity.setDeletedAt(null);
    }
    
    /**
     * Verifica se uma entidade está ativa (não deletada logicamente).
     * 
     * @param entity Entidade a ser verificada
     * @param <T> Tipo da entidade que implementa SoftDeletable
     * @return true se a entidade está ativa, false caso contrário
     */
    public <T extends SoftDeletable> boolean isActive(T entity) {
        if (entity == null) {
            return false;
        }
        
        return entity.isActive();
    }
}

