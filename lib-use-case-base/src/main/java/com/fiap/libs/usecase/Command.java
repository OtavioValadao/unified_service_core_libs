package com.fiap.libs.usecase;

/**
 * Interface especializada para comandos (Command) seguindo o padrão CQRS.
 * 
 * <p>Comandos representam operações que modificam o estado do sistema.
 * Geralmente retornam o recurso criado/atualizado ou Void para operações sem retorno.
 * 
 * <p>Exemplo de uso:
 * <pre>{@code
 * public interface CreateUserCommand extends Command<UserRequest, UserResponse> {
 * }
 * 
 * public interface DeleteUserCommand extends Command<Long, Void> {
 * }
 * }</pre>
 *
 * @param <I> Tipo do input (entrada)
 * @param <O> Tipo do output (saída)
 * @author FIAP - Unified Service Core Team
 * @since 1.5.1
 */
public interface Command<I, O> extends UseCase<I, O> {
    // Interface marker - herda comportamento de UseCase
    // Permite diferenciação semântica entre Command e Query
}

