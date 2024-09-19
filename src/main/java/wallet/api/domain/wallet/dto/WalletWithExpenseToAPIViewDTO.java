package wallet.api.domain.wallet.dto;

import wallet.api.domain.expense.dto.ExpenseToApiViewDto;
import wallet.api.domain.expense.entity.Expense;
import wallet.api.domain.wallet.entity.Wallet;

import java.util.List;

public record WalletWithExpenseToAPIViewDTO(
        String id,
        Integer balance,
        List<ExpenseToApiViewDto> expense
) {
    public WalletWithExpenseToAPIViewDTO(Wallet wallet, List<Expense> expenses){
        this(wallet.getId(), wallet.getBalance(), ExpenseToApiViewDto.fromList(expenses));
    }
}
