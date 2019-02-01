package de.balvi.cuba.sessioninfo.gui.session

import com.haulmont.chile.core.model.Instance
import com.haulmont.cuba.core.entity.Entity
import com.haulmont.cuba.core.entity.KeyValueEntity
import com.haulmont.cuba.core.global.Messages
import com.haulmont.cuba.core.global.Metadata
import com.haulmont.cuba.core.global.MetadataTools
import com.haulmont.cuba.security.entity.PermissionType
import com.haulmont.cuba.security.global.ConstraintData
import com.haulmont.cuba.security.global.UserSession
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

import javax.inject.Inject

@CompileStatic
@Component
class SessionDataLoader {
    @Inject Metadata metadata
    @Inject MetadataTools metadataTools
    @Inject Messages messages
    @Inject MetadataHelper metadataHelper

    List<KeyValueEntity> createUserInformation(UserSession userSession) {
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

    List<KeyValueEntity> createSessionAttribute(UserSession userSession) {
        def entities = []
        userSession.attributeNames.each {
            def value = userSession.getAttribute(it)
            String valueString = getStringValue(value)
            entities << createKeyValueEntity(it, valueString)
        }
        entities
    }

    List<KeyValueEntity> createConstraints(UserSession userSession) {
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
                'code'         : constraintData.code,
                'joinClause'   : constraintData.join,
                'groovyScript' : constraintData.groovyScript,
                'checkType'    : messages.getMessage(constraintData.checkType),
                'whereClause'  : constraintData.whereClause
        ] as Map<String, Object>)
    }

    List<KeyValueEntity> createPermissions(UserSession userSession) {
        def entities = []
        for (PermissionType permissionType : PermissionType.values()) {
            def permissions = userSession.getPermissionsByType(permissionType)

            permissions?.each { permissionName, permissionValue ->
                entities << createKeyValueEntity([
                        (UserSessionTableColumnNames.PERMISSION_TABLE_COLUMN_PERMISSION_TYPE)   : messages.getMessage(permissionType),
                        (UserSessionTableColumnNames.PERMISSION_TABLE_COLUMN_PERMISSION_NAME)   : permissionName,
                        (UserSessionTableColumnNames.PERMISSION_TABLE_COLUMN_PERMISSION_ALLOWED): getPermissionValueString(permissionType, permissionValue)
                ] as Map<String, Object>)
            }
        }
        entities
    }

    String getPermissionValueString(PermissionType permissionType, int permissionValue) {
        if (permissionType == PermissionType.ENTITY_ATTR) {
            switch (permissionValue) {
                case 1: 'read-only'; break
                case 2: 'modify'; break
                default: 'hide'; break
            }
        }
        else {
            (permissionValue != 0).toString()
        }
    }

    private KeyValueEntity createKeyValueEntity(String attribut, Object wert) {
        createKeyValueEntity([
                (UserSessionTableColumnNames.SESSION_TABLE_COLUMN_NAME) : attribut,
                (UserSessionTableColumnNames.SESSION_TABLE_COLUMN_VALUE): wert
        ])
    }

    private KeyValueEntity createKeyValueEntity(Map<String, Object> content) {
        def entity = new KeyValueEntity()
        content.each { k, v ->
            entity.setValue(k, getStringValue(v))
        }
        entity
    }

    private String getStringValue(def value) {
        if (!value) { return '' }
        isEntity(value) ? metadataTools.getInstanceName((Instance) value) : value.toString()
    }

    protected boolean isEntity(Object value) {
        Entity.isAssignableFrom(value.class)
    }
}