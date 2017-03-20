package de.balvi.cuba.sessioninfo.web.session

import com.haulmont.chile.core.model.Instance
import com.haulmont.cuba.core.entity.Entity
import com.haulmont.cuba.core.entity.KeyValueEntity
import com.haulmont.cuba.core.global.*
import com.haulmont.cuba.security.entity.PermissionType
import com.haulmont.cuba.security.global.ConstraintData
import com.haulmont.cuba.security.global.UserSession
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

import javax.inject.Inject

@CompileStatic
@Component
class SessionDataLoader {

    @Inject
    UserSessionSource userSessionSource

    @Inject
    Metadata metadata

    @Inject
    Messages messages

    @Inject
    MetadataHelper metadataHelper


    UserSession getUserSession() {
        userSessionSource.userSession
    }

    List<KeyValueEntity> createUserInformation() {
        [
                createKeyValueEntity(getMessage('tables.userinformation.user'), userSession.user),
                createKeyValueEntity(getMessage('tables.userinformation.substitutedUser'), userSession.substitutedUser),
                createKeyValueEntity(getMessage('tables.userinformation.group'), userSession.user.group),
                createKeyValueEntity(getMessage('tables.userinformation.roles'), userSession.roles.join(', '))
        ]
    }

    protected String getMessage(String msgKey) {
        messages.getMessage(getClass(), msgKey)
    }

    List<KeyValueEntity> createSessionAttribute() {
        def entities = []
        userSession.attributeNames.each {
            def entity = new KeyValueEntity()
            entity.setValue(UserSessionTableColumnNames.SESSION_TABLE_COLUMN_NAME, it)

            def wert = userSession.getAttribute(it)
            if (Entity.isAssignableFrom(wert.class)) {
                entity.setValue(UserSessionTableColumnNames.SESSION_TABLE_COLUMN_VALUE, ((Instance) wert).instanceName)
            } else {
                entity.setValue(UserSessionTableColumnNames.SESSION_TABLE_COLUMN_VALUE, wert.toString())
            }
            entities << entity
        }
        entities
    }

    List<KeyValueEntity> createConstraints() {
        def entities = []
        metadataHelper.entityCaptionMap.each { entityCaption, entityName ->
            def alleConstraints = userSession.getConstraints(entityName)
            alleConstraints?.each { constraintData ->
                entities << createConstraint(constraintData, entityCaption)
            }
        }
        entities
    }

    protected KeyValueEntity createConstraint(ConstraintData constraintData, String entityCaption) {
        createKeyValueEntity([
                'entityName'   : entityCaption,
                'operationType': messages.getMessage(constraintData.operationType),
                'code'        : constraintData.code,
                'joinClause'   : constraintData.join,
                'groovyScript' : constraintData.groovyScript,
                'checkType'    : messages.getMessage(constraintData.checkType),
                'whereClause'  : constraintData.whereClause
        ] as Map<String, Object>)
    }

    List<KeyValueEntity> createPermissions() {
        def entities = []
        PermissionType.values().each { PermissionType permissionType ->
            def permissions = userSession.getPermissionsByType(permissionType)

            permissions?.each { permissionName, permissionValue ->
                entities << createKeyValueEntity([
                        (UserSessionTableColumnNames.PERMISSION_TABLE_COLUMN_PERMISSION_TYPE)   : messages.getMessage(permissionType),
                        (UserSessionTableColumnNames.PERMISSION_TABLE_COLUMN_PERMISSION_NAME)   : permissionName,
                        (UserSessionTableColumnNames.PERMISSION_TABLE_COLUMN_PERMISSION_ALLOWED): permissionValue.toString()
                ] as Map<String, Object>)
            }
        }
        entities
    }


    private KeyValueEntity createKeyValueEntity(String attribut, Object wert) {
        createKeyValueEntity([
                (UserSessionTableColumnNames.SESSION_TABLE_COLUMN_NAME) : attribut,
                (UserSessionTableColumnNames.SESSION_TABLE_COLUMN_VALUE): wert
        ])
    }

    private KeyValueEntity createKeyValueEntity(Map<String, Object> content) {
        def result = new KeyValueEntity()
        content.each { k, v ->
            def wert = ''
            if (v) {
                if (isEntity(v)) {
                    wert = ((Instance) v).instanceName
                } else {
                    wert = v.toString()
                }
            }

            result.setValue(k, wert)

        }
        result
    }

    protected boolean isEntity(Object value) {
        Entity.isAssignableFrom(value.class)
    }
}