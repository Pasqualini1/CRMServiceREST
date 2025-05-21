package com.example.crmservicerest;

import com.example.crmservicerest.dto.AtualizacaoMedicoDto;
import com.example.crmservicerest.dto.CadastroMedicoDto;
import com.example.crmservicerest.dto.MedicoListagemDto;
import com.example.crmservicerest.exceptions.BusinessException;
import com.example.crmservicerest.services.MedicoService;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Path("/medicos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MedicoController {

    private MedicoService medicoService;

    public MedicoController() {
        try {
            this.medicoService = new MedicoService();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inicializar serviço de médico", e);
        }
    }

    @POST
    public Response cadastrar(CadastroMedicoDto dto) {
        try {
            medicoService.cadastrar(dto);
            return Response.status(Response.Status.CREATED).build();
        } catch (BusinessException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno no servidor")
                    .build();
        }
    }

    @GET
    public Response listar() {
        try {
            MedicoService service = new MedicoService();
            List<MedicoListagemDto> medicos = service.listar();
            return Response.ok(medicos).build();
        } catch (SQLException e) {
            return Response.serverError().entity("Erro ao acessar banco de dados.").build();
        }
    }

    @GET
    @Path("/{crm}")
    public Response buscarPorCrm(@PathParam("crm") String crm) {
        try {
            MedicoListagemDto medico = medicoService.buscarPorCrm(crm);
            return Response.ok(medico).build();
        } catch (BusinessException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno no servidor")
                    .build();
        }
    }

    @PUT
    @Path("/{crm}")
    public Response atualizar(@PathParam("crm") String crm, AtualizacaoMedicoDto dto) {
        try {
            medicoService.atualizar(crm, dto);
            return Response.ok().build();
        } catch (BusinessException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno no servidor")
                    .build();
        }
    }

    @DELETE
    @Path("/{crm}")
    public Response desativar(@PathParam("crm") String crm) {
        try {
            medicoService.desativar(crm);
            return Response.noContent().build();
        } catch (BusinessException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno no servidor")
                    .build();
        }
    }
}
