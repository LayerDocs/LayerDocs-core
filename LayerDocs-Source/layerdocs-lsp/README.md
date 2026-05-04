# lsp

This module contains sources for the LayerDocs language server, which follows the [Language Server Protocol (LSP)](https://microsoft.github.io/language-server-protocol/),
allowing integration with code editors and IDEs:

- [LayerDocs for VS Code](https://github.com/layerdocs-labs/layerdocs-vscode)

Features include:
- Syntax highlighting for function calls via semantic tokens
- Completions for functions, parameters and values
- Documentation on hover
- Diagnostics

The implementation is based on the [LSP4J](https://github.com/eclipse-lsp4j/lsp4j) library.