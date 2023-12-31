package com.eugenedevv.pesamint.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Created by Eugene Devv on 22 Oct, 2023
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditDebitRequest {
    private String accountNumber;
    private BigDecimal amount;
}
