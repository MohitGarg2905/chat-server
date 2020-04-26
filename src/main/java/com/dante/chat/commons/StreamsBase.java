package com.dante.chat.commons;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutionException;

public abstract class StreamsBase {

    protected abstract void createStream(final StreamsBuilder builder);

    protected abstract String getInputTopic();

    @PostConstruct
    public void setup() throws ExecutionException, InterruptedException {
        KafkaPropertiesUtils.createTopic(getInputTopic(), 1, new Short("1"));
        final StreamsBuilder builder = new StreamsBuilder();
        createStream(builder);
        final Topology topology = builder.build();
        final KafkaStreams streams = new KafkaStreams(topology, KafkaPropertiesUtils.getProperties(getInputTopic()));

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
                // this handler will be called whenever stream thread
                // terminates via some exception
                // can send mail or some other action
                System.err.println("Stream thread terminated unexpectedly, thread name : "+thread.getName()+", message : " + throwable.getMessage());
            } catch (Throwable e) {
                System.err.println("Stream thread terminated unexpectedly, thread name : "+thread.getName()+", message : " + throwable.getMessage());
            }
        });

        try {
            streams.start();
        } catch (Exception e) {
            System.err.println(e.getLocalizedMessage());
        }
    }

}
