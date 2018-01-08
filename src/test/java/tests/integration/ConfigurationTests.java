package tests.integration;

import static org.assertj.core.api.BDDAssertions.then;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import hello.HelloWorldConfiguration;
import hello.utils.JsonSerializer;

/**
 * Basic integration tests for service demo application.
 *
 * @author Dave Syer
 */
@RunWith(SpringJUnit4ClassRunner.class)
//@Import({ SpringBootActuatorConfig.class})
@SpringBootTest(classes = HelloWorldConfiguration.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@TestPropertySource(properties = { "management.port=0" })
public class ConfigurationTests {

	@Autowired(required = false)
	@Qualifier("oauth2StatelessSecurityContext")
	private Boolean stateless = Boolean.TRUE;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private Filter springSecurityFilterChain;

	private MockMvc mockMvc;

	private List<String> endpointsToBeIgnored; 
	@Before
	public void setup() {
		mockMvc = MockMvcBuilders
	            .webAppContextSetup(context)
	            .apply(springSecurity())  
	            .build();
		endpointsToBeIgnored= Arrays.asList("/info","/**/favicon.ico", "/dump", "/metrics/name:.*", "/autoconfig", "/env/name:.*", "/loggers/name:.*", 
				"/loggers", "/configprops", "/metrics", "/error", "/beans", "/mappings", "/**", "/heapdump", "/loggers/name:.*", "/health", "/error", 
				"/auditevents", "/webjars/**", "/trace", "/env");
	}

	@LocalServerPort
	private int port;

	@Value("${local.management.port}")
	private int mgt;

	@Autowired
	private TestRestTemplate testRestTemplate;

	@Test
	@WithMockUser(username = "user", password = "password", roles = "USER")
	public void withMockUser() throws Exception {
		this.mockMvc.perform(get("/hello-world")).andExpect(status().isOk());
	}

	@Test
	public void checkHealth() throws Exception {
		mockMvc.perform(get("/health"))
        .andExpect(status().isOk())
        ;
	}
	 
	@Test
	@WithMockUser(username = "user", password = "password", roles = "USER")
	public void listAllFEndpoints() throws Exception {
		MvcResult result =  mockMvc.perform(get("/mappings").accept(MediaType.APPLICATION_JSON)).andReturn();
		 HashMap<String, Object> bla = JsonSerializer.maybeGetUnmarshalledObject(result.getResponse().getContentAsString());
		Set<String> keySet = bla.keySet();
		keySet.stream().forEach(System.out::println); 
		// get until the first , clean of it's {}, [], and get the text until the first space. 
		// from the second part of the String, get what's after method. -> after methods, get what's in between [ ] *first occurances. 
		System.out.println("New lines");
		for(String key: keySet) {
			List<String> subSets = Arrays.asList(key.split("\\,"));
			String path = subSets.get(0).replaceAll("\\[|\\]|\\{|\\}", "").split("\\ ")[0];
			String methods="", clean="";
			if(subSets.size()>1) {
				methods = subSets.get(1).split("\\=")[1];
				clean = methods.replaceAll("\\[|\\]|\\}", "");
			}
			if(!endpointsToBeIgnored.contains(path)) {
				if(clean.toLowerCase().equals("get")) {
					MvcResult result2 = mockMvc.perform(get(path)).andReturn();
					int status = result2.getResponse().getStatus();
					System.out.println(path+" "+status+ " "+ clean);
				}
				if(clean.toLowerCase().equals("post")) {
					MvcResult result2 = mockMvc.perform(post(path)).andReturn();
					int status = result2.getResponse().getStatus();
					System.out.println(path+" "+status+ " "+ clean);
				}
			}
		}
	}

//	@Test
	@WithMockUser(username = "user", password = "password", roles = "USER")
	public void shouldReturn200WhenSendingRequestToController() throws Exception {
		@SuppressWarnings("rawtypes")
		ResponseEntity<Map> entity = this.testRestTemplate
				.getForEntity("http://localhost:" + this.port + "/hello-world", Map.class);

		then(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	//@Test
	@WithMockUser(username = "user", password = "password", roles = "USER")
	public void shouldReturn200WhenSendingRequestToManagementEndpoint() throws Exception {
		@SuppressWarnings("rawtypes")
		ResponseEntity<Map> entity = this.testRestTemplate.getForEntity("http://localhost:" + this.mgt + "/info",
				Map.class);

		then(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
	}
}