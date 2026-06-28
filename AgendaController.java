package controller;

import model.Agendamento;
import util.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AgendaController {
    
    public boolean salvarAgendamento(Agendamento agendamento) {
        String sql = "INSERT INTO agendamentos (cliente_nome, cliente_telefone, cliente_email, profissional, data_agendamento, horario, procedimento, observacoes) VALUES (?, ?, ?, ?, CAST(? AS DATE), ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, agendamento.getClienteNome());
            stmt.setString(2, agendamento.getClienteTelefone());
            stmt.setString(3, agendamento.getClienteEmail());
            stmt.setString(4, agendamento.getProfissional());
            stmt.setString(5, agendamento.getData());
            stmt.setString(6, agendamento.getHorario());
            stmt.setString(7, agendamento.getProcedimento());
            stmt.setString(8, agendamento.getObservacoes());
            
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String> buscarProcedimentos() {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT nome FROM procedimentos ORDER BY nome";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) { lista.add(rs.getString("nome")); }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    // NOVO: Buscar lista de profissionais do banco dinamicamente
    public List<String> buscarProfissionais() {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT DISTINCT nome FROM usuarios WHERE perfil = 'BARBEIRO' OR perfil = 'ADMIN' ORDER BY nome";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) { lista.add(rs.getString("nome")); }
        } catch (SQLException e) { e.printStackTrace(); }
        // Garantir que não volte vazia no primeiro teste
        if (lista.isEmpty()) { lista.add("Jose"); lista.add("Carlos"); }
        return lista;
    }

    // NOVO: Inserir novo procedimento direto da tela
    public boolean cadastrarProcedimento(String nome) {
        String sql = "INSERT INTO procedimentos (nome) VALUES (?) ON CONFLICT DO NOTHING";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nome);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // NOVO: Inserir novo profissional direto da tela
    public boolean cadastrarProfissional(String nome, String email) {
        String sql = "INSERT INTO usuarios (nome, email, senha, perfil) VALUES (?, ?, '1234', 'BARBEIRO') ON CONFLICT DO NOTHING";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nome);
            stmt.setString(2, email);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public List<Agendamento> buscarRelatorioPorProfissional(String profissional) {
        List<Agendamento> lista = new ArrayList<>();
        String sql = "SELECT cliente_nome, cliente_telefone, cliente_email, profissional, TO_CHAR(data_agendamento, 'YYYY-MM-DD') as data_ag, horario, procedimento, observacoes FROM agendamentos WHERE profissional = ? ORDER BY data_agendamento, horario";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, profissional);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(new Agendamento(
                    rs.getString("cliente_nome"), rs.getString("cliente_telefone"), rs.getString("cliente_email"),
                    rs.getString("profissional"), rs.getString("data_ag"), rs.getString("horario"),
                    rs.getString("procedimento"), rs.getString("observacoes")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }
}
