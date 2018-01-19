package com.stacktrace.yo.genie.core.matchmaking.group;


import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class MatchGroupTest {

    private static ActorSystem system;

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create();
    }

    @AfterClass
    public static void teardown() {
        TestKit.shutdownActorSystem(system);
        system = null;
    }

    @Test
    public void testReplyWithMatchGroupJoined() {
        TestKit probe = new TestKit(system);
        ActorRef matchActor = system.actorOf(MatchGroup.props(UUID.randomUUID().toString(), "region-1"));
        String requestId = UUID.randomUUID().toString();
        matchActor.tell(new MatchGroup.Protocol.JoinMatchGroup(requestId), probe.getRef());
        MatchGroup.Protocol.MatchGroupJoined response = probe.expectMsgClass(MatchGroup.Protocol.MatchGroupJoined.class);
        assertEquals(requestId, response.requestId);
    }
    
}