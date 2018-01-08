package hello.controllers;

import org.apache.tomcat.util.http.fileupload.util.Streams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@RestController
public class EndpointDocController {
 private final RequestMappingHandlerMapping handlerMapping;

 @Autowired
 public EndpointDocController(RequestMappingHandlerMapping handlerMapping) {
  this.handlerMapping = handlerMapping;
 }

 @GetMapping("/endpointdoc")
 public @ResponseBody String show(Model model) {
  return  this.handlerMapping.getHandlerMethods().toString();
 } 
}