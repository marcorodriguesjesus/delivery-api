package com.deliverytech.delivery_api.dto;

import com.deliverytech.delivery_api.validation.ValidCategoria; // Importar
import com.deliverytech.delivery_api.validation.ValidCEP; // Importar
import com.deliverytech.delivery_api.validation.ValidHorarioFuncionamento; // Importar
import com.deliverytech.delivery_api.validation.ValidTelefone;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Schema(description = "DTO para criar ou atualizar um restaurante")
public class RestauranteRequestDTO {

    @Schema(description = "Nome oficial do restaurante", example = "Pizzaria Bella Italia")
    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres")
    private String nome;

    @Schema(description = "Tipo de culinária principal", example = "Italiana")
    @NotBlank(message = "A categoria é obrigatória")
    @ValidCategoria(acceptedValues = {"Italiana", "Hamburgueria", "Japonesa", "Lanches", "Indiana", "Pizza"},
            message = "Categoria inválida. Valores aceitos: Italiana, Hamburgueria, Japonesa, Lanches, Indiana, Pizza")
    private String categoria;

    @Schema(description = "Endereço físico do restaurante", example = "Av. Paulista, 1000, São Paulo/SP")
    @NotBlank(message = "O endereço é obrigatório")
    private String endereco;

    /**
     * ATIVIDADE 1.2: Campo "CEP" adicionado conforme solicitado.
     */
    @Schema(description = "CEP do restaurante", example = "01310-100")
    @NotBlank(message = "O CEP é obrigatório")
    @ValidCEP
    private String cep;

    @Schema(description = "Telefone de contato do restaurante", example = "(11) 3333-4444")
    @NotBlank(message = "O telefone é obrigatório")
    @ValidTelefone
    private String telefone;

    @Schema(description = "Valor cobrado pela entrega", example = "5.99")
    @NotNull(message = "A taxa de entrega é obrigatória")
    @DecimalMin(value = "0.0", message = "A taxa de entrega não pode ser negativa")
    private BigDecimal taxaEntrega;

    @Schema(description = "Tempo de entrega estimado em minutos", example = "45")
    @NotNull(message = "O tempo de entrega é obrigatório")
    @Min(value = 10, message = "O tempo de entrega deve ser de no mínimo 10 minutos")
    @Max(value = 120, message = "O tempo de entrega deve ser de no máximo 120 minutos")
    private Integer tempoEntregaEstimado;

    /**
     * ATIVIDADE 1.2: Campo "Horário de Funcionamento" adicionado conforme solicitado.
     */
    @Schema(description = "Horário de funcionamento", example = "18:00-23:30")
    @NotBlank(message = "O horário de funcionamento é obrigatório")
    @ValidHorarioFuncionamento
    private String horarioFuncionamento;
}