package audit.sessions

import java.time.LocalDateTime

class AuditoriaUsuarios {

    String usuario
    String idSesion
    String url
    String ip
    LocalDateTime fechaRegistro

    static constraints = {
        usuario nullable: true
        idSesion nullable: true
        url nullable: false
        ip nullable: true
        fechaRegistro nullable: true
    }

    static mapping = {
        version false
        id(generator: 'sequence', params: [sequence: 'auditoria_usuarios_id_seq'])
    }

}
