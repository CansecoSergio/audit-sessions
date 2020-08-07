package audit.sessions

import grails.gorm.transactions.Transactional
import org.grails.web.util.WebUtils
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

import java.time.LocalDateTime

@Transactional
class SessionsService {

    def springSecurityService

    def serviceMethod() {

    }

    void saveAuditoriaUsuario() {
        def grailsWebRequest = WebUtils.retrieveGrailsWebRequest()?.getCurrentRequest()

        try {
            AuditoriaUsuario auditoriaUsuario = new AuditoriaUsuario()

            auditoriaUsuario.usuario = springSecurityService.isLoggedIn() ?
                    springSecurityService.currentUser?.username : "NO AUTENTICADO"
            auditoriaUsuario.idSesion = RequestContextHolder.getRequestAttributes()?.getSessionId() ?: null
            auditoriaUsuario.url = grailsWebRequest?.getRequestURI()?.toString() ?: "SIN URL"
            auditoriaUsuario.ip = ((ServletRequestAttributes)
                    RequestContextHolder.currentRequestAttributes())?.getRequest()?.getRemoteAddr() ?: null
            auditoriaUsuario.fechaRegistro = LocalDateTime.now()

            auditoriaUsuario.save flush: true, failOnError: true
        } catch (Exception e) {
            log.error e
        }
    }
}
