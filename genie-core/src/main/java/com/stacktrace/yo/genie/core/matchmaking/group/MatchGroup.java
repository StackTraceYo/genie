package com.stacktrace.yo.genie.core.matchmaking.group;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.stacktrace.yo.genie.core.matchmaking.supervisor.MatchmakingManager;

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
                    if (this.regionId.equals(r.regionId) && this.groupId.equals(r.groupId)) {
                        myLogger.info("Adding Request {}", r.requestId);
                        addRequest(r.requestId, r.regionId);
                        getSender().tell(new Protocol.MatchGroupJoined(r.requestId, r.groupId, r.regionId), getSelf());
                    } else {
                        myLogger.warning(
                                "Ignoring JoinMatch request for {}-{}. This actor is responsible for {}-{}.",
                                r.groupId, r.regionId, this.groupId, this.regionId
                        );
                    }
                })
                .match(MatchmakingManager.Protocol.RequestCreateMatchGroup.class, r -> {
                    if (this.groupId.equals(r.groupId) && this.regionId.equals(r.regionId)) {
                        getSender().tell(new MatchmakingManager.Protocol.MatchGroupCreated(), getSelf());
                    } else {
                        myLogger.warning(
                                "Ignoring RequestCreateMatchGroup request for {}-{}.This actor is responsible for {}-{}.",
                                r.groupId, r.regionId, this.groupId, this.regionId
                        );
                    }
                }).build();
    }

    private List<String> addRequest(String requestId, String regionId) {
        if (requestList == null) {
            requestList = new ArrayList<>();
        }
        requestList.add(requestId + "-" + regionId);
        myLogger.info("Request List:{}", requestList);
        return requestList;
    }

    static class Protocol {

        static final class JoinMatchGroup {
            String requestId;
            String groupId;
            String regionId;

            JoinMatchGroup(final String requestId, final String groupId, final String regionId) {
                this.requestId = requestId;
                this.groupId = groupId;
                this.regionId = regionId;
            }
        }

        static final class MatchGroupJoined {
            String requestId;
            String groupId;
            String regionId;

            MatchGroupJoined(final String requestId, final String groupId, final String regionId) {
                this.requestId = requestId;
                this.groupId = groupId;
                this.regionId = regionId;
            }
        }
    }

}
