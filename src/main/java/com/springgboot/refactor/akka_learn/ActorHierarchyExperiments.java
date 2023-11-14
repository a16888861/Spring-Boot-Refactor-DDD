package com.springgboot.refactor.akka_learn;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Actor创建的基础例子
 */
public class ActorHierarchyExperiments {
    public static void main(String[] args) throws java.io.IOException {
        Logger log = LoggerFactory.getLogger(ActorHierarchyExperiments.class);
        ActorSystem system = ActorSystem.create("testSystem");

        ActorRef firstRef = system.actorOf(PrintMyActorRefActor.props(), "first-actor");
        log.info("First: " + firstRef);
        // 告诉ActorRef有一个打印的事件
        firstRef.tell("printit", ActorRef.noSender());
        // 回车停止
        System.out.println(">>> Press ENTER to exit <<<");
        try {
            System.in.read();
        } finally {
            // 停止Actor
            system.terminate();
        }
    }
}

class PrintMyActorRefActor extends AbstractActor {
    private final Logger log = LoggerFactory.getLogger(ActorHierarchyExperiments.class);

    static Props props() {
        return Props.create(PrintMyActorRefActor.class, PrintMyActorRefActor::new);
    }

    @Override
    public void preStart() throws Exception {
        log.info(this.getClass() + " createReceive before doSomeThing");
        super.preStart();
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                // 匹配到打印事件 创建一个子类的actor并打印
                .matchEquals("printit", p -> {
                    ActorRef secondRef = getContext().actorOf(Props.empty(), "second-actor");
                    log.info("Second: " + secondRef);
                })
                .build();
    }
}