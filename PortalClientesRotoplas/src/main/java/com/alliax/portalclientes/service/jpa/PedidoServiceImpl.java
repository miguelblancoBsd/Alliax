package com.alliax.portalclientes.service.jpa;

import com.alliax.portalclientes.domain.Pedido;
import com.alliax.portalclientes.repository.PedidoRepository;
import com.alliax.portalclientes.service.PedidoService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;


import java.util.List;

@Service("pedidoService")
@Repository
@Transactional
public class PedidoServiceImpl implements PedidoService {

    private final static Logger logger = Logger.getLogger(PedidoServiceImpl.class);

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Override
    @Transactional(readOnly=true)
    public List<Pedido> findAll() {
        return (List<Pedido>)pedidoRepository.findAll();
    }

    @Override
    @Transactional(readOnly=true)
    public Pedido findById(long idPedido) {
        return pedidoRepository.findOne(idPedido);
    }

    @Override
    public Pedido save(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    @Override
    public List<Pedido> findCotizacionesFlete(String fecha, String noCotizacion ,String noCliente){
        logger.info("findCotizacionesFlete fecha " + fecha );
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("DD/MM/YYYY");
            boolean flagFecha = (fecha!= null && fecha.length() >0) ;

            TypedQuery<Pedido> query = this.em.createNamedQuery("Pedido.findFletes",Pedido.class);
            query.setParameter("flagCot" , (noCotizacion!= null && noCotizacion.length() >0)  ? 0 : 1);
            query.setParameter("noCotizacion", noCotizacion);
            query.setParameter("flagCliente" , (noCliente!= null && noCliente.length() >0)  ? 0 : 1);
            query.setParameter("nroCliente", noCliente);
            logger.debug("Despues de setear parametos");

            List<Pedido> pedidos =  query.getResultList();

            if(flagFecha){
                List<Pedido> pedidosR =  new ArrayList();
                for(Pedido p : pedidos){
                    logger.info("Fecha Creacion" + p.getFechaCreacion());

                    String  fechaP = p.getFechaCreacion()!= null ? sdf.format(p.getFechaCreacion()) : null ;

                    if(fechaP!= null  && fechaP.equals(fecha)){
                        pedidosR.add(p);
                    }
                }
                return pedidosR;
            }
            return pedidos;
        } catch(NoResultException nre){
            logger.error("No existen Fletes " + nre.getLocalizedMessage());
            return null;
        }
    }
}
