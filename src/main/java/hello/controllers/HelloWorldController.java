package hello.controllers;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import hello.domain.Greeting;

@RestController
public class HelloWorldController {
	
	private final AtomicLong counter = new AtomicLong();
	private static final String template= "Hello, %s!";
	
	@GetMapping("/hello-world")
	public @ResponseBody Greeting getHello(@RequestParam(value="name", required=false, defaultValue="Stranger") String name){
		return new Greeting(counter.incrementAndGet(), String.format(template, name));
	}
	@PostMapping("/hello-world")
	public @ResponseBody Greeting sayHello(@RequestParam(value="name", required=false, defaultValue="Stranger") String name){
		return new Greeting(counter.incrementAndGet(), String.format(template, name));
	}
}
