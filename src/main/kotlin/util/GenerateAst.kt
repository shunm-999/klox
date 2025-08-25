package util

import java.io.PrintWriter
import kotlin.system.exitProcess

object GenerateAst {
    fun main(args: Array<String>) {
        if (args.size != 1) {
            println("Usage: generate_ast <output directory>")
            exitProcess(64)
        }
        val outputDir = args[0]

        defineAst(
            outputDir = outputDir,
            "Expr",
            listOf(
                "Binary   : left Expr, operator Token, right Expr",
                "Grouping : expression Expr",
                "Literal  : value Object",
                "Unary    : operator Token, right Expr"
            )
        )
    }

    private fun defineAst(
        outputDir: String,
        baseName: String,
        types: List<String>
    ) {
        val path = "$outputDir/$baseName.kt"

        PrintWriter(path, "UTF-8").use { writer ->
            writer.println("package com.craftinginterpreters.lox")
            writer.println()
            writer.println("import java.util.List")
            writer.println()
            writer.println("abstract class $baseName {")

            writer.println("}")
        }
    }
}