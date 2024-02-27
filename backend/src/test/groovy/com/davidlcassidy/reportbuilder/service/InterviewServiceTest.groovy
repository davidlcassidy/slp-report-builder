package com.davidlcassidy.reportbuilder.service

import com.davidlcassidy.reportbuilder.model.Interview
import com.davidlcassidy.reportbuilder.model.InterviewQuestion
import com.davidlcassidy.reportbuilder.repository.InterviewQuestionRepository
import com.davidlcassidy.reportbuilder.repository.InterviewRepository
import spock.lang.Specification

import java.time.LocalDateTime

class InterviewServiceTest extends Specification {

    // Variables
    static sessionId = UUID.randomUUID().toString()
    static machineId = UUID.randomUUID().toString()
    def startAt = LocalDateTime.now()
    def eventAt = LocalDateTime.now().toString()
    def eventType = "TestEventType"
    def numericEventValue = 42.0
    def eventList = [
            InterviewQuestion.builder().eventType("Type1").numericEventValue(5.0).build(),
            InterviewQuestion.builder().eventType("Type2").numericEventValue(3.0).build(),
            InterviewQuestion.builder().eventType("Type1").numericEventValue(2.0).build()
    ]
    def sessionList = [
            Interview.builder().sessionId("session1").machineId("machine1").startAt(LocalDateTime.now()).questionList(eventList).build(),
            Interview.builder().sessionId("session2").machineId("machine1").startAt(LocalDateTime.now().minusDays(1)).questionList(eventList).build(),
            Interview.builder().sessionId("session3").machineId("machine2").startAt(LocalDateTime.now().minusDays(2)).questionList(eventList).build()
    ]
    def sessionOptional = Optional.of(sessionList[0])

    // Repositories
    def sessionRepository = Mock(InterviewRepository)
    def eventRepository = Mock(InterviewQuestionRepository)

    // Service
    InterviewService sessionService = new InterviewService(
            sessionRepository: sessionRepository,
            interviewQuestionRepository: eventRepository
    )

    def "createSession should create and save a session"() {
        when:
        def result = sessionService.createSession(sessionId, machineId, startAt)

        then:
        1 * sessionRepository.save(_ as Interview)

        and:
        result != null
        result.sessionId == sessionId
        result.machineId == machineId
        result.startAt == startAt
    }

    def "createSessionEvent should create and save an event"() {
        when:
        def result = sessionService.createSessionEvent(sessionId, eventAt, eventType, numericEventValue)

        then:
        1 * sessionRepository.findBySessionId(sessionId) >> sessionList[0]
        1 * sessionRepository.save(sessionList[0]) >> Interview.builder().build()

        and:
        result != null
        result.eventAt == eventAt
        result.eventType == eventType
        result.numericEventValue == numericEventValue
    }

    def "createSessionEvent should return null for unknown session"() {
        when:
        def result = sessionService.createSessionEvent(sessionId, eventAt, eventType, numericEventValue)

        then:
        1 * sessionRepository.findBySessionId(sessionId) >> null
        0 * eventRepository.save(*_)

        result == null
    }

    def "getSessionAggregatedEvents should return aggregated events for a session"() {
        when:
        def result = sessionService.getSessionAggregatedEvents(sessionId, machineId)

        then:
        1 * sessionRepository.findTopByMachineIdAndSessionIdOrderByStartAtDesc(sessionId, machineId) >> sessionOptional

        and:
        result.isPresent()
        result.get().sessionId == sessionId
        result.get().events.size() == 2
        result.get().events[0].eventType == "Type2"
        result.get().events[0].totalEventValue == 3.0
        result.get().events[1].eventType == "Type1"
        result.get().events[1].totalEventValue == 7.0

    }

    def "getSessionAggregatedEvents should return empty for a session with no events"() {
        when:
        def result = sessionService.getSessionAggregatedEvents(sessionId, machineId)

        then:
        1 * sessionRepository.findTopByMachineIdAndSessionIdOrderByStartAtDesc(sessionId, machineId) >> Optional.of(Interview.builder().build())

        and:
        result.isEmpty()
    }

    def "getSessionAggregatedEvents should return empty for a non existing session"() {
        when:
        def result = sessionService.getSessionAggregatedEvents(sessionId, machineId)

        then:
        1 * sessionRepository.findTopByMachineIdAndSessionIdOrderByStartAtDesc(sessionId, machineId) >> Optional.empty()

        and:
        result.isEmpty()
    }

    def "getMachineIds should return a list of unique machine IDs"() {
        when:
        def result = sessionService.getMachineIds()

        then:
        1 * sessionRepository.findAll() >> sessionList

        and:
        result.size() == 2
        result.contains("machine1")
        result.contains("machine2")
    }

    def "getMostRecentSessionByMachineId should return the most recent session for a machine ID"() {
        when:
        def result = sessionService.getMostRecentSessionByMachineId(machineId)

        then:
        1 * sessionRepository.findTopByMachineIdOrderByStartAtDesc(machineId) >> Optional.of(sessionList[0])

        and:
        result.isPresent()
        result.get().sessionId == "session1"
        result.get().machineId == "machine1"
        result.get().startAt == sessionList[0].startAt
    }

    def "getMostRecentSessionByMachineId should return empty when no sessions are found for a machine ID"() {
        when:
        def result = sessionService.getMostRecentSessionByMachineId("nonexistentMachineId")

        then:
        1 * sessionRepository.findTopByMachineIdOrderByStartAtDesc("nonexistentMachineId") >> Optional.empty()

        and:
        result.isEmpty()
    }

}
