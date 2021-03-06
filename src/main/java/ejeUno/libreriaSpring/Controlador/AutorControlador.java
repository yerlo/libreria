package ejeUno.libreriaSpring.Controlador;

import static com.sun.corba.se.spi.presentation.rmi.StubAdapter.request;
import ejeUno.libreriaSpring.Entidad.Autor;
import ejeUno.libreriaSpring.Entidad.Usuario;
import ejeUno.libreriaSpring.Excepciones.MiExcepcion;
import ejeUno.libreriaSpring.Servicio.AutorServicio;
import ejeUno.libreriaSpring.Servicio.UsuarioServicio;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/autor")
public class AutorControlador {

    @Autowired
    private AutorServicio autorServicio;
    @Autowired
    private UsuarioServicio usuarioServicio;

    @GetMapping
    public ModelAndView mostrarAutores(HttpSession sesion, HttpServletRequest request) throws MiExcepcion, Exception {

        ModelAndView mav = new ModelAndView("autor");
        Map<String, ?> flashMap = RequestContextUtils.getInputFlashMap(request);
        if (flashMap != null) {
            mav.addObject("exito", flashMap.get("exito-name"));
            mav.addObject("error", flashMap.get("error-name"));
        }
        
        Usuario usuario = usuarioServicio.obteneUsuario((String) sesion.getAttribute("idUsuario"));
        if (usuario.getRol().getNombre().equals("CLIENTE")) {
        
            List<Autor> autores = autorServicio.obtenerAutor(true);
            autores.sort(Autor.compararNombre);
            mav.addObject("autores", autores);
        } else {
        
            List<Autor> autores = autorServicio.obtenerAutor();
            autores.sort(Autor.compararNombre);
            mav.addObject("autores", autores);
        }

        return mav;
    }

    /*@GetMapping("/crear")
    public ModelAndView crearAutor() throws Exception {
        try {
            ModelAndView mav = new ModelAndView("formulario-autor-editorial");
            mav.addObject("autor", new Autor());
            mav.addObject("title", "Crear Usuario");
            mav.addObject("action", "guardar");
            return mav;
        } catch (Exception e) {
            throw e;
        }
    }*/
    @PostMapping("/guardar")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER')")
    public RedirectView guardar(@RequestParam String nombre, RedirectAttributes attributes) throws Exception {
        try {
            autorServicio.crearAutor(nombre);
            attributes.addFlashAttribute("exito-name", "El Autor ha sido registrado exitosamente");
        } catch (Exception e) {
            attributes.addFlashAttribute("error-name", e.getMessage());
        }
        return new RedirectView("/autor");
    }

    @PostMapping("/modificar-nombre")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER')")
    public RedirectView guardar(@RequestParam String nombre, @RequestParam Boolean estado, @RequestParam String id, RedirectAttributes attributes) throws Exception {
        try {
            autorServicio.modificarAutor(id, nombre, estado);
            attributes.addFlashAttribute("exito-name", "El Autor ha sido editado exitosamente");
        } catch (Exception e) {
            attributes.addFlashAttribute("error-name", e.getMessage());
        }
        return new RedirectView("/autor");
    }

    @PostMapping("/modificar-estado")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER')")
    public RedirectView guardar(@RequestParam Boolean estado, @RequestParam String id, RedirectAttributes attributes) throws Exception {
        try {
            autorServicio.modificarAutor(id, estado);
            attributes.addFlashAttribute("exito-name", "El Autor ha sido " + ((estado) ? "deshabilitado" : "habilitado") + " exitosamente");
        } catch (Exception e) {
            attributes.addFlashAttribute("error-name", e.getMessage());
        }
        return new RedirectView("/autor");
    }

}
