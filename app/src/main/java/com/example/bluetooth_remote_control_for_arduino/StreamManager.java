package com.example.bluetooth_remote_control_for_arduino;
import java.io.OutputStream;

public class StreamManager {
        private static StreamManager instance;
        private OutputStream outputStream;

        private StreamManager() {}

        public static StreamManager getInstance() {
            if (instance == null) {
                instance = new StreamManager();
            }
            return instance;
        }

        public OutputStream getOutputStream() {
            return outputStream;
        }

        public void setOutputStream(OutputStream stream) {
            outputStream = stream;
        }
}
