package com.expense.service.dto;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class updateExpanseDTO {
    private String amount;
    private String currency;
    private String marchant;
    private String chennel;
}
