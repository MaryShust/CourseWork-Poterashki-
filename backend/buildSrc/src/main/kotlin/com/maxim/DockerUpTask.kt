package com.maxim

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import javax.inject.Inject

class DockerUpTask(
    @Inject
    private val execOperations: ExecOperations
): DefaultTask() {

    val composeFilePath: Property<String> = project.objects
        .property(String::class.java)
        .convention("compose.yaml")


    @TaskAction
    fun invoke() {
        val composeFile = project.file(composeFilePath)

        if (!composeFile.exists()) {
            logger.error("Compose file: {} does not exists", composeFilePath)
            throw IllegalArgumentException("Compose file: $composeFilePath does not exists")
        }

        logger.info("Executing docker compose up for {}", composeFilePath)

        execOperations.exec {
            commandLine("docker", "compose",
                "-f", composeFile.absolutePath,
                "up", "-d")
        }.assertNormalExitValue()

        logger.info("Successful up of {}", composeFilePath)
    }
}