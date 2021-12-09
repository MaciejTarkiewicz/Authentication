package com.tarkiewicz

import com.tarkiewicz.configuration.TestContainerFixture
import io.micronaut.runtime.EmbeddedApplication
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject

@MicronautTest
class AuthenticationSpec extends TestContainerFixture {

    @Inject
    EmbeddedApplication<?> application

    def setup() {
        mongoDBContainer.start()
    }

    void 'test it works'() {
        expect:
        application.running
    }

}
