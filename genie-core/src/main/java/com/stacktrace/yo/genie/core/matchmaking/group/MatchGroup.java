package com.stacktrace.yo.genie.core.matchmaking.group;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.util.ArrayList;
import java.util.List;

public class MatchGroup extends AbstractActor {

    private final String groupId;
    private final String regionId;
    private List<String> requestList;

    private final LoggingAdapter myLogger = Logging.getLogger(getContext().getSystem(), this);

    public MatchGroup(final String groupId, final String regionId) {
        this.groupId = groupId;
        this.regionId = regionId;
    }

    public static Props props(String groupId, String regionId) {
        return Props.create(MatchGroup.class, groupId, regionId);
    }

    @Override
    public void preStart() {
        myLogger.info("Match-Group Actor {}-{} started", groupId, regionId);
    }

    @Override
    public void postStop() {
        myLogger.info("Match-Group Actor {}-{} stopped", groupId, regionId);
    }


    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Protocol.JoinMatchGroup.class, r -> {
                    myLogger.info("Adding Request {}", r.requestId);
                    addRequest(r.requestId);
                    getSender().tell(new Protocol.MatchGroupJoined(r.requestId), getSelf());
                })
                .build();
    }

    private List<String> addRequest(String requestId) {
        if (requestList == null) {
            requestList = new ArrayList<>();
        }
        requestList.add(requestId);
        myLogger.info("Request List:{}", requestList);
        return requestList;
    }

    static class Protocol {

        static final class JoinMatchGroup {
            String requestId;

            JoinMatchGroup(final String requestId) {
                this.requestId = requestId;
            }
        }

        static final class MatchGroupJoined {
            String requestId;

            MatchGroupJoined(final String requestId) {
                this.requestId = requestId;
            }
        }
    }

}
