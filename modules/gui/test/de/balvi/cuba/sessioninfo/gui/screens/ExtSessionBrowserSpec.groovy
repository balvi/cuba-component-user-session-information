package de.balvi.cuba.sessioninfo.gui.screens

import com.haulmont.cuba.gui.Screens
import com.haulmont.cuba.gui.components.Action
import com.haulmont.cuba.gui.components.Table
import com.haulmont.cuba.gui.screen.OpenMode
import com.haulmont.cuba.security.app.UserSessionService
import com.haulmont.cuba.security.entity.UserSessionEntity
import com.haulmont.cuba.security.global.UserSession
import de.balvi.cuba.sessioninfo.gui.session.UserSessionInformation
import spock.lang.Specification

class ExtSessionBrowserSpec extends Specification {
    ExtSessionBrowser sut

    UserSessionService userSessionService = Mock(UserSessionService)
    Table<UserSessionEntity> sessionsTable = Mock(Table)
    UserSessionInformation userSessionInformation = Mock(UserSessionInformation)

    Screens screens = Mock(Screens)

    def setup() {
        sut = new ExtSessionBrowser(
                uss : userSessionService,
                sessionsTable: sessionsTable,
                screens: screens
        )
    }

    def 'sessionInfo opens a UserSessionInformation screen for choosen session, if one is choosen'() {
        given:
        UUID sessionUUID = UUID.randomUUID()
        Action.ActionPerformedEvent event = Mock(Action.ActionPerformedEvent)
        UserSessionEntity userSessionEntity = Mock(UserSessionEntity)
        userSessionEntity.getId() >> sessionUUID
        UserSession userSession = Mock(UserSession)

        sessionsTable.getSingleSelected() >> userSessionEntity
        userSessionService.getUserSession(sessionUUID) >> userSession

        when:
        sut.sessionInfo(event)

        then:
        1 * screens.create(UserSessionInformation, _ as OpenMode) >> userSessionInformation
        1 * userSessionInformation.setUserSessionToDisplay(userSession)
        1 * screens.show(userSessionInformation)
    }
}
