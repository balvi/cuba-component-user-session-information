package de.balvi.cuba.sessioninfo.web.session.loader

import com.haulmont.cuba.core.entity.KeyValueEntity
import com.haulmont.cuba.core.global.Messages
import com.haulmont.cuba.core.global.Metadata
import com.haulmont.cuba.core.global.UserSessionSource
import com.haulmont.cuba.security.entity.Group
import com.haulmont.cuba.security.entity.User
import com.haulmont.cuba.security.global.UserSession
import de.balvi.cuba.sessioninfo.web.session.SessionDataLoader
import spock.lang.Specification

class SessionDataLoaderCreateUserInformationSpec extends Specification {


    SessionDataLoader sut
    Metadata metadata
    Messages messages
    UserSession userSession
    Group group
    User user


    def setup() {

        metadata = Mock(Metadata)
        messages = Mock(Messages)
        sut = new SessionDataLoader(
                metadata: metadata,
                messages: messages,
        )

        userSession = Mock(UserSession)


        user = Mock(User)
        user.getInstanceName() >> "My User"

        group = Mock(Group)
        group.getInstanceName() >> "My Group"
        user.getGroup() >> group

        userSession.getUser() >> user
        userSession.getRoles() >> ['role1', 'role2']


    }

    def "createUserInformation creates a list of KV-Entities with user information"() {

        when:
        def result = sut.createUserInformation(userSession)

        then:
        result.size() == 4
        result.every {it instanceof KeyValueEntity}
    }

    def "createUserInformation returns the user as an entry"() {

        given:
        messages.getMessage(SessionDataLoader, 'tables.userinformation.user') >> "User"

        when:
        def result = sut.createUserInformation(userSession)

        then:
        result[0].getValue("attribute") == "User"
        result[0].getValue("value") == "My User"
    }

    def "createUserInformation returns an empty substituted user if there is no user substitution available"() {

        given:
        messages.getMessage(SessionDataLoader, 'tables.userinformation.substitutedUser') >> "Substituted User"

        when:
        def result = sut.createUserInformation(userSession)

        then:
        result[1].getValue("attribute") == "Substituted User"
        result[1].getValue("value") == ""
    }

    def "createUserInformation returns the substituted user if there is a user substitution available"() {

        given:
        messages.getMessage(SessionDataLoader, 'tables.userinformation.substitutedUser') >> "Substituted User"

        and:
        def substituierterBenutzer = Mock(User)
        substituierterBenutzer.getInstanceName() >> "My substituted User"
        userSession.getSubstitutedUser() >> substituierterBenutzer

        when:
        def result = sut.createUserInformation(userSession)

        then:
        result[1].getValue("value") == "My substituted User"
    }


    def "createUserInformation returns the current group"() {

        given:
        messages.getMessage(SessionDataLoader, 'tables.userinformation.group') >> "Group"

        when:
        def result = sut.createUserInformation(userSession)

        then:
        result[2].getValue("attribute") == "Group"
        result[2].getValue("value") == "My Group"
    }

    def "createUserInformation returns all available roles as a csv list"() {

        given:
        messages.getMessage(SessionDataLoader, 'tables.userinformation.roles') >> "Roles"

        when:
        def result = sut.createUserInformation(userSession)

        then:
        result[3].getValue("attribute") == "Roles"
        result[3].getValue("value") == "role1, role2"
    }
}
