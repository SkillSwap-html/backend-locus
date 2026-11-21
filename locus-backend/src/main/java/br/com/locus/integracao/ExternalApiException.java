package br.com.locus.integracao;

public class ExternalApiException extends RuntimeException {

    private final String provider;
    private final int status;

    public ExternalApiException(String provider, int status, String message) {
        super(message);
        this.provider = provider;
        this.status = status;
    }

    public String getProvider() {
        return provider;
    }

    public int getStatus() {
        return status;
    }
}
