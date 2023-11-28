package com.springgboot.refactor.akka_learn;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DeviceGroup extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    final String groupId;

    public DeviceGroup(String groupId) {
        this.groupId = groupId;
    }

    public static Props props(String groupId) {
        return Props.create(DeviceGroup.class, () -> new DeviceGroup(groupId));
    }

    @AllArgsConstructor
    public static final class RequestDeviceList {
        final long requestId;
    }

    @Getter
    @AllArgsConstructor
    public static final class ReplyDeviceList {
        final long requestId;
        final Set<String> ids;
    }

    final Map<String, ActorRef> deviceIdToActor = new HashMap<>();
    final Map<ActorRef, String> actorToDeviceId = new HashMap<>();

    @Override
    public void preStart() {
        log.info("DeviceGroup {} started", groupId);
    }

    @Override
    public void postStop() {
        log.info("DeviceGroup {} stopped", groupId);
    }

    private void onTrackDevice(Device.RequestTrackDevice trackMsg) {
        if (this.groupId.equals(trackMsg.groupId)) {
            ActorRef deviceActor = deviceIdToActor.get(trackMsg.deviceId);
            if (deviceActor != null) {
                deviceActor.forward(trackMsg, getContext());
            } else {
                // 创建设备 执行转发
                log.info("Creating device actor for {}", trackMsg.deviceId);
                // 当前节点创建当前组的设备 然后推送到现有设备map中
                deviceActor = getContext().actorOf(Device.props(groupId, trackMsg.deviceId), "device-" + trackMsg.deviceId);
                actorToDeviceId.put(deviceActor, trackMsg.deviceId);
                deviceIdToActor.put(trackMsg.deviceId, deviceActor);
                // 执行转发/通知 forward or tell
                deviceActor.forward(trackMsg, getContext());
//        deviceActor.tell(trackMsg, getSelf());
            }
        } else {
            log.warning(
                    "Ignoring TrackDevice request for {}. This actor is responsible for {}.",
                    groupId, this.groupId
            );
        }
    }

    private void onTerminated(Terminated t) {
        ActorRef deviceActor = t.getActor();
        String deviceId = actorToDeviceId.get(deviceActor);
        log.info("Device actor for {} has been terminated", deviceId);
        actorToDeviceId.remove(deviceActor);
        deviceIdToActor.remove(deviceId);
    }

    private void onDeviceList(RequestDeviceList r) {
        getSender().tell(new ReplyDeviceList(r.requestId, deviceIdToActor.keySet()), getSelf());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Device.RequestTrackDevice.class, this::onTrackDevice)
                .match(RequestDeviceList.class, this::onDeviceList)
                .match(Terminated.class, this::onTerminated)
                .build();
    }
}