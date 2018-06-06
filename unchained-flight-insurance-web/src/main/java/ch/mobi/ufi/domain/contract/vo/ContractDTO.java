package ch.mobi.ufi.domain.contract.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class ContractDTO {
    @NotNull
    private String flightNumber;
    @NotNull
    private LocalDate arrivalDate;
}
