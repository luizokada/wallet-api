package wallet.api.domain.wallet.dto;

import wallet.api.domain.wallet.entity.Wallet;

public record WalletToApiViewDTO(
        String id,
        Integer balance
) {
    public WalletToApiViewDTO(Wallet wallet){
        this(wallet.getId(), wallet.getBalance());
    }
}
