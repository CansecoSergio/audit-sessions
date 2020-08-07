package audit.sessions

import grails.gorm.transactions.Transactional
import groovy.util.logging.Slf4j
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.web.context.request.ServletRequestAttributes

import javax.servlet.http.HttpSessionEvent
import javax.servlet.http.HttpSessionListener
import java.time.LocalDateTime

@Slf4j
class ExpiringSessionEventListener implements HttpSessionListener {

    private String ipAddress = ""

    @Override
    void sessionCreated(HttpSessionEvent se) {
        this.ipAddress = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getRemoteAddr().toString()
    }

    @Override
    void sessionDestroyed(HttpSessionEvent se) {
        if (((new Date().getTime() - se?.session?.lastAccessedTime) / 1000) >= se.session.maxInactiveInterval) {
            cleanUpUser(se, ipAddress)
        }
    }

    @Transactional
    void cleanUpUser(HttpSessionEvent event, String ipAddress) {
        SecurityContext securityContext = event?.session?.getAttribute("SPRING_SECURITY_CONTEXT")

        if (securityContext) {
            Authentication authentication = securityContext?.authentication
            def username = authentication?.principal?.username

            try {
                BitacoraSesiones bitacoraLogeo = new BitacoraSesiones()
                bitacoraLogeo.actividad = 'SESION EXPIRADA'
                bitacoraLogeo.fecha = LocalDateTime.now()
                bitacoraLogeo.usuario = username
                bitacoraLogeo.ip = ipAddress

                bitacoraLogeo.save flush: true, failOnError: true
            } catch (Exception e) {
                println e.getMessage()
            }
        }
    }

}
