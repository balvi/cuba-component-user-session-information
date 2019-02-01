package de.balvi.cuba.sessioninfo.gui.screens

import com.haulmont.cuba.gui.Screens
import com.haulmont.cuba.gui.app.security.session.browse.SessionBrowser
import com.haulmont.cuba.gui.components.Action
import com.haulmont.cuba.gui.screen.OpenMode
import com.haulmont.cuba.gui.screen.Subscribe
import com.haulmont.cuba.security.global.UserSession
import de.balvi.cuba.sessioninfo.gui.session.UserSessionInformation

import javax.inject.Inject

class ExtSessionBrowser extends SessionBrowser {
    @Inject
    Screens screens

    @Subscribe('sessionsTable.sessionInfo')
    @SuppressWarnings('UnusedMethodParameter')
    void sessionInfo(Action.ActionPerformedEvent event) {
        UserSession userSession = uss.getUserSession(sessionsTable.singleSelected.id)
        UserSessionInformation userSessionInformation = screens.create(UserSessionInformation, OpenMode.DIALOG)
        userSessionInformation.userSessionToDisplay = userSession
        screens.show(userSessionInformation)
    }
}