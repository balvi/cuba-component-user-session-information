package de.balvi.cuba.sessioninfo.gui.screens

import com.haulmont.cuba.gui.WindowManager
import com.haulmont.cuba.gui.app.security.session.browse.SessionBrowser
import com.haulmont.cuba.gui.components.Action

import javax.inject.Named

class ExtSessionBrowser extends SessionBrowser {

    @Named('sessionsTable.sessionInfo')
    protected Action sessionInfoAction


    @Override
    void init(Map<String, Object> params) {
        super.init(params)

        addAction(sessionInfoAction)
    }

    void sessionInfo() {
        def userSession = uss.getUserSession(sessionsTable.singleSelected.id)
        openWindow('sessioninfo$userSessionInformation', WindowManager.OpenType.DIALOG, [userSessionToDisplay: userSession])
    }
}