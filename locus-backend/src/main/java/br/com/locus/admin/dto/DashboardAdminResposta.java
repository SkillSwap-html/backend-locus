package br.com.locus.admin.dto;

import java.util.List;

public record DashboardAdminResposta(
        long usuarios,
        long viagens,
        long destinosUnicos,
        List<DestinoDashboard> destinos) {

    public record DestinoDashboard(
            String destino,
            long quantidade) {
    }
}
