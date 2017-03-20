package de.balvi.cuba.sessioninfo.web.session

import com.haulmont.chile.core.model.MetaClass
import com.haulmont.chile.core.model.MetaProperty
import com.haulmont.cuba.gui.components.BoxLayout
import com.haulmont.cuba.gui.components.Frame
import com.haulmont.cuba.gui.components.Table
import com.haulmont.cuba.gui.data.impl.ValueCollectionDatasourceImpl
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory
import spock.lang.Specification

class SessionTableCreatorSpec extends Specification {


    SessionTableCreator sut
    ComponentsFactory componentsFactory
    ValueCollectionDatasourceImpl tableDs
    MetaClass datasourceMetaClass

    Table neueTable
    BoxLayout tableBox
    Frame frame

    def setup() {

        componentsFactory = Mock(ComponentsFactory)
        sut = new SessionTableCreator(
                componentsFactory: componentsFactory
        )

        tableDs = Mock(ValueCollectionDatasourceImpl)

        datasourceMetaClass = Mock(MetaClass)
        tableDs.getMetaClass() >> datasourceMetaClass


        neueTable = Mock(Table)
        componentsFactory.createComponent(Table) >> neueTable
        tableBox = Mock(BoxLayout)
        frame = Mock(Frame)
    }

    def "createTable returns a new table through the component factory"() {

        when:
        def result = sut.createTable(null, tableBox, tableDs, frame, [:])

        then:
        result == neueTable
    }

    def "createTable removes the table from the box if it is already there"() {

        given:
        def alteTable = Mock(Table)

        when:
        sut.createTable(alteTable, tableBox, tableDs, frame, [:])

        then:
        tableBox.remove(alteTable)
    }

    def "createTable adds the table in the box"() {

        when:
        sut.createTable(null, tableBox, tableDs, frame, [:])

        then:
        tableBox.add(neueTable)
    }

    def "createTable sets the datasource of the table"() {

        when:
        sut.createTable(null, tableBox, tableDs, frame, [:])

        then:
        1 * neueTable.setDatasource(tableDs)
    }

    def "createTable sets the frame of the table"() {

        when:
        sut.createTable(null, tableBox, tableDs, frame, [:])

        then:
        1 * neueTable.setFrame(frame)
    }


    def "createTable deactivates the normal table session for the new table"() {

        when:
        sut.createTable(null, tableBox, tableDs, frame, [:])

        then:
        1 * neueTable.setSettingsEnabled(false)
        1 * neueTable.setColumnReorderingAllowed(false)
        1 * neueTable.setContextMenuEnabled(false)
        1 * neueTable.setSortable(false)
        1 * neueTable.setColumnControlVisible(false)
    }



    def "createTable sets the layout information for the table"() {

        when:
        sut.createTable(null, tableBox, tableDs, frame, [:])

        then:
        1 * neueTable.setSizeFull()
    }

    def "createTable adds for every metaProperty of the datasource a column in the table"() {

        given: "there are two meta properties of the datasouce"
        def column1Property = Mock(MetaProperty)
        column1Property.getName() >> "column1"

        def column2Property = Mock(MetaProperty)
        column2Property.getName() >> "column2"

        datasourceMetaClass.getProperties() >> [column1Property, column2Property]

        when:
        sut.createTable(null, tableBox, tableDs, frame, [:])

        then:
        1 * neueTable.addColumn({ Table.Column column ->
            column.caption == "column1"
        })
        1 * neueTable.addColumn({ Table.Column column ->
            column.caption == "column2"
        })
    }




}
