package de.balvi.cuba.sessioninfo.gui.session.loader

import com.haulmont.cuba.core.global.UserSessionSource
import com.haulmont.cuba.security.entity.User
import com.haulmont.cuba.security.global.UserSession
import de.balvi.cuba.sessioninfo.gui.session.SessionDataLoader
import spock.lang.Specification

class SessionDataLoaderCreateSessionAttributeSpec extends Specification {


    SessionDataLoader sut
    UserSession userSession


    def setup() {

        sut = new SessionDataLoader()
        userSession = Mock(UserSession)

    }

    def "createSessionAttribute creates a KV-Entity with an attribute - value pair"() {

        given:
        userSession.getAttributeNames() >> ['attribute1']

        and:
        userSession.getAttribute('attribute1') >> 'value1'
        when:
        def result = sut.createSessionAttribute(userSession)

        then:
        result.size() == 1
        result[0].getValue('attribute') == 'attribute1'
        result[0].getValue('value') == 'value1'
    }

    def "createSessionAttribute creates a list of KV-Entities with all session attributes"() {

        given:
        userSession.getAttributeNames() >> ['attribute1', 'attribute2']

        and:
        userSession.getAttribute('attribute1') >> 'value1'
        userSession.getAttribute('attribute2') >> 'value2'
        when:
        def result = sut.createSessionAttribute(userSession)

        then:
        result.size() == 2
        result.any {it.getValue('attribute') == 'attribute1' && it.getValue('value') == 'value1'}
        result.any {it.getValue('attribute') == 'attribute2' && it.getValue('value') == 'value2'}
    }


    def "createSessionAttribute returns the toString representation of a value from a session attribute"() {

        given:
        userSession.getAttributeNames() >> ['attribute1']

        and:
        userSession.getAttribute('attribute1') >> true
        when:
        def result = sut.createSessionAttribute(userSession)

        then:
        result[0].getValue('value') == 'true'
    }

    def "createSessionAttribute returns the instance name if the value of the session attribute is an entity"() {

        given:
        userSession.getAttributeNames() >> ['user']

        and:
        def user = Mock(User)
        user.getInstanceName() >> 'myUser'
        userSession.getAttribute('user') >> user

        when:
        def result = sut.createSessionAttribute(userSession)

        then:
        result[0].getValue('value') == 'myUser'
    }


}
