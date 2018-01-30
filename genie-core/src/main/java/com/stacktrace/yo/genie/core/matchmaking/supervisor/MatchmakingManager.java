package com.stacktrace.yo.genie.core.matchmaking.supervisor;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class MatchmakingManager extends AbstractActor {

    private final LoggingAdapter myLogger = Logging.getLogger(getContext().getSystem(), this);

    public static Props props() {
        return Props.create(MatchmakingManager.class);
    }

    @Override
    public void preStart() {
        myLogger.info("Genie Matching Supervisor Started");
    }

    @Override
    public void postStop() {
        myLogger.info("Genie Matching Supervisor Stopped");
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .build();
    }


    public final static class Protocol {

        public static final class RequestCreateMatchGroup {
            public final String groupId;
            public final String regionId;

            public RequestCreateMatchGroup(String groupId, String regionId) {
                this.groupId = groupId;
                this.regionId = regionId;
            }
        }

        public static final class MatchGroupCreated {
        }

    }

}
