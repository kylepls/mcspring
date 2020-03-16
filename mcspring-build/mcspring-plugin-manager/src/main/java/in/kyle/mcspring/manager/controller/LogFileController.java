package in.kyle.mcspring.manager.controller;

import org.springframework.stereotype.Controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;

import in.kyle.mcspring.RequiresSpigot;
import lombok.SneakyThrows;

@Controller
@RequiresSpigot
class LogFileController {
    
    private final Path logsFolder = Paths.get("logs");
    
    @PostConstruct
    @SneakyThrows
    void setup() {
        Files.list(logsFolder)
                .filter(p -> p.toString().endsWith(".log.gz"))
                .forEach(this::delete);
    }
    
    @SneakyThrows
    private void delete(Path f) {
        Files.delete(f);
    }
}
