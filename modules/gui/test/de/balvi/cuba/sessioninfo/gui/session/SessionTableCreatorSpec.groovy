package de.balvi.cuba.sessioninfo.gui.session

import com.haulmont.chile.core.model.MetaClass
import com.haulmont.chile.core.model.MetaProperty
import com.haulmont.cuba.gui.UiComponents
import com.haulmont.cuba.gui.components.BoxLayout
import com.haulmont.cuba.gui.components.Frame
import com.haulmont.cuba.gui.components.Table
import com.haulmont.cuba.gui.data.impl.ValueCollectionDatasourceImpl
import spock.lang.Specification

class SessionTableCreatorSpec extends Specification {
    SessionTableCreator sut

    ValueCollectionDatasourceImpl tableDs = Mock(ValueCollectionDatasourceImpl)
    MetaClass datasourceMetaClass = Mock(MetaClass)
    UiComponents uiComponents = Mock(UiComponents)

    Table newTable = Mock(Table)
    BoxLayout tableBox = Mock(BoxLayout)
    Frame frame = Mock(Frame)

    def setup() {
        sut = new SessionTableCreator(
                uiComponents: uiComponents
        )
        tableDs.getMetaClass() >> datasourceMetaClass
        uiComponents.create(Table) >> newTable
    }

    def "createTable returns a new table through the component factory"() {
        when:
        def result = sut.createTable(tableBox, tableDs, frame, [:])

        then:
        result == newTable
    }

    def "createTable removes the table from the box if it is already there"() {
        given:
        def alteTable = Mock(Table)

        when:
        sut.createTable(tableBox, tableDs, frame, [:])

        then:
        tableBox.remove(alteTable)
    }

    def "createTable adds the table in the box"() {
        when:
        sut.createTable(tableBox, tableDs, frame, [:])

        then:
        tableBox.add(newTable)
    }

    def "createTable sets the datasource of the table"() {
        when:
        sut.createTable(tableBox, tableDs, frame, [:])

        then:
        1 * newTable.setDatasource(tableDs)
    }

    def "createTable sets the frame of the table"() {
        when:
        sut.createTable(tableBox, tableDs, frame, [:])

        then:
        1 * newTable.setFrame(frame)
    }


    def "createTable deactivates the normal table session for the new table"() {
        when:
        sut.createTable(tableBox, tableDs, frame, [:])

        then:
        1 * newTable.setSettingsEnabled(false)
        1 * newTable.setColumnReorderingAllowed(false)
        1 * newTable.setContextMenuEnabled(false)
        1 * newTable.setSortable(false)
        1 * newTable.setColumnControlVisible(false)
    }



    def "createTable sets the layout information for the table"() {
        when:
        sut.createTable(tableBox, tableDs, frame, [:])

        then:
        1 * newTable.setSizeFull()
    }

    def "createTable adds for every metaProperty of the datasource a column in the table"() {
        given: "there are two meta properties of the datasouce"
        def column1Property = Mock(MetaProperty)
        column1Property.getName() >> "column1"

        def column2Property = Mock(MetaProperty)
        column2Property.getName() >> "column2"

        datasourceMetaClass.getProperties() >> [column1Property, column2Property]

        when:
        sut.createTable(tableBox, tableDs, frame, [:])

        then:
        1 * newTable.addColumn({ Table.Column column ->
            column.caption == "column1"
        })
        1 * newTable.addColumn({ Table.Column column ->
            column.caption == "column2"
        })
    }
}
