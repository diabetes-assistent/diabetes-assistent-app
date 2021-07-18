package com.github.diabetesassistant.dosage.domain

import com.auth0.jwt.interfaces.DecodedJWT
import com.github.diabetesassistant.auth.data.UserDTO
import org.mockito.Mockito
import com.github.diabetesassistant.dosage.data.DosageClient
import com.github.diabetesassistant.dosage.data.InsulinPresetsDTO
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class DosageServiceTest {
    private fun fixtures(): Pair<DosageClient, DosageService> {
        val client: DosageClient = Mockito.mock(DosageClient::class.java)

        return Pair(client, DosageService(client))
    }

    @Test
    fun shouldReturnCalculatedDosage() {
        runBlocking {
            val (clientMock, testee) = fixtures()
            val user = UserDTO("123", "foo@email.com", "patient")
            val presets = Result.success(InsulinPresetsDTO(
               0.5,
                   20,
                2,
                    120,
                    260,
                    80
            ))
            Mockito.`when`(clientMock.getInsulinPresets(user)).thenReturn(presets)

            val actual = testee.calculateDosage("123", "foo@email.com")
            val expected = Result.success(Token("access", decodedJWT))

            Assert.assertEquals(expected, actual)
        }
    }
}