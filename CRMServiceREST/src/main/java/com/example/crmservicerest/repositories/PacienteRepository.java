package com.example.crmservicerest.repositories;

import com.example.crmservicerest.domain.Endereco;
import com.example.crmservicerest.domain.Paciente;
import com.example.crmservicerest.infrastructure.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PacienteRepository {

    public void salvar(Paciente paciente) {
        if (paciente.getId() == null) {
            String sqlInsert = """
                INSERT INTO pacientes (
                                       nome, email, telefone, cpf, ativo, rua, 
                                       numero, complemento, bairro, cidade, estado, cep)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {

                ps.setString(1, paciente.getNome());
                ps.setString(2, paciente.getEmail());
                ps.setString(3, paciente.getTelefone());
                ps.setString(4, paciente.getCpf());
                ps.setBoolean(5, paciente.isAtivo());
                ps.setString(6, paciente.getEndereco().getRua());
                ps.setString(7, paciente.getEndereco().getNumero());
                ps.setString(8, paciente.getEndereco().getComplemento());
                ps.setString(9, paciente.getEndereco().getBairro());
                ps.setString(10, paciente.getEndereco().getCidade());
                ps.setString(11, paciente.getEndereco().getEstado());
                ps.setString(12, paciente.getEndereco().getCep());

                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    paciente.setId(rs.getLong(1));
                }

            } catch (SQLException e) {
                throw new RuntimeException("Erro ao inserir paciente: "
                        + e.getMessage(), e);
            }

        } else {

            String sqlUpdate = """
                UPDATE pacientes SET
                    nome = ?, telefone = ?, ativo = ?, 
                    rua = ?, numero = ?, complemento = ?, 
                    bairro = ?, cidade = ?, estado = ?, cep = ?
                WHERE id = ?
            """;

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {

                ps.setString(1, paciente.getNome());
                ps.setString(2, paciente.getTelefone());
                ps.setBoolean(3, paciente.isAtivo());
                ps.setString(4, paciente.getEndereco().getRua());
                ps.setString(5, paciente.getEndereco().getNumero());
                ps.setString(6, paciente.getEndereco().getComplemento());
                ps.setString(7, paciente.getEndereco().getBairro());
                ps.setString(8, paciente.getEndereco().getCidade());
                ps.setString(9, paciente.getEndereco().getEstado());
                ps.setString(10, paciente.getEndereco().getCep());
                ps.setLong(11, paciente.getId());

                ps.executeUpdate();

            } catch (SQLException e) {
                throw new RuntimeException("Erro ao atualizar paciente: " + e.getMessage(), e);
            }
        }
    }


    public Optional<Paciente> buscarPorId(Long id) {
        String sql = "SELECT * FROM pacientes WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapearPaciente(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar paciente por ID: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    public List<Paciente> listarTodos() {
        List<Paciente> pacientes = new ArrayList<>();
        String sql = "SELECT * FROM pacientes";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                pacientes.add(mapearPaciente(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar pacientes: " + e.getMessage(), e);
        }

        return pacientes;
    }

    private Paciente mapearPaciente(ResultSet rs) throws SQLException {
        Endereco endereco = new Endereco(
                rs.getString("rua"),
                rs.getString("numero"),
                rs.getString("complemento"),
                rs.getString("bairro"),
                rs.getString("cidade"),
                rs.getString("estado"),
                rs.getString("cep")
        );

        Paciente paciente = new Paciente(
                rs.getString("nome"),
                rs.getString("email"),
                rs.getString("telefone"),
                rs.getString("cpf"),
                endereco
        );
        paciente.setId(rs.getLong("id"));
        paciente.setAtivo(rs.getBoolean("ativo"));
        return paciente;
    }

    public Optional<Paciente> buscarPorCpf(String cpf) {
        String sql = "SELECT * FROM pacientes WHERE cpf = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, cpf);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Paciente paciente = mapearPaciente(rs);
                return Optional.of(paciente);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar paciente por CPF: "
                    + e.getMessage(), e);
        }

        return Optional.empty();
    }

}
