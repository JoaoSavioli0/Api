package com.condolives.api.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.condolives.api.dto.insight.GenerateInsightRequest;
import com.condolives.api.dto.insight.InsightGenerationResponse;
import com.condolives.api.entity.Condominium;
import com.condolives.api.entity.Insight.Insight;
import com.condolives.api.entity.Insight.InsightGeneration;
import com.condolives.api.enums.PostStatus;
import com.condolives.api.exception.ServiceException;
import com.condolives.api.repository.Amenity.BookingRepository;
import com.condolives.api.repository.Condominium.CondominiumRepository;
import com.condolives.api.repository.Insight.InsightGenerationRepository;
import com.condolives.api.repository.Insight.InsightRepository;
import com.condolives.api.repository.Outer.VisitorRepository;
import com.condolives.api.repository.Post.Ticket.TicketRepository;
import com.condolives.api.repository.ServiceLine.ServiceLineRepository;
import com.condolives.api.repository.User.CondoMemberRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InsightServiceImpl implements InsightService {

    private static final String ANTHROPIC_URL = "https://api.anthropic.com/v1/messages";
    private static final Set<String> VALID_DEPTHS = Set.of("basic", "full");
    private static final Set<String> VALID_SOURCES = Set.of("tickets", "servicos", "reservas", "portaria");

    @Value("${anthropic.api-key:}")
    private String anthropicApiKey;

    @Value("${anthropic.model:claude-sonnet-4-6}")
    private String anthropicModel;

    private final CondominiumRepository condominiumRepository;
    private final InsightRepository insightRepository;
    private final InsightGenerationRepository generationRepository;
    private final TicketRepository ticketRepository;
    private final ServiceLineRepository serviceLineRepository;
    private final CondoMemberRepository condoMemberRepository;
    private final BookingRepository bookingRepository;
    private final VisitorRepository visitorRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private HttpClient httpClient;

    @PostConstruct
    void init() {
        httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public int getCredits(UUID condominiumId) {
        return findCondominiumOrThrow(condominiumId).getCredits();
    }

    public int calculateCost(GenerateInsightRequest request) {
        return computeCost(request);
    }

    @Transactional(noRollbackFor = ServiceException.class)
    public InsightGenerationResponse generate(GenerateInsightRequest request, UUID condominiumId) {
        validateRequest(request);
        int cost = computeCost(request);

        Condominium condominium = findCondominiumOrThrow(condominiumId);
        if (condominium.getCredits() < cost) {
            throw new ServiceException(
                    "Créditos insuficientes. Necessário: " + cost + ", disponível: " + condominium.getCredits(), 422);
        }

        condominium.setCredits(condominium.getCredits() - cost);
        condominiumRepository.save(condominium);

        InsightGeneration generation = generationRepository.save(InsightGeneration.builder()
                .condominiumId(condominiumId)
                .generatedAt(Instant.now())
                .creditsUsed(cost)
                .periodStart(request.periodStart())
                .periodEnd(request.periodEnd())
                .analysisDepth(request.analysisDepth())
                .dataSources(String.join(",", request.dataSources()))
                .status("processing")
                .build());

        try {
            String dataJson = aggregateData(request, condominiumId);
            List<Map<String, Object>> rawInsights = callAnthropicApi(buildPrompt(request, dataJson));

            List<Insight> saved = rawInsights.stream().map(raw -> insightRepository.save(Insight.builder()
                    .generationId(generation.getId())
                    .condominiumId(condominiumId)
                    .category(sanitize(raw.get("category"), "geral"))
                    .severity(sanitize(raw.get("severity"), "info"))
                    .title(sanitize(raw.get("title"), "Insight"))
                    .description(sanitize(raw.get("description"), ""))
                    .actionLabel((String) raw.get("actionLabel"))
                    .build())).toList();

            generation.setStatus("completed");
            generationRepository.save(generation);

            return InsightGenerationResponse.from(generation, saved);

        } catch (ServiceException e) {
            refundAndFail(condominium, generation, cost, e.getMessage());
            throw e;
        } catch (Exception e) {
            refundAndFail(condominium, generation, cost, e.getMessage());
            throw new ServiceException("Falha ao gerar insights: " + e.getMessage(), 500);
        }
    }

    public List<InsightGenerationResponse> listGenerations(UUID condominiumId) {
        return generationRepository.findByCondominiumIdOrderByGeneratedAtDesc(condominiumId)
                .stream()
                .map(gen -> InsightGenerationResponse.from(gen, insightRepository.findByGenerationId(gen.getId())))
                .toList();
    }

    private void refundAndFail(Condominium condominium, InsightGeneration generation, int cost, String errorMsg) {
        condominium.setCredits(condominium.getCredits() + cost);
        condominiumRepository.save(condominium);
        generation.setStatus("failed");
        generation.setErrorMessage(errorMsg != null ? errorMsg.substring(0, Math.min(errorMsg.length(), 500)) : "Erro desconhecido");
        generationRepository.save(generation);
    }

    private int computeCost(GenerateInsightRequest request) {
        long days = ChronoUnit.DAYS.between(request.periodStart(), request.periodEnd());
        int months = (int) Math.max(1, Math.min(12, Math.ceil(days / 30.0)));
        int base = 2;
        int depthCost = "full".equals(request.analysisDepth()) ? 5 : 0;
        int sourcesCost = request.dataSources() == null ? 0 : request.dataSources().size();
        return base + months + depthCost + sourcesCost;
    }

    private void validateRequest(GenerateInsightRequest request) {
        if (request.periodEnd().isBefore(request.periodStart())) {
            throw new ServiceException("A data de fim deve ser após a data de início", 422);
        }
        if (!VALID_DEPTHS.contains(request.analysisDepth())) {
            throw new ServiceException("Profundidade inválida. Use 'basic' ou 'full'", 422);
        }
        for (String source : request.dataSources()) {
            if (!VALID_SOURCES.contains(source)) {
                throw new ServiceException("Fonte de dados inválida: " + source, 422);
            }
        }
    }

    private String aggregateData(GenerateInsightRequest request, UUID condominiumId) throws Exception {
        Instant start = request.periodStart().atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant end = request.periodEnd().atTime(23, 59, 59).atOffset(ZoneOffset.UTC).toInstant();
        List<String> sources = request.dataSources();
        boolean fullDepth = "full".equals(request.analysisDepth());

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("totalMoradores", condoMemberRepository.countByCondominiumId(condominiumId));

        if (sources.contains("tickets")) {
            Map<String, Object> tickets = new LinkedHashMap<>();
            tickets.put("totalNoPeriodo", ticketRepository.countByCondominiumIdInPeriod(condominiumId, start, end));
            tickets.put("abertos", ticketRepository.countByCondominiumIdAndStatus(condominiumId, PostStatus.ABERTO));
            tickets.put("emAndamento", ticketRepository.countByCondominiumIdAndStatus(condominiumId, PostStatus.EM_ANDAMENTO));
            tickets.put("concluidos", ticketRepository.countByCondominiumIdAndStatus(condominiumId, PostStatus.CONCLUIDO));
            tickets.put("concluidosNoPeriodo", ticketRepository.countByCondominiumIdAndStatusInPeriod(condominiumId, PostStatus.CONCLUIDO, start, end));
            if (fullDepth) {
                List<Map<String, String>> recent = ticketRepository.findByCondominiumIdInPeriod(condominiumId, start, end)
                        .stream().limit(20).map(t -> {
                            Map<String, String> m = new LinkedHashMap<>();
                            m.put("titulo", t.getTitle() != null ? t.getTitle() : "");
                            m.put("status", t.getStatus() != null ? t.getStatus().name() : "");
                            String desc = t.getDescription() != null ? t.getDescription() : "";
                            m.put("descricao", desc.substring(0, Math.min(desc.length(), 120)));
                            return m;
                        }).toList();
                tickets.put("solicitacoesRecentes", recent);
            }
            data.put("solicitacoes", tickets);
        }

        if (sources.contains("servicos")) {
            data.put("servicos", Map.of(
                    "planejados", serviceLineRepository.countByCondominiumIdAndStatus(condominiumId, "planejado"),
                    "emAndamento", serviceLineRepository.countByCondominiumIdAndStatus(condominiumId, "em_andamento"),
                    "concluidos", serviceLineRepository.countByCondominiumIdAndStatus(condominiumId, "concluido"),
                    "pausados", serviceLineRepository.countByCondominiumIdAndStatus(condominiumId, "pausado")));
        }

        if (sources.contains("reservas")) {
            data.put("reservasDeEspacos", Map.of(
                    "totalNoPeriodo",
                    bookingRepository.countByCondominiumIdAndDateBetween(condominiumId, request.periodStart(), request.periodEnd())));
        }

        if (sources.contains("portaria")) {
            data.put("portaria", Map.of(
                    "visitantesNoPeriodo",
                    visitorRepository.countByCondominiumIdAndCreatedAtBetween(condominiumId, start, end)));
        }

        return objectMapper.writeValueAsString(data);
    }

    private String buildPrompt(GenerateInsightRequest request, String dataJson) {
        String depthLabel = "full".equals(request.analysisDepth()) ? "Contexto completo (dados individuais disponíveis)" : "Dados objetivos (apenas agregados)";
        return """
                Você é um consultor especializado em gestão de condomínios residenciais no Brasil.
                Analise os dados abaixo e gere insights práticos, identificando padrões, problemas, propondo soluções e sugerindo melhorias ou novos investimentos.

                PERÍODO ANALISADO: %s até %s
                PROFUNDIDADE: %s

                DADOS:
                %s

                Responda SOMENTE com um array JSON válido, sem texto adicional e sem blocos de código markdown:
                [
                  {
                    "category": "categoria (manutenção|segurança|financeiro|ocupação|satisfação|portaria|infraestrutura|comunicação)",
                    "severity": "info|warning|critical",
                    "title": "título objetivo e direto (máx 80 caracteres)",
                    "description": "análise do dado, problema identificado e sugestão de ação (máx 350 caracteres)",
                    "actionLabel": "texto do botão de ação (máx 30 caracteres, ou null se não aplicável)"
                  }
                ]

                Gere entre 4 e 8 insights relevantes. severity: info=observação neutra/positiva, warning=atenção necessária, critical=ação urgente necessária.
                """.formatted(request.periodStart(), request.periodEnd(), depthLabel, dataJson);
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> callAnthropicApi(String prompt) throws Exception {
        if (anthropicApiKey == null || anthropicApiKey.isBlank()) {
            throw new ServiceException("Anthropic API key não configurada (anthropic.api-key)", 500);
        }

        String requestJson = objectMapper.writeValueAsString(Map.of(
                "model", anthropicModel,
                "max_tokens", 4096,
                "messages", List.of(Map.of("role", "user", "content", prompt))));

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(ANTHROPIC_URL))
                .header("x-api-key", anthropicApiKey)
                .header("anthropic-version", "2023-06-01")
                .header("content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                .timeout(Duration.ofSeconds(90))
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new ServiceException("Erro na API Anthropic: HTTP " + response.statusCode() + " — " + response.body(), 500);
        }

        Map<String, Object> body = objectMapper.readValue(response.body(), Map.class);
        List<Map<String, Object>> content = (List<Map<String, Object>>) body.get("content");
        String text = ((String) content.get(0).get("text")).strip();

        if (text.startsWith("```")) {
            text = text.replaceAll("(?s)^```[a-z]*\\n?", "").replaceAll("```\\s*$", "").strip();
        }

        return objectMapper.readValue(text, new TypeReference<List<Map<String, Object>>>() {});
    }

    private String sanitize(Object value, String fallback) {
        if (value instanceof String s && !s.isBlank()) return s;
        return fallback;
    }

    private Condominium findCondominiumOrThrow(UUID condominiumId) {
        return condominiumRepository.findById(condominiumId)
                .orElseThrow(() -> new ServiceException("Condomínio não encontrado", 404));
    }
}
