package ejeUno.libreriaSpring.Servicio;

import ejeUno.libreriaSpring.Entidad.Cliente;
import ejeUno.libreriaSpring.Entidad.Libro;
import ejeUno.libreriaSpring.Entidad.Prestamo;
import ejeUno.libreriaSpring.Excepciones.MiExcepcion;
import ejeUno.libreriaSpring.Repositorio.LibroRepositorio;
import ejeUno.libreriaSpring.Repositorio.PrestamoRepositorio;
import ejeUno.libreriaSpring.Validacion.ValidacionInterface;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PrestamoServicio implements ValidacionInterface {

    @Autowired
    private PrestamoRepositorio prestamoRepositorio;
    @Autowired
    private LibroRepositorio libroRepositorio;

    @Transactional
    public void guardarTransaccion(Boolean estado, LocalDate fechaDevolucion, Integer cantidad, Cliente cliente, Libro libro) throws Exception, MiExcepcion {
        try {
            validacionEstado(estado, "Libro");
            validacionFechaDevolucion(fechaDevolucion);
            validacionPresencia(cantidad, "cantidad");
            validacionPresencia(cliente, "cliente");
            validacionPresencia(libro, "libro");
            validacionEstado(cliente.getEstado(), "Cliente");
            validacionEstado(libro.getEstado(), "Libro");
            validacionTransaccion(cantidad, libro.getEjemplaresRestantes(), libro.getEjemplares());

            libro.setEjemplaresPrestados(libro.getEjemplaresPrestados() + cantidad);
            libro.setEjemplaresRestantes(Math.abs(libro.getEjemplares() - libro.getEjemplaresPrestados()));
            libroRepositorio.save(libro);
        } catch (MiExcepcion es) {
            throw es;
        } catch (Exception e) {
            throw e;
        }
        try {
            Prestamo prestamo = new Prestamo();
            prestamo.setCantidad(cantidad);
            prestamo.setCantidadRegistro(cantidad);
            prestamo.setFechaPrestamo(LocalDate.now());
            prestamo.setFechaDevolucion(fechaDevolucion);
            prestamo.setEstado(true);
            prestamo.setCliente(cliente);
            prestamo.setLibro(libro);

            prestamoRepositorio.save(prestamo);
        } catch (Exception e) {
            libro.setEjemplaresPrestados(Math.abs(libro.getEjemplaresPrestados() - cantidad));
            libro.setEjemplaresRestantes(Math.abs(libro.getEjemplares() - libro.getEjemplaresPrestados()));
            libroRepositorio.save(libro);
            throw e;
        }
    }

    @Transactional
    public void guardarTransaccion(Prestamo prestamo, Integer cantidad, Cliente cliente, Libro libro) throws Exception, MiExcepcion {
        try {
            validacionPresencia(cantidad, "cantidad");
            validacionPresencia(cliente, "Cliente");
            validacionPresencia(libro, "Libro");
            validacionEstado(cliente.getEstado(), "Cliente");
            validacionEstado(libro.getEstado(), "Libro");
            validacionEstado(prestamo.getEstado(), "Prestamo");
            validacionTransaccion(cantidad, prestamo.getCantidad(), libro.getEjemplares());

            libro.setEjemplaresPrestados(Math.abs(libro.getEjemplaresPrestados() - cantidad));
            libro.setEjemplaresRestantes(Math.abs(libro.getEjemplares() - libro.getEjemplaresPrestados()));

            libroRepositorio.save(libro);
        } catch (MiExcepcion es) {
            throw es;
        } catch (Exception e) {
            throw e;
        }
        try {

            prestamo.setCantidad(Math.abs(prestamo.getCantidad() - cantidad));
            prestamo.setFechaPrestamo(LocalDate.now());
            prestamo.setCliente(cliente);
            prestamo.setLibro(libro);
            if (prestamo.getCantidad() <= 0) {
                prestamo.setEstado(false);
                prestamo.setFechaFinalizada(LocalDate.now());
            }

            prestamoRepositorio.save(prestamo);
        } catch (Exception e) {
            libro.setEjemplaresPrestados(Math.abs(libro.getEjemplaresPrestados() + cantidad));
            libro.setEjemplaresRestantes(Math.abs(libro.getEjemplares() - libro.getEjemplaresPrestados()));
            libroRepositorio.save(libro);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public Prestamo obtenerPrestamo(String id) throws Exception {
        try {

            Optional<Prestamo> respuesta = prestamoRepositorio.findById(id);
            validacionPresencia(respuesta, "Prestamo");
            return prestamoRepositorio.findById(id).get();
        } catch (Exception e) {
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public List<Prestamo> obtenerPrestamo() throws Exception {
        try {

            return prestamoRepositorio.findAll();
        } catch (Exception e) {
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public List<Prestamo> obtenerPrestamoxRol(Boolean estado, String rol) throws Exception {
        try {
            return prestamoRepositorio.prestamoRol(estado, rol);
        } catch (Exception e) {
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public List<Prestamo> obtenerPrestamoxPerfil(Boolean estado, String usuarioId) throws Exception {
        try {
            return prestamoRepositorio.prestamoPerfil(estado, usuarioId);
        } catch (Exception e) {
            throw e;
        }
    }

    /*@Transactional(readOnly = true)
    public List<Prestamo> obtenerPrestamo(Boolean estado, String id) throws Exception {
        try {
            return prestamoRepositorio.findAll(estado, id);
        } catch (Exception e) {
            throw e;
        }
    }*/
    
        @Transactional(readOnly = true)
    public List<Prestamo> obtenerPrestamo(Boolean estado) throws Exception {
        try {
            return prestamoRepositorio.findByEstado(estado);
        } catch (Exception e) {
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public Long obtenerCantidadPrestamo(String id) throws Exception {
        try {

            return prestamoRepositorio.cantidadPrestamo(id);
        } catch (Exception e) {
            throw e;
        }
    }
}
