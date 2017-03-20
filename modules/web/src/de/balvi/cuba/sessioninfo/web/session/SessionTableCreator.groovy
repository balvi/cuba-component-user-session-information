package de.balvi.cuba.sessioninfo.web.session

import com.haulmont.chile.core.model.MetaClass
import com.haulmont.cuba.core.global.Messages
import com.haulmont.cuba.gui.components.BoxLayout
import com.haulmont.cuba.gui.components.Frame
import com.haulmont.cuba.gui.components.Table
import com.haulmont.cuba.gui.data.impl.ValueCollectionDatasourceImpl
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory
import org.springframework.stereotype.Component

import javax.inject.Inject

@Component
class SessionTableCreator {

    @Inject
    ComponentsFactory componentsFactory

    @Inject
    Messages messages

    Table createTable(Table table, BoxLayout tableBox, ValueCollectionDatasourceImpl tableDs, Frame frame, Map<String, String> columnCaptions) {
        if (table) {
            tableBox.remove(table)
        }

        def newTable = componentsFactory.createComponent(Table)

        configureTableEnvironment(tableDs, newTable, frame, tableBox)
        configureTableLayout(tableDs, newTable,columnCaptions)
        configureTableSettings(newTable)

        newTable

    }

    protected void configureTableEnvironment(ValueCollectionDatasourceImpl tableDs, Table table, Frame frame, BoxLayout tableBox) {
        table.datasource = tableDs
        table.frame = frame
        tableBox.add(table)
    }

    protected void configureTableLayout(ValueCollectionDatasourceImpl tableDs, Table table, Map<String, String> columnCaptions) {
        addTableColumns(tableDs, table, columnCaptions)
        table.setSizeFull()
    }

    protected void configureTableSettings(Table table) {
        table.settingsEnabled = false
        table.columnReorderingAllowed = false
        table.contextMenuEnabled = false
        table.sortable = false
        table.columnControlVisible = false
    }

    private void addTableColumns(ValueCollectionDatasourceImpl tableDs, Table table, Map<String, String> columnCaptions) {
        MetaClass meta = tableDs.metaClass

        meta.properties.each {
            Table.Column column = new Table.Column(meta.getPropertyPath(it.name))
            column.caption = columnCaptions[it.name] ?: it.name
            table.addColumn(column)
        }
    }

}
