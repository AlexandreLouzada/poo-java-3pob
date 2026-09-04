// Código de Produção
class ValidadorSenha {
    public boolean isForte(String senha) {
        if (senha == null || senha.length() < 8) {
            return false;
        }
        boolean temMaiuscula = senha.chars().anyMatch(Character::isUpperCase);
        boolean temDigito = senha.chars().anyMatch(Character::isDigit);
        return temMaiuscula && temDigito;
    }
}
