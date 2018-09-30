package hello.hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/rest2")
public class GreetingController {

	private static final Logger log = LoggerFactory.getLogger(RestEndApplication.class);

    @Autowired
    private RestTemplate restTemplate;
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value="cal", defaultValue="50") String cal) throws Exception {

    	Greeting value = new Greeting();


        return value;
    }
    
    @RequestMapping("/test")
    public String test(@RequestParam(value="cal", defaultValue="50") String cal) throws Exception {
    	// log.info(cal);

    	double cal2 = Math.abs(Double.valueOf(cal)-50);
        System.out.println(cal2);

        String str = restTemplate.getForObject("http://rest-service-2:16007/rest3/test?cal=" + cal2, String.class);
        System.out.println(str);

        return String.valueOf(cal2);
    }
}
