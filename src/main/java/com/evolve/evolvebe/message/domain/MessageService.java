package com.evolve.evolvebe.message.domain;

import com.evolve.evolvebe.global.exception.CustomException;
import com.evolve.evolvebe.global.exception.ErrorCode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    
    private final MessageInfoMapper messageInfoMapper;

    @Value("${message.path}")
    private String filePath;

    public MessageInfo.Main getMessage(String id){
        Message message = messageRepository.getMessage(id);
        if (message == null) return null;
        return messageInfoMapper.of(message);
    }

    public Page<MessageInfo.Main> getMessages(Pageable pageable, MessageCommand.GetList command) {
        Page<Message> messages = messageRepository.getMessages(pageable, command);
        return messageInfoMapper.of(messages);
    }

    public void createMessage(MessageCommand.Create command) {
        if (messageRepository.getMessage(command.getId()) != null) {
            throw new CustomException(ErrorCode.EXISTS_ALREADY_MESSAGE);
        }
        Message message = Message.builder()
            .id(command.getId())
            .koText(command.getKoText())
            .enText(command.getEnText())
            .isActive(command.getIsActive())
            .build();

        messageRepository.saveMessage(message);
    }

    public void updateMessage(String id, MessageCommand.Update command) {
        Message message = messageRepository.getMessage(id);
        if (message == null) throw new CustomException(ErrorCode.NOT_FOUND_MESSAGE);

        message.update(
            command.getKoText(),
            command.getEnText(),
            command.getIsActive()
        );
    }

    public void deleteMessage(String id) {
        Message message = messageRepository.getMessage(id);
        if (message == null) throw new CustomException(ErrorCode.NOT_FOUND_MESSAGE);

        messageRepository.deleteMessage(id);
    }

    public void deployTranslatedMessagesAll() {
        List<Message> messages = messageRepository.getMessages(Boolean.TRUE, Sort.by(Sort.Direction.ASC, "id"));

        Map<String, String> messageKoMap = new LinkedHashMap<>();
        Map<String, String> messageEnMap = new LinkedHashMap<>();

        System.out.println(messages.stream().map(Message::getId).toList());

        for (Message message : messages) {
            messageKoMap.put(message.getId(), message.getKoText());
            messageEnMap.put(message.getId(), message.getEnText());
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonKo = gson.toJson(messageKoMap);
        String jsonEn = gson.toJson(messageEnMap);

        String pathKo = filePath + "\\translation_ko.json";
        String pathEn = filePath + "\\translation_en.json";
        FileWriter writerEn;
        FileWriter writerKo;

        try {
            writerKo = new FileWriter(pathKo, Charset.forName("UTF-8"));
            writerKo.write(jsonKo);
            writerKo.close();

            writerEn = new FileWriter(pathEn, Charset.forName("UTF-8"));
            writerEn.write(jsonEn);
            writerEn.close();
        } catch (IOException e) {
            throw new CustomException(ErrorCode.ERROR_DEPLOY_MESSAGE);
        }

    }
}
