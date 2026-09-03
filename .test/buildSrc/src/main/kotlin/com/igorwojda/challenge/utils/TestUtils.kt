package com.igorwojda.challenge.utils

import org.gradle.api.Project
import java.io.File
import org.slf4j.LoggerFactory

object TestUtils {
private val log = LoggerFactory.getLogger(TestUtils::class.java)

    fun generateTestFiles(project: Project) {
		log.debug(">>> Entering generateTestFiles(project={})", project)
        getChallengeDirectories(project).forEach {
            generateTestFiles(it)
        }
		log.debug("<<< Exiting generateTestFiles(project={})", project)
    }

    /**
     * Generate test files for a given challenge by combining challenge file with available solutions
     */
    private fun generateTestFiles(challengeDirectoryPath: File) {
        val generatedChallengeDirecotryPath = challengeDirectoryPath
            .path
            .replace("kotlin/com/igorwojda/", "kotlin/generated/com/igorwojda/")

        // May be already pre-cached by CI
        deleteDirectory(File(generatedChallengeDirecotryPath))

        val testFiles = KotlinGeneratorUtils.getTestFiles(challengeDirectoryPath)

        testFiles
            .forEach {
                createTestFile(generatedChallengeDirecotryPath, it)
            }
    }

    private fun deleteDirectory(directory: File) {
		log.debug(">>> Entering deleteDirectory(directory={})", directory)
        if (directory.isDirectory) {
            val files = directory.listFiles()

            // if the directory contains any file
            if (files != null) {
                for (file in files) {

                    // recursive call if the subdirectory is non-empty
                    deleteDirectory(file)
                }
            }
        }
        directory.delete()
		log.debug("<<< Exiting deleteDirectory(directory={})", directory)
    }

    /**
     * Create a test files for a given challenge
     */
    private fun createTestFile(generatedChallengeDirecotryPath: String, testFile: TestFile) {
		log.debug(">>> Entering createTestFile(generatedChallengeDirecotryPath={},testFile={})", generatedChallengeDirecotryPath, testFile)
        val testDirectory = File("$generatedChallengeDirecotryPath/${testFile.relativePath}/")
        testDirectory.mkdirs()

        val targetChallengeFile = File("${testDirectory.path}/${testFile.fileName}")
        targetChallengeFile.writeText(testFile.lines.joinToString(separator = "\n"))
		log.debug("<<< Exiting createTestFile(generatedChallengeDirecotryPath={},testFile={})", generatedChallengeDirecotryPath, testFile)
    }

    /**
     * Return list of project names
     */
    private fun getChallengeDirectories(project: Project): List<File> {
		log.debug(">>> Entering getChallengeDirectories(project={})", project)
        val path = "${project.rootDir.path}/../src/test/kotlin/com/igorwojda"
        val directory = File(path)
        val miscDirectoryName = "misc"

        return directory
            .walk()
            .filter { it.isDirectory }
            .filter { it.name != miscDirectoryName }
            .filter { it.isHighLevelDirectory }
            .toList()
			log.debug("<<< Exiting getChallengeDirectories(project={})", project)
    }

    /**
     * Checks whatever or not directory is high level directory (challenge grouping directory)
     */
    private val File.isHighLevelDirectory get() = this.isDirectory && this.listFiles().none { it.isDirectory }
}
