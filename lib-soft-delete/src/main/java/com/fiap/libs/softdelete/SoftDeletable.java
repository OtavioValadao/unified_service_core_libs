package com.fiap.libs.softdelete;

import java.time.LocalDateTime;

/**
 * Interface marker para entidades que suportam soft delete e soft activate.
 * 
 * <p>Entidades que implementam esta interface podem ser marcadas como deletadas
 * logicamente sem remover fisicamente do banco de dados, permitindo recuperação futura.
 * 
 * <p>Exemplo de uso:
 * <pre>{@code
 * @Entity
 * public class CustomerEntity extends EntityBase implements SoftDeletable {
 *     // Campos herdados de EntityBase:
 *     // - Long id
 *     // - LocalDateTime createdAt
 *     // - LocalDateTime updatedAt
 *     // - LocalDateTime deletedAt
 *     // - Boolean isActive
 * }
 * }</pre>
 *
 * @author FIAP - Unified Service Core Team
 * @since 1.5.2
 */
public interface SoftDeletable {
    
    /**
     * Define a data/hora de deleção lógica.
     * 
     * @param deletedAt Data/hora da deleção (null para reativar)
     */
    void setDeletedAt(LocalDateTime deletedAt);
    
    /**
     * Retorna a data/hora de deleção lógica.
     * 
     * @return Data/hora da deleção ou null se não deletado
     */
    LocalDateTime getDeletedAt();
    
    /**
     * Define o status ativo/inativo da entidade.
     * 
     * @param isActive true para ativo, false para inativo
     */
    void setIsActive(Boolean isActive);
    
    /**
     * Retorna o status ativo/inativo da entidade.
     * 
     * @return true se ativo, false se inativo
     */
    Boolean getIsActive();
    
    /**
     * Verifica se a entidade está ativa (não deletada logicamente).
     * 
     * @return true se a entidade está ativa e não foi deletada
     */
    default boolean isActive() {
        return Boolean.TRUE.equals(getIsActive()) && getDeletedAt() == null;
    }
}

