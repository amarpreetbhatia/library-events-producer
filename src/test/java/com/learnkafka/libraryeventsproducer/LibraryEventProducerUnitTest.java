package com.learnkafka.libraryeventsproducer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnkafka.libraryeventsproducer.domain.Book;
import com.learnkafka.libraryeventsproducer.domain.LibraryEvent;
import com.learnkafka.libraryeventsproducer.producer.LibraryEventProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LibraryEventProducerUnitTest {

    @Mock
    KafkaTemplate<Integer,String> kafkaTemplate;

    @Spy
    ObjectMapper objectMapper;

    @InjectMocks
    LibraryEventProducer libraryEventProducer;

    @Test
    public void testsendLibraryEventApproach2_Failure() {
        LibraryEvent libraryEvent = LibraryEvent
                .builder()
                .libraryEventId(null)
                .book(Book.builder().bookId(123).bookAuthor("Amarpreet").bookName("Kakfa Sample").build())
                .build();

        SettableListenableFuture future = new SettableListenableFuture();
        future.setException(new RuntimeException("Exception calling Kafka"));
        when(kafkaTemplate.send(isA(ProducerRecord.class))).thenReturn(future);

        assertThrows(Exception.class, () -> libraryEventProducer.sendLibraryEventApproach2(libraryEvent).get());
    }

    @Test
    public void testsendLibraryEventApproach2_Success() throws JsonProcessingException, ExecutionException, InterruptedException {
        LibraryEvent libraryEvent = LibraryEvent
                .builder()
                .libraryEventId(null)
                .book(Book.builder().bookId(123).bookAuthor("Amarpreet").bookName("Kakfa Sample").build())
                .build();
        String record = objectMapper.writeValueAsString(libraryEvent);
        SettableListenableFuture future = new SettableListenableFuture();
        ProducerRecord<Integer,String> producerRecord = new ProducerRecord("library-events",libraryEvent.getLibraryEventId(),record);
        RecordMetadata recordMetadata = new RecordMetadata(
                new TopicPartition("library-events",1),
                1,1,10202,System.currentTimeMillis(),
                1,2);
        SendResult<Integer,String> sendResult = new SendResult<>(producerRecord,recordMetadata);

        future.set(sendResult);

        when(kafkaTemplate.send(isA(ProducerRecord.class))).thenReturn(future);

        ListenableFuture<SendResult<Integer,String>> listenableFuture = libraryEventProducer.sendLibraryEventApproach2(libraryEvent);
        SendResult<Integer,String> receivedSendResult = listenableFuture.get();
        assertTrue(receivedSendResult.getRecordMetadata().partition() == 1);
    }
}
