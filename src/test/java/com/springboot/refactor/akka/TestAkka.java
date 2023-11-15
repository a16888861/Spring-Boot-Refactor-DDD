package com.springboot.refactor.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.TestKit;
import com.alibaba.fastjson2.JSONObject;
import com.springgboot.refactor.akka_learn.Device;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;

@Slf4j
public class TestAkka {
    @Test
    public void testReplyWithEmptyReadingIfNoTemperatureIsKnown() {
        ActorSystem system = ActorSystem.create("testSystem");
        TestKit probe = new TestKit(system);
        ActorRef deviceActor = system.actorOf(Device.props("group", "device"));
//        deviceActor.tell(new Device.ReadTemperature(42L), probe.lastSender());
        deviceActor.tell(new Device.ReadTemperature(42L), probe.testActor());
        Device.RespondTemperature response = probe.expectMsgClass(Device.RespondTemperature.class);
        assertEquals(42L, response.requestId);
        assertEquals(Optional.empty(), response.value);
    }

    @Test
    public void testReplyWithLatestTemperatureReading() {
        ActorSystem system = ActorSystem.create("testSystem");
        TestKit probe = new TestKit(system);
        ActorRef deviceActor = system.actorOf(Device.props("group", "device"));

        // actor 写入 读取
        deviceActor.tell(new Device.RecordTemperature(1L, 24.0), probe.testActor());
        assertEquals(1L, probe.expectMsgClass(Device.TemperatureRecorded.class).requestId);

        // actor 写入 读取
        deviceActor.tell(new Device.ReadTemperature(2L), probe.testActor());
        Device.RespondTemperature response1 = probe.expectMsgClass(Device.RespondTemperature.class);
        log.info("response1:{}",JSONObject.toJSONString(response1));
        assertEquals(2L, response1.requestId);
        assertEquals(Optional.of(24.0), response1.value);

        deviceActor.tell(new Device.RecordTemperature(3L, 55.0), probe.testActor());
        assertEquals(3L, probe.expectMsgClass(Device.TemperatureRecorded.class).requestId);

        deviceActor.tell(new Device.ReadTemperature(4L), probe.testActor());
        Device.RespondTemperature response2 = probe.expectMsgClass(Device.RespondTemperature.class);
        assertEquals(4L, response2.requestId);
        assertEquals(Optional.of(55.0), response2.value);
    }
}
