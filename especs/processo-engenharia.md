# Processo de Engenharia — CI/CD

Este material consolida os requisitos mínimos de qualidade e segurança da esteira.

## Requisitos obrigatórios

- [x] Lint
- [x] Build
- [x] Unit tests (>=80%)
- [x] Integration tests
- [x] Sonar
- [x] SAST security
- [x] Dependency scan
- [x] Code review obrigatório
- [x] Coverage gate
- [x] Quality gate

## Documento vivo de implementação

Detalhes técnicos e histórico das mudanças estão em:

- `processo-ci-cd.md`

## Diretriz operacional

Toda evolução da esteira deve:

1. Atualizar `processo-ci-cd.md` com objetivo, alteração e impacto.
2. Preservar os gates obrigatórios desta especificação.
3. Manter branch protection para revisão obrigatória por Code Owners.