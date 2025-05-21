package com.example.crmservicerest.services;

import com.example.crmservicerest.domain.Endereco;
import com.example.crmservicerest.domain.Especialidade;
import com.example.crmservicerest.domain.Medico;
import com.example.crmservicerest.dto.AtualizacaoMedicoDto;
import com.example.crmservicerest.dto.CadastroMedicoDto;
import com.example.crmservicerest.dto.MedicoListagemDto;
import com.example.crmservicerest.exceptions.BusinessException;
import com.example.crmservicerest.repositories.MedicoRepository;

import java.sql.SQLException;
import java.util.List;

public class MedicoService {

    private final MedicoRepository repository;

    public MedicoService() throws SQLException {
        this.repository = new MedicoRepository();
    }

    public void cadastrar(CadastroMedicoDto dto) {
        if (isNullOrEmpty(dto.getNome()))
            throw new BusinessException("O nome é obrigatório.");
        if (isNullOrEmpty(dto.getEmail()))
            throw new BusinessException("O e-mail é obrigatório.");
        if (isNullOrEmpty(dto.getCrm()))
            throw new BusinessException("O CRM é obrigatório.");
        if (dto.getEspecialidade() == null)
            throw new BusinessException("A especialidade é obrigatória.");

        Endereco endereco = new Endereco(
                dto.getRua(),
                dto.getNumero(),
                dto.getComplemento(),
                dto.getBairro(),
                dto.getCidade(),
                dto.getEstado(),
                dto.getCep()
        );

        Medico medico = new Medico(
                dto.getNome(),
                dto.getEmail(),
                dto.getTelefone(),
                dto.getCrm(),
                Especialidade.valueOf(dto.getEspecialidade().toUpperCase()),
                endereco
        );

        repository.salvar(medico);
        System.out.println("Médico cadastrado com sucesso: "
                + medico.getNome() + " (CRM: " + medico.getCrm() + ")");
    }

    public List<MedicoListagemDto> listar() {
        List<Medico> medicos = repository.listarTodos();
        return medicos.stream()
                .map(m -> new MedicoListagemDto(
                        m.getNome(),
                        m.getEmail(),
                        m.getCrm(),
                        m.getEspecialidade() != null ? m.getEspecialidade().name() : null,
                        m.isAtivo()
                ))
                .toList();
    }

    public void atualizar(String crm, AtualizacaoMedicoDto dto) {
        Medico medico = repository.buscarPorCrm(crm)
                .orElseThrow(() -> new BusinessException("Médico não encontrado."));

        if (dto.getNome() != null && !dto.getNome().isBlank()) {
            medico.setNome(dto.getNome());
        }

        if (dto.getTelefone() != null && !dto.getTelefone().isBlank()) {
            medico.setTelefone(dto.getTelefone());
        }

        if (dto.getRua() != null) {
            medico.getEndereco().atualizar(
                    dto.getRua(),
                    dto.getNumero(),
                    dto.getComplemento(),
                    dto.getBairro(),
                    dto.getCidade(),
                    dto.getEstado(),
                    dto.getCep()
            );
        }

        repository.salvar(medico);
        System.out.println("Médico atualizado com sucesso: "
                + medico.getNome() + " (CRM: " + medico.getCrm() + ")");
    }

    public void excluir(String crm) {
        Medico medico = repository.buscarPorCrm(crm)
                .orElseThrow(() -> new BusinessException("Médico não encontrado."));
        medico.desativar();
        repository.salvar(medico);
        System.out.println("Médico desativado: "
                + medico.getNome() + " (CRM: " + medico.getCrm() + ")");
    }

    private boolean isNullOrEmpty(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    public MedicoListagemDto buscarPorCrm(String crm) {
        Medico medico = repository.buscarPorCrm(crm)
                .orElseThrow(() -> new BusinessException("Médico não encontrado."));

        System.out.println("Médico buscado por CRM: " + crm);

        return new MedicoListagemDto(
                medico.getNome(),
                medico.getEmail(),
                medico.getCrm(),
                medico.getEspecialidade() != null ? medico.getEspecialidade().name() : null,
                medico.isAtivo()
        );
    }

    public void desativar(String crm) {
        Medico medico = repository.buscarPorCrm(crm)
                .orElseThrow(() -> new BusinessException("Médico com CRM " + crm + " não encontrado."));
        medico.desativar();
        repository.salvar(medico);
        System.out.println("Médico desativado com sucesso: "
                + medico.getNome() + " (CRM: " + medico.getCrm() + ")");
    }
}
