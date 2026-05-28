package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.FiktionCharset
import dev.s7a.fiktion.generators.oneOf
import dev.s7a.fiktion.generators.string

internal fun FakeContext.fileName(): String =
    "${string(length = DEFAULT_FILE_BASENAME_LENGTH, charset = FiktionCharset.LowercaseAlphaNumeric)}.${oneOf(FILE_EXTENSIONS)}"

private val FILE_EXTENSIONS: List<String> =
    listOf(
        "txt",
        "md",
        "log",
        "csv",
        "tsv",
        "json",
        "xml",
        "yaml",
        "yml",
        "properties",
        "conf",
        "ini",
        "html",
        "css",
        "js",
        "ts",
        "java",
        "kt",
        "sql",
        "pdf",
        "png",
        "jpg",
        "jpeg",
        "gif",
        "zip",
        "tar",
        "gz",
        "bin",
        "tmp",
    )

private const val DEFAULT_FILE_BASENAME_LENGTH: Int = 12
