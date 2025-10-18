# klox — A Kotlin implementation of Lox

klox is a Kotlin implementation of the Lox language from the excellent book
"Crafting Interpreters" by Robert Nystrom. This repository contains a
practical interpreter implemented in Kotlin as an exercise and learning
project.

I am deeply grateful to https://craftinginterpreters.com/ — this project
would not exist without the clear explanations and guidance found there.

## Overview

- Language: Kotlin
- Goal: Implement the Lox interpreter architecture (scanner, parser, AST,
  resolver, and interpreter) following the ideas in "Crafting Interpreters".
- Intended use: learning, experimentation, and reference.

## Features

- Tokenizer (scanner)
- Parser producing an AST
- Tree-walk interpreter (expression and statement evaluation)
- Basic runtime error reporting and simple REPL / script execution

## Getting started

Prerequisites:
- JDK 11+ (or your project's configured Kotlin/JVM target)
- Gradle (recommended) or use the included Gradle wrapper if present

Build:
- With wrapper (if present): `./gradlew build`
- Or with gradle: `gradle build`

Run:
- Execute a Lox source file: `./gradlew runFile"`

## Example

Given a file `hello.lox`:
```
print "Hello, Lox!";
```
Run it with the project's runner as described above to see the output.

## Implementation notes

This repository follows the structure and concepts from "Crafting Interpreters".
Expect modules for:
- scanning / tokenization
- parsing into AST nodes
- AST visitor implementations
- runtime environment and values
- error reporting and testing helpers

## Credits

Deep thanks to Robert Nystrom and the "Crafting Interpreters" project:
https://craftinginterpreters.com/

