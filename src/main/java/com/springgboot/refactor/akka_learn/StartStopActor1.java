package com.springgboot.refactor.akka_learn;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

/**
 * Actor启动停止的例子
 */
class StartStopActor1 extends AbstractActor {

  public static void main(String[] args) {
    ActorSystem system = ActorSystem.create("testSystem");
    ActorRef first = system.actorOf(StartStopActor1.props(), "first");
    System.out.println(first);
    first.tell("stop", ActorRef.noSender());
    // todo 可以再次使用sbt命令来启动这个项目 但是死信了 在停止之前重新试过 个人猜测这里应该配置好通信
//    first.tell("sbt", ActorRef.noSender());
  }

  static Props props() {
    return Props.create(StartStopActor1.class, StartStopActor1::new);
  }

  @Override
  public void preStart() {
    System.out.println("first started");
    ActorRef second = getContext().actorOf(StartStopActor2.props(), "second");
    System.out.println(second);
  }

  @Override
  public void postStop() {
    System.out.println("first stopped");
  }

  @Override
  public Receive createReceive() {
    return receiveBuilder()
        .matchEquals("stop", s -> {
          getContext().stop(getSelf());
        })
        .build();
  }
}

class StartStopActor2 extends AbstractActor {

  static Props props() {
    return Props.create(StartStopActor2.class, StartStopActor2::new);
  }

  @Override
  public void preStart() {
    System.out.println("second started");
  }

  @Override
  public void postStop() {
    System.out.println("second stopped");
  }

  // Actor.emptyBehavior is a useful placeholder when we don't
  // want to handle any messages in the actor.
  @Override
  public Receive createReceive() {
    return receiveBuilder()
        .build();
  }
}