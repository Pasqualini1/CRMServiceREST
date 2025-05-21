package com.example.crmservicerest.services;

import com.example.crmservicerest.domain.Endereco;
import com.example.crmservicerest.domain.Paciente;
import com.example.crmservicerest.dto.AtualizacaoPacienteDto;
import com.example.crmservicerest.dto.CadastroPacienteDto;
import com.example.crmservicerest.dto.PacienteListagemDto;
import com.example.crmservicerest.exceptions.BusinessException;
import com.example.crmservicerest.repositories.PacienteRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class PacienteService {

    private final PacienteRepository repository;

    public PacienteService() {
        this.repository = new PacienteRepository();
    }

    public void cadastrar(CadastroPacienteDto dto) {
        if (isNullOrEmpty(dto.getNome())) throw new BusinessException(
                "O nome do paciente é obrigatório.");
        if (isNullOrEmpty(dto.getEmail())) throw new BusinessException(
                "O e-mail do paciente é obrigatório.");
        if (isNullOrEmpty(dto.getTelefone())) throw new BusinessException(
                "O telefone do paciente é obrigatório.");
        if (isNullOrEmpty(dto.getCpf())) throw new BusinessException(
                "O CPF do paciente é obrigatório.");
        if (isNullOrEmpty(dto.getRua()) || isNullOrEmpty(dto.getBairro()) ||
                isNullOrEmpty(dto.getCidade()) ||
                isNullOrEmpty(dto.getEstado()) || isNullOrEmpty(dto.getCep())) {
            throw new BusinessException(
                    "Endereço incompleto. Todos os campos obrigatórios devem ser preenchidos.");
        }

        Endereco endereco = new Endereco(
                dto.getRua(),
                dto.getNumero(),
                dto.getComplemento(),
                dto.getBairro(),
                dto.getCidade(),
                dto.getEstado(),
                dto.getCep()
        );

        Paciente paciente = new Paciente(
                dto.getNome(),
                dto.getEmail(),
                dto.getTelefone(),
                dto.getCpf(),
                endereco
        );

        repository.salvar(paciente);
        System.out.println("Paciente cadastrado com sucesso: "
                + paciente.getNome() + " (CPF: " + paciente.getCpf() + ")");
    }

    public List<PacienteListagemDto> listar() {
        List<PacienteListagemDto> lista = repository.listarTodos().stream()
                .filter(Paciente::isAtivo)
                .map(p -> new PacienteListagemDto(p.getNome(),
                        p.getEmail(), p.getCpf()))
                .sorted(Comparator.comparing(PacienteListagemDto::getNome,
                        String.CASE_INSENSITIVE_ORDER))
                .toList();

        System.out.println("Listagem de pacientes realizada. Total: " + lista.size());
        return lista;
    }

    public void atualizar(AtualizacaoPacienteDto dto) {
        Paciente paciente = repository.buscarPorCpf(dto.getCpf())
                .orElseThrow(() -> new BusinessException("Paciente não encontrado."));

        if (dto.getNome() != null && !dto.getNome().isBlank()) {
            paciente.setNome(dto.getNome());
        }

        if (dto.getTelefone() != null && !dto.getTelefone().isBlank()) {
            paciente.setTelefone(dto.getTelefone());
        }

        if (dto.getRua() != null) {
            paciente.getEndereco().atualizar(
                    dto.getRua(),
                    dto.getNumero(),
                    dto.getComplemento(),
                    dto.getBairro(),
                    dto.getCidade(),
                    dto.getEstado(),
                    dto.getCep()
            );
        }

        paciente.setAtivo(dto.isAtivo()); 

        repository.salvar(paciente);
        System.out.println("Paciente atualizado com sucesso: "
                + paciente.getNome() + " (CPF: " + paciente.getCpf() + ")");
    }

    public void excluir( Long id) {
        Paciente paciente = repository.buscarPorId(id)
                .orElseThrow(() -> new BusinessException("Paciente não encontrado."));

        paciente.desativar();
        repository.salvar(paciente);
        System.out.println("Paciente desativado: "
                + paciente.getNome() + " (ID: " + paciente.getId() + ")");
    }

    private boolean isNullOrEmpty(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    public Optional<Paciente> buscarPorCpf(String cpf) {
        Optional<Paciente> pacienteOpt = repository.buscarPorCpf(cpf);
        pacienteOpt.ifPresent(p -> System.out.println("Paciente encontrado: "
                + p.getNome() + " | CPF: " + p.getCpf()));
        return pacienteOpt;
    }

    public void desativar(String cpf) {
        Paciente paciente = repository.buscarPorCpf(cpf)
                .orElseThrow(() -> new BusinessException("Paciente não encontrado."));

        paciente.desativar();
        repository.salvar(paciente);

        System.out.println("Paciente desativado: " + paciente.getNome() + " (CPF: " + paciente.getCpf() + ")");
    }

}
