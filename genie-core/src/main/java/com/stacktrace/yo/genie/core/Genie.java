package com.stacktrace.yo.genie.core;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.stacktrace.yo.genie.core.matchmaking.supervisor.MatchmakingManager;

import java.io.IOException;

public class Genie {

    public static void main(String args[]) throws IOException {

        ActorSystem system = ActorSystem.create("genie-system");
        try {
            ActorRef supervisor = system.actorOf(MatchmakingManager.props(), "genie-supervisor");
            System.out.println(">>> Press ENTER to exit <<<");
            System.in.read();
        } finally {
            system.terminate();
        }
    }
}

