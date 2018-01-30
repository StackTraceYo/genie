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
        String requestId = UUID.randomUUID().toString();
        String groupId = UUID.randomUUID().toString();
        String regionId = "region-1";
        ActorRef matchActor = system.actorOf(MatchGroup.props(groupId, regionId));


        matchActor.tell(new MatchGroup.Protocol.JoinMatchGroup(requestId, groupId, regionId), probe.getRef());
        MatchGroup.Protocol.MatchGroupJoined response = probe.expectMsgClass(MatchGroup.Protocol.MatchGroupJoined.class);

        assertEquals(requestId, response.requestId);
        assertEquals(regionId, response.regionId);
    }

    @Test
    public void testIgnoreWrongRegionOrGroupRequests() {
        TestKit probe = new TestKit(system);
        String requestId = UUID.randomUUID().toString();
        String groupId = UUID.randomUUID().toString();
        String regionId = "region-1";
        ActorRef matchActor = system.actorOf(MatchGroup.props(groupId, regionId));

        //wrong region
        matchActor.tell(new MatchGroup.Protocol.JoinMatchGroup(requestId, groupId, "wrong-region"), probe.getRef());
        probe.expectNoMsg();

        //wrong group
        matchActor.tell(new MatchGroup.Protocol.JoinMatchGroup(requestId, "wrong-group", regionId), probe.getRef());
        probe.expectNoMsg();
    }

}