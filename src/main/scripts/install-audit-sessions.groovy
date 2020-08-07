description "Example description", "grails example-usage"

println "Instalando audit-sessions"
println "..."

println "Modificando en archivo application.groovy"
if (!modificarConfig()) {
    addStatus '''¡No fue posible modificar el archivo application.groovy!'''
    return false
}

println "Modificando en archivo resources.groovy"
if (!modificarResources()) {
    addStatus '''¡No fue posible modificar el archivo resources.groovy!'''
    return false
}

addStatus '''
*******************************************************
* El archivo grails-app/conf/application.groovy se ha *
* modificado y configurado correctamente.             *
*                                                     *
* El archivo grails-app/conf/spring/resource.groovy   *
* se ha modificado y configurado correctamente.       *
*******************************************************
'''

private boolean modificarConfig() {
    boolean status = false
    file("grails-app/conf/application.groovy").withWriterAppend {
        BufferedWriter writer ->
            writer.newLine()
            writer.writeLine '// Añadido por el plugin audit-sessions:'
            writer.writeLine "grails.plugin.springsecurity.logout.handlerNames = ['rememberMeServices', 'securityContextLogoutHandler', 'loggingSecurityEventListener']"
            writer.writeLine "grails.plugin.springsecurity.useSecurityEventListener = true"
            status = true
    }

    return status
}

private boolean modificarResources() {
    boolean status = false

    String importSessionRegistry = "import org.springframework.security.core.session.SessionRegistryImpl"
    String importExpiring = "import audit.sessions.ExpiringSessionEventListener"
    String importLogging = "import audit.sessions.LoggingSecurityEventListener"

    String beanLogging = "loggingSecurityEventListener(LoggingSecurityEventListener)"
    String beanSessionRegistry = "sessionRegistry(SessionRegistryImpl)"
    String beanExpiring = "expiringSessionEventListener(ExpiringSessionEventListener)"

    def listImports = [importSessionRegistry, importExpiring, importLogging]
    def listBeans = [beanLogging, beanSessionRegistry, beanExpiring]
    def listaImportsRepetidos = []
    def listaBeansRepetidos = []

    file("grails-app/conf/spring/resources.groovy").eachLine {
        linea ->
            listImports.eachWithIndex { String elementoLista, int index ->
                if (elementoLista.equals(linea.toString().trim())) {
                    listaImportsRepetidos.add(elementoLista)
                }
            }

            listBeans.eachWithIndex { String elementoLista, int index ->
                if (elementoLista.equals(linea.toString().trim())) {
                    listaBeansRepetidos.add(elementoLista)
                }

            }
    }

    String imports = ""
    String beans = ""

    listImports.each {
        String elemento ->
            boolean existe = false

            listaImportsRepetidos.each {
                String elementoRepetido ->
                    if (elementoRepetido.equals(elemento)) {
                        existe = true
                    }
            }
            if (!existe)
                imports += elemento + '\n'
    }

    listBeans.each {
        String elemento ->
            boolean existe = false

            listaBeansRepetidos.each {
                String elementoRepetido ->
                    if (elementoRepetido.equals(elemento)) {
                        existe = true
                    }
            }
            if (!existe)
                beans += '\t' + elemento + '\n'
    }

    String textoFinalArchivo = ""

    textoFinalArchivo = imports + '\n'

    def patron = /^beans \= \{.*/

    file("grails-app/conf/spring/resources.groovy").eachLine {
        String linea, int contador ->

            textoFinalArchivo += linea.toString() + '\n'

            if (linea ==~ patron) {
                textoFinalArchivo += beans + '\n'
            }
    }

    file("grails-app/conf/spring/resources.groovy").write(textoFinalArchivo)
    status = true

    return status
}