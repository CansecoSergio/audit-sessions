package audit.sessions

import java.time.LocalDateTime

class BitacoraSesiones {

    String usuario
    String actividad
    String ip
    LocalDateTime fecha

    static constraints = {
        usuario nullable: true
        actividad nullable: true
        ip nullable: true
        fecha nullable: true
    }

    static mapping = {
        version false
        id(generator: 'sequence', params: [sequence: 'bitacora_sesiones_id_seq'])
    }

}
