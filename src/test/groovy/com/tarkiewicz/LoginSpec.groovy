package com.tarkiewicz

import com.tarkiewicz.client.AppClient
import com.tarkiewicz.configuration.TestContainerFixture
import com.tarkiewicz.endpoint.dto.RegisterDto
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.security.authentication.UsernamePasswordCredentials
import io.micronaut.security.token.jwt.render.BearerAccessRefreshToken
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Stepwise
import spock.lang.Unroll

@MicronautTest
@Stepwise
class LoginSpec extends TestContainerFixture {

    def setup() {
        mongoDBContainer.start()
    }

    @Inject
    AppClient appClient

    def 'shouldProperLoginUser'() {
        when:
        appClient.register(new RegisterDto("username", "password", "user@domain.com"))
        def response = appClient.login(new UsernamePasswordCredentials("username", "password"))

        then:
        response.username == "username"
        response.accessToken != null
        response.refreshToken != null
        response.tokenType == "Bearer"
    }

    @Unroll
    def 'shouldNotLoginUser'() {
        when:
        appClient.login(new UsernamePasswordCredentials(username, password))

        then:
        final HttpClientResponseException exception = thrown()
        exception.response.status == HttpStatus.UNAUTHORIZED
        exception.message.contains(message)

        where:
        username        | password        | message
        "username"      | "wrongPassword" | "Credentials Do Not Match"
        "wrongUsername" | "password"      | "User Not Found"

    }
}
