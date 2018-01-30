package com.stacktrace.yo.genie.core.matchmaking.region;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import com.stacktrace.yo.genie.core.matchmaking.group.MatchGroup;
import com.stacktrace.yo.genie.core.matchmaking.supervisor.MatchmakingManager;

import java.util.HashMap;
import java.util.Map;

public class MatchRegion extends AbstractActor {


    private final LoggingAdapter myLogger = Logging.getLogger(getContext().getSystem(), this);
    private final String regionId;
    private final Map<String, ActorRef> matchGroups = new HashMap<>();


    public MatchRegion(final String regionId) {
        this.regionId = regionId;
    }

    public static Props props(String regionId) {
        return Props.create(MatchRegion.class, regionId);
    }

    @Override
    public void preStart() {
        myLogger.info("Match-Group Region {} started", regionId);
    }

    @Override
    public void postStop() {
        myLogger.info("Match-Group Region {} stopped", regionId);
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder
                .create()
                .match(MatchmakingManager.Protocol.RequestCreateMatchGroup.class, this::onCreateRequest)
                .build();
    }

    private void onCreateRequest(MatchmakingManager.Protocol.RequestCreateMatchGroup message) {
        if (this.regionId.equals(message.regionId)) {
            ActorRef group = matchGroups.get(message.groupId);
            if (group != null) {
                group.forward(message, getContext());
            } else {
                myLogger.info("Creating MatchGroup {}", message.groupId);
                group = getContext().actorOf(MatchGroup.props(message.groupId, this.regionId), "message.groupId" + "=" + this.regionId);
                matchGroups.put(message.groupId, group);
                group.forward(message, getContext());
            }
        } else {
            myLogger.warning(
                    "Ignoring RequestCreateMatchGroup request for {}. This actor is responsible for {}.",
                    message.regionId, this.regionId
            );
        }
    }
}
