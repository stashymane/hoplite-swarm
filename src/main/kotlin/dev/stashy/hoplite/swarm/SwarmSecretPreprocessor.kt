package dev.stashy.hoplite.swarm

import com.sksamuel.hoplite.*
import com.sksamuel.hoplite.fp.invalid
import com.sksamuel.hoplite.fp.valid
import com.sksamuel.hoplite.preprocessor.TraversingPrimitivePreprocessor
import java.nio.file.Files
import java.nio.file.Path

/**
 * Hoplite preprocessor for Docker Swarm secrets.
 * @property secretPath The path to look for secret files in.
 * @property trimValues Whether to trim leading and trailing whitespace & lines from the secret value.
 */
class SwarmSecretPreprocessor(
    private val secretPath: Path = Path.of("/run/secrets"),
    private val trimValues: Boolean = true
) : TraversingPrimitivePreprocessor() {
    private val regex = Regex("swarm://(.+?)")

    override fun handle(node: PrimitiveNode, context: DecoderContext): ConfigResult<Node> = when (node) {
        is StringNode ->
            when (val match = regex.matchEntire(node.value)) {
                null -> node.valid()
                else -> {
                    val secretName = match.groupValues[1]
                    getSecret(secretName, node)
                }
            }

        else -> node.valid()
    }

    private fun getSecret(name: String, node: StringNode): ConfigResult<Node> = runCatching {
        val content = Files.readString(secretPath.resolve(name))
        val value = if (trimValues) content.trim() else content
        node.copy(value = value)
            .withMeta(CommonMetadata.UnprocessedValue, node.value)
            .withMeta(CommonMetadata.Secret, true)
            .withMeta(CommonMetadata.RemoteLookup, "Swarm secret '$name'")
            .valid()
    }.getOrElse { ConfigFailure.PreprocessorFailure("Failed to load secret $name from Swarm", it).invalid() }
}
