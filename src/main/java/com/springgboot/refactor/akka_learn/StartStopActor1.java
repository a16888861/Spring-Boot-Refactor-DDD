package com.springgboot.refactor.akka_learn;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.Await;
import scala.concurrent.Future;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

/**
 * Actor启动停止的例子
 */
class StartStopActor1 extends AbstractActor {

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("testSystem");
        ActorRef first = system.actorOf(StartStopActor1.props(), "first");
        System.out.println(first);
//        first.tell("stop", ActorRef.noSender());
    }

    static Props props() {
        return Props.create(StartStopActor1.class, StartStopActor1::new);
    }

    @Override
    public void preStart() {
        System.out.println("first started");

        // 获取消息发送方
//        super.getSender();

        ActorRef second = getContext().actorOf(StartStopActor2.props(), "second");
        // 发送消息并告诉对方自身的地址 如果不想告诉地址就用 ActorRef.noSender() 这个是经典版的写法 新版的写法好像不一样 先了解概念
        // 一般来说，从AbstractActor那里得到回复有两种方式：第一种是通过已发送的消息（actorRef.tell(msg, sender)），只有原始发送者是AbstractActor时才有效，第二种是通过Future
//        second.tell("hello", super.getSelf());
        Timeout timeout = Timeout.create(Duration.ofSeconds(5));
        Future<Object> future = Patterns.ask(second, "hello", timeout);
        try {
            String result = (String) Await.result(future, timeout.duration());
            System.out.println(result);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(second);
    }

    @Override
    public void postStop() {
        System.out.println("first stopped");
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .matchEquals("helloFirst", s -> {
                    System.out.println("receive second msg");
                })
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
                // 当收到hello消息后再通知发送方自己收到消息了
                .matchEquals("hello", f -> {
                    System.out.println("123123123");
//                    getContext().getSender().tell("helloFirst", super.getSelf());
                    getContext().getSender().tell("helloFirst", ActorRef.noSender());
                })
                .build();
    }
}