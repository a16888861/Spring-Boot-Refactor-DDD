package com.springboot.refactor.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.testkit.javadsl.TestKit;
import com.alibaba.fastjson2.JSONObject;
import com.springgboot.refactor.akka_learn.Device;
import com.springgboot.refactor.akka_learn.DeviceGroup;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

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

    @Test
    public void testRegisterDeviceActor() {
        ActorSystem system = ActorSystem.create("testSystem");
        TestKit probe = new TestKit(system);
        ActorRef groupActor = system.actorOf(DeviceGroup.props("group"));

        groupActor.tell(new Device.RequestTrackDevice("group", "device1"), probe.getRef());
        probe.expectMsgClass(Device.DeviceRegistered.class);
        ActorRef deviceActor1 = probe.getLastSender();

        groupActor.tell(new Device.RequestTrackDevice("group", "device2"), probe.getRef());
        probe.expectMsgClass(Device.DeviceRegistered.class);
        ActorRef deviceActor2 = probe.getLastSender();
        assertNotEquals(deviceActor1, deviceActor2);

        // Check that the device actors are working
        deviceActor1.tell(new Device.RecordTemperature(0L, 1.0), probe.getRef());
        assertEquals(0L, probe.expectMsgClass(Device.TemperatureRecorded.class).requestId);
        deviceActor2.tell(new Device.RecordTemperature(1L, 2.0), probe.getRef());
        assertEquals(1L, probe.expectMsgClass(Device.TemperatureRecorded.class).requestId);
    }

    @Test
    public void testIgnoreRequestsForWrongGroupId() {
        ActorSystem system = ActorSystem.create("testSystem");
        TestKit probe = new TestKit(system);
        ActorRef groupActor = system.actorOf(DeviceGroup.props("group"));

        groupActor.tell(new Device.RequestTrackDevice("wrongGroup", "device1"), probe.getRef());
        probe.expectNoMessage();
    }

    @Test
    public void testReturnSameActorForSameDeviceId() {
        ActorSystem system = ActorSystem.create("testSystem");
        TestKit probe = new TestKit(system);
        ActorRef groupActor = system.actorOf(DeviceGroup.props("group"));

        groupActor.tell(new Device.RequestTrackDevice("group", "device1"), probe.getRef());
        probe.expectMsgClass(Device.DeviceRegistered.class);
        ActorRef deviceActor1 = probe.getLastSender();

        groupActor.tell(new Device.RequestTrackDevice("group", "device1"), probe.getRef());
        probe.expectMsgClass(Device.DeviceRegistered.class);
        ActorRef deviceActor2 = probe.getLastSender();
        assertEquals(deviceActor1, deviceActor2);
    }

    @Test
    public void testListActiveDevices() {
        ActorSystem system = ActorSystem.create("testSystem");
        TestKit probe = new TestKit(system);
        ActorRef groupActor = system.actorOf(DeviceGroup.props("group"));

        groupActor.tell(new Device.RequestTrackDevice("group", "device1"), probe.getRef());
        probe.expectMsgClass(Device.DeviceRegistered.class);

        groupActor.tell(new Device.RequestTrackDevice("group", "device2"), probe.getRef());
        probe.expectMsgClass(Device.DeviceRegistered.class);

        groupActor.tell(new DeviceGroup.RequestDeviceList(0L), probe.getRef());
        DeviceGroup.ReplyDeviceList reply = probe.expectMsgClass(DeviceGroup.ReplyDeviceList.class);
        assertEquals(0L, reply.getRequestId());
        assertEquals(Stream.of("device1", "device2").collect(Collectors.toSet()), reply.getIds());
    }

    @Test
    public void testListActiveDevicesAfterOneShutsDown() {
        ActorSystem system = ActorSystem.create("testSystem");
        TestKit probe = new TestKit(system);
        ActorRef groupActor = system.actorOf(DeviceGroup.props("group"));

        groupActor.tell(new Device.RequestTrackDevice("group", "device1"), probe.getRef());
        probe.expectMsgClass(Device.DeviceRegistered.class);
        ActorRef toShutDown = probe.getLastSender();

        groupActor.tell(new Device.RequestTrackDevice("group", "device2"), probe.getRef());
        probe.expectMsgClass(Device.DeviceRegistered.class);

        groupActor.tell(new DeviceGroup.RequestDeviceList(0L), probe.getRef());
        DeviceGroup.ReplyDeviceList reply = probe.expectMsgClass(DeviceGroup.ReplyDeviceList.class);
        assertEquals(0L, reply.getRequestId());
        assertEquals(Stream.of("device1", "device2").collect(Collectors.toSet()), reply.getIds());

        probe.watch(toShutDown);
        toShutDown.tell(PoisonPill.getInstance(), ActorRef.noSender());
        // 关闭actor actor在收到这个关闭事件之后 将设备从map中删除
        probe.expectTerminated(toShutDown);

        // using awaitAssert to retry because it might take longer for the groupActor
        // to see the Terminated, that order is undefined
        probe.awaitAssert(() -> {
            groupActor.tell(new DeviceGroup.RequestDeviceList(1L), probe.getRef());
            DeviceGroup.ReplyDeviceList r =
                    probe.expectMsgClass(DeviceGroup.ReplyDeviceList.class);
            assertEquals(1L, r.getRequestId());
            assertEquals(Stream.of("device2").collect(Collectors.toSet()), r.getIds());
            return null;
        });
    }
}
