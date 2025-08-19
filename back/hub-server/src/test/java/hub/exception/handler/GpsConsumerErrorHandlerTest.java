package hub.exception.handler;

import com.rabbitmq.client.Channel;
import hub.exception.InvalidMessageFormatException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.messaging.converter.MessageConversionException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GpsConsumerErrorHandlerTest {

    @Mock
    private Channel channel;

    @Mock
    private Message amqpMessage;

    @Mock
    private org.springframework.messaging.Message<?> message;

    @InjectMocks
    private GpsConsumerErrorHandler errorHandler;


    @BeforeEach
    void setUp() {
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setConsumerQueue("test.queue");
        when(amqpMessage.getMessageProperties()).thenReturn(messageProperties);
        when(amqpMessage.getBody()).thenReturn("invalid message".getBytes());
    }

    @Test
    @DisplayName("메시지 변환 시 InvalidMessageFormatException 테스트")
    void handleError_WithMessageConversionException() {
        MessageConversionException conversionException = new MessageConversionException("Conversion failed");
        ListenerExecutionFailedException failedException = new ListenerExecutionFailedException("Listener failed", conversionException, amqpMessage);

        assertThrows(InvalidMessageFormatException.class, () -> {
            errorHandler.handleError(amqpMessage, channel,message, failedException);
        });
    }

}
