package com.learnkafka.libraryeventsproducer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnkafka.libraryeventsproducer.controller.LibraryEventsController;
import com.learnkafka.libraryeventsproducer.domain.Book;
import com.learnkafka.libraryeventsproducer.domain.LibraryEvent;
import com.learnkafka.libraryeventsproducer.producer.LibraryEventProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LibraryEventsController.class)
@AutoConfigureMockMvc
public class LibraryEventControllerUnitTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    LibraryEventProducer libraryEventProducer;

    ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testPostLibraryEvent() throws Exception {
        LibraryEvent libraryEvent = LibraryEvent
                .builder()
                .libraryEventId(null)
                .book(Book.builder().bookId(123).bookAuthor("Amarpreet").bookName("Kakfa Sample").build())
                .build();
        String request = mapper.writeValueAsString(libraryEvent);

//        doNothing().when(libraryEventProducer).sendLibraryEventApproach2(isA(LibraryEvent.class));
        when(libraryEventProducer.sendLibraryEventApproach2(isA(LibraryEvent.class))).thenReturn(null);
        mockMvc.perform(
                post("/v1/libraryevent")
                    .content(request)
                    .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated());
    }

    @Test
    public void testPostLibraryEvent_4xx_when_bookId_is_null() throws Exception {
        LibraryEvent libraryEvent = LibraryEvent
                .builder()
                .libraryEventId(null)
                .book(Book.builder().bookId(null).bookAuthor("Amarpreet").bookName(null).build())
                .build();
        String request = mapper.writeValueAsString(libraryEvent);

//        doNothing().when(libraryEventProducer).sendLibraryEventApproach2(isA(LibraryEvent.class));
        when(libraryEventProducer.sendLibraryEventApproach2(isA(LibraryEvent.class))).thenReturn(null);
        String exceptedContent = "book.bookId - must not be null,book.bookName - must not be blank";
        mockMvc.perform(
                post("/v1/libraryevent")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest())
        .andExpect(content().string(exceptedContent));
    }

    @Test
    public void testPostLibraryEvent_4xx_when_book_is_null() throws Exception {
        LibraryEvent libraryEvent = LibraryEvent
                .builder()
                .libraryEventId(null)
                .book(null)
                .build();
        String request = mapper.writeValueAsString(libraryEvent);

//        doNothing().when(libraryEventProducer).sendLibraryEventApproach2(isA(LibraryEvent.class));
        when(libraryEventProducer.sendLibraryEventApproach2(isA(LibraryEvent.class))).thenReturn(null);
        String exceptedContent = "book - must not be null";
        mockMvc.perform(
                post("/v1/libraryevent")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest())
                .andExpect(content().string(exceptedContent));
    }
}
