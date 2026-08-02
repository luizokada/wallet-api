package wallet.api.domain.transaction.dto;

import java.util.Date;
import java.util.List;

import wallet.api.domain.category.dtos.CategoryToApiViewDTO;
import wallet.api.domain.transaction.entity.Transaction;
import wallet.api.domain.transaction.entity.TransactionType;

public record TransactionToApiViewDTO(
        String id,
        TransactionType type,
        String description,
        int amount,
        Date date,
        CategoryToApiViewDTO category,
        Integer installmentNumber,
        Integer installmentTotal
) {

    public static List<TransactionToApiViewDTO> fromList(List<Transaction> transactions) {
        return transactions.stream().map(transaction -> {
            CategoryToApiViewDTO category = null;
            if (transaction.getCategory() != null) {
                category = new CategoryToApiViewDTO(transaction.getCategory());
            }
            return new TransactionToApiViewDTO(transaction, category);
        }).toList();

    }

    public TransactionToApiViewDTO(Transaction transaction, CategoryToApiViewDTO category) {

        this(transaction.getId(), transaction.getType(), transaction.getDescription(), transaction.getAmount(), transaction.getDate(), category, transaction.getInstallmentNumber(), transaction.getInstallmentTotal());
    }
}
