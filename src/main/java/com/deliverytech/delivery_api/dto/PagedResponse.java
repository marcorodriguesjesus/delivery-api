package com.deliverytech.delivery_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * ATIVIDADE 3.2 e 3.4: DTO padronizado para respostas paginadas.
 * Contém o conteúdo da página e os metadados de paginação.
 *
 * @param <T> O tipo do conteúdo da lista (ex: ClienteResponseDTO)
 */
@Getter
@Schema(description = "Wrapper para respostas paginadas com metadados")
public class PagedResponse<T> {

    @Schema(description = "A lista de itens da página atual")
    private final List<T> content;

    @Schema(description = "O número da página atual (começando em 0)", example = "0")
    private final int page;

    @Schema(description = "A quantidade de itens por página", example = "10")
    private final int size;

    @Schema(description = "O número total de elementos em todas as páginas", example = "100")
    private final long totalElements;

    @Schema(description = "O número total de páginas", example = "10")
    private final int totalPages;

    @Schema(description = "Indica se esta é a primeira página", example = "true")
    private final boolean first;

    @Schema(description = "Indica se esta é a última página", example = "false")
    private final boolean last;

    /**
     * Construtor que converte um Page (do Spring Data) para este DTO.
     */
    public PagedResponse(Page<T> page) {
        this.content = page.getContent();
        this.page = page.getNumber();
        this.size = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.first = page.isFirst();
        this.last = page.isLast();
    }
}