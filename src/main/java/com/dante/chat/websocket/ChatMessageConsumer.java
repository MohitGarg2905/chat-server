package com.dante.chat.websocket;

import com.dante.chat.commons.KafkaPropertiesUtils;
import lombok.SneakyThrows;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import static com.dante.chat.commons.TopicsUtils.getUserChatTopic;

public class ChatMessageConsumer {

    private static final Logger logger = LogManager.getLogger(ChatMessageConsumer.class);

    private final WebSocketSession session;
    private final String bootstrapServers;
    private final Long userId;
    private KafkaStreams streams;

    public ChatMessageConsumer(String bootstrapServers, WebSocketSession session, Long userId) throws ExecutionException, InterruptedException {
        this.bootstrapServers = bootstrapServers;
        this.session = session;
        this.userId = userId;
        setup();
    }

    protected void createStream(final StreamsBuilder builder) {
        KStream<String, String> source = builder.stream(getInputTopic());
        source.foreach(
                (key,value) -> publishMessage(value)
        );
    }

    private String getInputTopic(){
        return getUserChatTopic(this.userId);
    }

    private String getApplicationId(){
        return getInputTopic()+"_"+this.session.getId();
    }

    @SneakyThrows
    private void publishMessage(String payload){
        if(this.session!=null) {
            session.sendMessage(new TextMessage(payload));
        }
    }

    private Properties getProperties(){
        Properties streamsConfiguration = KafkaPropertiesUtils.getProperties();
        streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, getApplicationId());
        streamsConfiguration.put(StreamsConfig.CLIENT_ID_CONFIG, getApplicationId()+"client");
        streamsConfiguration.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

        return streamsConfiguration;
    }

    private void setup() throws ExecutionException, InterruptedException {
        KafkaPropertiesUtils.createTopic(getInputTopic(), 1, (short) 1);
        final StreamsBuilder builder = new StreamsBuilder();
        createStream(builder);
        final Topology topology = builder.build();
        this.streams = new KafkaStreams(topology, getProperties());

        Runtime.getRuntime().addShutdownHook(new Thread(
                () -> {
                    try {
                        streams.close();
                    } catch (Exception e) {

                    }
                }
        ));

        streams.setUncaughtExceptionHandler((thread, throwable) -> {

            try {
                logger.error("Stream thread terminated unexpectedly, thread name : {}, message : {}", thread.getName(), throwable.getMessage(), throwable);
            } catch (Exception e) {
                logger.error("Stream thread terminated unexpectedly, thread name : {}, message : {}", thread.getName(), e.getMessage(), e);
            }
        });

        try {
            streams.start();
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }

    private void closeStream() {
        if (streams != null) {
            streams.close();
            logger.info("Closing stream for event : {} ...", this::getInputTopic);
        }
    }

    public void closeSession(){
        closeStream();
        try(AdminClient adminClient = AdminClient.create(getProperties())){
            adminClient.deleteConsumerGroups(Collections.singletonList(getApplicationId()));
        }
    }
}
