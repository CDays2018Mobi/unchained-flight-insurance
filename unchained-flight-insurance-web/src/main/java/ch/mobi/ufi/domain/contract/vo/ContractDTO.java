package ch.mobi.ufi.domain.contract.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class ContractDTO {
    private String flightNumber;
    private LocalDate arrivalDate;
}
