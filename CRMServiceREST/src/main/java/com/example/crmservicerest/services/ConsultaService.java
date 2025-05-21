package com.example.crmservicerest.services;

import com.example.crmservicerest.domain.Consulta;
import com.example.crmservicerest.domain.Medico;
import com.example.crmservicerest.domain.Paciente;
import com.example.crmservicerest.dto.AgendamentoConsultaDto;
import com.example.crmservicerest.exceptions.BusinessException;
import com.example.crmservicerest.repositories.ConsultaRepository;
import com.example.crmservicerest.repositories.MedicoRepository;
import com.example.crmservicerest.repositories.PacienteRepository;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class ConsultaService {

    private final MedicoRepository medicoRepository;
    private final PacienteRepository pacienteRepository;
    private final ConsultaRepository consultaRepository;

    public ConsultaService() {
        try {
            this.medicoRepository = new MedicoRepository();
            this.pacienteRepository = new PacienteRepository();
            this.consultaRepository = new ConsultaRepository();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao conectar com o banco de dados: " + e.getMessage(), e);
        }
    }

    public void agendarConsulta(AgendamentoConsultaDto dto) {
        LocalDateTime data = dto.getDataHoraFormatada();

        if (data.getHour() < 7 || data.getHour() > 18) {
            throw new BusinessException("Consultas devem ser agendadas entre 07:00 e 18:00.");
        }

        if (data.getDayOfWeek() == DayOfWeek.SUNDAY) {
            throw new BusinessException("Consultas não podem ser agendadas aos domingos.");
        }

        if (Duration.between(LocalDateTime.now(), data).toMinutes() < 30) {
            throw new BusinessException("Consultas devem ser agendadas com pelo menos 30 minutos de antecedência.");
        }

        Paciente paciente = pacienteRepository.buscarPorCpf(dto.getCpfPaciente())
                .orElseThrow(() -> new BusinessException("Paciente não encontrado."));
        if (!paciente.isAtivo()) {
            throw new BusinessException("Paciente inativo.");
        }

        Medico medico;
        if (!isNullOrBlank(dto.getCrm())) {
            medico = medicoRepository.buscarPorCrm(dto.getCrm())
                    .orElseThrow(() -> new BusinessException("Médico não encontrado com o CRM informado."));
        } else {
            if (dto.getEspecialidade() == null) {
                throw new BusinessException("Especialidade obrigatória se não for informado o médico.");
            }
            medico = medicoRepository.buscarPorEspecialidadeELivre(dto.getEspecialidade(), data)
                    .orElseThrow(() -> new BusinessException("Nenhum médico disponível nesta data para a especialidade informada."));
        }

        if (!medico.isAtivo()) {
            throw new BusinessException("Médico inativo.");
        }

        boolean conflito = consultaRepository.verificarConflito(medico.getCrm(), data);
        if (conflito) {
            throw new BusinessException("Médico já possui consulta marcada neste horário.");
        }

        Consulta consulta = new Consulta(medico, paciente, data);
        consultaRepository.salvar(consulta);

        System.out.println("Consulta agendada com sucesso:");
        System.out.println("   Paciente: " + paciente.getNome());
        System.out.println("   Médico: " + medico.getNome() + " (CRM: " + medico.getCrm() + ")");
        System.out.println("   Especialidade: " + medico.getEspecialidade());
        System.out.println("   Data e hora: " + data.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
    }

    public void cancelar(Long idConsulta, String motivo) {
        try {
            boolean atualizado = consultaRepository.cancelarConsulta(idConsulta, motivo);
            if (!atualizado) {
                throw new BusinessException("Consulta não encontrada.");
            }

            System.out.println("Consulta cancelada com sucesso (ID: " + idConsulta + ")");
            System.out.println("Motivo: " + motivo);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao cancelar consulta: " + e.getMessage(), e);
        }
    }

    public void cancelarPorCpf(String cpfPaciente, String motivo) {
        try {
            Long idConsulta = consultaRepository.buscarIdConsultaAtivaPorCpf(cpfPaciente)
                    .orElseThrow(() -> new BusinessException("Nenhuma consulta ativa encontrada para o CPF informado."));
            cancelar(idConsulta, motivo);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao cancelar consulta por CPF: " + e.getMessage(), e);
        }
    }

    public List<Consulta> listarConsultasAtivas() {
        return consultaRepository.buscarConsultasAtivas();
    }

    private boolean isNullOrBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
