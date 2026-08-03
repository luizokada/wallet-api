package wallet.api.domain.transaction.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import wallet.api.domain.transaction.entity.Transaction;

public record TransactionPageDTO(
        List<TransactionToApiViewDTO> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last
) {

    public static TransactionPageDTO from(Page<Transaction> source) {
        return new TransactionPageDTO(
                TransactionToApiViewDTO.fromList(source.getContent()),
                source.getNumber(),
                source.getSize(),
                source.getTotalElements(),
                source.getTotalPages(),
                source.isLast()
        );
    }
}
