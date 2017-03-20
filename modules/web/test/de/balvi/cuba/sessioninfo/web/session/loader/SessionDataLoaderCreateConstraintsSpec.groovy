package de.balvi.cuba.sessioninfo.web.session.loader

import com.haulmont.chile.core.model.MetaClass
import com.haulmont.chile.core.model.Session
import com.haulmont.cuba.core.global.MessageTools
import com.haulmont.cuba.core.global.Messages
import com.haulmont.cuba.core.global.Metadata
import com.haulmont.cuba.core.global.UserSessionSource
import com.haulmont.cuba.security.entity.Constraint
import com.haulmont.cuba.security.entity.ConstraintCheckType
import com.haulmont.cuba.security.entity.ConstraintOperationType
import com.haulmont.cuba.security.global.ConstraintData
import com.haulmont.cuba.security.global.UserSession
import de.balvi.cuba.sessioninfo.web.session.MetadataHelper
import de.balvi.cuba.sessioninfo.web.session.SessionDataLoader
import spock.lang.Specification

class SessionDataLoaderCreateConstraintsSpec extends Specification {

    SessionDataLoader sut
    Metadata metadata
    Messages messages
    UserSession userSession
    MessageTools messageTools
    MetadataHelper metadataHelper


    def setup() {

        metadata = Mock(Metadata)
        messages = Mock(Messages)
        metadataHelper = Mock(MetadataHelper)
        sut = new SessionDataLoader(
                metadata: metadata,
                messages: messages,
                metadataHelper: metadataHelper
        )

        userSession = Mock(UserSession)

        def session = Mock(Session)
        session.getClass('sec$Constraint') >> Mock(MetaClass)
        metadata.getSession() >> session

        and:
        messageTools = Mock(MessageTools)
        messages.getTools() >> messageTools

        and:
        metadataHelper.getEntityCaptionMap() >> [
                'User (sec$User)': 'sec$User'
        ]
    }

    def "createConstraints determines for all given entities the corresponding constraints"() {

        given:

        metadataHelper = Mock(MetadataHelper)
        sut.metadataHelper = metadataHelper
        metadataHelper.getEntityCaptionMap() >> [
                'User (sec$User)': 'sec$User',
                'Role (sec$Role)': 'sec$Role'
        ]

        when:
        sut.createConstraints(userSession)

        then:
        1 * userSession.getConstraints('sec$User')
        1 * userSession.getConstraints('sec$Role')
    }

    def "createConstraints creates on entry for all constraints of an entity"() {

        given:
        userSession.getConstraints('sec$User') >> [new ConstraintData(new Constraint()), new ConstraintData(new Constraint())]

        when:
        def result = sut.createConstraints(userSession)

        then:
        result.size() == 2
    }

    def "createConstraints uses the  caption name of an entity in the KV entity under the key 'entityName'"() {

        given:
        def constraint = new Constraint()
        userSession.getConstraints('sec$User') >> [new ConstraintData(constraint)]

        when:
        def result = sut.createConstraints(userSession)

        then:
        result[0].getValue('entityName') == 'User (sec$User)'
    }

    def "createConstraints uses the localized name of the operationType of the constraints"() {

        given:
        messages.getMessage(ConstraintOperationType.ALL) >> "all"

        and:
        def constraint = new Constraint(operationType: ConstraintOperationType.ALL)
        userSession.getConstraints('sec$User') >> [new ConstraintData(constraint)]

        when:
        def result = sut.createConstraints(userSession)

        then:
        result[0].getValue('operationType') == "all"
    }


    def "createConstraints copies the code of the constraint in the KV-Entity"() {

        given:
        def constraint = new Constraint(code: 'myCode')
        userSession.getConstraints('sec$User') >> [new ConstraintData(constraint)]

        when:
        def result = sut.createConstraints(userSession)

        then:
        result[0].getValue('code') == "myCode"
    }

    def "createConstraints copies the joinClause of the constraint in the KV-Entity"() {

        given:
        def constraint = new Constraint(joinClause: 'myJoinClause')
        userSession.getConstraints('sec$User') >> [new ConstraintData(constraint)]

        when:
        def result = sut.createConstraints(userSession)

        then:
        result[0].getValue('joinClause') == "myJoinClause"
    }

    def "createConstraints copies the groovyScript of the constraint in the KV-Entity"() {

        given:
        def constraint = new Constraint(groovyScript: 'myGroovyScript')
        userSession.getConstraints('sec$User') >> [new ConstraintData(constraint)]

        when:
        def result = sut.createConstraints(userSession)

        then:
        result[0].getValue('groovyScript') == "myGroovyScript"
    }

    def "createConstraints copies the localised name of the checkType of the constraint in the KV-Entity"() {

        given:
        messages.getMessage(ConstraintCheckType.DATABASE) >> "DB"

        and:
        def constraint = new Constraint(checkType: ConstraintCheckType.DATABASE)
        userSession.getConstraints('sec$User') >> [new ConstraintData(constraint)]

        when:
        def result = sut.createConstraints(userSession)

        then:
        result[0].getValue('checkType') == "DB"
    }

    def "createConstraints copies the whereClause of the constraint in the KV-Entity"() {

        given:
        def constraint = new Constraint(whereClause: 'myWhereClause')
        userSession.getConstraints('sec$User') >> [new ConstraintData(constraint)]

        when:
        def result = sut.createConstraints(userSession)

        then:
        result[0].getValue('whereClause') == "myWhereClause"
    }
}
