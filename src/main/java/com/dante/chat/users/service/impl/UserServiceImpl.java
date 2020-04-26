package com.dante.chat.users.service.impl;

import com.dante.chat.commons.JsonUtils;
import com.dante.chat.commons.KafkaProducerFactory;
import com.dante.chat.commons.KafkaPropertiesUtils;
import com.dante.chat.commons.PasswordUtils;
import com.dante.chat.commons.exceptions.InvalidCredentialsException;
import com.dante.chat.commons.exceptions.UnAuthorizedException;
import com.dante.chat.users.model.*;
import com.dante.chat.users.repositories.AuthRepository;
import com.dante.chat.users.repositories.UserRepository;
import com.dante.chat.users.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static com.dante.chat.commons.KafkaPropertiesUtils.getProperties;
import static com.dante.chat.commons.TopicsUtils.getUserChatTopic;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthRepository authRepository;

    private KafkaProducer<String, byte[]> kafkaProducer ;

    @PostConstruct
    public void setup(){
        this.kafkaProducer = KafkaProducerFactory.getKafkaProducer(getProperties());
    }

    @Override
    public Long addUser(String email, String password) throws NoSuchAlgorithmException, ExecutionException, InterruptedException {
        UserEntity userEntity = new UserEntity();
        userEntity.setEmailId(email);
        userEntity.setPassword(PasswordUtils.hashPassword(password));

        userEntity = userRepository.save(userEntity);

        KafkaPropertiesUtils.createTopic(getUserChatTopic(userEntity.getId()), 1, new Short("1"));
        return userEntity.getId();
    }

    @Override
    public String login(String emailId, String password) throws NoSuchAlgorithmException {
        Optional<UserEntity> userEntityOptional = userRepository.findByEmailIdAndPassword(emailId, PasswordUtils.hashPassword(password));
        if(!userEntityOptional.isPresent()){
            throw new InvalidCredentialsException();
        }
        Long userId = userEntityOptional.get().getId();
        AuthEntity authEntity = AuthEntity.builder().userId(userId).token(UUID.randomUUID().toString()).build();
        authEntity = authRepository.save(authEntity);

        return authEntity.getToken();
    }

    @Override
    public Long validateTokenAndGetUserId(String token){
        Optional<AuthEntity> authEntityOptional = authRepository.findByToken(token);
        if(!authEntityOptional.isPresent())
            throw new UnAuthorizedException();
        return authEntityOptional.get().getUserId();
    }

    @Override
    public void sendMessage(String token, ChatMessage chatMessage) throws IOException {
        Long userId = validateTokenAndGetUserId(token);
        KafkaChatMessageObject kafkaChatMessageObject = KafkaChatMessageObject.builder()
                .fromUserId(userId)
                .toUserId(chatMessage.getToUserId())
                .message(chatMessage.getMessage())
                .timestamp(new Date())
                .messageType(MessageType.MESSAGE)
                .build();
        String topic = getUserChatTopic(chatMessage.getToUserId());
        byte[] message = JsonUtils.toJson(kafkaChatMessageObject).getBytes();
        //byte[] message = convertPaylodToAvro(kafkaChatMessageObject);
        ProducerRecord<String, byte[]> data = new ProducerRecord<>(topic,null,null,message);
        this.kafkaProducer.send(data);
    }
}
