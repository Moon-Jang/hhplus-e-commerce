package kr.hhplus.ecommerce.application.client;

public interface MessageClient {
    <T> void send(String topic, T message);
    <T> void send(String topic, String partitionKey, T message);
}