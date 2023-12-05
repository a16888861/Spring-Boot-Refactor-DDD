package com.springgboot.refactor.akka_learn;

import akka.actor.ActorSystem;
import akka.http.javadsl.model.Uri;
import akka.management.cluster.bootstrap.ClusterBootstrap;
import akka.management.javadsl.AkkaManagement;
import com.typesafe.config.Config;

import java.util.concurrent.CompletionStage;

public class AkkaLearnTest {

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("testSystem");
        final Config config = system.settings().config().getConfig("akka.kafka.producer");
        System.out.println(config);

        // Akka Management hosts the HTTP routes used by bootstrap
        CompletionStage<Uri> start = AkkaManagement.get(system).start();
        // Starting the bootstrap process needs to be done explicitly
        ClusterBootstrap.get(system).start();
    }
}
