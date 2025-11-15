package com.maxim

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import javax.inject.Inject

/**
 * Сборка docker image
 */
open class DockerImageBuildTask(
    @Inject
    private val execOperations: ExecOperations
) : DefaultTask() {

    val imageName: Property<String> = project.objects
        .property(String::class.java)
        .convention(project.name)

    val imageTag: Property<String> = project.objects
        .property(String::class.java)
        .convention(project.version.toString())

    val dockerfilePath: Property<String> = project.objects
        .property(String::class.java)
        .convention("Dockerfile")

    val dockerBuildContext:  Property<String> = project.objects
        .property(String::class.java)
        .convention(project.projectDir.absolutePath)

    @TaskAction
    fun invoke() {
        val dockerFile = project.file(dockerfilePath)

        if (!dockerFile.exists()) {
            logger.error("Dockerfile does not exists: {}", dockerfilePath)
            throw IllegalArgumentException("Dockerfile does not exists: $dockerfilePath")
        }

        val fullImageName = "$imageName:$imageTag"
        logger.info("Building image : {}", fullImageName)
        execOperations.exec {
            commandLine("docker", "image", "build",
                "-t", fullImageName,
                "-f", dockerFile.absolutePath,
                dockerBuildContext
            )
        }.assertNormalExitValue()
        logger.info("Image built successfully")
    }
}