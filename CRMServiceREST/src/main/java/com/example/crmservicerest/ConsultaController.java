package com.example.crmservicerest;

import com.example.crmservicerest.domain.Consulta;
import com.example.crmservicerest.dto.AgendamentoConsultaDto;
import com.example.crmservicerest.dto.CancelamentoConsultaDto;
import com.example.crmservicerest.dto.ExceptionResponseDTO;
import com.example.crmservicerest.exceptions.BusinessException;
import com.example.crmservicerest.services.ConsultaService;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/consultas")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ConsultaController {

    private final ConsultaService service;

    public ConsultaController() {
        this.service = new ConsultaService();
    }

    @POST
    @Path("/agendar")
    public Response agendarConsulta(AgendamentoConsultaDto dto) {
        try {
            service.agendarConsulta(dto);
            return Response.status(Response.Status.CREATED).build();
        } catch (BusinessException e) {
            ExceptionResponseDTO error = new ExceptionResponseDTO(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        } catch (Exception e) {
            ExceptionResponseDTO error = new ExceptionResponseDTO("Ocorreu um erro interno ao agendar a consulta.");
            return Response.serverError().entity(error).build();
        }
    }

    @PUT
    @Path("/cancelar")
    public Response cancelarConsulta(CancelamentoConsultaDto dto) {
        try {
            service.cancelarPorCpf(dto.getCpfPaciente(), dto.getMotivo());
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (BusinessException e) {
            ExceptionResponseDTO error = new ExceptionResponseDTO(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        } catch (Exception e) {
            ExceptionResponseDTO error = new ExceptionResponseDTO("Erro interno ao cancelar consulta.");
            return Response.serverError().entity(error).build();
        }
    }

    @GET
    @Path("/ativas")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listarConsultasAtivas() {
        try {
            List<Consulta> consultas = service.listarConsultasAtivas();
            return Response.ok(consultas).build();
        } catch (Exception e) {
            ExceptionResponseDTO error = new ExceptionResponseDTO("Erro ao buscar consultas.");
            return Response.serverError().entity(error).build();
        }
    }
}

