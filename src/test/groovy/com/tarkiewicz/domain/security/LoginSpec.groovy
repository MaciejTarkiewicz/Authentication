package com.tarkiewicz.domain.security

import com.tarkiewicz.client.AppClient
import com.tarkiewicz.configuration.TestContainerFixture
import com.tarkiewicz.endpoint.dto.request.RegisterRequestDto
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.security.authentication.UsernamePasswordCredentials
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Stepwise
import spock.lang.Unroll

@MicronautTest
@Stepwise
class LoginSpec extends TestContainerFixture {

    def setup() {
        mongoDBContainer.start()
        kafkaContainer.start()
    }

    @Inject
    AppClient appClient

    def 'shouldProperLoginUser'() {
        given: "Register user"
        appClient.register(new RegisterRequestDto("username", "password", "user@domain.com"))

        when: "Login user"
        def response = appClient.login(new UsernamePasswordCredentials("username", "password"))

        then: "Corrected response has been returned"
        response.username == "username"
        response.accessToken != null
        response.refreshToken != null
        response.tokenType == "Bearer"
    }

    @Unroll
    def 'shouldNotLoginUser'() {
        when: "Try to login with incorrect data"
        appClient.login(new UsernamePasswordCredentials(username, password))

        then: "The user is not legged and a status of 401 with a valid message has been returned"
        final HttpClientResponseException exception = thrown()
        exception.response.status == HttpStatus.UNAUTHORIZED
        exception.message.contains(message)

        where:
        username        | password        | message
        "username"      | "wrongPassword" | "Credentials Do Not Match"
        "wrongUsername" | "password"      | "User Not Found"
    }
}
