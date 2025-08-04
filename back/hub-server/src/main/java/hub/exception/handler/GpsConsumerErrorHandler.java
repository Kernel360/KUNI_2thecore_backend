package hub.exception.handler;

import com.rabbitmq.client.Channel;
import hub.exception.InvalidMessageFormatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.messaging.converter.MessageConversionException;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component("gpsConsumerErrorHandler")
public class GpsConsumerErrorHandler implements RabbitListenerErrorHandler {

    @Override
    public Object handleError(Message message, Channel channel, org.springframework.messaging.Message<?> message1, ListenerExecutionFailedException e) throws Exception {
        String payload = new String(message.getBody(), StandardCharsets.UTF_8);

        log.error("Error processing message from RabbitMQ queue: {}", message.getMessageProperties().getConsumerQueue());
        log.error("Payload: {}", payload);
        log.error("Exception: {}", e.getMessage(), e.getCause());

        if (e.getCause() instanceof MessageConversionException) {
            throw new InvalidMessageFormatException("Invalid message format: " + payload, e.getCause());
        }

        throw e;
    }
}
