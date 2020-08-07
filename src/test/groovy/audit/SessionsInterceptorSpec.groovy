package audit

import grails.testing.web.interceptor.InterceptorUnitTest
import spock.lang.Specification

class SessionsInterceptorSpec extends Specification implements InterceptorUnitTest<SessionsInterceptor> {

    def setup() {
    }

    def cleanup() {

    }

    void "Test sessions interceptor matching"() {
        /*when:"A request matches the interceptor"
            withRequest(controller:"sessions")

        then:"The interceptor does match"
            interceptor.doesMatch()*/
    }
}
