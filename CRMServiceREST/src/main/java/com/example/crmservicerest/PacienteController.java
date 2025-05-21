package com.example.crmservicerest;

import com.example.crmservicerest.domain.Paciente;
import com.example.crmservicerest.dto.AtualizacaoPacienteDto;
import com.example.crmservicerest.dto.CadastroPacienteDto;
import com.example.crmservicerest.dto.MedicoListagemDto;
import com.example.crmservicerest.dto.PacienteListagemDto;
import com.example.crmservicerest.exceptions.BusinessException;
import com.example.crmservicerest.services.MedicoService;
import com.example.crmservicerest.services.PacienteService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Path("/pacientes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PacienteController {

    private final PacienteService service = new PacienteService();

    @POST
    public Response cadastrar(CadastroPacienteDto dto) {
        try {
            service.cadastrar(dto);
            return Response.status(Response.Status.CREATED).build();
        } catch (BusinessException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @GET
    public Response listar() {
        try {
            List<PacienteListagemDto> pacientes = service.listar();
            return Response.ok(pacientes).build();
        } catch (Exception e) {
            return Response.serverError().entity("Erro ao acessar banco de dados.").build();
        }
    }

    @GET
    @Path("/{cpf}")
    public Response buscarPorCpf(@PathParam("cpf") String cpf) {
        Optional<Paciente> pacienteOpt = service.buscarPorCpf(cpf);
        if (pacienteOpt.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Paciente com CPF " + cpf + " não encontrado.")
                    .build();
        }
        return Response.ok(pacienteOpt.get()).build();
    }

    @PUT
    @Path("/{cpf}")
    public Response atualizar(@PathParam("cpf") String cpf, AtualizacaoPacienteDto dto) {
        try {
            dto.setCpf(cpf);
            service.atualizar(dto);
            return Response.noContent().build();
        } catch (BusinessException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @DELETE
    @Path("/{cpf}")
    public Response excluir(@PathParam("cpf") String cpf) {
        try {
            service.desativar(cpf);
            return Response.noContent().build();
        } catch (BusinessException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();
        }
    }
}
