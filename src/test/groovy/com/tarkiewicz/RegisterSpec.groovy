package com.tarkiewicz

import com.tarkiewicz.client.AppClient
import com.tarkiewicz.configuration.TestContainerFixture
import com.tarkiewicz.endpoint.dto.RegisterDto
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Unroll

@MicronautTest
class RegisterSpec extends TestContainerFixture {

    def setup() {
        mongoDBContainer.start()
    }

    @Inject
    AppClient appClient

    def 'shouldProperRegisterUser'() {
        when:
        def response = appClient.register(new RegisterDto("username", "password", "user@domain.com"))

        then:
        response.status == HttpStatus.CREATED
        response.body()["message"].contains("Successfully created account with username")
    }

    @Unroll
    def 'shouldNotRegisterUserWhenSomeAttributeIsMissing'() {
        when:
        appClient.register(new RegisterDto(username, password, email))

        then:
        final HttpClientResponseException exception = thrown()
        exception.response.status == HttpStatus.BAD_REQUEST
        exception.getMessage().contains("Bad Request")

        where:
        username   | password   | email
        null       | "password" | "user@domain.com"
        "username" | null       | "user@domain.com"
        "username" | "password" | null
    }
}
