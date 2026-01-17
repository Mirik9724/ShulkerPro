package net.Mirik9724.api

import java.io.File
import java.io.InputStream
import java.nio.file.Files

/**
 * copy files from jar
 * @param resourcePath
 * @param targetDir
 */
fun copyFileFromJar(
    resourcePath: String,
    targetDir: String,
){
    val targetFile = java.io.File(targetDir, java.io.File(resourcePath).name)

    if (!targetFile.exists()) {
        val inputStream: java.io.InputStream = object {}.javaClass.classLoader.getResourceAsStream(resourcePath)
            ?: throw IllegalArgumentException("Resource '$resourcePath' not found in JAR")
        java.nio.file.Files.copy(inputStream, targetFile.toPath())
        inputStream.close()
    }
}

//Copy .kt to your work for correct work
// (c) MirikAPI 2025-2025