package com.maxim

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Property

open class DockerComposePlugin : Plugin<Project> {


    override fun apply(target: Project) {
        val extension = target.extensions.create("dockerCompose", DockerComposeExtension::class.java,
            target)

        val tasks = target.tasks

        target.afterEvaluate {
            tasks.register("dockerUp", DockerUpTask::class.java) {
                composeFilePath.set(extension.composeFile)
            }
            tasks.register("dockerDown", DockerDownTask::class.java) {
                composeFilePath.set(extension.composeFile)
            }
        }
    }
}

open class DockerComposeExtension(project: Project) {
    val composeFile: Property<String> = project.objects
        .property(String::class.java)
        .convention("docker-compose.yml")
}