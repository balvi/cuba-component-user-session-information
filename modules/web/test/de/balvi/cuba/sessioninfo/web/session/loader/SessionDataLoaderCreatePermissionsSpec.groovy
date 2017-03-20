package de.balvi.cuba.sessioninfo.web.session.loader

import com.haulmont.cuba.core.global.MessageTools
import com.haulmont.cuba.core.global.Messages
import com.haulmont.cuba.core.global.UserSessionSource
import com.haulmont.cuba.security.entity.PermissionType
import com.haulmont.cuba.security.global.UserSession
import de.balvi.cuba.sessioninfo.web.session.SessionDataLoader
import de.balvi.cuba.sessioninfo.web.session.UserSessionTableColumnNames
import spock.lang.Specification

class SessionDataLoaderCreatePermissionsSpec extends Specification {

    SessionDataLoader sut
    Messages messages
    UserSessionSource userSessionSource
    UserSession userSession
    MessageTools messageTools


    def setup() {

        messages = Mock(Messages)
        userSessionSource = Mock(UserSessionSource)
        sut = new SessionDataLoader(
                messages: messages,
                userSessionSource: userSessionSource,
        )

        userSession = Mock(UserSession)
        userSessionSource.getUserSession() >> userSession

        and:
        messageTools = Mock(MessageTools)
        messages.getTools() >> messageTools

    }

    def "createPermissions determines for all permission types the permissions from the user session"() {

        when:
        sut.createPermissions()

        then:
        1 * userSession.getPermissionsByType(PermissionType.ENTITY_OP)
        1 * userSession.getPermissionsByType(PermissionType.ENTITY_ATTR)
        1 * userSession.getPermissionsByType(PermissionType.SCREEN)
        1 * userSession.getPermissionsByType(PermissionType.SPECIFIC)
        1 * userSession.getPermissionsByType(PermissionType.UI)
    }

    def "createPermissions creates a KV-Entity for every permission"() {

        given:
        userSession.getPermissionsByType(PermissionType.ENTITY_OP) >> [
                'permission1': 1,
                'permission2': 0,
        ]

        when:
        def result = sut.createPermissions()

        then:
        result.size() == 2
    }

    def "createPermissions copies the permissionType, the name and the allowed value in the KV-entity"() {

        given:
        userSession.getPermissionsByType(PermissionType.ENTITY_OP) >> [
                'permission1': 1
        ]

        and:
        messages.getMessage(PermissionType.ENTITY_OP) >> "entity Op caption"

        when:
        def result = sut.createPermissions()

        then:
        result[0].getValue(UserSessionTableColumnNames.PERMISSION_TABLE_COLUMN_PERMISSION_TYPE) == 'entity Op caption'

        result[0].getValue(UserSessionTableColumnNames.PERMISSION_TABLE_COLUMN_PERMISSION_NAME) == 'permission1'
        result[0].getValue(UserSessionTableColumnNames.PERMISSION_TABLE_COLUMN_PERMISSION_ALLOWED) == '1'
    }

}
