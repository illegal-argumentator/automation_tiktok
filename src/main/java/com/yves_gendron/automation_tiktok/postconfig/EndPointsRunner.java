package com.yves_gendron.automation_tiktok.postconfig;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EndPointsRunner implements CommandLineRunner {
    private static final Logger LOG = LoggerFactory.getLogger(EndPointsRunner.class);
    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Override
    public void run(String... args) throws Exception {

        var message = requestMappingHandlerMapping
                .getHandlerMethods()
                .keySet()
                .stream()
                .map(rmi -> new WebMapDetails(
                        rmi.getMethodsCondition().getMethods().stream().findFirst().orElse(null),
                        rmi.getPatternValues().stream().findFirst().orElseThrow()
                ))
                .filter(webMapDetails -> webMapDetails.method() != null)
                .sorted(WebMapDetails::compareTo)
                .map(wmd -> "%-6s %s".formatted(wmd.method(),wmd.path()))
                .collect(Collectors.joining("\n"));

        LOG.info("Endpoints:\n\n" + message + "\n");
    }

    record WebMapDetails(RequestMethod method, String path) implements Comparable<WebMapDetails> {
        @Override
        public int compareTo(WebMapDetails o) {
            var methodCompare = method.compareTo(o.method);

            if(methodCompare != 0){
                return methodCompare;
            }

            return path.compareTo(o.path);
        }
    }
}