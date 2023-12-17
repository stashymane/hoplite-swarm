package dev.stashy.hoplite.swarm

import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.parsers.PropsPropertySource
import java.nio.file.Files
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class SwarmSecretPreprocessorTest {
    @Test
    fun testSecret(): Unit = Jimfs.newFileSystem(Configuration.unix()).use { fs ->
        val path = fs.getPath("/run/secrets")
        Files.createDirectories(path)

        val secrets = mapOf("secret1" to "secret content", "secret2" to "another secret")
        val props = Properties()

        secrets.forEach { (key, value) ->
            Files.write(path.resolve(key), value.toByteArray())
            props[key] = "swarm://$key"
        }

        val config = ConfigLoaderBuilder.default()
            .addPreprocessor(SwarmSecretPreprocessor(path))
            .addPropertySource(PropsPropertySource(props))
            .build()
            .loadConfigOrThrow<TestConfig>()

        assertEquals(secrets["secret1"], config.secret1)
        assertEquals(secrets["secret2"], config.secret2)
    }
}

data class TestConfig(val secret1: String, val secret2: String)
