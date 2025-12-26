package com.fiap.libs.softdelete;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Interface base para repositórios JPA que suportam operações de soft delete.
 * 
 * <p>Esta interface estende JpaRepository e adiciona métodos específicos para
 * trabalhar com entidades que implementam SoftDeletable.
 * 
 * <p>Exemplo de uso:
 * <pre>{@code
 * public interface CustomerRepository extends SoftDeleteRepository<CustomerEntity, Long> {
 *     // Métodos customizados específicos do domínio
 *     Optional<CustomerEntity> findByEmail(String email);
 * }
 * 
 * // Uso no adapter:
 * @Repository
 * public class CustomerRepositoryAdapter {
 *     
 *     private final CustomerRepository repository;
 *     private final SoftDeleteHelper softDeleteHelper;
 *     
 *     public void softDelete(Long id) {
 *         CustomerEntity entity = repository.findById(id)
 *             .orElseThrow(() -> new ResourceNotFoundException("Customer", id));
 *         
 *         softDeleteHelper.softDelete(entity);
 *         repository.save(entity);
 *     }
 * }
 * }</pre>
 *
 * @param <T> Tipo da entidade que implementa SoftDeletable
 * @param <ID> Tipo do identificador da entidade
 * @author FIAP - Unified Service Core Team
 * @since 1.5.2
 */
public interface SoftDeleteRepository<T extends SoftDeletable, ID> extends JpaRepository<T, ID> {
    
    /**
     * Busca uma entidade por ID que não foi deletada logicamente.
     * 
     * @param id Identificador da entidade
     * @return Optional contendo a entidade se encontrada e ativa, empty caso contrário
     */
    default Optional<T> findByIdAndActive(ID id) {
        return findById(id)
            .filter(SoftDeletable::isActive);
    }
    
    /**
     * Busca uma entidade por ID que não foi deletada logicamente (deletedAt is null).
     * 
     * @param id Identificador da entidade
     * @return Optional contendo a entidade se encontrada e não deletada, empty caso contrário
     */
    default Optional<T> findByIdAndNotDeleted(ID id) {
        return findById(id)
            .filter(entity -> entity.getDeletedAt() == null);
    }
}

