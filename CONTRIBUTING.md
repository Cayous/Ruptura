# Contributing to Ruptura

Thank you for your interest in contributing to Ruptura!

**Português**: Este projeto aceita contribuições em português e inglês.

## Bug Reports / Reportar Bugs

When reporting bugs, please include:
- Android version and device model
- Steps to reproduce
- Expected vs actual behavior
- Screenshots if applicable

**Ao reportar bugs, por favor inclua**:
- Versão do Android e modelo do dispositivo
- Passos para reproduzir
- Comportamento esperado vs atual
- Capturas de tela, se aplicável

## Feature Requests / Solicitar Funcionalidades

Before requesting a feature:
- Check existing issues to avoid duplicates
- Describe the problem your feature solves
- Provide use case examples

**Antes de solicitar uma funcionalidade**:
- Verifique issues existentes
- Descreva o problema que sua funcionalidade resolve
- Forneça exemplos de casos de uso

## Development Setup / Configuração de Desenvolvimento

### Requirements / Requisitos
- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17 or newer
- Android device (API 31+) - emulators won't work for usage stats

### Build / Compilar

```bash
git clone https://github.com/YOUR_USERNAME/ruptura.git
cd ruptura
./gradlew assembleDebug
```

## Code Guidelines / Diretrizes de Código

- **Architecture**: Follow Clean Architecture + MVVM pattern
- **Kotlin**: Use idiomatic Kotlin (coroutines, flows, data classes)
- **Compose**: Use Jetpack Compose for all UI
- **Testing**: Add unit tests for business logic

**Arquitetura**: Siga Clean Architecture + MVVM
**Kotlin**: Use Kotlin idiomático (coroutines, flows, data classes)
**Compose**: Use Jetpack Compose para toda a UI
**Testes**: Adicione testes unitários para lógica de negócio

## Commit Messages / Mensagens de Commit

Follow conventional commits format:

```
<type>(<scope>): <description>

Examples:
feat(diagnostics): add weekly usage comparison
fix(focus): prevent screen lock crash on Android 14
docs(readme): update installation instructions
```

Types: `feat`, `fix`, `docs`, `refactor`, `test`, `chore`

## Pull Request Process / Processo de Pull Request

1. Create feature branch / Criar branch de feature
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. Make changes / Fazer alterações
   - Follow existing code patterns / Siga padrões existentes
   - Update documentation if needed / Atualize documentação se necessário
   - Test on physical device / Teste em dispositivo físico

3. Commit / Fazer commit
   ```bash
   git commit -m "feat(scope): description"
   ```

4. Push and create PR / Push e criar PR
   ```bash
   git push origin feature/your-feature-name
   ```
   - Open PR against `master` branch
   - Fill out PR template
   - Link related issues

5. Code Review / Revisão de Código
   - Address review feedback / Responda ao feedback
   - Keep commits clean / Mantenha commits limpos

## Testing / Testes

```bash
# Unit Tests / Testes Unitários
./gradlew test

# Manual Testing / Testes Manuais
# Test on real device / Teste em dispositivo real
```

## Project Priorities / Prioridades do Projeto

1. Core stability and bug fixes / Estabilidade e correção de bugs
2. User experience improvements / Melhorias de experiência do usuário
3. Performance optimization / Otimização de desempenho
4. Accessibility features / Recursos de acessibilidade

## Questions? / Dúvidas?

- Open a GitHub Discussion for general questions
- Check existing issues and discussions first
- Abra uma GitHub Discussion para perguntas gerais
- Verifique issues e discussões existentes primeiro

## License / Licença

By contributing, you agree that your contributions will be licensed under the Apache 2.0 License.

Ao contribuir, você concorda que suas contribuições serão licenciadas sob a Licença Apache 2.0.

---

Thank you for contributing to Ruptura! / Obrigado por contribuir com Ruptura!
