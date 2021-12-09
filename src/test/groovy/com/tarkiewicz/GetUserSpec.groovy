package com.tarkiewicz

import com.tarkiewicz.client.AppClient
import com.tarkiewicz.configuration.TestContainerFixture
import com.tarkiewicz.endpoint.dto.RegisterDto
import io.micronaut.http.HttpResponse
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
class GetUserSpec extends TestContainerFixture {

    def setup() {
        mongoDBContainer.start()
    }

    @Inject
    AppClient appClient

    def 'shouldProperGetUser'() {
        when:
        appClient.register(new RegisterDto("username", "password", "user@domain.com"))
        def loginResponse = appClient.login(new UsernamePasswordCredentials("username", "password"))
        def account = appClient.getUser("username", String.format("Bearer %s", loginResponse.accessToken))

        then:
        account.status() == HttpStatus.OK
        account.body()["username"] == "username"
        account.body()["email"] == "user@domain.com"
    }

    def 'shouldNotGetUserBecauseAccessTokenWasNotProvided'() {
        when:
        appClient.getUser("username", String.format("Bearer %s", "mock"))

        then:
        final HttpClientResponseException exception = thrown()
        exception.response.status == HttpStatus.UNAUTHORIZED

    }

}
