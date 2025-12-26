package com.fiap.libs.usecase;

/**
 * Interface especializada para queries (Query) seguindo o padrão CQRS.
 * 
 * <p>Queries representam operações de leitura que não modificam o estado do sistema.
 * Devem ser idempotentes e não ter efeitos colaterais.
 * 
 * <p>Exemplo de uso:
 * <pre>{@code
 * public interface GetUserByIdQuery extends Query<Long, UserResponse> {
 * }
 * 
 * public interface ListUsersQuery extends Query<Pageable, Page<UserResponse>> {
 * }
 * }</pre>
 *
 * @param <I> Tipo do input (entrada)
 * @param <O> Tipo do output (saída)
 * @author FIAP - Unified Service Core Team
 * @since 1.5.1
 */
public interface Query<I, O> extends UseCase<I, O> {
    // Interface marker - herda comportamento de UseCase
    // Permite diferenciação semântica entre Command e Query
}

