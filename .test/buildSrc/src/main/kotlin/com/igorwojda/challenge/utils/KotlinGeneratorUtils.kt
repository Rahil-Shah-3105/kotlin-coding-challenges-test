package com.igorwojda.challenge.utils

import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassBody
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtImportList
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import java.io.File
import org.slf4j.LoggerFactory

object KotlinGeneratorUtils {
	private val log = LoggerFactory.getLogger(KotlinGeneratorUtils::class.java)
    fun getTestFiles(challengeDirectoryPath: File): List<TestFile> {
		log.debug(">>> Entering getTestFiles(challengeDirectoryPath={})", challengeDirectoryPath)
        val challengeKtFile =
            KotlinParserUtils.getChallengeKtFile(challengeDirectoryPath, ChallengeFile.CHALLENGE_KT)
        val solutionKtFile =
            KotlinParserUtils.getChallengeKtFile(challengeDirectoryPath, ChallengeFile.SOLUTIONS_KT)
        val challengeName = getChallengeName(challengeKtFile)

        val solutions = getSolutions(solutionKtFile)

		log.debug("<<< Exiting getTestFiles(challengeDirectoryPath={})", challengeDirectoryPath)
        return solutions.map {
            getTestFile(challengeName, challengeKtFile, solutionKtFile, it)
        }
    }

    private fun getTestFile(
        challengeName: String,
        challengeKtFile: KtFile,
        solutionKtFile: KtFile,
        solution: KtObjectDeclaration,
    ): TestFile {

		log.debug(">>> Entering getTestFile(challengeName={},challengeKtFile={},solutionKtFile={},solution={})", challengeName, challengeKtFile, solutionKtFile, solution)
        val solutionName = checkNotNull(solution.name) { "Solution name is null" }
        val packageStr = getPackage(challengeKtFile, solutionName)
        val imports = getImports(solutionKtFile, challengeKtFile)
        val solutionMembers = getSolutionMembers(solution)
        val tests = getTests(challengeKtFile)

        val lines = listOf(
            listOf(packageStr),
            listOf("\n"),
            imports,
            listOf("\n"),
            solutionMembers,
            listOf("\n"),
            tests
        ).flatten()

        val testrSolutionFileName = getFileName(solution, challengeName)
        val relativePath = solutionName.lowercase()

		log.debug("<<< Exiting getTestFile(challengeName={},challengeKtFile={},solutionKtFile={},solution={})", challengeName, challengeKtFile, solutionKtFile, solution)
        return TestFile(testrSolutionFileName, relativePath, lines)
    }

    private fun getChallengeName(challengeKtFile: KtFile) =
        challengeKtFile
            .packageFqName
            .toString()
            .removePrefix("com.igorwojda.")
            .replace(".", "_")

    private fun getTests(challengeKtFile: KtFile): List<String> {
		log.debug(">>> Entering getTests(challengeKtFile={})", challengeKtFile)
        val tests = challengeKtFile
            .children
            .filterIsInstance<KtClass>()
            .firstOrNull { it.name == "Test" }
            .also { checkNotNull(it) { "Missing 'Test' class in ${challengeKtFile.name}" } }
            ?.text
            ?.split("\n")

		log.debug("<<< Exiting getTests(challengeKtFile={})", challengeKtFile)
        return tests ?: listOf()
    }

    private fun getImports(
        solutionKtFile: KtFile,
        challengeKtFile: KtFile,
    ) = (solutionKtFile.children.toList() + challengeKtFile.children.toList())
        .filterIsInstance<KtImportList>()
        .flatMap { it.imports }
        .map { it.text }
        .toSet()
        .toList()

    private fun getPackage(ktFile: KtFile, solutionName: String) =
        "package generated.${ktFile.packageFqName}.$solutionName".lowercase()

    private fun getFileName(solution: KtObjectDeclaration, challengeName: String): String {
		log.debug(">>> Entering getFileName(solution={},challengeName={})", solution, challengeName)
        val solutionName = checkNotNull(solution.name) { "Solution name is null" }
		log.debug("<<< Exiting getFileName(solution={},challengeName={})", solution, challengeName)
        return "Test_${challengeName}_$solutionName.kt"
    }

    private fun getSolutions(ktFile: KtFile) = ktFile
        .children
        .filterIsInstance<KtObjectDeclaration>()
        .filterNot { it.name == "KtLintWillNotComplain" }

    private fun getSolutionMembers(solution: KtObjectDeclaration) = solution
        .children
        .filterIsInstance<KtClassBody>()
        .first()
        .children
        .joinToString(separator = "\n\n") { it.text }
        .split("\n")
}

data class TestFile(
    val fileName: String,
    val relativePath: String,
    val lines: List<String>,
)
