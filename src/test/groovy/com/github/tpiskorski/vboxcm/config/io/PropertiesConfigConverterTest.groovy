package com.github.tpiskorski.vboxcm.config.io

import com.github.tpiskorski.vboxcm.config.Config
import com.github.tpiskorski.vboxcm.config.io.PropertiesConfigConverter
import spock.lang.Specification
import spock.lang.Subject

class PropertiesConfigConverterTest extends Specification {

    @Subject converter = new PropertiesConfigConverter()

    def 'should convert properties to config'() {
        given:
        def properties = new Properties([
                'ssh.user'       : 'root',
                'ssh.password'   : 'root',
                'poll.interval'  : '20',
                'backup.location': 'some/location'
        ])

        when:
        def config = converter.convert(properties)

        then:
        config.sshUser == 'root'
        config.sshPassword == 'root'
        config.pollInterval == 20
        config.backupLocation == 'some/location'
    }

    def 'should convert config to properties'() {
        given:
        def config = Config.builder()
                .sshUser('root')
                .sshPassword('root')
                .pollInterval(20)
                .backupLocation('some/location')
                .build()

        when:
        def properties = converter.convert(config)

        then:
        properties.get('ssh.user') == 'root'
        properties.get('ssh.password') == 'root'
        properties.get('poll.interval') == '20'
        properties.get('backup.location') == 'some/location'
    }
}
