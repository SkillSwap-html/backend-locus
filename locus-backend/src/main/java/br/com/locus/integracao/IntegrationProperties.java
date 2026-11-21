package br.com.locus.integracao;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.integrations")
public class IntegrationProperties {

    private static String normalizarConfiguracao(String valor) {
        String normalizado = valor == null ? "" : valor.trim();
        if ((normalizado.startsWith("\"") && normalizado.endsWith("\""))
                || (normalizado.startsWith("'") && normalizado.endsWith("'"))) {
            normalizado = normalizado.substring(1, normalizado.length() - 1).trim();
        }
        return normalizado.replace("\\:", ":").replace("\\/", "/");
    }

    private final Google google = new Google();
    private final Gemini gemini = new Gemini();
    private final Groq groq = new Groq();

    public Google getGoogle() {
        return google;
    }

    public Gemini getGemini() {
        return gemini;
    }

    public Groq getGroq() {
        return groq;
    }


    public static class Google {

        private final Maps maps = new Maps();
        private final Places places = new Places();
        private final Routes routes = new Routes();
        private final StreetView streetView = new StreetView();

        public Maps getMaps() {
            return maps;
        }

        public Places getPlaces() {
            return places;
        }

        public Routes getRoutes() {
            return routes;
        }

        public StreetView getStreetView() {
            return streetView;
        }
    }

    public static class Maps {

        private boolean enabled;
        private String javascriptApiKey = "";
        private String apiKey = "";
        private String baseUrl = "https://maps.googleapis.com";
        private int timeoutMs = 5000;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getJavascriptApiKey() {
            return javascriptApiKey;
        }

        public void setJavascriptApiKey(String javascriptApiKey) {
            this.javascriptApiKey = normalizarConfiguracao(javascriptApiKey);
        }

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = normalizarConfiguracao(apiKey);
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = normalizarConfiguracao(baseUrl);
        }

        public int getTimeoutMs() {
            return timeoutMs;
        }

        public void setTimeoutMs(int timeoutMs) {
            this.timeoutMs = timeoutMs;
        }
    }

    public static class Places extends Maps {
    }

    public static class Routes extends Maps {
    }

    public static class StreetView extends Maps {
    }

    public static class Gemini {

        private boolean enabled;
        private String apiKey = "";
        private String baseUrl = "https://generativelanguage.googleapis.com";
        private String model = "gemini-3.5-flash";
        private int timeoutMs = 15000;
        private int maxOutputTokens = 2048;
        private double temperature = 0.2d;
        private boolean webSearchEnabled;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = normalizarConfiguracao(apiKey);
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = normalizarConfiguracao(baseUrl);
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = normalizarConfiguracao(model);
        }

        public int getTimeoutMs() {
            return timeoutMs;
        }

        public void setTimeoutMs(int timeoutMs) {
            this.timeoutMs = timeoutMs;
        }

        public int getMaxOutputTokens() {
            return maxOutputTokens;
        }

        public void setMaxOutputTokens(int maxOutputTokens) {
            this.maxOutputTokens = maxOutputTokens;
        }

        public double getTemperature() {
            return temperature;
        }

        public void setTemperature(double temperature) {
            this.temperature = temperature;
        }

        public boolean isWebSearchEnabled() {
            return webSearchEnabled;
        }

        public void setWebSearchEnabled(boolean webSearchEnabled) {
            this.webSearchEnabled = webSearchEnabled;
        }
    }

    public static class Groq {

        private boolean enabled;
        private String apiKey = "";
        private String baseUrl = "https://api.groq.com";
        private String model = "openai/gpt-oss-20b";
        private int timeoutMs = 15000;
        private int maxOutputTokens = 2048;
        private double temperature = 0.2d;
        private boolean webSearchEnabled;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = normalizarConfiguracao(apiKey);
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = normalizarConfiguracao(baseUrl);
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = normalizarConfiguracao(model);
        }

        public int getTimeoutMs() {
            return timeoutMs;
        }

        public void setTimeoutMs(int timeoutMs) {
            this.timeoutMs = timeoutMs;
        }

        public int getMaxOutputTokens() {
            return maxOutputTokens;
        }

        public void setMaxOutputTokens(int maxOutputTokens) {
            this.maxOutputTokens = maxOutputTokens;
        }

        public double getTemperature() {
            return temperature;
        }

        public void setTemperature(double temperature) {
            this.temperature = temperature;
        }

        public boolean isWebSearchEnabled() {
            return webSearchEnabled;
        }

        public void setWebSearchEnabled(boolean webSearchEnabled) {
            this.webSearchEnabled = webSearchEnabled;
        }
    }

}
