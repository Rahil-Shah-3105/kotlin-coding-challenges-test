package com.igorwojda.challenge.utils

import com.intellij.openapi.util.Disposer
import com.intellij.psi.PsiManager
import com.intellij.testFramework.LightVirtualFile
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.psi.KtFile
import java.io.File
import org.slf4j.LoggerFactory

object KotlinParserUtils {
	private val log = LoggerFactory.getLogger(KotlinParserUtils::class.java)
    private val project by lazy {
        KotlinCoreEnvironment.createForProduction(
            Disposer.newDisposable(),
            CompilerConfiguration(),
            EnvironmentConfigFiles.JVM_CONFIG_FILES
        ).project
    }

    fun getChallengeFile(challengeDirectoryPath: File, challengeFile: ChallengeFile): File {
		log.debug(">>> Entering getChallengeFile(challengeDirectoryPath={},challengeFile={})", challengeDirectoryPath, challengeFile)
        val path = "${challengeDirectoryPath.path}/${challengeFile.fileName}"
		log.debug("<<< Exiting getChallengeFile(challengeDirectoryPath={},challengeFile={})", challengeDirectoryPath, challengeFile)
        return File(path)
    }

    fun getChallengeKtFile(challengeDirectoryPath: File, challengeFile: ChallengeFile): KtFile {
		log.debug(">>> Entering getChallengeKtFile(challengeDirectoryPath={},challengeFile={})", challengeDirectoryPath, challengeFile)
        val file = getChallengeFile(challengeDirectoryPath, challengeFile)
        val fullFileName = "${challengeDirectoryPath.path}/${challengeFile.fileName}"
		log.debug("<<< Exiting getChallengeKtFile(challengeDirectoryPath={},challengeFile={})", challengeDirectoryPath, challengeFile)
        return getChallengeKtFile(file.readText(), fullFileName)
    }

    private fun getChallengeKtFile(codeString: String, fileName: String) =
        PsiManager.getInstance(project)
            .findFile(
                LightVirtualFile(fileName, KotlinFileType.INSTANCE, codeString)
            ) as KtFile
}
