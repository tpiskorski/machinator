package tpiskorski.vboxcm.lifecycle.state.manager

import javafx.collections.ObservableList
import spock.lang.Specification
import spock.lang.Subject
import tpiskorski.vboxcm.core.server.Server
import tpiskorski.vboxcm.core.server.ServerService
import tpiskorski.vboxcm.lifecycle.state.serialize.io.ObjectPersister
import tpiskorski.vboxcm.lifecycle.state.serialize.io.ObjectRestorer
import tpiskorski.vboxcm.lifecycle.state.serialize.model.SerializableServer

class ServerStateManagerTest extends Specification {

    def serverService = Mock(ServerService)

    def objectPersister = Mock(ObjectPersister)
    def objectRestorer = Mock(ObjectRestorer)

    @Subject persister = new ServerStateManager(serverService)

    def setup() {
        persister.objectPersister = objectPersister
        persister.objectRestorer = objectRestorer
    }

    def 'should persist servers state'() {
        given:
        def servers = createServers()

        when:
        persister.persist()

        then:
        1 * serverService.getServers() >> servers
        1 * objectPersister.persist(_, _)
    }

    def 'should restore servers state'() {
        given:
        def servers = createSerializableServers()

        when:
        persister.restore()

        then:
        1 * objectRestorer.restore(_, _) >> servers
        3 * serverService.add(_)
    }

    def createServers() {
        [
                new Server('some', '123'),
                new Server('some', '321'),
                new Server('other', '123')
        ] as ObservableList
    }

    def createSerializableServers() {
        createServers().collect { new SerializableServer(it) }
    }
}