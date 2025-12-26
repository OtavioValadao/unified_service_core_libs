package com.fiap.libs.usecase;

/**
 * Interface base para casos de uso seguindo o padrão CQRS e Clean Architecture.
 * 
 * <p>Esta interface define o contrato básico para execução de casos de uso,
 * permitindo separação clara entre comandos (modificações) e queries (consultas).
 * 
 * <p>Exemplo de uso:
 * <pre>{@code
 * public interface CreateUserCommand extends UseCase<UserRequest, UserResponse> {
 * }
 * 
 * @Service
 * public class CreateUserUseCase implements CreateUserCommand {
 *     @Override
 *     public UserResponse execute(UserRequest input) {
 *         // Lógica de criação
 *         return userResponse;
 *     }
 * }
 * }</pre>
 *
 * @param <I> Tipo do input (entrada)
 * @param <O> Tipo do output (saída)
 * @author FIAP - Unified Service Core Team
 * @since 1.5.1
 */
public interface UseCase<I, O> {
    
    /**
     * Executa o caso de uso com o input fornecido.
     * 
     * @param input Dados de entrada para o caso de uso
     * @return Resultado da execução do caso de uso
     * @throws RuntimeException ou suas subclasses em caso de erro na execução
     */
    O execute(I input);
}

