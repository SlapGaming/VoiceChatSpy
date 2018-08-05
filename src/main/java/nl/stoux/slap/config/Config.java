package nl.stoux.slap.config;

import org.apache.logging.log4j.util.Strings;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.function.Supplier;

public class Config {

    private static final String WEBSOCKET_PORT = "websocket_port";

    private static final String DISCORD_TOKEN_FIELD = "discord_token";
    private static final String DISCORD_OWNER_FIELD = "discord_owner";

    private static final String TS_SERVER_FIELD = "ts_server";
    private static final String TS_PORT_FIELD = "ts_port";
    private static final String TS_USER_FIELD = "ts_user";
    private static final String TS_PASS_FIELD = "ts_pass";

    private Properties properties;

    public Config() throws Exception {
        // Check if it exists
        Path configPath = Paths.get("config.properties");
        File configFile = configPath.toFile();
        if (!configFile.exists()) {
            // Otherwise write the example to it
            URL exampleConfigResource = this.getClass().getClassLoader().getResource("config.example.properties");
            if (exampleConfigResource == null) {
                throw new Exception("Config & Config example is missing?");
            }

            List<String> strings = Files.readAllLines(new File(exampleConfigResource.getFile()).toPath(), StandardCharsets.UTF_8);
            Files.write(configPath, strings, StandardCharsets.UTF_8);
            throw new Exception("Missing config.properties file!");
        }

        // Load the properties
        properties = new Properties();
        properties.load(new FileInputStream(configFile));

        validPorts(this::getWebsocketPort, this::getTeamspeakPort);

        notEmptyResults(this::getDiscordToken, this::getDiscordOwner,
                this::getTeamspeakServer, this::getTeamspeakUser, this::getTeamspeakPassword);
    }

    public Integer getWebsocketPort() {
        return parsePortProperty(WEBSOCKET_PORT);
    }

    public String getDiscordToken() {
        return this.properties.getProperty(DISCORD_TOKEN_FIELD);
    }

    public String getDiscordOwner() {
        return this.properties.getProperty(DISCORD_OWNER_FIELD);
    }

    public String getTeamspeakServer() {
        return this.properties.getProperty(TS_SERVER_FIELD);
    }

    public Integer getTeamspeakPort() {
        return parsePortProperty(TS_PORT_FIELD);
    }

    public String getTeamspeakUser() {
        return this.properties.getProperty(TS_USER_FIELD);
    }

    public String getTeamspeakPassword() {
        return this.properties.getProperty(TS_PASS_FIELD);
    }

    private Integer parsePortProperty(String property) {
        try {
            String port = this.properties.getProperty(property, "");
            if (Strings.isBlank(port)) {
                return null;
            }
            return Integer.parseInt(port);
        } catch (Exception e) {
            return null;
        }
    }

    private void validPorts(Supplier<Integer>... functions) throws Exception {
        for (Supplier<Integer> function : functions) {
            Integer port = function.get();
            if (port == null || port < 1 || port > 65535) {
                throw new Exception("Invalid port!");
            }
        }
    }

    private void notEmptyResults(Supplier<String>... functions) throws Exception {
        for (Supplier<String> function : functions) {
            String s = function.get();
            if (Strings.isBlank(s)) {
                throw new Exception("Missing config settings!");
            }
        }
    }

}
