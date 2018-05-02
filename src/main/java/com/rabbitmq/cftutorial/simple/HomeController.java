package com.rabbitmq.cftutorial.simple;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


@Controller
public class HomeController {
    @Autowired AmqpTemplate amqpTemplate;
    @Autowired RabbitAdmin admin;
    @Autowired private GaugeService gaugeService;
   @Autowired
   private RestTemplate restTemplate;

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }


    @RequestMapping(value = "/")
    public String home(Model model) {
        model.addAttribute(new Message());

        java.util.Properties props;
        Integer messageCount;
        props = admin.getQueueProperties("messages");
        messageCount = Integer.parseInt(props.get("QUEUE_MESSAGE_COUNT").toString());
        gaugeService.submit("rabbitmq.message.queuedepth", messageCount);
        //String targeturi = "https://metrics-forwarder.sys.banning.cf-app.com/v1/metrics";
        //String exchange = restTemplate.postForObject(targeturi, "POST", String.class);
        System.out.println("Rabbitmq messages queue depth: " + messageCount);
        return "WEB-INF/views/home.jsp";
    }

    @RequestMapping(value = "/publish", method=RequestMethod.POST)
    public String publish(Model model, Message message) {
        // Send a message to the "messages" queue
        amqpTemplate.convertAndSend("messages", message.getValue());
        model.addAttribute("published", true);
        return home(model);
    }

    @RequestMapping(value = "/get", method=RequestMethod.POST)
    public String get(Model model) {
        // Receive a message from the "messages" queue
        String message = (String)amqpTemplate.receiveAndConvert("messages");
        if (message != null)
            model.addAttribute("got", message);
        else
            model.addAttribute("got_queue_empty", true);

        return home(model);
    }
}
