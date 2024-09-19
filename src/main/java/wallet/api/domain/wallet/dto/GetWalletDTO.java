package wallet.api.domain.wallet.dto;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public record GetWalletDTO(
        @DateTimeFormat
        Date startDate,
        @DateTimeFormat
        Date endDate
) {
}
