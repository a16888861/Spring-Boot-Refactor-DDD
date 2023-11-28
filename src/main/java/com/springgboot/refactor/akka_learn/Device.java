package com.springgboot.refactor.akka_learn;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.util.Optional;

public class Device extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    public final String groupId;

    public final String deviceId;

    public Device(String groupId, String deviceId) {
        this.groupId = groupId;
        this.deviceId = deviceId;
    }

    // 定义设备 Actor 及其读取协议
    public static Props props(String groupId, String deviceId) {
        return Props.create(Device.class, () -> new Device(groupId, deviceId));
    }

    // 添加写入协议
    public static final class RecordTemperature {
        public final long requestId;
        public final double value;

        public RecordTemperature(long requestId, double value) {
            this.requestId = requestId;
            this.value = value;
            System.out.println("RecordTemperature:{" + requestId + "},{" + value + "}");
        }
    }

    public static final class TemperatureRecorded {
        public final long requestId;

        public TemperatureRecorded(long requestId) {
            this.requestId = requestId;
        }
    }

    public static final class ReadTemperature {
        final long requestId;

        public ReadTemperature(long requestId) {
            this.requestId = requestId;
        }
    }

    public static final class RespondTemperature {
        public final long requestId;
        public final Optional<Double> value;

        public RespondTemperature(long requestId, Optional<Double> value) {
            this.requestId = requestId;
            this.value = value;
        }
    }

    public static final class RequestTrackDevice {
        public final String groupId;
        public final String deviceId;

        public RequestTrackDevice(String groupId, String deviceId) {
            this.groupId = groupId;
            this.deviceId = deviceId;
        }
    }

    public static final class DeviceRegistered {
        public final String groupId;
        public final String deviceId;

        public DeviceRegistered(String groupId, String deviceId) {
            this.groupId = groupId;
            this.deviceId = deviceId;
        }
    }

    Optional<Double> lastTemperatureReading = Optional.empty();

    @Override
    public void preStart() {
        log.info("Device actor {}-{} started", groupId, deviceId);
    }

    @Override
    public void postStop() {
        log.info("Device actor {}-{} stopped", groupId, deviceId);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(RequestTrackDevice.class, r -> {
                    if (this.groupId.equals(r.groupId) && this.deviceId.equals(r.deviceId)) {
                        log.info("RequestTrackDevice reading {},{} with {}", r.groupId, getSender());
                        // 获取发送方 然后通知他, 将收到的写入协议丢过去 & 告诉发送方自身的地址
                        getSender().tell(new DeviceRegistered(r.groupId, r.deviceId), getSelf());
                    } else {
                        log.warning(
                                "Ignoring TrackDevice request for {}-{}.This actor is responsible for {}-{}.",
                                r.groupId, r.deviceId, this.groupId, this.deviceId
                        );
                    }
                })
                // 匹配写入协议
                .match(RecordTemperature.class, r -> {
                    log.info("Recorded temperature reading {} with {}", r.value, r.requestId);
                    lastTemperatureReading = Optional.of(r.value);
                    // 获取发送方 然后通知他, 将收到的写入协议丢过去 & 告诉发送方自身的地址
                    getSender().tell(new TemperatureRecorded(r.requestId), getSelf());
                })
                // 匹配读取协议
                .match(ReadTemperature.class, r -> {
                    getSender().tell(new RespondTemperature(r.requestId, lastTemperatureReading), getSelf());
                })
                .build();
    }
}