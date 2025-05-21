package com.example.crmservicerest.repositories;

import com.example.crmservicerest.domain.Endereco;
import com.example.crmservicerest.domain.Especialidade;
import com.example.crmservicerest.domain.Medico;
import com.example.crmservicerest.infrastructure.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MedicoRepository {

    private final Connection connection;

    public MedicoRepository() throws SQLException {
        this.connection = DatabaseConnection.getConnection();
    }

    public void salvar(Medico medico) {
        try {
            if (medico.getId() == null) {
                String sql = """
                    INSERT INTO medicos (nome, email, telefone, crm, especialidade, ativo,
                                         rua, numero, complemento, bairro, cidade, estado, cep)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

                PreparedStatement stmt =
                        connection.prepareStatement(sql,
                                Statement.RETURN_GENERATED_KEYS);
                stmt.setString(1, medico.getNome());
                stmt.setString(2, medico.getEmail());
                stmt.setString(3, medico.getTelefone());
                stmt.setString(4, medico.getCrm());
                stmt.setString(5, medico.getEspecialidade().name());
                stmt.setBoolean(6, medico.isAtivo());

                stmt.setString(7, medico.getEndereco().getRua());
                stmt.setString(8, medico.getEndereco().getNumero());
                stmt.setString(9, medico.getEndereco().getComplemento());
                stmt.setString(10, medico.getEndereco().getBairro());
                stmt.setString(11, medico.getEndereco().getCidade());
                stmt.setString(12, medico.getEndereco().getEstado());
                stmt.setString(13, medico.getEndereco().getCep());

                stmt.executeUpdate();

                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    medico.setId(rs.getLong(1));
                }
            } else {
                String sql = """
                    UPDATE medicos
                    SET nome = ?, email = ?, telefone = ?, ativo = ?,
                        rua = ?, numero = ?, complemento = ?, bairro = ?, cidade = ?, estado = ?, cep = ?
                    WHERE id = ?
                """;

                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setString(1, medico.getNome());
                stmt.setString(2, medico.getEmail());
                stmt.setString(3, medico.getTelefone());
                stmt.setBoolean(4, medico.isAtivo());

                stmt.setString(5, medico.getEndereco().getRua());
                stmt.setString(6, medico.getEndereco().getNumero());
                stmt.setString(7, medico.getEndereco().getComplemento());
                stmt.setString(8, medico.getEndereco().getBairro());
                stmt.setString(9, medico.getEndereco().getCidade());
                stmt.setString(10, medico.getEndereco().getEstado());
                stmt.setString(11, medico.getEndereco().getCep());

                stmt.setLong(12, medico.getId());

                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar médico: " + e.getMessage(), e);
        }
    }

    public List<Medico> listarTodos() {
        List<Medico> medicos = new ArrayList<>();
        String sql = "SELECT * FROM medicos";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Medico medico = new Medico();
                medico.setId(rs.getLong("id"));
                medico.setNome(rs.getString("nome"));
                medico.setEmail(rs.getString("email"));
                medico.setTelefone(rs.getString("telefone"));
                medico.setCrm(rs.getString("crm"));
                medico.setEspecialidade(Especialidade.valueOf(rs.getString("especialidade")));
                medico.setAtivo(rs.getBoolean("ativo"));

                Endereco endereco = new Endereco(
                        rs.getString("rua"),
                        rs.getString("numero"),
                        rs.getString("complemento"),
                        rs.getString("bairro"),
                        rs.getString("cidade"),
                        rs.getString("estado"),
                        rs.getString("cep")
                );
                medico.setEndereco(endereco);

                medicos.add(medico);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return medicos;
    }

    public Optional<Medico> buscarPorCrm(String crm) {
        try {
            String sql = "SELECT * FROM medicos WHERE crm = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, crm);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(montarMedico(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar médico por CRM: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    public Optional<Medico> buscarPorEspecialidadeELivre(
            Especialidade especialidade, LocalDateTime data) {
        try {
            String sql = """
                SELECT * FROM medicos m
                WHERE m.especialidade = ?
                  AND m.ativo = true
                  AND NOT EXISTS (
                      SELECT 1 FROM consultas c
                      WHERE c.medico_id = m.id AND c.data = ?
                  )
                LIMIT 1
            """;

            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, especialidade.name());
            stmt.setTimestamp(2, Timestamp.valueOf(data));
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(montarMedico(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erro ao buscar médico por especialidade e horário livre: "
                            + e.getMessage(), e);
        }

        return Optional.empty();
    }

    private Medico montarMedico(ResultSet rs) throws SQLException {
        Medico medico = new Medico();
        medico.setId(rs.getLong("id"));
        medico.setNome(rs.getString("nome"));
        medico.setEmail(rs.getString("email"));
        medico.setTelefone(rs.getString("telefone"));
        medico.setCrm(rs.getString("crm"));
        medico.setEspecialidade(
                Especialidade.valueOf(rs.getString("especialidade").toUpperCase()));
        medico.setAtivo(rs.getBoolean("ativo"));

        Endereco endereco = new Endereco(
                rs.getString("rua"),
                rs.getString("numero"),
                rs.getString("complemento"),
                rs.getString("bairro"),
                rs.getString("cidade"),
                rs.getString("estado"),
                rs.getString("cep")
        );
        medico.setEndereco(endereco);
        return medico;
    }

    public boolean existePorCrm(String crm) throws SQLException {
        String sql = "SELECT COUNT(*) FROM medicos WHERE crm = ? AND ativo = true";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, crm);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        }
    }

    public void desativar(String crm) throws SQLException {
        String sql = "UPDATE medicos SET ativo = false WHERE crm = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, crm);
            stmt.executeUpdate();
        }
    }
}
