package de.balvi.cuba.sessioninfo.web.session

import com.haulmont.chile.core.model.Session
import com.haulmont.cuba.core.entity.KeyValueEntity
import com.haulmont.cuba.core.global.MessageTools
import com.haulmont.cuba.core.global.Messages
import com.haulmont.cuba.core.global.Metadata
import com.haulmont.cuba.gui.components.BoxLayout
import com.haulmont.cuba.gui.components.Frame
import com.haulmont.cuba.gui.data.impl.ValueCollectionDatasourceImpl
import spock.lang.Specification

class UserSessionInformationSpec extends Specification {


    UserSessionInformationMock sut
    SessionTableCreator sessionTableCreator
    SessionDataLoader sessionDataLoader
    Frame frame
    Metadata metadata
    private Messages messages
    private MessageTools messageTools

    def setup() {

        sessionTableCreator = Mock(SessionTableCreator)
        sessionDataLoader = Mock(SessionDataLoader)
        metadata = Mock(Metadata)
        metadata.getSession() >> Mock(Session)
        messages = Mock(Messages)
        messageTools = Mock(MessageTools)
        messages.getTools() >> messageTools
        sut = new UserSessionInformationMock(
                sessionTableCreator: sessionTableCreator,
                sessionDataLoader: sessionDataLoader,
                datasource: Mock(ValueCollectionDatasourceImpl),
                metadata: metadata,
                messages: messages
        )

        frame = Mock(Frame)
        frame.getMessagesPack() >> 'de.balvi.cuba.sessioninfo.web.session'
        sut.setWrappedFrame(frame)

    }

    def "init creates a table for the user information"() {
        given:
        sut.userTableBox = Mock(BoxLayout)
        and:
        def userDs = Mock(ValueCollectionDatasourceImpl)
        sut.datasource = userDs
        when:
        sut.init()
        then:
        1 * sessionTableCreator.createTable(_,sut.userTableBox,userDs,_, _)
    }


    def "init adds the columns 'attribute' and 'value' to the user table"() {
        given:
        def userDs = Mock(ValueCollectionDatasourceImpl)
        sut.datasource = userDs
        when:
        sut.init()
        then:
        (1.._) * userDs.addProperty(UserSessionTableColumnNames.SESSION_TABLE_COLUMN_NAME)
        (1.._) * userDs.addProperty(UserSessionTableColumnNames.SESSION_TABLE_COLUMN_VALUE)
    }


    def "init adds every KV-Entity from the sessionDataLoader to the user datasouce"() {
        given:
        def userDs = Mock(ValueCollectionDatasourceImpl)
        sut.datasource = userDs
        and:
        sessionDataLoader.createUserInformation() >> [Mock(KeyValueEntity), Mock(KeyValueEntity)]
        when:
        sut.init()
        then:
        2 * userDs.includeItem(_ as KeyValueEntity)
    }

    def "init creates a table for session attributes"() {
        given:
        sut.sessionTableBox = Mock(BoxLayout)
        and:
        def sessionDs = Mock(ValueCollectionDatasourceImpl)
        sut.datasource = sessionDs
        when:
        sut.init()
        then:
        1 * sessionTableCreator.createTable(_,sut.sessionTableBox,sessionDs,_, _)
    }

    def "init adds the columns 'attribute' and 'value' to the session attribute table"() {
        given:
        def sessionDs = Mock(ValueCollectionDatasourceImpl)
        sut.datasource = sessionDs
        when:
        sut.init()
        then:
        (1.._) * sessionDs.addProperty(UserSessionTableColumnNames.SESSION_TABLE_COLUMN_NAME)
        (1.._) * sessionDs.addProperty(UserSessionTableColumnNames.SESSION_TABLE_COLUMN_VALUE)
    }


    def "init adds every KV-Entity from the sessionDataLoader to the session attribute datasouce"() {
        given:
        def sessionDs = Mock(ValueCollectionDatasourceImpl)
        sut.datasource = sessionDs
        and:
        sessionDataLoader.createSessionAttribute() >> [Mock(KeyValueEntity), Mock(KeyValueEntity)]
        when:
        sut.init()
        then:
        2 * sessionDs.includeItem(_ as KeyValueEntity)
    }



    def "init creates a table for permissions"() {
        given:
        sut.permissionsTableBox = Mock(BoxLayout)
        and:
        def permissionsDs = Mock(ValueCollectionDatasourceImpl)
        sut.datasource = permissionsDs
        when:
        sut.init()
        then:
        1 * sessionTableCreator.createTable(_,sut.permissionsTableBox,permissionsDs,_, _)
    }


    def "init adds the columns 'permissionType', 'allowed' and 'name' to the permissions table"() {
        given:
        def permissionDs = Mock(ValueCollectionDatasourceImpl)
        sut.datasource = permissionDs
        when:
        sut.init()
        then:
        1 * permissionDs.addProperty(UserSessionTableColumnNames.PERMISSION_TABLE_COLUMN_PERMISSION_TYPE)
        1 * permissionDs.addProperty(UserSessionTableColumnNames.PERMISSION_TABLE_COLUMN_PERMISSION_ALLOWED)
        1 * permissionDs.addProperty(UserSessionTableColumnNames.PERMISSION_TABLE_COLUMN_PERMISSION_NAME)
    }


    def "init adds every KV-Entity from the sessionDataLoader to the permissions datasouce"() {
        given:
        def permissionDs = Mock(ValueCollectionDatasourceImpl)
        sut.datasource = permissionDs
        and:
        sessionDataLoader.createPermissions() >> [Mock(KeyValueEntity), Mock(KeyValueEntity)]
        when:
        sut.init()
        then:
        2 * permissionDs.includeItem(_ as KeyValueEntity)
    }


    def "init creates a table for constraints"() {
        def constraintsTableBox = Mock(BoxLayout)
        given:
        sut.constraintsTableBox = constraintsTableBox
        and:
        def constraintsDs = Mock(ValueCollectionDatasourceImpl)
        sut.datasource = constraintsDs
        when:
        sut.init()
        then:
        1 * sessionTableCreator.createTable(_,sut.constraintsTableBox,constraintsDs,_, _)
    }

    def "init adds the all columns of a Constraint to the constraint datasource"() {
        given:
        def constraintsDs = Mock(ValueCollectionDatasourceImpl)
        sut.datasource = constraintsDs

        and: 'die Keys der Datasource sind die Namen der Attribute'
        messageTools.getPropertyCaption(_,_) >> {k,v -> v}

        when:
        sut.init()

        then:
        1 * constraintsDs.addProperty('entityName')
        1 * constraintsDs.addProperty('checkType')
        1 * constraintsDs.addProperty('operationType')
        1 * constraintsDs.addProperty('code')
        1 * constraintsDs.addProperty('joinClause')
        1 * constraintsDs.addProperty('whereClause')
        1 * constraintsDs.addProperty('groovyScript')
    }


    def "init adds every KV-Entity from the sessionDataLoader to the constraints datasouce"() {
        given:
        def permissionDs = Mock(ValueCollectionDatasourceImpl)
        sut.datasource = permissionDs
        and:
        sessionDataLoader.createConstraints() >> [Mock(KeyValueEntity), Mock(KeyValueEntity)]
        when:
        sut.init()
        then:
        2 * permissionDs.includeItem(_ as KeyValueEntity)
    }


}

class UserSessionInformationMock extends UserSessionInformation {

    ValueCollectionDatasourceImpl datasource
    @Override
    protected ValueCollectionDatasourceImpl createValueCollectionDs() {
        datasource
    }
}
