package com.stacktrace.yo.genie.core.matchmaking.supervisor;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class MatchingSupervisor extends AbstractActor {

    private final LoggingAdapter myLogger = Logging.getLogger(getContext().getSystem(), this);

    public static Props props() {
        return Props.create(MatchingSupervisor.class);
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
}
