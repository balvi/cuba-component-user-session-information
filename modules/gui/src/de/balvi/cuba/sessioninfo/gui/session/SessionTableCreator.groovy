package de.balvi.cuba.sessioninfo.gui.session

import com.haulmont.chile.core.model.MetaClass
import com.haulmont.cuba.gui.UiComponents
import com.haulmont.cuba.gui.components.BoxLayout
import com.haulmont.cuba.gui.components.Frame
import com.haulmont.cuba.gui.components.Table
import com.haulmont.cuba.gui.data.impl.ValueCollectionDatasourceImpl
import org.springframework.stereotype.Component

import javax.inject.Inject

@Component
class SessionTableCreator {
    @Inject
    UiComponents uiComponents

    Table createTable(BoxLayout tableBox, ValueCollectionDatasourceImpl tableDs, Frame frame, Map<String, String> columnCaptions) {
        Table newTable = uiComponents.create(Table)

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
        table.with {
            settingsEnabled = false
            columnReorderingAllowed = false
            contextMenuEnabled = false
            sortable = false
            columnControlVisible = false
        }
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
