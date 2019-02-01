package de.balvi.cuba.sessioninfo.gui.session

import com.haulmont.chile.core.model.MetaClass
import com.haulmont.cuba.core.entity.KeyValueEntity
import com.haulmont.cuba.core.global.Metadata
import com.haulmont.cuba.core.global.MetadataTools
import com.haulmont.cuba.gui.components.AbstractWindow
import com.haulmont.cuba.gui.components.BoxLayout
import com.haulmont.cuba.gui.components.Table
import com.haulmont.cuba.gui.data.DsBuilder
import com.haulmont.cuba.gui.data.impl.ValueCollectionDatasourceImpl
import com.haulmont.cuba.gui.screen.UiController
import com.haulmont.cuba.gui.screen.UiDescriptor
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory
import com.haulmont.cuba.security.global.UserSession

import javax.inject.Inject

@UiController('sessioninfo$userSessionInformation')
@UiDescriptor('user-session-information.xml')
class UserSessionInformation extends AbstractWindow {
    @Inject ComponentsFactory componentsFactory
    @Inject Metadata metadata
    @Inject MetadataTools metadataTools
    @Inject SessionTableCreator sessionTableCreator
    @Inject SessionDataLoader sessionDataLoader
    @Inject UserSession userSession

    @Inject BoxLayout userTableBox
    @Inject BoxLayout sessionTableBox
    @Inject BoxLayout constraintsTableBox
    @Inject BoxLayout permissionsTableBox

    UserSession userSessionToDisplay

    Table userTable
    Table sessionTable
    Table constraintsTable
    Table permissionsTable

    @Override
    void init(Map<String, Object> params) {
        userSessionToDisplay = userSessionToDisplay ?: userSession

        initCaption()
        def keyValueTableColumns = [
                (UserSessionTableColumnNames.SESSION_TABLE_COLUMN_NAME) : getMessage('tables.columns.attribute'),
                (UserSessionTableColumnNames.SESSION_TABLE_COLUMN_VALUE): getMessage('tables.columns.value')
        ]
        initUserTable(keyValueTableColumns)
        initSessionTable(keyValueTableColumns)
        initConstraintTable()
        initPermissionsTable()
    }

    protected initCaption() {
        String userSessionName = metadataTools.getInstanceName(userSessionToDisplay?.currentOrSubstitutedUser)
        caption = formatMessage('caption', userSessionName)
    }

    protected Table initUserTable(Map<String, String> tableColumns) {
        sessionTableCreator.createTable(
                userTable,
                userTableBox,
                createDatasource(sessionDataLoader.createUserInformation(userSessionToDisplay), tableColumns),
                frame,
                tableColumns
        )
    }

    protected Table initSessionTable(Map<String, String> tableColumns) {
        sessionTableCreator.createTable(
                sessionTable,
                sessionTableBox,
                createDatasource(sessionDataLoader.createSessionAttribute(userSessionToDisplay), tableColumns),
                frame,
                tableColumns
        )
    }

    protected Table initPermissionsTable() {
        def permissionTableColumns = [
                (UserSessionTableColumnNames.PERMISSION_TABLE_COLUMN_PERMISSION_NAME)   : getMessage('tables.permissions.columns.name'),
                (UserSessionTableColumnNames.PERMISSION_TABLE_COLUMN_PERMISSION_ALLOWED): getMessage('tables.permissions.columns.allowed'),
                (UserSessionTableColumnNames.PERMISSION_TABLE_COLUMN_PERMISSION_TYPE)   : getMessage('tables.permissions.columns.permissionType')
        ]
        sessionTableCreator.createTable(
                permissionsTable,
                permissionsTableBox,
                createDatasource(sessionDataLoader.createPermissions(userSessionToDisplay), permissionTableColumns),
                frame,
                permissionTableColumns
        )
    }

    protected Table initConstraintTable() {

        def constraintMetaClass = metadata.session.getClass('sec$Constraint')

        def constraintColumns = [
                entityName: getPropertyCaption(constraintMetaClass, 'entityName'),
                checkType: getPropertyCaption(constraintMetaClass, 'checkType'),
                operationType: getPropertyCaption(constraintMetaClass, 'operationType'),
                code: getPropertyCaption(constraintMetaClass, 'code'),
                joinClause: getPropertyCaption(constraintMetaClass, 'joinClause'),
                whereClause: getPropertyCaption(constraintMetaClass, 'whereClause'),
                groovyScript: getPropertyCaption(constraintMetaClass, 'groovyScript')
        ]
        sessionTableCreator.createTable(constraintsTable, constraintsTableBox, createDatasource(sessionDataLoader.createConstraints(userSessionToDisplay), constraintColumns), frame, constraintColumns)
    }

    protected String getPropertyCaption(MetaClass constraintMetaClass, String propertyName) {
        messages.tools.getPropertyCaption(constraintMetaClass, propertyName)
    }

    private ValueCollectionDatasourceImpl createDatasource(List<KeyValueEntity> result, Map<String, String> columns) {
        ValueCollectionDatasourceImpl valueCollectionDs = createValueCollectionDs()
        result.each { valueCollectionDs.includeItem(it) }

        columns.keySet().each {
            valueCollectionDs.addProperty(it)
        }
        valueCollectionDs
    }

    protected ValueCollectionDatasourceImpl createValueCollectionDs() {
        DsBuilder.create(dsContext).reset().setAllowCommit(false)
                .buildValuesCollectionDatasource()
    }
}