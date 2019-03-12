package com.github.tpiskorski.vboxcm.stub.generator

import com.github.tpiskorski.vboxcm.core.server.Server
import com.github.tpiskorski.vboxcm.core.server.ServerService
import com.github.tpiskorski.vboxcm.core.vm.VirtualMachineService
import com.github.tpiskorski.vboxcm.stub.generator.VirtualMachineStubGenerator
import javafx.collections.ObservableList
import spock.lang.Specification
import spock.lang.Subject

class VirtualMachineStubGeneratorTest extends Specification {

    def serverService = Mock(ServerService)
    def virtualMachineService = Mock(VirtualMachineService)

    @Subject generator = new VirtualMachineStubGenerator(
            serverService, virtualMachineService
    )

    def 'should generate vms for each server'() {
        given:
        def servers = ([Mock(Server)] * 5) as ObservableList

        when:
        generator.afterPropertiesSet()

        then:
        1 * serverService.getServers() >> servers
        (5.._) * virtualMachineService.add(*_)
    }
}
