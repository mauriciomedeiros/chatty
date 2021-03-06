package com.neoway.chatty.api.web;

import com.neoway.chatty.api.config.EndpointConfig;
import com.neoway.chatty.api.domain.Message;
import com.neoway.chatty.api.domain.resource.ErrorResource;
import com.neoway.chatty.api.service.MessageService;
import com.neoway.chatty.api.utils.URIPathBinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URISyntaxException;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class MessageController {

    private MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @RequestMapping(value = EndpointConfig.MESSAGES_COLLECTION, method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Iterable<Message> list(@RequestParam String recipient){
        if(! StringUtils.isEmpty(recipient)){
            return messageService.findByRecipientUsername(recipient);
        }
        return messageService.findAll();
    }

    @RequestMapping(value = EndpointConfig.MESSAGES_COLLECTION, method = POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Object> sendMessage(@Valid @RequestBody Message message, Errors errors) throws URISyntaxException {

        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(ErrorResource.badRequest(errors));
        }

        messageService.send(message);

        return ResponseEntity
                .created(URIPathBinder.resourceLocationBuilder(message.getId()))
                .body(message);
    }

}
