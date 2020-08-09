package audit.sessions

import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.SpringSecurityUtils
import groovy.util.logging.Slf4j
import org.springframework.context.ApplicationListener
import org.springframework.security.authentication.event.AbstractAuthenticationEvent
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.LogoutHandler
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.time.LocalDateTime

@Slf4j
class LoggingSecurityEventListener implements ApplicationListener<AbstractAuthenticationEvent>, LogoutHandler {

    @Override
    @Transactional
    void onApplicationEvent(AbstractAuthenticationEvent event) {
        event.authentication.with {
            def usuario = principal.hasProperty('username')?.getProperty(principal) ?: principal
            def actividad

            try {
                switch (event.class.simpleName) {
                    case 'AuthenticationSuccessEvent':
                        actividad = 'AUTENTICACION EXITOSA'
                        break;
                    case 'InteractiveAuthenticationSuccessEvent':
                        actividad = 'LOGIN REALIZADO'
                        break;
                    case 'AuthenticationFailureBadCredentialsEvent':
                        actividad = 'AUTENTICACION FALLIDA. CREDENCIALES INCORRECTAS'
                        break;
                    case 'AuthenticationFailureDisabledEvent':
                        actividad = 'AUTENTICACION FALLIDA. USUARIO INHABILITADO'
                        break;
                    case 'AuthenticationFailureCredentialsExpiredEvent':
                        actividad = 'AUTENTICACION FALLIDA. PASSWORD EXPIRADO'
                        break;
                    case 'AuthenticationFailureExpiredEvent':
                        actividad = 'AUTENTICACION FALLIDA. CUENTA DE USUARIO EXPIRADA'
                        break;
                    case 'AuthenticationFailureLockedEvent':
                        actividad = 'AUTENTICACION FALLIDA. CUENTA DE USUARIO BLOQUEADA'
                        break;
                    case 'SessionFixationProtectionEvent':
                        actividad = 'EVENTO DE CONSTRUCCION Y PROTECCION DE SESIONES'
                        break;
                    default:
                        actividad = event.class.simpleName
                        break;
                }

                BitacoraSesiones registro = new BitacoraSesiones()
                registro.actividad = actividad
                registro.fecha = LocalDateTime.now()
                registro.usuario = usuario
                registro.ip = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                        .getRequest().getRemoteAddr() ?: null

                registro.save flush: true, failOnError: true
            } catch (Exception e) {
                log.error e
            }
        }
    }

    @Override
    @Transactional
    void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        authentication.with { it ->
            def autenticacion = it?.getPrincipal() ?: null;
            if (autenticacion) {
                def usuario = autenticacion.hasProperty('username')?.getProperty(autenticacion) ?: autenticacion
                try {
                    BitacoraSesiones registro = new BitacoraSesiones()
                    registro.actividad = 'LOGOUT REALIZADO'
                    registro.fecha = LocalDateTime.now()
                    registro.usuario = usuario
                    registro.ip = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                            .getRequest().getRemoteAddr() ?: null

                    registro.save flush: true, failOnError: true
                } catch (e) {
                    log.error e
                }
            }
        }
    }

}
