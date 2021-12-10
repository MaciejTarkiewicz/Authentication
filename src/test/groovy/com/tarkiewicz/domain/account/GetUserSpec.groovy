package com.tarkiewicz.domain.account

import com.tarkiewicz.client.AppClient
import com.tarkiewicz.configuration.TestContainerFixture
import com.tarkiewicz.endpoint.dto.request.RegisterRequestDto
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.security.authentication.UsernamePasswordCredentials
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Stepwise

@MicronautTest
@Stepwise
class GetUserSpec extends TestContainerFixture {

    def setup() {
        mongoDBContainer.start()
        kafkaContainer.start()
    }

    @Inject
    AppClient appClient

    def 'shouldProperGetUser'() {
        given: "Register user"
        appClient.register(new RegisterRequestDto("username", "password", "user@domain.com"))

        and: "Login user"
        def loginResponse = appClient.login(new UsernamePasswordCredentials("username", "password"))

        when: "Get user"
        def account = appClient.getUser("username", String.format("Bearer %s", loginResponse.accessToken))

        then: "Status is 200 and correct data was fetched"
        account.status() == HttpStatus.OK
        account.body()["username"] == "username"
        account.body()["email"] == "user@domain.com"
    }

    def 'shouldNotGetUserBecauseUserWithProvidedUserNameNotExist'() {
        given: "Login user"
        def loginResponse = appClient.login(new UsernamePasswordCredentials("username", "password"))

        when: "Get another user"
        def response = appClient.getUser("username1", String.format("Bearer %s", loginResponse.accessToken))

        then: "Status is 404 and correct response message is generated"
        response.status == HttpStatus.NOT_FOUND
        response.body()["message"].contains("Cannot find user with username")

    }

    def 'shouldNotGetUserBecauseAccessTokenWasNotProvided'() {
        when: "Get user"
        appClient.getUser("username", String.format("Bearer %s", "mock"))

        then: "Status is 401 when access token is not correct"
        final HttpClientResponseException exception = thrown()
        exception.response.status == HttpStatus.UNAUTHORIZED
    }
}
