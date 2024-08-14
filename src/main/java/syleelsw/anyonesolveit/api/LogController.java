package syleelsw.anyonesolveit.api;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import syleelsw.anyonesolveit.aops.IgnoreValidation;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Getter @Setter
class Key {
    String key;
}
@RestController @Slf4j
public class LogController {

    private static final String LOG_FILE_PATH = "log/access.log";
    @Value("${anyone.log key}")
    private String LOGKEY;
    @GetMapping("/")
    public ResponseEntity HealthCheck() {
        return new ResponseEntity(HttpStatus.OK);
    }
    @PostMapping("/logs") @IgnoreValidation
    public ResponseEntity<Map<String, Object>> getLogs(@RequestParam(value = "lastPosition", defaultValue = "0") long lastPosition, @RequestBody Key key) throws IOException {
        if(key == null || key.key == null || !key.key.equals(LOGKEY)) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        Map<String, Object> response = new HashMap<>();
        String logs = "";
        long newPosition = lastPosition;

        try {
            if (Files.exists(Paths.get(LOG_FILE_PATH))) {
                byte[] logBytes;
                try (var logFileChannel = Files.newByteChannel(Paths.get(LOG_FILE_PATH))) {
                    logBytes = new byte[(int) logFileChannel.size()];
                    logFileChannel.read(java.nio.ByteBuffer.wrap(logBytes));
                }

                if (lastPosition < logBytes.length) {
                    logs = new String(logBytes, (int) lastPosition, logBytes.length - (int) lastPosition);
                }
                newPosition = logBytes.length;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        response.put("logs", logs);
        response.put("newPosition", newPosition);
        return new ResponseEntity(response, HttpStatus.OK);
    }
}