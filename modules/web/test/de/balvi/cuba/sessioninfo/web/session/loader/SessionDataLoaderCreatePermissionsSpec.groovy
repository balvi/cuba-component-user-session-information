package de.balvi.cuba.sessioninfo.web.session.loader

import com.haulmont.cuba.core.global.MessageTools
import com.haulmont.cuba.core.global.Messages
import com.haulmont.cuba.core.global.UserSessionSource
import com.haulmont.cuba.security.entity.PermissionType
import com.haulmont.cuba.security.global.UserSession
import de.balvi.cuba.sessioninfo.web.session.SessionDataLoader
import de.balvi.cuba.sessioninfo.web.session.UserSessionTableColumnNames
import spock.lang.Specification
import spock.lang.Unroll

class SessionDataLoaderCreatePermissionsSpec extends Specification {

    SessionDataLoader sut
    Messages messages
    UserSession userSession
    MessageTools messageTools


    def setup() {

        messages = Mock(Messages)
        sut = new SessionDataLoader(
                messages: messages,
        )

        userSession = Mock(UserSession)

        and:
        messageTools = Mock(MessageTools)
        messages.getTools() >> messageTools

    }

    def "createPermissions determines for all permission types the permissions from the user session"() {

        when:
        sut.createPermissions(userSession)

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
        def result = sut.createPermissions(userSession)

        then:
        result.size() == 2
    }

    def "createPermissions copies the permissionType and the name in the KV-entity"() {

        given:
        userSession.getPermissionsByType(PermissionType.ENTITY_OP) >> [
                'permission1': 1
        ]

        and:
        messages.getMessage(PermissionType.ENTITY_OP) >> "entity Op caption"

        when:
        def result = sut.createPermissions(userSession)

        then:
        result[0].getValue(UserSessionTableColumnNames.PERMISSION_TABLE_COLUMN_PERMISSION_TYPE) == 'entity Op caption'
        result[0].getValue(UserSessionTableColumnNames.PERMISSION_TABLE_COLUMN_PERMISSION_NAME) == 'permission1'
    }

    def "createPermissions renders true / false for the permission value in case of a permisstionType = ENTITY_OP"() {

        given:
        userSession.getPermissionsByType(PermissionType.ENTITY_OP) >> [
                'permission1': 1
        ]

        when:
        def result = sut.createPermissions(userSession)

        then:
        result[0].getValue(UserSessionTableColumnNames.PERMISSION_TABLE_COLUMN_PERMISSION_ALLOWED) == 'true'
    }


    @Unroll
    def "createPermissions renders '#renderedValue' for the permission value: '#permissionValue' in case of a permisstionType = ENTITY_OP"() {

        given:
        userSession.getPermissionsByType(PermissionType.ENTITY_ATTR) >> [
                'permission1': permissionValue
        ]

        when:
        def result = sut.createPermissions(userSession)

        then:
        result[0].getValue(UserSessionTableColumnNames.PERMISSION_TABLE_COLUMN_PERMISSION_ALLOWED) == renderedValue

        where:
        permissionValue || renderedValue
        1               || 'read-only'
        2               || 'modify'
        0               || 'hide'
    }

}
