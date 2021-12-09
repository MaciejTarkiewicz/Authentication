package com.tarkiewicz.domain.account

import com.tarkiewicz.client.AppClient
import com.tarkiewicz.configuration.TestContainerFixture
import com.tarkiewicz.endpoint.dto.request.RegisterRequestDto
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Stepwise
import spock.lang.Unroll

@MicronautTest
@Stepwise
class RegisterSpec extends TestContainerFixture {

    def setup() {
        mongoDBContainer.start()
    }

    @Inject
    AppClient appClient

    def 'shouldProperRegisterUser'() {
        when: "Register user with all data"
        def response = appClient.register(new RegisterRequestDto("username", "password", "user@domain.com"))

        then: "Response status is 201 with correct message"
        response.status == HttpStatus.CREATED
        response.body()["message"].contains("Successfully created account with username")
    }

    @Unroll
    def 'shouldNotRegisterUserWhenSomeAttributeIsMissing'() {
        when: "Register user without some field"
        appClient.register(new RegisterRequestDto(username, password, email))

        then: "The user is not registered and a status of 400 with a valid message has been returned"
        final HttpClientResponseException exception = thrown()
        exception.response.status == HttpStatus.BAD_REQUEST
        exception.message.contains("Bad Request")

        where:
        username   | password   | email
        null       | "password" | "user@domain.com"
        "username" | null       | "user@domain.com"
        "username" | "password" | null
    }

    def 'shouldNotRegisterUserWhenUsernameAlreadyExist'() {
        when: "Try to register user with already exists username"
        appClient.register(new RegisterRequestDto("username", "password", "user@domain.com"))

        then: "The user is not registered and a status of 409 with a valid message has been returned"
        final HttpClientResponseException exception = thrown()
        exception.response.status == HttpStatus.CONFLICT
        exception.message.contains("The user with username: username already exist, please choose another username")

    }
}
