package audit

class SessionsInterceptor {

    def sessionsService
    def springSecurityService

    SessionsInterceptor() {
        matchAll()
                .excludes(controller: 'login')
                .excludes(controller: 'logout')
    }

    boolean before() {
        if (springSecurityService.isLoggedIn()) {
            sessionsService.saveAuditoriaUsuario()
            true
        }

        true
    }

    boolean after() { true }

    void afterView() {
        // no-op
    }
}
