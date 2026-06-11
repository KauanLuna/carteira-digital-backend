package luna.kauan.festajuninapiraporinha.domain.entity;

import jakarta.persistence.*;
import lombok.Builder;
import luna.kauan.festajuninapiraporinha.domain.enums.Role;

import java.util.UUID;

@Entity
@Table(name = "usuarios")
@Builder
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    @Column(nullable = false)
    private String senha; // Será armazenada em hash (BCrypt), mesmo sendo numérica no input do cliente.

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // Relacionamento 1:1.
    // Nota pragmática para o MVP: Usuários BARRACA e CAIXA não precisam de carteira.
    // Portanto, o mapeamento permite que a carteira seja opcional dependendo da Role.
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Carteira carteira;

    public Usuario(UUID id, String nome, String cpf, String senha, Role role, Carteira carteira) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.senha = senha;
        this.role = role;
        this.carteira = carteira;
    }

    public Usuario() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Carteira getCarteira() {
        return carteira;
    }

    public void setCarteira(Carteira carteira) {
        this.carteira = carteira;
    }
}
