package de.balvi.cuba.sessioninfo.web.session

import com.haulmont.chile.core.model.MetaClass
import com.haulmont.cuba.core.global.MessageTools
import com.haulmont.cuba.core.global.Messages
import com.haulmont.cuba.core.global.Metadata
import com.haulmont.cuba.core.global.MetadataTools
import org.springframework.stereotype.Component

import javax.inject.Inject

@Component
class MetadataHelper {

    @Inject
    Metadata metadata


    @Inject
    Messages messages


    Map<String, String> getEntityCaptionMap() {
        metadataTools.allPersistentMetaClasses?.collectEntries { [(getEntityCaption(it)): it.name] }
    }

    protected String getEntityCaption(MetaClass metaClass) {
        "${messageTools.getEntityCaption(metaClass)} (${metaClass.name})"
    }

    protected MetadataTools getMetadataTools() {
        metadata.tools
    }

    protected MessageTools getMessageTools() {
        messages.tools
    }
}