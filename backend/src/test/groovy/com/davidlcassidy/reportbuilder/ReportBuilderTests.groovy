package com.davidlcassidy.reportbuilder

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@SpringBootTest
@ContextConfiguration(classes = ReportBuilder)
class ReportBuilderTests extends Specification {

    def "context loads successfully"() {
        when:
        ReportBuilder jdChallenge = new ReportBuilder()

        then:
        notThrown(Exception)
    }
}

