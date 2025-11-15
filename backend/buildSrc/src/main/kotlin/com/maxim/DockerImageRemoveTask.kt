package com.maxim

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import javax.inject.Inject

/**
 * Сборка docker image
 */
abstract class DockerImageRemoveTask : DefaultTask() {

    @get:Inject
    abstract val execOperations: ExecOperations

    val imageName: Property<String> = project.objects
        .property(String::class.java)
        .convention(project.name)

    val imageTag: Property<String> = project.objects
        .property(String::class.java)
        .convention("latest")

    @TaskAction
    fun invoke() {
        val fullImageName = "$imageName:$imageTag"

        logger.info("Removing image : {}", fullImageName)

        execOperations.exec {
            commandLine(
                "docker", "rmi",
                fullImageName
            )
        }.assertNormalExitValue()

        logger.info("Image removed successfully")
    }
}