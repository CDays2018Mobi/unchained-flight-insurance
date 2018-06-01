package ch.mobi.ufi.rest.endpoint;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping(path = "/api/hello/v1/say-hello")
public class SayHello {

    @GetMapping
    public ResponseEntity<String> hello(@RequestParam(value = "who", required = false) String who) {
        String msg = String.format("Hello %s !", who);
    	LOG.info(msg);
		return ok(msg);
    }
}

