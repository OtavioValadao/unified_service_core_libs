package com.fiap.libs.softdelete;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Classe base abstrata para entidades JPA que suportam soft delete.
 * 
 * <p>Esta classe fornece campos comuns para todas as entidades:
 * <ul>
 *   <li>id - Identificador único (auto-gerado)</li>
 *   <li>createdAt - Data/hora de criação (auto-preenchida)</li>
 *   <li>updatedAt - Data/hora de atualização (auto-atualizada)</li>
 *   <li>deletedAt - Data/hora de deleção lógica (null se não deletado)</li>
 *   <li>isActive - Status ativo/inativo (padrão: true)</li>
 * </ul>
 * 
 * <p>Exemplo de uso:
 * <pre>{@code
 * @Entity
 * @Table(name = "customers")
 * public class CustomerEntity extends EntityBase {
 *     private String name;
 *     private String email;
 * }
 * }</pre>
 *
 * @author FIAP - Unified Service Core Team
 * @since 1.5.3
 */
@MappedSuperclass
@Data
public abstract class EntityBase implements SoftDeletable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "isActive", nullable = false)
    private Boolean isActive = true;
}

