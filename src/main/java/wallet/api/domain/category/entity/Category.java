package wallet.api.domain.category.entity;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import wallet.api.domain.category.dtos.CreateCategoryDTO;
import wallet.api.domain.category.dtos.UpdateCategoryDTO;
import wallet.api.domain.transaction.entity.Transaction;
import wallet.api.domain.transaction.entity.TransactionType;
import wallet.api.domain.user.entity.User;

@Table(name = "categories")
@Entity(name = "Category")
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Getter
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private TransactionType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User owner;

    @JsonManagedReference
    @OneToMany(mappedBy = "category", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private Set<Transaction> transactions = new HashSet<>();

    public Category(String id, String name, String description, TransactionType type) {
        this(id, name, description, type, null);
    }

    public Category(String id, String name, String description, TransactionType type, User owner) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.owner = owner;
    }

    public Category(CreateCategoryDTO dto, User owner) {
        this.name = dto.name();
        this.description = dto.description();
        this.type = dto.type();
        this.owner = owner;
    }

    // type NÃO é atualizável: mudar o tipo invalidaria as transações já ligadas à categoria
    public void update(UpdateCategoryDTO dto) {
        if (dto.name() != null) {
            this.name = dto.name();
        }
        if (dto.description() != null) {
            this.description = dto.description();
        }
    }

    public boolean isDefault() {
        return owner == null;
    }

}
