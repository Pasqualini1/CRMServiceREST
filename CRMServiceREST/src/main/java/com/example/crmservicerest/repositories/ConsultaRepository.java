package com.example.crmservicerest.repositories;

import com.example.crmservicerest.domain.Consulta;
import com.example.crmservicerest.domain.Especialidade;
import com.example.crmservicerest.domain.Medico;
import com.example.crmservicerest.domain.Paciente;
import com.example.crmservicerest.infrastructure.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ConsultaRepository {

    private final Connection connection;

    public ConsultaRepository() throws SQLException {
        this.connection = DatabaseConnection.getConnection();
        this.connection.setAutoCommit(false);
    }

    public void salvar(Consulta consulta) {
        String sql = "INSERT INTO consultas (nome_medico, crm, especialidade, nome_paciente, cpf_paciente, data, ativa, motivo_cancelamento) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, consulta.getMedico().getNome());
            ps.setString(2, consulta.getMedico().getCrm());
            ps.setString(3, consulta.getMedico().getEspecialidade().toString());
            ps.setString(4, consulta.getPaciente().getNome());
            ps.setString(5, consulta.getPaciente().getCpf());
            ps.setTimestamp(6, Timestamp.valueOf(consulta.getData()));
            ps.setBoolean(7, consulta.isAtiva());
            ps.setString(8, consulta.getMotivoCancelamento());

            ps.executeUpdate();
            connection.commit();

            System.out.println("Consulta salva com sucesso.");
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException("Erro ao fazer rollback", ex);
            }
            throw new RuntimeException("Erro ao salvar consulta", e);
        }
    }

    public boolean cancelarConsulta(Long idConsulta, String motivo) {
        String sql = "UPDATE consultas SET ativa = false, motivo_cancelamento = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, motivo);
            ps.setLong(2, idConsulta);

            int linhasAfetadas = ps.executeUpdate();
            connection.commit();
            return linhasAfetadas > 0;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException("Erro ao fazer rollback", ex);
            }
            throw new RuntimeException("Erro ao cancelar consulta", e);
        }
    }

    public boolean verificarConflito(String crm, LocalDateTime data) {
        String sql = "SELECT COUNT(*) FROM consultas WHERE crm = ? AND data = ? AND ativa = true";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, crm);
            ps.setTimestamp(2, Timestamp.valueOf(data));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao verificar conflito de horário", e);
        }
    }

    public Optional<Long> buscarIdConsultaAtivaPorCpf(String cpfPaciente) {
        String sql = "SELECT id FROM consultas WHERE cpf_paciente = ? AND ativa = true ORDER BY data DESC LIMIT 1";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, cpfPaciente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(rs.getLong("id"));
                } else {
                    return Optional.empty();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar consulta ativa por CPF", e);
        }
    }

    public List<Consulta> buscarConsultasAtivas() {
        String sql = "SELECT * FROM consultas WHERE ativa = true ORDER BY data ASC";
        List<Consulta> consultas = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Medico medico = new Medico();
                medico.setNome(rs.getString("nome_medico"));
                medico.setCrm(rs.getString("crm"));
                medico.setEspecialidade(Especialidade.valueOf(rs.getString("especialidade"))); // enum

                Paciente paciente = new Paciente();
                paciente.setNome(rs.getString("nome_paciente"));
                paciente.setCpf(rs.getString("cpf_paciente"));

                // Criar Consulta
                Consulta consulta = new Consulta(
                        medico,
                        paciente,
                        rs.getTimestamp("data").toLocalDateTime()
                );

                consulta.setId(rs.getLong("id"));

                String motivo = rs.getString("motivo_cancelamento");
                if (motivo != null && !motivo.isBlank()) {
                    consulta.setMotivoCancelamento(motivo);
                }

                consultas.add(consulta);
            }

            return consultas;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar consultas agendadas", e);
        }
    }

}
