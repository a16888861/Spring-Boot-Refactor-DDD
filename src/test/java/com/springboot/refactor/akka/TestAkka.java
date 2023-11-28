package com.springboot.refactor.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
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
//        deviceActor.tell(new Device.ReadTemperature(42L), probe.getLastSender());
        deviceActor.tell(new Device.ReadTemperature(42L), probe.getRef());
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
        deviceActor.tell(new Device.RecordTemperature(1L, 24.0), probe.getRef());
        assertEquals(1L, probe.expectMsgClass(Device.TemperatureRecorded.class).requestId);

        // actor 写入 读取
        deviceActor.tell(new Device.ReadTemperature(2L), probe.getRef());
        Device.RespondTemperature response1 = probe.expectMsgClass(Device.RespondTemperature.class);
        log.info("response1:{}",JSONObject.toJSONString(response1));
        assertEquals(2L, response1.requestId);
        assertEquals(Optional.of(24.0), response1.value);

        deviceActor.tell(new Device.RecordTemperature(3L, 55.0), probe.getRef());
        assertEquals(3L, probe.expectMsgClass(Device.TemperatureRecorded.class).requestId);

        deviceActor.tell(new Device.ReadTemperature(4L), probe.getRef());
        Device.RespondTemperature response2 = probe.expectMsgClass(Device.RespondTemperature.class);
        assertEquals(4L, response2.requestId);
        assertEquals(Optional.of(55.0), response2.value);
    }

    @Test
    public void testReplyToRegistrationRequests() {
        ActorSystem system = ActorSystem.create("testSystem");
        TestKit probe = new TestKit(system);
        ActorRef deviceActor = system.actorOf(Device.props("group", "device"));

        deviceActor.tell(new Device.RequestTrackDevice("group", "device"), probe.getRef());
        probe.expectMsgClass(Device.DeviceRegistered.class);
        assertEquals(deviceActor, probe.getLastSender());
    }

    @Test
    public void testIgnoreWrongRegistrationRequests() {
        ActorSystem system = ActorSystem.create("testSystem");
        TestKit probe = new TestKit(system);
        ActorRef deviceActor = system.actorOf(Device.props("group", "device"));

        deviceActor.tell(new Device.RequestTrackDevice("wrongGroup", "device"), probe.getRef());
        // 期望无消息 如果有消息通知 不能用这个
        probe.expectNoMessage();

        deviceActor.tell(new Device.RequestTrackDevice("group", "wrongDevice"), probe.getRef());
        probe.expectNoMessage();

        // 此处为发送正确匹配的值 然后进行输出
        deviceActor.tell(new Device.RequestTrackDevice("group", "device"), probe.getRef());
        assertEquals(deviceActor, probe.getLastSender());
        Device.DeviceRegistered deviceRegistered = probe.expectMsgClass(Device.DeviceRegistered.class);
        System.out.println(JSONObject.toJSONString(deviceRegistered));
    }
}
