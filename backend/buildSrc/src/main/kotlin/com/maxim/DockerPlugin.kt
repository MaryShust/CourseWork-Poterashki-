package com.maxim

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Property

open class DockerPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val extension = target.extensions.create("docker", DockerExtension::class.java,
            target)

        val tasks = target.tasks

        target.afterEvaluate {
            tasks.register("buildImage", DockerImageBuildTask::class.java) {
                imageName.set(extension.imageName)
                imageTag.set(extension.tag)
                dockerfilePath.set(extension.dockerfile)
                dockerBuildContext.set(extension.dockerBuildContext)
                dependsOn(tasks.getByName("jar"))
            }
            tasks.register("dockerUp", DockerUpTask::class.java) {
                composeFilePath.set(extension.composeFile)
            }
            tasks.register("dockerDown", DockerDownTask::class.java) {
                composeFilePath.set(extension.dockerfile)
            }
        }

    }
}

open class DockerExtension(project: Project) {
    val imageName: Property<String> = project.objects
        .property(String::class.java)
        .convention(project.name)
    val tag: Property<String> = project.objects
        .property(String::class.java)
        .convention(project.version.toString())
    val dockerfile: Property<String> = project.objects
        .property(String::class.java)
        .convention("Dockerfile")
    val dockerBuildContext:  Property<String> = project.objects
        .property(String::class.java)
        .convention(project.projectDir.absolutePath)
    val composeFile: Property<String> = project.objects
        .property(String::class.java)
        .convention("docker-compose.yml")
}